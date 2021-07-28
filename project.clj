(defproject nhp "0.1.0-SNAPSHOT"
  :description "Daniel Janus's homepage generator"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.xml "0.2.0-alpha6"]
                 [clojure.java-time "0.3.2"]
                 [hiccup "2.0.0-alpha2"]
                 [org.flatland/ordered "1.5.7"]
                 [io.forward/yaml "1.0.9" :exclusions [org.flatland/ordered]]
                 [markdown-clj "1.0.7"]
                 [me.raynes/fs "1.4.6"]
                 [reaver "0.1.2"]]
  :plugins [[lein-sass "0.4.0"]]
  :main nhp.core
  :sass {:src              "src/sass"
         :output-directory "resources/css"}
  :repl-options {:init-ns nhp.core})
