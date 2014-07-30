(ns multicode.coffeescript
  (:require [multicode.lang :refer :all]
            [clojure.string :as string]))

(defn- generate-array [value]
  (format "[%s]" (string/join ", " value)))

(defn- generate-string [value]
  (format "'%s'" (name value)))

(defn- generate-char [value]
  (format "'%s'" value))

(defn- generate-hash [value]
  (let [parts (reverse (map #(str (name (first %)) ": " (last %))
                            value))]
    (format "{%s}" (string/join ", " parts))))

(defmethod transform-method-name :coffeescript [_ method-name]
  (let [[first & more] (string/split (str method-name) #"-")]
    (str first (string/join "" (map #(string/capitalize %) more)))))

(defmethod transform-var-name :coffeescript [_ var-name]
  (let [[first & more] (string/split (str var-name) #"-")]
    (str first (string/join "" (map #(string/capitalize %) more)))))

(defmethod generate-def :coffeescript [_ var-name value]
  (format "%s = %s" (transform-var-name :coffeescript var-name) value))

(defmethod get-terminator :coffeescript [_] "")

(defmulti generate-coffeescript-value (fn [data] (class data)))
(defmethod generate-coffeescript-value java.lang.String [data]
  (generate-string data))
(defmethod generate-coffeescript-value java.lang.Character [data]
  (generate-char data))
(defmethod generate-coffeescript-value clojure.lang.Keyword [data]
  (generate-string data))
(defmethod generate-coffeescript-value clojure.lang.Cons [data]
  (generate-array (map generate-coffeescript-value (eval data))))
(defmethod generate-coffeescript-value clojure.lang.PersistentList [data]
  (generate-array (map generate-coffeescript-value data)))
(defmethod generate-coffeescript-value clojure.lang.PersistentVector [data]
  (generate-array (map generate-coffeescript-value data)))
(defmethod generate-coffeescript-value nil [_] "null")
(defmethod generate-coffeescript-value clojure.lang.PersistentArrayMap [data]
  (generate-hash
    (reduce #(merge %1 {(first %2), (generate-coffeescript-value (last %2))})
            {}
            data)))
(defmethod generate-coffeescript-value :default [data]
  data)

(defmethod generate-value :coffeescript [_ value]
  (generate-coffeescript-value value))
