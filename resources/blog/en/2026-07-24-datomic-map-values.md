---
date: 2026-07-24
title: "Things I wish Datomic had: Map values"
categories:
  - programming
  - Clojure
  - Datomic
  - Datahike
---

## Whence this post

[JEP 401][1] – a proposal to add value classes to Java – has been crystallizing for almost six years now, but I only learned about it this morning. I skimmed it, nodding along to myself as I thought “gee, I can’t imagine going back to Java” and “wonder how this might make Clojure more performant?”. But then I thought about some Datomic (actually, Datahike) data remodelling that I’d been working on recently, and I realized that the two areas are connected.

So here’s a braindump, in an effort to clear up my mental image of all this.

 [1]: https://openjdk.org/jeps/401

## A simple example

Consider this Clojure value:

```clojure
(def prog
  {:program/name    "Apache Maven"
   :program/url     "https://maven.apache.org"
   :program/version {:version/major 3
                     :version/minor 9
                     :version/patch 16}})
```

If you’re like me, you’ll have a warm, comfortable feeling in your heart looking at this. Plain data at rest. Accessible, transformable. What’s not to love?

Well, now try storing it in Datomic. Easy! Let’s first define a schema:

```clojure
(def schema
  [{:db/ident :program/name
    :db/valueType :db.type/string
    :db/unique :db.unique/identity
    :db/cardinality :db.cardinality/one}
   {:db/ident :program/url
    :db/valueType :db.type/uri
    :db/cardinality :db.cardinality/one}
   {:db/ident :version/major
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident :version/minor
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident :version/patch
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident :program/version
    :db/valueType :db.type/ref
    :db/isComponent true
    :db/cardinality :db.cardinality/one}])
```

And now we can transact it (I’ll assume we have a DB connection, `conn`):

```clojure
@(d/transact conn schema)
@(d/transact conn [prog])
```

Done. Now we can check what Maven’s version is:

```clojure
(let [db (d/db conn)
      mvn (d/entity db [:program/name "Apache Maven"])]
  (:program/version (d/touch mvn)))
;=> {:db/id 17592186045419, :version/major 3, :version/minor 9, :version/patch 16}
```

It works!

## Meh

But I’m not exactly happy about this.

What I’d really like to get is `#:version{:major 3, :minor 9, :patch 16}`. But Datomic models maps as entities – focal points that bind attributes with values. That’s fine for the top-level `program` map, but `version` is not an entity! It’s just a value, like a number or a string. It has internal structure, but, conceptually, it’s just an atomic value, an element of the set of all possible `major.minor.patch` version numbers.

Yet Datomic forces us to “reify” the version map as an entity. That means that it automatically gets a `:db/id`. If a new version of Maven gets released, and we transact that fact:

```clojure
@(d/transact conn
             [[[:program/name "Apache Maven"]
               :program/version
               #:version{:major 3, :minor 9, :patch 17}]])
```

then Datomic will create a fresh artificial entity, give it the three version attributes, and associate it with the Maven entity. But the old one still exists in the DB! It’s “semi-orphaned” (detached from the rest of the object graph in the current state, but still reachable via history). If Maven for some reason goes back to 3.9.16 via a similar transaction, then we’ll get a _third_ entity that is a _duplicate_ of the first one.

Even worse, there’s nothing stopping us from changing attributes of that entity:

```clojure
(let [db (d/db conn)
      mvn (d/entity db [:program/name "Apache Maven"])]
  @(d/transact conn [[(-> mvn :program/version :db/id) :version/major 4]]))
```

Now the identity of version hasn’t changed, but Maven is at 4.9.17, and every other entity that happened to be referencing the artificial version one is at 4.9.17 too! Clearly, this kind of thing should be disallowed.

Also note that I had to say `:db/isComponent true` in the schema for the transaction to have succeeded at all. `isComponent` means that the child entity only makes sense in the context of parent; otherwise, I’d have to lift the version map to the top-level of the transaction, give it a temporary id, and use that id to refer to it in the program map.

## Two kinds of maps

At this point, I realize there are two kinds of maps: those that describe snapshots of state of some stateful entity at some point in time (like `program`), and those that are merely juxtapositions of named values (like `version`). For want of better names, for now on I’ll call these “snapshot maps” and “plain maps”, respectively.

Quoting from [Clojure’s Approach to Identity and State][2]:

 [2]: https://clojure.org/about/state

> We need to move away from a notion of state as "the content of this memory block" to one of "the value currently associated with this identity". Thus an identity can be in different states at different times, but _the state itself doesn’t change_. That is, an identity is not a state, an identity _**has**_ a state. Exactly one state at any point in time. And that state is a true value, i.e. it never changes.

With this in mind, we could reformulate the distinction as follows: “snapshot maps” are the state of _some_ identity, while “plain maps” are not.

In Clojure, the two kinds of maps look and work exactly the same, but as we saw, they are conceptually very different. It is typically obvious which kind a given map belongs to; e.g. snapshot maps typically have a `:id` field that holds an integer or a UUID. But sometimes the exact same map can be viewed as either (is `{:x 1 :y 2}` just a 2D point – a plain map – or a snapshot of a point moving around – a snapshot map?) It’s not just maps either; we could make the same distinction for vectors, or indeed scalar values, but maps are where it’s most commonly encountered.

I find the distinction philosophically interesting. For example, could we envision a variant of Clojure that makes it explicit? Or discriminate between them based on the value’s metadata? Should `deref` automatically set that metadata? Should operations on the map preserve that metadata?

Besides semantic version numbers, here are some more examples of plain maps that appear frequently:

- Temporal values. These are typically disguised as instances of `java.util.Date` or `java.time.*` classes, but are conceptually immutable collections of fields. For example, `java.time.Instant` values look like `{:epochSecond long, :nano int}`.
- Prices, consisting of an amount and a currency. Back when [Fy!][3] used Datomic, we modelled prices as “artificial entities” as described in this post, leading to a massive proliferation of duplicate/orphaned price entities accruing in everyday operation.

 [3]: https://www.iamfy.co/

## What else could we do?

Going back to Datomic: if we want to avoid artificial entities, what alternatives do we have?

- Store them as strings instead, like `"3.9.16"`. Strings are plain values and they map cleanly onto how we typically denote versions. This may be the right solution if our app also presents versions this way and doesn’t do any fancy processing of it. A potential problem is that strings don’t sort correctly as versions: `"11.0.2"` is lexicographically smaller than `"2.2.1"`, so the values will appear in an incoherent order in the AVET index. If we want to extract programs with versions in a certain range, the index can’t help us – we’ll need to do a full scan of the DB, then parse and sort manually.

- Store them as [tuples][4] instead, like `[3 9 16]`. Datomic supports short vectors as values that don’t have an identity. In our case, we can represent versions numbers as 3-tuples of longs (either homo- or heterogeneous ones will do), `[major minor patch]`. This gives us the correct sorting. The downside is that the rest of our app probably doesn’t think about versions this way, so we need to translate
the “domain” values that we use elsewhere to tuples before transacting, and translate it back in the data access layer.<br><br>This leads to increased verbosity (one of the nice things about Datomic is that, most of the time, it lets you get away without having a data access layer at all, as you can use the entities
returned by `d/entity` directly in your domain logic code). Still, I think it’s _the_ right thing to do.

 [4]: https://docs.datomic.com/schema/schema-reference.html#tuples

- Flatten the data: don’t have the `:program/version` attribute at all, and instead associate the `:version/*` attributes directly with the program entity. This may make sense in some cases, but loses grouping, which can make it harder to programmatically process such data. Also, what if the program could have many versions?

- Use Datahike instead of Datomic, and use its [unstructured input][5] feature in the “content identity” mode. This still creates artificial entities, but avoids duplication because it automatically infers ids from the map content, so two maps with the same content will refer to the same entity. However, it still has the “accidental mutability” problem with the artificial entity. You have to either opt in to content identity for all sub-maps of the map being transacted, or opt out of it en masse. Plus, it just feels hacky.

 [5]: https://github.com/replikativ/datahike/blob/main/doc/unstructured.md

## In an ideal world…

…I’d like to be able to just define new types in Datomic. Just like there’s built-in supports for `java.util.Date`s (as `:db.type/inst`) or `java.net.URI`s (as `:db.type/uri`), I’d like to somehow tell Datomic to “support instances of  `java.time.LocalDate` as `:db.type/date`”. Or, “let `:db.type/semver` be a map mapping `:version/major`, `:version/minor`, and `:version/patch` to longs”.

How such an API could look like is open to discussion: I don’t have concrete ideas here. Whatever the design, though, I think it’d be a win.
