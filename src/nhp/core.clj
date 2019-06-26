(ns nhp.core
  (:require
    [me.raynes.fs :as fs]
    [nhp.blogs :as blogs]
    [nhp.home :as home]
    [nhp.poems :as poems]
    [nhp.talks :as talks]))

(defn copy-assets []
  (doseq [dir ["css" "fonts" "img" "js"]]
    (fs/copy-dir (str "resources/" dir) "out")))

(defn -main []
  (copy-assets)
  (home/generate)
  (talks/generate)
  (poems/generate)
  (blogs/generate))
