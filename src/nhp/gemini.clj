(ns nhp.gemini
  (:require [clojure.string :as string]
            [reaver])
  (:import [org.jsoup Jsoup]
           [org.jsoup.nodes Attributes Comment Document Document$OutputSettings Element TextNode]
           [org.jsoup.parser Tag]
           [org.jsoup.safety Whitelist]))

;; parsed HTML to Gemtext conversion

(defn text-node? [x]
  (instance? TextNode x))

(defn element?
  ([x] (instance? Element x))
  ([x tag] (and (element? x)
                (let [tag-name (-> x .tag str)]
                  (= (name tag) tag-name)))))

(defn ors [x y]
  (if (and x (not (string/blank? x))) x y))

(defn paragraph->text [p & [state]]
  (reduce (fn [{:keys [text links link-count] :as acc} node]
            (cond
              (text-node? node)  (update acc :text str (.text node))
              (element? node :a) (-> acc
                                     (update :text str (.text node) " [" (inc link-count) "]")
                                     (update :links conj (format "=> %s [%s]" (reaver/attr node :href) (inc link-count)))
                                     (update :link-count inc))
              (element? node :code) (update acc :text str "“" (.text node) "”")
              (element? node :i) (update acc :text str (.text node))
              (element? node :b) (update acc :text str (.text node))
              (element? node :strong) (update acc :text str (.text node))
              (element? node :img) (-> acc
                                       (assoc :img (format "=> %s %s" (reaver/attr node :src) (or (reaver/attr node :alt) "Image"))))
              :otherwise (do
                           (prn "unexpected" node)
                           (update acc :text str (.text node)))))
          (merge {:text "" :links [] :link-count 0} state)
          (.childNodes p)))

(defn list->text [type state node]
  (let [items (.childNodes node)
        output (reduce (fn [acc node]
                         (let [res (paragraph->text node (select-keys acc [:link-count]))]
                           (-> acc
                               (update :lines conj (:text res))
                               (update :links into (:links res))
                               (assoc :link-count (:link-count res)))))
                       {:lines [], :links [], :link-count (:link-count state)}
                       items)]
    (assoc output
           :text (case type
                   :ul (string/join "\n" (map #(str "* " %) (:lines output)))
                   :ol (string/join "\n" (map-indexed #(str (inc %1) ". " %2) (:lines output)))))))

(defn item->text [state node]
  (let [st (select-keys state [:link-count])
        res (cond (element? node :p) (paragraph->text node st)
                  (element? node :h2) (-> (paragraph->text node st) (update :text #(str "## " %)))
                  (element? node :pre) {:text (str "```\n" (.text node) "\n```")
                                        :link-count (:link-count state)}
                  (element? node :ul) (list->text :ul st node)
                  (element? node :ol) (list->text :ol st node)
                  :otherwise (do
                               (prn "unexpected" node)
                               (paragraph->text node st)))]
    (-> state
        (update :lines conj (or (:img res) (:text res)))
        (update :links into (:links res))
        (assoc :link-count (:link-count res)))))

(defn document->gemtext [doc]
  (let [items (-> doc (reaver/select "body") first .childNodes)
        output (reduce item->text
                       {:link-count 0, :lines [], :links []}
                       items)]
    (str
     (string/join "\n\n" (:lines output))
     "\n\n## Links\n\n"
     (string/join "\n\n" (:links output)))))

(defn blog->gemtext [{:keys [content]
                      {:keys [title]} :front-matter}]
  (str "# " title "\n\n"
       (document->gemtext (reaver/parse content))))
