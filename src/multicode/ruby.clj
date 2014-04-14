(ns multicode.ruby
  (:require [multicode.lang :refer :all]))

(defn- generate-string [value]
  (format "'%s'" value))

(defn- generate-array [value]
  (format "[%s]" (clojure.string/join ", " value)))

(defn- generate-hash [value]
  (let [parts (reverse (map #(str (first %) " => " (last %))
                            value))]
    (format "{%s}" (clojure.string/join ", " parts))))

(defmethod get-terminator :ruby [_] "")

(defmethod transform-method-name :ruby [_ method-name]
  (clojure.string/replace method-name #"-" "_"))

(defmethod transform-var-name :ruby [_ var-name]
  (clojure.string/replace var-name #"-" "_"))

(defmethod generate-def :ruby [_ var-name value]
  (format "%s = %s" (transform-var-name :ruby var-name) value))

(defmethod generate-value :ruby [_ value]
  (cond
    (string? value) (generate-string value)
    (= clojure.lang.PersistentVector (type value)) (generate-array (map #(generate-value :ruby %) value))
    (= clojure.lang.PersistentArrayMap (type value)) (generate-hash
                                                       (reduce #(merge %1 {(generate-value :ruby (first %2)), (generate-value :ruby (last %2))})
                                                               {}
                                                               value))
    :else value))
