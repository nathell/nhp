(ns nhp.core
  (:require
    [clojure.java.io :as io]
    [me.raynes.fs :as fs]
    [nhp.blogs :as blogs]
    [nhp.cycling :as cycling]
    [nhp.czytatki :as czytatki]
    [nhp.home :as home]
    [nhp.poems :as poems]
    [nhp.talks :as talks]))

(defn copy-assets []
  (doseq [domain ["danieljanus.pl" "blog.danieljanus.pl" "plblog.danieljanus.pl"]
          dir ["css" "fonts" "img" "js"]]
    (io/make-parents (str "out/" domain "/a"))
    (fs/copy-dir (str "resources/" dir) (str "out/" domain))))

(defn build [_args]
  (copy-assets)
  (home/generate)
  (cycling/generate)
  (talks/generate)
  (poems/generate)
  (blogs/generate)
  (czytatki/generate))
