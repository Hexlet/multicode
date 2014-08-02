(ns multicode.helper
  (:require [clojure.string :as s]))

(defn object-name? [method-name]
  (let [first-char (first (str method-name))]
    (and (.endsWith (str method-name) ".")
         (= (s/upper-case first-char)
            (str first-char)))))
