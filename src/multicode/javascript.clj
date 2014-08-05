(ns multicode.javascript
  (:require [multicode.lang :refer :all]
            [multicode.helper :as h]
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

(defmethod generate-object-create :javascript [_ args]
  (format "new %s(%s)" (h/class-name (generate-value :javascript (first args)))
                       (string/join ", " (map #( generate-value :javascript %) (rest args)))))

(defmethod transform-method-name :javascript [_ method-name]
  (let [[first & more] (string/split (str method-name) #"-")]
    (str first (string/join "" (map #(string/capitalize %) more)))))

(defmethod transform-var-name :javascript [_ var-name]
  (let [[first & more] (string/split (str var-name) #"-")]
    (str first (string/join "" (map #(string/capitalize %) more)))))

(defmethod generate-def :javascript [_ var-name value]
  (format "var %s = %s" (transform-var-name :javascript var-name) value))

(defmulti generate-javascript-value (fn [data] (class data)))
(defmethod generate-javascript-value java.lang.String [data]
  (generate-string data))
(defmethod generate-javascript-value java.lang.Character [data]
  (generate-char data))
(defmethod generate-javascript-value clojure.lang.Keyword [data]
  (generate-string data))
(defmethod generate-javascript-value clojure.lang.Cons [data]
  (generate-array (map generate-javascript-value (eval data))))
(defmethod generate-javascript-value clojure.lang.PersistentList [data]
  (if (h/object-name? (first data))
    (generate-object-create :javascript data)
    (generate-array (map generate-javascript-value data))))
(defmethod generate-javascript-value clojure.lang.PersistentVector [data]
  (generate-array (map generate-javascript-value data)))
(defmethod generate-javascript-value nil [_] "null")
(defmethod generate-javascript-value clojure.lang.PersistentArrayMap [data]
  (generate-hash
    (reduce #(merge %1 {(first %2), (generate-javascript-value (last %2))})
            {}
            data)))
(defmethod generate-javascript-value :default [data]
  data)

(defmethod generate-value :javascript [_ value]
  (generate-javascript-value value))
