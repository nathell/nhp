(ns nhp.czytatki
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [nhp.layout :as layout]))

(defn propagate
  "Given a key `k` and a seq of maps `s`, goes through `s` and if an
  element doesn't contain `k`, assocs it to the last-seen value.

  (propagate :b [{:a 1 :b 2} {:a 2} {:a 3} {:a 4 :b 5}])
  ;=> [{:a 1 :b 2} {:a 2 :b 2} {:a 3 :b 2} {:a 4 :b 5}]"
  [k [fst & rst :as s]]
  (loop [acc [fst]
         to-go rst
         seen? (contains? fst k)
         last-val (get fst k)]
    (if-not (seq to-go)
      acc
      (let [[fst & rst] to-go
            has? (contains? fst k)]
        (recur (conj acc (if (or has? (not seen?))
                           fst
                           (assoc fst k last-val)))
               rst
               (or seen? (contains? fst k))
               (if has? (get fst k) last-val))))))

(defn parse-header [n s]
  (let [marker (str (apply str (repeat n \*)) \space)]
    (when (string/starts-with? s marker)
      (subs s (count marker)))))

(defn parse-line [s]
  (or
   (when-let [h (parse-header 1 s)]
     {:type :h1, :year h})
   (when-let [h (parse-header 2 s)]
     {:type :h2, :book h, :filename (first (string/split h #"\."))})
   {:type :text, :text s}))

(defn read-all []
  (with-open [f (io/reader (io/resource "czytatki/czytatki.org"))]
    (->> (line-seq f)
         (drop 2)
         (map parse-line)
         (propagate :year)
         (partition-by :year)
         (map (fn [c]
                (->> c
                     (propagate :book)
                     (filter #(and (:book %)
                                   (contains? #{:h2 :text} (:type %))))
                     (partition-by :book)))))))

(defn text [item]
  (string/join "\n" (map :text item)))

(defn generate-index [czytatki]
  (->>
   (for [year-czytatki czytatki]
     (into [(str "# " (-> year-czytatki first first :year))]
           (for [item year-czytatki
                 :let [content (text item)
                       has-text? (seq (string/trim content))
                       {:keys [book year filename]} (first item)]]
             (if has-text?
               (format "=> %s/%s.gmi %s" year filename book)
               book))))
   flatten
   (string/join "\n")))

(defn emit-index [czytatki]
  (layout/output-page "gemini/czytatki/index.gmi" (generate-index czytatki)))

(defn generate []
  (let [czytatki (read-all)]
    (emit-index czytatki)
    (doseq [year-czytatki czytatki
            item year-czytatki
            :let [content (text item)
                  {:keys [year book filename]} (first item)]
            :when (seq (string/trim content))]
      (layout/output-page (str "gemini/czytatki/" year "/" filename ".gmi")
                          (str "# " book "\n" content)))))
