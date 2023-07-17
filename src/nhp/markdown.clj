(ns nhp.markdown
  (:require
    [nextjournal.markdown :as md]
    [nextjournal.markdown.parser :as md.parser]
    [nextjournal.markdown.transform :as md.transform]
    [nhp.highlight :as highlight]))

(def hiccup-renderers
  (merge md.transform/default-hiccup-renderers
         {:plain (partial md.transform/into-markup [:span])
          :code (fn [ctx {:keys [text content language] :as node}]
                  (if (and language (seq language))
                    [:pre
                     (into [:code {:class (str "hljs " language)}]
                           (keep (partial md.transform/->hiccup (assoc ctx ::md.transform/parent node)))
                           [{:type :text
                             :text (-> content first :text (highlight/highlight language))}])]
                    (md.transform/into-markup [:pre.viewer-code.not-prose] ctx node)))}))

(defn ->hiccup [content]
  (md.transform/->hiccup hiccup-renderers content))

(def parse md/parse)

;; https://github.com/nextjournal/markdown/issues/7
(defmethod md.parser/apply-token "html_block" [doc {inlined-html :content}]
  (md.parser/push-node doc {:type :text :text inlined-html}))

(defn trim-content [min-content-length {:keys [content] :as node}]
  (let [counts (map (comp count md.transform/->text) content)
        cumulative-counts (reductions + counts)
        num-paragraphs (inc (count (take-while #(< % min-content-length) cumulative-counts)))]
    (update node :content (partial take num-paragraphs))))
