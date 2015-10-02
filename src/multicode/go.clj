(ns multicode.go
  (:require [multicode.lang :refer :all]
            [multicode.helper :as h]
            [clojure.string :as string]))

(defn- generate-array [type value]
  (format "[]%s{%s}" type (string/join ", " value)))

(defn- generate-string [value]
  (format "\"%s\"" (name value)))

(defn- generate-char [value]
  (format "'%s'" value))

(defn- generate-hash [key-type value-type value]
  (let [parts (reverse (map #(str "\"" (name (first %)) "\"" ": " (last %))
                            value))]
    (format "map[%s]%s{%s}" key-type value-type (string/join ", " parts))))

(defmethod generate-object-create :go [_ args]
  (format "%s{%s}" (h/class-name (generate-value :go (first args)))
          (string/join ", " (map #( generate-value :go %) (rest args)))))

(defmethod transform-method-name :go [_ method-name]
  (-> method-name
      (h/camel-case)
      (string/replace #"/" ".")))

(defmethod transform-var-name :go [_ var-name]
  (h/camel-case var-name))

(defmethod generate-def :go [_ var-name value]
  (format "%s := %s" (transform-var-name :go var-name) value))

(defmulti calc-type (fn [data] (class data)))
(defmethod calc-type java.lang.String [data]
  "string")
(defmethod calc-type java.lang.Long [data]
  "int")
(defmethod calc-type java.lang.Boolean [data]
  "bool")
(defmethod calc-type java.lang.Character [data]
  "rune")
(defmethod calc-type clojure.lang.Keyword [data]
  "string")
(defmethod calc-type clojure.lang.PersistentVector [data]
  (format "[]%s" (calc-type  (first data))))
(defmethod calc-type clojure.lang.PersistentArrayMap [data]
  (format "map[%s]%s"
          (calc-type (key (first data)))
          (calc-type (val (first data)))))
(defmethod calc-type clojure.lang.PersistentList [data]
  (if (h/object-name? (first data))
    (h/class-name (first data) )
    (format "[]%s" (calc-type (first data)))))
(defmethod calc-type :default [data]
  (class data))

(defmethod get-terminator :go [_] "")

(defmulti generate-go-value (fn [data] (class data)))
(defmethod generate-go-value java.lang.String [data]
  (generate-string data))
(defmethod generate-go-value java.lang.Character [data]
  (generate-char data))
(defmethod generate-go-value clojure.lang.Keyword [data]
  (generate-string data))
(defmethod generate-go-value clojure.lang.Cons [data]
  (generate-array
    (calc-type (first (eval data)))
    (map generate-go-value (eval data))))
(defmethod generate-go-value clojure.lang.PersistentList [data]
  (if (h/object-name? (first data))
    (generate-object-create :go data)
    (generate-array
      (calc-type (first data))
      (map generate-go-value data))))
(defmethod generate-go-value clojure.lang.PersistentVector [data]
  (generate-array
    (calc-type (first data))
    (map generate-go-value data)))
(defmethod generate-go-value nil [_] "nil")
(defmethod generate-go-value clojure.lang.PersistentArrayMap [data]
  (generate-hash
    (calc-type (key (first data)))
    (calc-type (val (first data)))
    (reduce #(merge %1 {(first %2), (generate-go-value (last %2))})
            {}
            data)))
(defmethod generate-go-value :default [data]
  data)

(defmethod generate-value :go [_ value]
  (generate-go-value value))
