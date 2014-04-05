(ns multicode.core
  (:gen-class))

(use '[clojure.core.match :only  (match)])

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defn- generate-value [var]
  (match [var]
         [(_ :guard integer?)] (list 'int var)
         ))

(defn- generate-method-name [name]
  (symbol
    (clojure.string/join
      [":"
       (clojure.string/replace name #"-" "_")])))

(defn ast-generator [clojure-code]
  (defn ast-iter [node]
    (if (coll? node)
      (reverse
        (reduce
          (fn [ast item]
            (conj ast (ast-iter item)))
          (list (generate-method-name (first node)) 'nil 'send)
          (rest node)))
      (generate-value node)))
  (ast-iter clojure-code))
