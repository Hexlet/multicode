(ns multicode.core
  (:require [clojure.core.match :refer (match)])
  (:gen-class))

;; (defn -main
;;   "I don't do a whole lot ... yet."
;;   [& args]
;;   (println "Hello, World!"))

(defn transform-method-name [name]
  (clojure.string/replace name #"-" "_"))

(defn generate-call [method-name args]
  (format "%s(%s)"
          (transform-method-name method-name)
          (if args
            (reduce #(apply str (str %1) ", " (str %2)) args)
            "")))

(defn generate-value [value]
  (cond
    (string? value) (format "'%s'" value)
    :else value))

(defn prettify-expression [[method-name & args]]
  (if args
    (generate-call method-name (map (fn [item]
                                      (if (coll? item)
                                        (prettify-expression item)
                                        (generate-value item)))
                                    args))
    (generate-call method-name '())))

(defn prettify-code [clojure-code]
  (apply str (map #(prettify-expression %1) clojure-code)))
