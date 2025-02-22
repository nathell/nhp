(ns nhp.atom
  (:require
    [clojure.data.xml :as xml]
    [hiccup.core :as hiccup]
    [java-time :as t]))

(xml/alias-uri 'atom "http://www.w3.org/2005/Atom")
(xml/alias-uri 'xhtml "http://www.w3.org/1999/xhtml")

(def lang->domain
  {"pl" "plblog.danieljanus.pl"
   "en" "blog.danieljanus.pl"})

(def lang->title
  {"en" "code · words · emotions: Daniel Janus’s blog"
   "pl" "kod · słowa · emocje: blog Daniela Janusa"})

(defn feed-id [lang]
  (str "tag:" (lang->domain lang) ",2019:feed"))

(defn post-id [{:keys [slug lang front-matter]}]
  (str "tag:" (lang->domain lang) ","
       (t/format "YYYY-MM-dd" (t/zoned-date-time (:date front-matter) "UTC"))
       ":post:" slug))

(defn updated [posts]
  (->> posts
       (map (comp :date :front-matter))
       (apply max-key #(.getTime %))
       t/instant
       str))

(defn post-domainless-url [{{date :date} :front-matter, slug :slug}]
  (format "/%s/%s/"
          (t/format "yyyy/MM/dd" (t/local-date-time date "UTC"))
          slug))

(defn post-url [{:keys [lang] :as post}]
  (str "http://" (lang->domain lang) (post-domainless-url post)))

(defn content->xhtml [content]
  [{:tag ::xhtml/p, :content ["content"]}])

(defn post->entry [{content :content, lang :lang, {:keys [title subtitle date]} :front-matter, :as post}]
  {:tag ::atom/entry
   :content [{:tag ::atom/id, :content [(post-id post)]}
             {:tag ::atom/title, :content [(cond-> title subtitle (str " " subtitle))]}
             {:tag ::atom/link, :attrs {:href (post-url post)}}
             {:tag ::atom/updated, :content [(str (t/instant date))]}
             {:tag ::atom/content, :attrs {:type "html"}, :content (hiccup/html content)}]})

(defn feed [posts]
  (let [lang (:lang (first posts))]
    {:tag ::atom/feed
     :content (into
               [{:tag ::atom/id, :content [(feed-id lang)]}
                {:tag ::atom/title, :content [(lang->title lang)]}
                {:tag ::atom/link, :attrs {:href (str "http://" (lang->domain lang))}}
                {:tag ::atom/updated, :content [(updated posts)]}
                {:tag ::atom/author, :content [{:tag ::atom/name, :content "Daniel Janus"}
                                               {:tag ::atom/uri, :content "http://danieljanus.pl"}
                                               {:tag ::atom/email, :content "dj@danieljanus.pl"}]}]
               (map post->entry posts))}))

(defn feed-string [posts]
  (xml/indent-str (feed posts)))
