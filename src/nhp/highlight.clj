(ns nhp.highlight
  (:require [clojure.java.io :as io])
  (:import [javax.script ScriptEngineManager]))

(defn get-javascript-engine []
  (-> (ScriptEngineManager.)
      (.getEngineByMimeType "application/javascript")))

(def highlighter
  (future
    (let [engine (get-javascript-engine)]
      (doseq [f ["highlight.js" "clojure.min.js" "delphi.min.js" "haskell.min.js" "latex.min.js" "lisp.min.js" "x86asm.min.js"]]
        (.eval engine (slurp (io/resource (str "js/" f)))))
      engine)))

(def render-code "hljs.highlight(code, {language: lang}).value")

(defn highlight [code lang]
  (.put @highlighter "code" code)
  (.put @highlighter "lang" lang)
  (.eval @highlighter render-code))
