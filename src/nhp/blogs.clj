(ns nhp.blogs
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [java-time :as t]
    [markdown.core :as markdown]
    [me.raynes.fs :as fs]
    [nhp.atom :as atom]
    [nhp.gemini :as gemini]
    [nhp.layout :as layout]
    [reaver]
    [yaml.core :as yaml]))

(defn instant->local-date
  [inst]
  (-> inst (.atZone (java.time.ZoneId/of "UTC")) .toLocalDate))

(def pl-month-names-gen
  ["stycznia" "lutego" "marca" "kwietnia" "maja" "czerwca" "lipca" "sierpnia" "września" "października" "listopada" "grudnia"])

(def en-month-names
  ["January" "February" "March" "April" "May" "June" "July" "August" "September" "October" "November" "December"])

(def i18n
  {"en" {:blog-title "code • words • emotions"
         :blog-subtitle "Daniel Janus’s blog"
         :home-page "home page"
         :read-more "Continue reading"
         :next-page "Next posts"
         :prev-page "Previous posts"}
   "pl" {:blog-title "kod • słowa • emocje"
         :blog-subtitle "blog Daniela Janusa"
         :home-page "strona główna"
         :read-more "Czytaj dalej"
         :next-page "Następne notki"
         :prev-page "Poprzednie notki"}})

(def lang->domain {"en" "blog.danieljanus.pl"
                   "pl" "plblog.danieljanus.pl"})

(defn date-gen
  [f d]
  (let [[d m y] (-> d t/instant instant->local-date (t/as :day-of-month :month-of-year :year))]
    (f d m y)))

(def date-pl (partial date-gen (fn [d m y] (str d " " (pl-month-names-gen (dec m)) " " y))))
(def date-en (partial date-gen (fn [d m y] (str d " " (en-month-names (dec m)) " " y))))

(let [sierotki ["a" "w" "o" "u" "i" "z" "od" "do" "na" "za" "po" "ze" "za" "we" "to" "co" "nie"]
      re (re-pattern (str "(?i)\\b(?<!<)(" (string/join "|" sierotki) ") "))]
  (defn unsierotkize [s]
    (string/replace s re "$1&nbsp;")))

(defn unsierotkize-paragraph [s state]
  (if (or (:lists state) (:paragraph state))
    [(unsierotkize s) state]
    [s state]))

(defn chop [s n]
  (subs s 0 (- (count s) n)))

(defn read-blog [filename]
  (let [raw (slurp filename)
        [_ front-matter content] (string/split raw #"---\n" 3)]
    {:file filename
     :slug (-> filename io/file .getName (subs 11) (chop 3))
     :front-matter (yaml/parse-string front-matter)
     :content (markdown/md-to-html-string content :reference-links? true :footnotes? true :custom-transformers [unsierotkize-paragraph])}))

(defn read-all-blogs [lang]
  (->> (fs/list-dir (io/resource (str "blog/" lang)))
       (filter #(string/ends-with? (str %) ".md"))
       (map #(assoc (read-blog %) :lang lang))
       (sort-by (comp :date :front-matter))
       reverse))

(defn blog-date
  [{:keys [lang front-matter]}]
  (let [date-fn-map {"en" date-en, "pl" date-pl}
        date-fn (date-fn-map lang)]
    (when date-fn
      (date-fn (:date front-matter)))))

(defn post [{{title :title} :front-matter, content :content, extra :extra :as blog}]
  [:div.blog-post
   [:h2.title (unsierotkize title)]
   [:p.date (blog-date blog)]
   [:div.body content]
   extra])

(def blog-url atom/post-domainless-url)

(defn page-url [i]
  (if (= i 1)
    "/"
    (str "/page/" i "/")))

(defn navigation [prev-url prev-title next-url next-title]
  [:div.blog-navigation
   (when prev-url
     [:a.prev {:href prev-url} (str "← " prev-title)])
   (when next-url
     [:a.next {:href next-url} (str next-title " →")])])

(defn note-navigation [prev next]
  (navigation (when prev (blog-url prev))
              (get-in prev [:front-matter :title])
              (when next (blog-url next))
              (get-in next [:front-matter :title])))

(defn page-navigation [lang page-no page-count]
  (navigation (when (> page-no 1) (page-url (dec page-no)))
              (get-in i18n [lang :prev-page])
              (when (< page-no page-count) (page-url (inc page-no)))
              (get-in i18n [lang :next-page])))

(defn blog-header [lang]
  [:div.blog-header
   [:div.blog-title
    [:p.big
     [:a {:href "/"} (get-in i18n [lang :blog-title])]]
    [:p.small (get-in i18n [lang :blog-subtitle])]]
   [:ul.blog-header-menu
    (when (= lang "en")
      [:li [:a {:href "//plblog.danieljanus.pl"} "blog po polsku"]])
    (when (= lang "pl")
      [:li [:a {:href "//blog.danieljanus.pl"} "English blog"]])
    [:li [:a {:href "/atom.xml"} "RSS"]]
    [:li [:a {:href "//danieljanus.pl"} (get-in i18n [lang :home-page])]]]])

(def min-content-length 200)

(defn trim-content [s]
  (let [[result & remaining] (string/split s #"(?=<[ph])")]
    (loop [result result remaining remaining]
      (cond
        (empty? remaining) result
        (>= (count result) min-content-length) result
        :otherwise (recur (str result (first remaining))
                          (rest remaining))))))

(defn contains-code? [blog]
  (-> blog :content reaver/parse (reaver/select "code") boolean))

(defn blog-page [[prev blog next]]
  (let [code? (contains-code? blog)]
    (layout/page {:title (get-in blog [:front-matter :title]),
                  :extra-head (when code? [[:link {:rel "stylesheet" :type "text/css" :href "/css/ascetic.css"}]])
                  :content [:div.main.blog
                            (blog-header (:lang blog))
                            (post blog)
                            (note-navigation prev next)
                            (when code? [:script {:src "/js/highlight.pack.js"}])
                            (when code? [:script "hljs.initHighlightingOnLoad();"])]})))

(defn emit-single-blog-pages [blogs]
  (doseq [[_ blog _ :as chunk] (partition 3 1 (concat [nil] blogs [nil]))
          :let [url (blog-url blog)
                lang (:lang blog)]]
    (layout/output-page (str (lang->domain lang) url "index.html")
                        (blog-page chunk))))

(defn trim-blog [{:keys [lang] :as blog}]
  (let [content (trim-content (:content blog))
        read-more [:p.body.read-more [:a {:href (blog-url blog)} (get-in i18n [lang :read-more])]]
        extra (when (not= content (:content blog)) read-more)]
    (assoc blog :content content :extra extra)))

(defn blog-multi-page [page-no page-count blogs]
  (layout/page {:title "Daniel Janus – blog"
                :extra-head [[:link {:rel "stylesheet" :type "text/css" :href "/css/ascetic.css"}]
                             [:link {:rel "alternate" :type "application/atom+xml" :href "/atom.xml"}]]
                :content [:div.main.blog
                          (blog-header (:lang (first blogs)))
                          (map #(post (trim-blog %)) blogs)
                          (page-navigation (-> blogs first :lang) page-no page-count)
                          [:script {:src "/js/highlight.pack.js"}]
                          [:script "hljs.initHighlightingOnLoad();"]]}))

(def entries-per-page 10)

(defn emit-multi-blog-pages [blogs]
  (let [lang (-> blogs first :lang)
        pages (partition-all entries-per-page blogs)
        page-count (count pages)]
    (doseq [[i blogs] (map-indexed (fn [i v] [(inc i) v]) pages)]
      (layout/output-page (str (lang->domain lang) (page-url i) "index.html")
                          (blog-multi-page i page-count blogs)))))

(defn gemini-blog-index [blogs]
  (string/join "\n"
               (map (fn [{{:keys [title date]} :front-matter, lang :lang, :as blog}]
                      (format "=> %s/index.gmi %s – %s" (subs (blog-url blog) 1) (subs (pr-str date) 7 17) title))
                    blogs)))

(defn emit-gemini-blog [blogs]
  (doseq [blog blogs
          :let [url (blog-url blog)
                lang (:lang blog)]]
    (layout/output-page (str "gemini/blog/" lang "/" url "index.gmi")
                        (gemini/blog->gemtext blog)))
  (layout/output-page (str "gemini/blog/" (:lang (first blogs)) "/index.gmi")
                      (gemini-blog-index blogs)))

(defn emit-atom-feed [blogs]
  (let [lang (:lang (first blogs))]
    (layout/output-page (str (lang->domain lang) "/atom.xml")
                        (atom/feed-string blogs))))

(defn generate-blog [lang]
  (let [blogs (read-all-blogs lang)]
    (emit-multi-blog-pages blogs)
    (emit-single-blog-pages blogs)
    (emit-atom-feed blogs)
    (emit-gemini-blog blogs)))

(defn generate []
  (doseq [lang ["en" "pl"]]
    (generate-blog lang)))
