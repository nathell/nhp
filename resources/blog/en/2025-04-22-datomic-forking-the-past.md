---
date: 2025-04-22
title: No, really, you can’t branch Datomic from the past
subtitle: "(and what you can do instead)"
categories: programming clojure datomic
---

I have a love-hate relationship with [Datomic][1]. Datomic is a Clojure-based database based on a record of immutable facts; this post assumes a passing familiarity with it – if you haven’t yet, I highly recommend checking it out, it’s enlightening even if you end up not using it.

 [1]: https://www.datomic.com/

I’ll leave ranting on the “hate” part for some other time; here, I’d like to focus on some of the love – and its limits.

Datomic has this feature called “speculative writes”. It allows you to take an immutable database value, apply some new facts to it (speculatively, i.e., without sending them over to the transactor – this is self-contained within the JVM), and query the resulting database value _as if_ those facts had been transacted for real.

This is incredibly powerful. It lets you “fork” a Datomic connection (with the help of an ingenious library called [Datomock][2]), so that you can see all of the data in the source database up to the point of forking, but any new writes happen only in memory. You can develop on top of production data, but without any risk of damaging them! I remember how aghast I was upon first hearing about the concept, but now can’t imagine my life without it. Datomock’s author offers an analogy to Git: it’s like database values being commits, and connections being branches.

 [2]: https://github.com/vvvvalvalval/datomock/

Another awesome feature of Datomic is that it lets you travel back in time. You can call [`as-of`][3] on a database value, passing a timestamp, and you get back a db _as it was at that point in time_ – which you can query to your heart’s content. This aids immensely in forensic debugging, and helps answer questions which would have been outright impossible to answer with classical DBMSs.

 [3]: https://docs.datomic.com/reference/filters.html#as-of

Now, we’re getting to the crux of this post: `as-of` and speculative writes don’t compose together. If you try to create a Datomocked connection off of a database value obtained from `as-of`, you’ll get back a connection to which you can transact new facts, but you’ll never be able to see them. The analogy to Git falls down here: it’s as if Git only let you branch `HEAD`.

This is a well-known gotcha among Datomic users. From [Datomic’s documentation][4]:

 [4]: https://docs.datomic.com/reference/filters.html#as-of-not-branch

> **as-of Is Not a Branch**

> Filters are applied to an unfiltered database value obtained from `db` or `with`. In particular, the combination of `with` and `as-of` means "`with` followed by `as-of`", regardless of which API call you make first. `with` plus `as-of` lets you see a speculative db with recent datoms filtered out, but it does not let you branch the past.

So it appears that this is an insurmountable obstacle: you can’t fork the past with Datomic.

Or can you?

Reddit user NamelessMason has tried to [reimplement `as-of` on top of `d/filter`][5], yielding what seems to be a working approach to “datofork”! Quoting his post:

 [5]: https://www.reddit.com/r/Clojure/comments/yemxzi/datomic_with_asof_or_the_elusive_branching_off_a/

> Datomic supports 4 kinds of filters: `as-of`, `since`, `history` and custom `d/filter`, where you can filter by arbitrary datom predicate. […]

> `d/as-of` sets a effective upper limit on the T values visible through the Database object. This applies both to existing datoms as well as any datoms you try to add later. But since the `tx` value for the next transaction is predictable, and custom filters compose just fine, perhaps we could just white-list future transactions?

```clojure
(defn as-of'' [db t]
  (let [tx-limit (d/t->tx t)
        tx-allow (d/t->tx (d/basis-t db))]
    (d/filter db (fn [_ [e a v tx]] (or (<= tx tx-limit) (> tx tx-allow))))))
```

> […] Seems to work fine!

Sadly, it doesn’t actually work fine. Here’s a counterexample:

```clojure
(def conn (let [u "datomic:mem:test"] (d/create-database u) (d/connect u)))

;; Let's add some basic schema
@(d/transact conn [{:db/ident :test/id :db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one :db/unique :db.unique/identity}])
(d/basis-t (d/db conn)) ;=> 1000

;; Now let's transact an entity
@(d/transact conn [{:test/id "test", :db/ident ::the-entity}])
(d/basis-t (d/db conn)) ;=> 1001

;; And in another transaction let's change the :test/id of that entity
@(d/transact conn [[:db/add ::the-entity :test/id "test2"]])
(d/basis-t (d/db conn)) ;=> 1003

;; Trying a speculative write, forking from 1001
(def db' (-> (d/db conn)
             (as-of'' 1001)
             (d/with [[:db/add ::the-entity :test/id "test3"]])
             :db-after))
(:test/id (d/entity db' ::the-entity)) ;=> "test" (WRONG! it should be "test3")
```

To recap what we just did: we transacted version A of an entity, then an updated version B, then tried to fork C off of A, but we’re still seeing A’s version of the data. Can we somehow save the day?

To see what `d/filter` is doing, we can add a debug `println` to the filtering function, following NamelessMason’s example (I’m translating `tx` values to `t` for easier understanding):

```clojure
(defn as-of'' [db t]
  (let [tx-limit (d/t->tx t)
        tx-allow (d/t->tx (d/basis-t db))]
    (d/filter db (fn [_ [e a v tx :as datom]]
                   (let [result (or (<= tx tx-limit) (> tx tx-allow))]
                     (printf "%s -> %s\n" (pr-str [e a v (d/tx->t tx)]) result)
                     result)))))
```

Re-running the above speculative write snippet now yields:

```clojure
[17592186045418 72 "test" 1003] -> false
[17592186045418 72 "test" 1001] -> true
```

So `d/filter` saw that tx 1003 retracts the `"test"` value for our datom, but it’s rejected because it doesn’t meet the condition `(or (<= tx tx-limit) (> tx tx-allow))`. And at this point, it never even looks at datoms in the speculative transaction 1004, the one that asserted our `"test3"`. It looks like Datomic’s `d/filter` does some optimizations where it skips datoms if it determines they cannot apply based on previous ones.

But even if it _did_ do what we want (i.e., include datoms from tx 1001 and 1004 but not 1003), it would have been impossible. Let’s see what datoms our speculative transaction introduces:

```clojure
(-> (d/db conn)
    (as-of'' 1001)
    (d/with [[:db/add ::the-entity :test/id "test3"]])
    :tx-data
    (->> (mapv (juxt :e :a :v (comp d/tx->t :tx) :added))))
;=> [[13194139534316 50 #inst "2025-04-22T12:48:40.875-00:00" 1004 true]
;=>  [17592186045418 72 "test3" 1004 true]
;=>  [17592186045418 72 "test2" 1004 false]]
```

It adds the value of `"test3"` but retracts `"test2"`! Not `"test"`! It appears that `d/with` looks at the unfiltered database value to produce new datoms for the speculative db value (corroborated by the fact that we don’t get any output from the filtering fn at this point; we only do when we actually query `db'`). Our filter cannot work: transactions 1001 plus 1004 would be “add `"test"`, retract `"test2"`, add `"test3"`”, which is not internally consistent.

So, no, really, you can’t branch Datomic from the past.

Which brings us back to square one: what can we do? What is our usecase for branching the past, anyway?

Dunno about you, but to me the allure is integration testing. Rather than having to maintain an elaborate set of fixtures, with artificial entity names peppered with the word “example”, I want to test on data that’s _close_ to production; that _feels_ like production. Ideally, it _is_ production data, isolated and made invincible by forking. At the same time, tests have to behave predictably: I don’t want a test to fail just because someone deleted yesterday an entity from production that the test depends on. Being able to fork the past would have been a wonderful solution if it worked, but… it’s what it is.

So now I’m experimenting with a different approach. My observation here is that my app’s Datomic database is (and I’d wager a guess that most real-world DBs are as well) “mostly hierarchical”. That is, while its graph of entities might be a giant strongly-connected blob, it can be subdivided into many small subgraphs by judiciously removing edges.

This makes sense for testing. A test typically focuses on a handful of “top-level entities” that I need to be present in my testing database like they are in production, along with all their dependencies – sub-entities that they point to. Say, if I were developing a UI for the [MusicBrainz database][6] and testing the release page, I’d need a release entity, along with its tracks, label, medium, artist, country etc to be present in my testing DB. But just one release is enough; I don’t need all 10K of them.

 [6]: https://github.com/Datomic/mbrainz-sample

My workflow is thus:

- create an empty in-memory DB
- feed it with the same schema that production has
- get hold of a production db with a fixed as-of
- given a “seed entity”, perform a graph traversal (via EAVT and VAET indexes) starting from that entity to determine reachable entities, judiciously blacklisting attributes (and whitelisting “backward-pointing” ones) to avoid importing too much
- copy those entities to my fresh DB
- run the test!

This can be done generically. I’ve written [some proof-of-concept code][7] that wraps a Datomic db to implement the [Loom][8] graph protocol, so that one can use Loom’s graph algorithms to perform a breadth-first entity scan, and a function to walk over those entities and convert them to a transaction applicable on top of a pristine DB. So far I’ve been able to extract meaningful small sub-dbs (on the order of ~10K datoms) from my huge production DB of 17+ billion datoms.

This is a gist for now, but let me know if there’s interest and I can convert it into a proper library.

 [7]: https://gist.github.com/nathell/d3b6f9509a00857cd1843e366797f884
 [8]: https://github.com/aysylu/loom
