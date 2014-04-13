(ns multicode.core
  (:require [clojure.core.match :refer (match)])
  (:gen-class))

;; (defn -main
;;   "I don't do a whole lot ... yet."
;;   [& args]
;;   (println "Hello, World!"))

(defn to-args [coll] (reduce #(str (str %1) ", " (str %2)) coll))

(defn transform-method-name [name]
  (clojure.string/replace name #"-" "_"))

(defn generate-def [name value]
  (format "%s = %s" name value))

(defn generate-value [value]
  (letfn [(to-hash [value]
            (format "{%s}"
                    (reduce #(str (str %1) ", " (str %2))
                            (map #(str (first %1) " => " (last %1))
                                 value))))]
    (cond
      (string? value) (format "'%s'" value)
      (= clojure.lang.PersistentVector (type value)) (format "[%s]" (to-args value))
      (= clojure.lang.PersistentArrayMap (type value)) (to-hash value)
      :else value)))

(defn generate-unary [operator value]
  (format "!%s" (generate-value value)))

(defn generate-call [method-name args]
  (format "%s(%s)"
          (transform-method-name method-name)
          (if args
            (to-args args)
            "")))

(defn generate-expression [method-name args]
  (cond
    (= method-name 'not) (generate-unary 'not (first args))
    (= method-name 'def) (generate-def (first args) (last args))
    :else (generate-call method-name args)))

(defn prettify-expression [[method-name & r]]
  (let [args (or r '())
        calculated-args (map (fn [item]
                               (if (= clojure.lang.PersistentList (type item))
                                 (prettify-expression item)
                                 (generate-value item)))
                             args)]
    (generate-expression method-name calculated-args)))

(defn prettify-code [clojure-code]
  (clojure.string/join "\n" (map #(prettify-expression %1) clojure-code)))
