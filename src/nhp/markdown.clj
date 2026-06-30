(ns nhp.markdown
  (:require
    [hiccup2.core :as hiccup]
    [nextjournal.markdown :as md]
    [nhp.highlight :as highlight]))

(defn render-image [{:as ctx ::keys [parent]} {:as node :keys [attrs]}]
  (let [{:keys [title]} attrs]
    (if (= :paragraph (:type parent))
      [:img.inline attrs]
      [:figure.image
       [:img (cond-> attrs title (assoc :alt title))]
       (md/into-hiccup [:figcaption] ctx node)])))

(defn render-footnote [ctx {:as node :keys [ref label]}]
  (let [[tag par & content] (md/into-hiccup [:div.footnote] ctx node)
        [par-tag & par-content] par]
    (into [tag
           (into [par-tag [:sup.footnote-ref {:data-ref ref} (inc ref)] " "]
                 par-content)]
          content)))

(def hiccup-renderers
  (merge md/default-hiccup-renderers
         {:image       render-image
          :html-inline (comp hiccup/raw md/node->text)
          :html-block  (comp hiccup/raw md/node->text)
          :plain       (partial md/into-hiccup [:span])
          :footnote    render-footnote
          :code        (fn [ctx {:keys [text content language] :as node}]
                         (if (and language (seq language))
                           [:pre
                            (into [:code {:class (str "hljs " language)}]
                                  (keep (partial md/->hiccup (assoc ctx :nextjournal.markdown.transform/parent node)))
                                  [{:type :text
                                    :text (-> content first :text (highlight/highlight language))}])]
                           [:pre
                            (md/into-hiccup [:code] ctx node)]))}))

(defn ->hiccup [{:keys [footnotes] :as content}]
  (cond->
      (md/->hiccup hiccup-renderers content)
    (seq footnotes) (into [[:hr]])
    (seq footnotes)
    (into
     (map (partial md/->hiccup hiccup-renderers) footnotes))))

(def parse md/parse)

(defn trim-content [min-content-length {:keys [content] :as node}]
  (let [counts (map (comp count md/node->text) content)
        cumulative-counts (reductions + counts)
        num-paragraphs (inc (count (take-while #(< % min-content-length) cumulative-counts)))]
    (-> node
        (update :content (partial take num-paragraphs))
        (assoc :footnotes []))))
