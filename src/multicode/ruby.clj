(ns multicode.ruby
  (:require [multicode.lang :refer :all]
            [clojure.string :as string]))

(defn- generate-string [value]
  (format "'%s'" value))

(defn- generate-char [value]
  (generate-string value))

(defn- generate-array [value]
  (format "[%s]" (string/join ", " value)))

(defn- generate-hash [value]
  (let [parts (reverse (map #(str (first %) " => " (last %))
                            value))]
    (format "{%s}" (string/join ", " parts))))

(defmethod get-terminator :ruby [_] "")

(defmethod transform-method-name :ruby [_ method-name]
  (string/replace method-name #"-" "_"))

(defmethod transform-var-name :ruby [_ var-name]
  (string/replace var-name #"-" "_"))

(defmethod generate-def :ruby [_ var-name value]
  (format "%s = %s" (transform-var-name :ruby var-name) value))

(defmulti generate-ruby-value (fn [data] (class data)))
(defmethod generate-ruby-value java.lang.String [data]
  (generate-string data))
(defmethod generate-ruby-value java.lang.Character [data]
  (generate-char data))
(defmethod generate-ruby-value clojure.lang.Cons [data]
  (generate-array (map (partial generate-ruby-value) (eval data))))
(defmethod generate-ruby-value clojure.lang.PersistentList [data]
  (generate-array (map (partial generate-ruby-value) data)))
(defmethod generate-ruby-value clojure.lang.PersistentVector [data]
  (generate-array (map (partial generate-ruby-value) data)))
(defmethod generate-ruby-value nil [_] "nil")
(defmethod generate-ruby-value clojure.lang.APersistentMap [data]
  (generate-hash
    (reduce #(merge %1 {(generate-ruby-value (first %2)), (generate-ruby-value (last %2))})
            {}
            data)))
(defmethod generate-ruby-value :default [data]
  data)

(defmethod generate-value :ruby [_ value]
  (generate-ruby-value value))
