(ns multicode.code
  (:gen-class))

(defn generate [lang clojure-code]
  (let [ast (ast-generator clojure-code)]
    ))
