(ns multicode.python
  (:require [multicode.lang :refer :all]
            [clojure.string :as string]))

(defn- generate-array [value]
  (format "[%s]" (string/join ", " value)))

(defn- generate-string [value] (format "'%s'" (name value)))

(defn- generate-hash [value]
  (let [parts (reverse (map #(str (name (first %)) ": " (last %))
                            value))]
    (format "{%s}" (string/join ", " parts))))

(defmethod get-terminator :python [_] "")

(defmethod transform-method-name :python [_ method-name]
  (let [[first & more] (string/split (str method-name) #"-")]
    (str first (string/join "" (map #(string/capitalize %) more)))))

(defmethod transform-var-name :python [_ var-name] (string/replace var-name #"-" "_"))

(defmethod generate-def :python [_ var-name value]
  (format "%s = %s" (transform-var-name :python var-name) value))

(defmulti generate-python-value (fn [data] (class data)))
(defmethod generate-python-value java.lang.String [data]
  (generate-string data))
(defmethod generate-python-value clojure.lang.Keyword [data]
  (generate-string data))
(defmethod generate-python-value clojure.lang.PersistentVector [data]
  (generate-array (map (partial generate-python-value) data)))
(defmethod generate-python-value clojure.lang.PersistentArrayMap [data]
  (generate-hash
    (reduce #(merge %1 {(generate-python-value (first %2)), (generate-python-value (last %2))})
            {}
            data)))
(defmethod generate-python-value java.lang.Boolean [data]
  (symbol (string/capitalize data)))
(defmethod generate-python-value :default [data]
  data)

(defmethod generate-value :python [_ value]
 (generate-python-value value))