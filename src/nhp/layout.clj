(ns nhp.layout
  (:require
    [clojure.java.io :as io]
    [hiccup.page :refer [html5]]))

(defn page [{:keys [title content extra-head]}]
  (html5
   (into
    [:head
     [:meta {:charset "UTF-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1.0"}]
     [:link {:rel "stylesheet" :type "text/css" :href "/css/nhp.css"}]
     [:link {:rel "icon" :type "image/svg+xml" :href "data:image/svg+xml,<svg%20xmlns='http://www.w3.org/2000/svg'%20viewBox='0%200%2016%2016'><text%20x='0'%20y='14'>DJ</text></svg>"}]
     [:title (or title "Daniel Janus")]]
    extra-head)
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
