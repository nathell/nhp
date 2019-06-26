(ns nhp.layout
  (:require
    [clojure.java.io :as io]
    [hiccup.page :refer [html5]]))

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

(defn link [url text]
  (if url
    [:a {:href url} text]
    text))

(defn back []
  [:p
   (link "/" "← Back to home page")])

(defn output-page [filename page]
  (let [filename (str "out/" filename)]
    (io/make-parents filename)
    (spit filename page)))
