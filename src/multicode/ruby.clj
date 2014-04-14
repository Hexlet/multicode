(ns multicode.ruby
  (:require [multicode.lang :refer :all]
            [clojure.walk :only [walk]]))

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

(defmulti generate-ruby-value (fn [data] (class data)))
(defmethod generate-ruby-value java.lang.String [data]
  (generate-string data))
(defmethod generate-ruby-value clojure.lang.PersistentVector [data]
  (generate-array (map (partial generate-ruby-value) data)))
(defmethod generate-ruby-value clojure.lang.PersistentArrayMap [data]
  (generate-hash
    (reduce #(merge %1 {(generate-ruby-value (first %2)), (generate-ruby-value (last %2))})
            {}
            data)))
(defmethod generate-ruby-value :default [data]
  data)

(defmethod generate-value :ruby [_ value]
 (generate-ruby-value value))
