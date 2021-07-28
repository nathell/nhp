(ns nhp.core
  (:require
    [clojure.java.io :as io]
    [me.raynes.fs :as fs]
    [nhp.blogs :as blogs]
    [nhp.home :as home]
    [nhp.poems :as poems]
    [nhp.talks :as talks]))

(defn copy-assets []
  (doseq [domain ["danieljanus.pl" "blog.danieljanus.pl" "plblog.danieljanus.pl"]
          dir ["css" "fonts" "img" "js"]]
    (io/make-parents (str "out/" domain "/a"))
    (fs/copy-dir (str "resources/" dir) (str "out/" domain))))

(defn -main []
  (copy-assets)
  (home/generate)
  (talks/generate)
  (poems/generate)
  (blogs/generate))
