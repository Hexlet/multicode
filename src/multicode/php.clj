(ns multicode.php
  (:require [multicode.lang :refer :all]
            [multicode.helper :as h]
            [clojure.string :as string]))

(defn- generate-string [value]
  (format "'%s'" value))

(defn- generate-char [value]
  (generate-string value))

(defn- generate-var [value]
  (let [var-name (h/camel-case value)]
    (if (h/object-name? var-name)
      var-name
      (str "$" var-name))))

(defn- generate-array [value]
  (format "array(%s)" (string/join ", " value)))

(defn- generate-hash [value]
  (let [parts (reverse (map #(str (first %) " => " (last %))
                            value))]
    (format "array(%s)" (string/join ", " parts))))

(defmethod generate-object-create :php [_ args]
  (format "%s(%s)" (h/class-name (generate-value :php (first args)))
                   (string/join ", " (map #( generate-value :php %) (rest args)))))

(defmethod get-terminator :php [_] ";")

(defmethod transform-method-name :php [_ method-name]
  (-> method-name
      (h/camel-case)
      (string/replace "." "->")
      (string/replace #"/" "::")))

(defmethod transform-var-name :php [_ var-name]
  (generate-var var-name))

(defmulti generate-php-value (fn [data] (class data)))
(defmethod generate-php-value java.lang.String [data]
  (generate-string data))
(defmethod generate-php-value java.lang.Character [data]
  (generate-char data))
(defmethod generate-php-value clojure.lang.Keyword [data]
  (generate-string (name data)))
(defmethod generate-php-value clojure.lang.Symbol [data]
  (generate-var data))
(defmethod generate-php-value clojure.lang.Cons [data]
  (generate-array (map generate-php-value (eval data))))
(defmethod generate-php-value clojure.lang.PersistentList [data]
  (if (h/object-name? (first data))
    (generate-object-create :php data)
    (generate-array (map generate-php-value data))))
(defmethod generate-php-value clojure.lang.PersistentVector [data]
  (generate-array (map generate-php-value data)))
(defmethod generate-php-value nil [_] "NULL")
(defmethod generate-php-value clojure.lang.APersistentMap [data]
  (generate-hash
    (reduce #(merge %1 {(generate-php-value (first %2)), (generate-php-value (last %2))})
            {}
            data)))
(defmethod generate-php-value :default [data]
  data)

(defmethod generate-value :php [_ value]
  (generate-php-value value))
