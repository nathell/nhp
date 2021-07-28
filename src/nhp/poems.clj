(ns nhp.poems
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [nhp.layout :as layout]))

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
                :translated-by "Daniel Janus"}]}])

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
   [:p (layout/link "/translations/" "← Do spisu tłumaczeń")]])

(defn poem-versions []
  (for [poem poems version (:versions poem)]
    (merge (dissoc poem :versions) version)))

(defn poem-page [{:keys [first-name last-name title] :as poem}]
  (layout/page {:title (format "DJ’s translations: %s %s – %s" first-name last-name title)
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
   [:p (layout/link "/" "← Do strony głównej")]])

(defn poems-page []
  (layout/page {:title "DJ’s translations"
                :content (poems-list)}))

(defn generate []
  (layout/output-page "danieljanus.pl/translations/index.html" (poems-page))
  (doseq [{:keys [path file] :as version} (poem-versions)]
    (let [output-file (str "danieljanus.pl/translations/" path "/" (string/replace file ".txt" ".html"))]
      (layout/output-page output-file (poem-page (read-poem version))))))
