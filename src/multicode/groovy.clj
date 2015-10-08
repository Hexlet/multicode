(ns multicode.groovy
  (:require [multicode.lang :refer :all]
            [multicode.helper :as h]
            [clojure.string :as string]))

(defn- generate-array [value]
  (format "[%s]" (string/join ", " value)))

(defn- generate-string [value]
  (format "\"%s\"" (name value)))

(defn- generate-char [value]
  (format "\"%s\"" value))

(defn- generate-hash [value]
  (let [parts (reverse (map #(str (name (first %)) ": " (last %))
                            value))]
    (format "[%s]" (string/join ", " parts))))

(defmethod get-terminator :groovy [_] "")

(defmethod generate-object-create :groovy [_ args]
  (format "new %s(%s)" (h/class-name (generate-value :groovy (first args)))
          (string/join ", " (map #( generate-value :groovy %) (rest args)))))

(defmethod transform-method-name :groovy [_ method-name]
  (-> method-name
      (h/camel-case)
      (string/replace #"/" ".")))

(defmethod transform-var-name :groovy [_ var-name]
  (h/camel-case var-name))

(defmethod generate-def :groovy [_ var-name value]
  (format "def %s = %s" (transform-var-name :groovy var-name) value))

(defmulti generate-groovy-value (fn [data] (class data)))
(defmethod generate-groovy-value java.lang.String [data]
  (generate-string data))
(defmethod generate-groovy-value java.lang.Character [data]
  (generate-char data))
(defmethod generate-groovy-value clojure.lang.Keyword [data]
  (generate-string data))
(defmethod generate-groovy-value clojure.lang.Cons [data]
  (generate-array (map generate-groovy-value (eval data))))
(defmethod generate-groovy-value clojure.lang.PersistentList [data]
  (if (h/object-name? (first data))
    (generate-object-create :groovy data)
    (generate-array (map generate-groovy-value data))))
(defmethod generate-groovy-value clojure.lang.PersistentVector [data]
  (generate-array (map generate-groovy-value data)))
(defmethod generate-groovy-value nil [_] "null")
(defmethod generate-groovy-value clojure.lang.PersistentArrayMap [data]
  (generate-hash
    (reduce #(merge %1 {(generate-groovy-value (first %2) ), (generate-groovy-value (last %2))})
            {}
            data)))
(defmethod generate-groovy-value :default [data]
  data)

(defmethod generate-value :groovy [_ value]
  (generate-groovy-value value))
