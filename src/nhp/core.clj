(ns nhp.core
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [hiccup.page :refer [html5]]
    [java-time :as t]
    [markdown.core :as markdown]
    [me.raynes.fs :as fs]
    [yaml.core :as yaml]))

(defn page [{:keys [title content extra-head]}]
  (html5
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0"}]
    [:link {:rel "stylesheet" :type "text/css" :href "/css/nhp.css"}]
    [:title (or title "Daniel Janus")]
    extra-head]
   [:body
    content]))

(defn home-content []
  [:div.main.home
   [:h1.header "Daniel Janus"]
   [:div.photo
    [:img {:src "img/dj.jpg"}]]
   [:div.content
    [:section.programming
     [:h2 "Programming"]
     [:ul
      [:li [:a {:href "https://www.works-hub.com"} "WorksHub"]
       ", where I work"]
      [:li [:a {:href "http://smyrna.danieljanus.pl"} "Smyrna"]
       ", a concordancer for Polish"]
      [:li [:a {:href "wladcyslow"} "Word Champions"]
       ", a word game (Polish only)"]
      [:li [:a {:href "https://github.com/nathell"} "GitHub projects"]]]]
    [:section.writing
     [:h2 "Writing"]
     [:ul
      [:li "Blogs: "
       [:a {:href "http://blog.danieljanus.pl"} "English"]
       ", "
       [:a {:href "http://plblog.danieljanus.pl"} "Polish"]]
      [:li
       [:a {:href "translations"} "Poetry translations"]
       " (English → Polish)"]]]
    [:section.speaking
     [:h2 "Speaking"]
     [:ul
      [:li [:a {:href "talks.html"} "List of my talks"]]]]
    [:section.contact
     [:h2 "Contact"]
     [:ul
      [:li "Email me: " [:a {:href "mailto:dj@danieljanus.pl"} "dj@danieljanus.pl"]]
      [:li [:a {:href "https://twitter.com/nathell"} "Twitter"]
       ", "
       [:a {:href "https://www.linkedin.com/in/nathell/"} "LinkedIn"]]]]]])

(defn home-page []
  (page {:content (home-content)}))

(defn link [url text]
  (if url
    [:a {:href url} text]
    text))

(defn table [headers & data]
  (into []
        (map (partial zipmap headers))
        (partition (count headers) data)))

(def upcoming-talks
  [{:title "NVC: protokół komunikacji człowiek–człowiek"
    :event "WarsawJS"
    :event-url "https://warsawjs.com"
    :city "Warsaw"
    :date "2019-05-08"}])

(def past-talks
  [{:title "Skyscraper: Restructuring the web"
    :url "talks/reveal.js/2016-clojurex.html"
    :event "Clojure eXchange"
    :event-url "https://skillsmatter.com/conferences/7430-clojure-exchange-2016"
    :city "London"
    :date "2016-12-02"}
   {:title "A case study of natural language processing in Clojure"
    :url "talks/reveal.js/2016-euroclojure.html"
    :event "EuroClojure"
    :event-url "http://2016.euroclojure.org/"
    :city "Bratislava"
    :date "2016-10-26"}
   {:title "React in the Land of Parentheses: ClojureScript, Reagent, re-frame"
    :url "talks/2016-warsawjs"
    :event "WarsawJS"
    :event-url "https://warsawjs.com"
    :city "Warsaw"
    :date "2016-08-10"}
   {:title "Od nieustrukturyzowanych danych do przeszukiwalnego korpusu bogatego w metadane: Skyscraper, P4, Smyrna"
    :url "talks/reveal.js/2016-ipi.html"
    :event "ICS PAS Seminar"
    :event-url "http://zil.ipipan.waw.pl/seminarium"
    :city "Warsaw"
    :date "2016-05-09"}
   {:title "Kontynuacje w Rubym: ciąg dalszy nastąpił"
    :url "talks/reveal.js/2016-continuations.html"
    :event "4Developers"
    :event-url "http://2016.4developers.org.pl/en/"
    :city "Warsaw"
    :date "2016-04-11"}
   {:title "Wprowadzenie do Clojure dla Rubiowców"
    :url "talks/reveal.js/2015-wrug.html"
    :event "WRUG"
    :event-url "http://wrug.eu/"
    :city "Warsaw"
    :date "2015-02-02"}
   {:title "Smyrna: An easy Polish concordancer in Clojure"
    :url "talks/reveal.js/2014-lambdadays.html"
    :event "Lambda Days"
    :event-url "http://www.lambdadays.org/lambdadays2014"
    :city "Kraków"
    :date "2014-02-28"}
   {:title "Lithium: a small Clojure-inspired Lisp for the bare metal"
    :url "talks/reveal.js/2013-euroclojure.html"
    :event "EuroClojure"
    :event-url "http://euroclojure.org/"
    :city "Berlin"
    :date "2013-10-15"}
   {:title "Nawiasem mówiąc: Clojure"
    :url "talks/2011-java4people"
    :event "Java4People"
    :event-url "http://devcrowd.pl/"
    :city "Szczecin"
    :date "2011-04-16"}
   {:title "Of Caves, Programming, and Poetry: Domain-specific languages in interactive fiction"
    :url "talks/pdf/2008-lshift-if.pdf"
    :event "LShift"
    :city "London"
    :date "2008-08-20"}
   {:title "Obsługa błędów, czyli jak sobie radzić z prawem Murphy’ego"
    :url "talks/pdf/2008-aula-errorhandling.pdf"
    :event "TechAula"
    :event-url "https://aulapolska.pl/"
    :city "Warsaw"
    :date "2008-06-26"}])

(defn talk [{:keys [title event url event-url city date]}]
  [:div.talk
   [:div.title (link url title)]
   [:div.event-line
    [:span.event (link event-url event)]
    " • "
    [:span.city city]
    ", "
    [:span.date date]]])

(defn talks-list
  [talks]
  [:div.timeline
   (for [[year group] (group-by #(subs (:date %) 0 4) talks)]
     [:div.timeline-item
      [:div.year year]
      [:div.items
       (map talk group)]])])

(defn back []
  [:p
   (link "/" "← Back to home page")])

(defn talks-content []
  [:div.main.content.talks
   [:h1 "Daniel Janus’s Talks"]
   [:section.upcoming
    [:h2 "Upcoming"]
    (talks-list upcoming-talks)]
   [:section.past
    [:h2 "Past"]
    (talks-list past-talks)]
   (back)])

(defn talks-page []
  (page {:title "DJ’s Talks"
         :content (talks-content)}))

(def poems
  [{:author {:first-name "Sondra", :last-name "Ball"}
    :original-title "The Villanelle"
    :path "sondra-ball/the-villanelle"
    :versions [{:file "01-vilanella.txt"
                :translated-by "Daniel Janus"}]}
   {:author {:first-name "Melissa", :last-name "Ginsburg"}
    :original-title "Tigers"
    :path "melissa-ginsburg/tigers"
    :versions [{:file "01-tygrysy.txt"
                :translated-by "Daniel Janus"}]}
   {:author {:first-name "Roger", :last-name "Waters"}
    :original-title "The Gunner’s Dream"
    :path "roger-waters/the-gunners-dream"
    :versions [{:file "01-sen-kanoniera.txt"
                :translated-by "Daniel Janus"}]}
   {:author {:first-name "Edwin Arlington", :last-name "Robinson"}
    :original-title "How Annandale Went Out"
    :path "edwin-arlington-robinson/how-annandale-went-out"
    :versions [{:file "01-jak-zgasl-annandale.txt"
                :translated-by "Daniel Janus"}]}
   {:author {:first-name "Edwin Arlington", :last-name "Robinson"}
    :original-title "Luke Havergal"
    :path "edwin-arlington-robinson/luke-havergal"
    :versions [{:file "00-luke-havergal.txt"
                :original true}
               {:file "01-lukasz-havergal.txt"
                :translated-by "Ludmiła Marjańska"
                :translator-female true}
               {:file "02-luke-havergal.txt"
                :translated-by "Daniel Janus"}
               {:file "03-asz-havergal.txt"
                :translated-by "baldrick"}]}
   {:author {:first-name "Les", :last-name "Murray"}
    :original-title "An Absolutely Ordinary Rainbow"
    :path "les-murray/an-absolutely-ordinary-rainbow"
    :versions [{:file "01-najzwyczajniejsza-tecza.txt"
                :translated-by "Daniel Janus"}]}
   {:author {:first-name "Ogden", :last-name "Nash"}
    :original-title "Possessions are Nine Points of Conversation"
    :path "ogden-nash/possessions-9points"
    :versions [{:file "01-masz-masz.txt"
                :translated-by "Daniel Janus"}]}
   ])

(defn read-poem [{:keys [path file] :as info}]
  (with-open [f (-> (str "poetry/" path "/" file) io/resource io/reader)]
    (let [[title _ & lines] (line-seq f)]
      (merge info
             {:title title
              :lines (vec lines)}))))

(defn render-line [line]
  (let [line (string/replace line #"_(.*)_" "<i>$1</i>")]
    (cond
      (re-matches #">.*<" line) [:p.line.center (subs line 1 (- (count line) 1))]
      (string/starts-with? line "> ") [:p.line.right (subs line 2)]
      :otherwise [:p.line line])))

(defn render-poem [{:keys [author title lines]}]
  [:div.main.content.poem
   [:h2.author (:first-name author) " " (:last-name author)]
   [:h2.title title]
   (map render-line lines)
   [:p (link "/translations/" "← Do spisu tłumaczeń")]])

(defn poem-versions []
  (for [poem poems version (:versions poem)]
    (merge (dissoc poem :versions) version)))

(defn poem-page [{:keys [first-name last-name title] :as poem}]
  (page {:title (format "DJ’s translations: %s %s – %s" first-name last-name title)
         :content (render-poem poem)}))

(defn poems-list []
  [:div.main.content.poems-list
   [:h2 "Tłumaczenia"]
   [:p "Czasami tłumaczę poezję. Na tej stronie zbieram przekłady."]
   (for [[{:keys [last-name first-name]} poems] (->> poems (group-by :author) (sort-by (fn [[k v]] [(:last-name k) (:first-name k)])))]
     [:div
      [:p.author first-name " " last-name]
      [:ul
       (for [poem poems
             :let [mine (merge poem (first (filter #(= (:translated-by %) "Daniel Janus") (:versions poem))))
                   canonical (first (filter :original (:versions poem)))
                   title (:title (read-poem mine))]]
         [:li
          [:a.poem-link.poem-title {:href (str "/translations/" (:path mine) "/" (string/replace (:file mine) ".txt" ".html"))} title]
          " ("
          [:span.original-title (:original-title poem)] ")"])]])
   [:p (link "/" "← Do strony głównej")]])

(defn poems-page []
  (page {:title "DJ’s translations"
         :content (poems-list)}))

(defn output-page [filename page]
  (let [filename (str "out/" filename)]
    (io/make-parents filename)
    (spit filename page)))

(defn generate-poems []
  (output-page "translations/index.html" (poems-page))
  (doseq [{:keys [path file] :as version} (poem-versions)]
    (let [output-file (str "translations/" path "/" (string/replace file ".txt" ".html"))]
      (output-page output-file (poem-page (read-poem version))))))

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

(def states (atom #{}))

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
     :content (markdown/md-to-html-string content :reference-links? true :custom-transformers [unsierotkize-paragraph])}))

(defn read-all-blogs [lang]
  (->> (fs/list-dir (io/resource (str "blog/" lang)))
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

(defn blog-url [{{date :date} :front-matter, slug :slug}]
  (format "/%s/%s/"
          (t/format "yyyy/MM/dd" (t/local-date-time date "UTC"))
          slug))

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

(defn blog-page [[prev blog next]]
  (page {:title (get-in blog [:front-matter :title]),
         :extra-head [:link {:rel "stylesheet" :type "text/css" :href "/css/ascetic.css"}]
         :content [:div.main.blog
                   (blog-header (:lang blog))
                   (post blog)
                   (note-navigation prev next)
                   [:script {:src "/js/highlight.pack.js"}]
                   [:script "hljs.initHighlightingOnLoad();"]]}))

(defn emit-single-blog-pages [blogs]
  (doseq [[_ blog _ :as chunk] (partition 3 1 (concat [nil] blogs [nil]))
          :let [url (blog-url blog)
                lang (:lang blog)]]
    (output-page (str "blog/" lang url "index.html")
                 (blog-page chunk))))

(defn trim-blog [{:keys [lang] :as blog}]
  (let [content (trim-content (:content blog))
        read-more [:p.body.read-more [:a {:href (blog-url blog)} (get-in i18n [lang :read-more])]]
        extra (when (not= content (:content blog)) read-more)]
    (assoc blog :content content :extra extra)))

(defn blog-multi-page [page-no page-count blogs]
  (page {:title "Daniel Janus – blog"
         :extra-head [:link {:rel "stylesheet" :type "text/css" :href "/css/ascetic.css"}]
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
      (output-page (str "blog/" lang (page-url i) "index.html")
                   (blog-multi-page i page-count blogs)))))

(defn generate-blog [lang]
  (let [blogs (read-all-blogs lang)]
    (emit-multi-blog-pages blogs)
    (emit-single-blog-pages blogs)))

(defn copy-assets []
  (doseq [dir ["css" "fonts" "img" "js"]]
    (fs/copy-dir (str "resources/" dir) "out")))

(defn build []
  (copy-assets)
  (output-page "index.html" (home-page))
  (output-page "talks.html" (talks-page))
  (generate-poems))

(build)
