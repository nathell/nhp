(ns nhp.markdown
  (:require
    [nextjournal.markdown :as md]
    [nextjournal.markdown.parser :as md.parser]
    [nextjournal.markdown.transform :as md.transform]
    [nhp.highlight :as highlight]))

(defn render-footnote [ctx {:as node :keys [ref label]}]
  (let [[tag par & content] (md.transform/into-markup [:div.footnote] ctx node)
        [par-tag & par-content] par]
    (into [tag
           (into [par-tag [:sup.footnote-ref {:data-ref ref} (inc ref)] " "]
                 par-content)]
          content)))

(def hiccup-renderers
  (merge md.transform/default-hiccup-renderers
         {:plain    (partial md.transform/into-markup [:span])
          :footnote render-footnote
          :code     (fn [ctx {:keys [text content language] :as node}]
                      (if (and language (seq language))
                        [:pre
                         (into [:code {:class (str "hljs " language)}]
                               (keep (partial md.transform/->hiccup (assoc ctx ::md.transform/parent node)))
                               [{:type :text
                                 :text (-> content first :text (highlight/highlight language))}])]
                        (md.transform/into-markup [:pre.viewer-code.not-prose] ctx node)))}))

(defn ->hiccup [{:keys [footnotes] :as content}]
  (cond->
      (md.transform/->hiccup hiccup-renderers content)
    (seq footnotes) (into [[:hr]])
    (seq footnotes)
    (into
     (map (partial md.transform/->hiccup hiccup-renderers) footnotes))))

(def parse md/parse)

;; https://github.com/nextjournal/markdown/issues/7
(defmethod md.parser/apply-token "html_block" [doc {inlined-html :content}]
  (md.parser/push-node doc {:type :text :text inlined-html}))

(defn trim-content [min-content-length {:keys [content] :as node}]
  (let [counts (map (comp count md.transform/->text) content)
        cumulative-counts (reductions + counts)
        num-paragraphs (inc (count (take-while #(< % min-content-length) cumulative-counts)))]
    (-> node
        (update :content (partial take num-paragraphs))
        (assoc :footnotes []))))
