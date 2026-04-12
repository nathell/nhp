(ns nhp.slug
  (:require [clojure.string :as str])
  (:import (java.text Normalizer Normalizer$Form)))

(def custom-slugs
  {"C++" "cpp"})

(defn- normalize [string-to-normalize]
  (let [normalized (Normalizer/normalize string-to-normalize Normalizer$Form/NFD)
        ascii (str/replace normalized #"[\P{ASCII}]+" "")]
    (str/lower-case ascii)))

(defn slugify [s]
  (or (custom-slugs s)
      (let [normalized (normalize s)
            split-s (str/split (str/triml normalized) #"[\p{Space}\p{P}]+")]
        (str/join "-" split-s))))
