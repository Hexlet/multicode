(ns multicode.core
  (:require [clojure.core.match :refer (match)])
  (:gen-class))

;; (defn -main
;;   "I don't do a whole lot ... yet."
;;   [& args]
;;   (println "Hello, World!"))

(defn- generate-value [var]
  (match [var]
         [(_ :guard integer?)] (list 'int var)))

(defn- generate-method-name [name]
  (keyword
    (clojure.string/replace name #"-" "_")))

(defn ast-generator [clojure-code]
  (letfn [(ast-iter [node]
          (if (coll? node)
            (reverse
              (reduce
                (fn [ast item]
                  (conj ast (ast-iter item)))
                (list (generate-method-name (first node)) 'nil 'send)
                (rest node)))
            (generate-value node)))]
    (ast-iter clojure-code)))
