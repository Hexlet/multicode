(ns multicode.javascript
  (:require [multicode.lang :refer :all]))

(defn- generate-array [value]
  (format "[%s]" (clojure.string/join ", " value)))

(defn- generate-string [value] (format "'%s'" (name value)))

(defn- generate-hash [value]
  (let [parts (reverse (map #(str (name (first %)) ": " (last %))
                            value))]
    (format "{%s}" (clojure.string/join ", " parts))))

(defmethod transform-method-name :javascript [_ method-name]
  (let [[first & rest] (clojure.string/split (str method-name) #"-")]
    (str first (clojure.string/join "" (map #(clojure.string/capitalize %) rest)))))

(defmethod transform-var-name :javascript [_ var-name]
  (let [[first & rest] (clojure.string/split (str var-name) #"-")]
    (str first (clojure.string/join "" (map #(clojure.string/capitalize %) rest)))))

(defmethod generate-def :javascript [_ var-name value]
  (format "var %s = %s" (transform-var-name :javascript var-name) value))

(defmethod generate-value :javascript [_ value]
  (cond
    (string? value) (generate-string value)
    (keyword? value) (generate-string value)
    (= clojure.lang.PersistentVector (type value)) (generate-array (map #(generate-value :javascript %) value))
    (= clojure.lang.PersistentArrayMap (type value)) (generate-hash
                                                       (reduce #(merge %1 {(name (first %2)), (generate-value :javascript (last %2))})
                                                               {}
                                                               value))
    :else value))
