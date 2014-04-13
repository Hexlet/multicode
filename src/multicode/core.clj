(ns multicode.core
  (:require [multicode.ruby :as ruby])
  (:require [multicode.javascript :as javascript])
  (:gen-class))

(defn transform-method-name [lang method-name]
  (cond
    (= :ruby lang) (ruby/transform-method-name method-name)
    (= :javascript lang) (javascript/transform-method-name method-name)
    :else method-name))

(defn get-terminator [lang]
  (cond
    (= :ruby lang) (ruby/get-terminator)
    :else ";"))

(defn transform-var-name [lang var-name]
  (cond
    (= :ruby lang) (ruby/transform-var-name var-name)
    :else var-name))

(defn generate-value [lang var-name]
  (cond
    (= :ruby lang) (ruby/generate-value var-name)
    (= :javascript lang) (javascript/generate-value var-name)
    :else var-name))

(defn generate-def [lang var-name value]
  (cond
    (= :ruby lang) (ruby/generate-def var-name value)
    (= :javascript lang) (javascript/generate-def var-name value)
    :else (format "%s = %s" (transform-var-name lang var-name) value)))

(defn generate-hash [lang value]
  (cond
  (= :ruby lang) (ruby/generate-hash value)))

(defn to-args [coll] (reduce #(str (str %1) ", " (str %2)) coll))

(defn generate-call [lang method-name args]
  (format "%s(%s)"
          (transform-method-name lang method-name)
          (if args
            (to-args args)
            "")))

(defn generate-unary [lang operator value]
  (format "!%s" (generate-value lang value)))

(defn generate-expression [lang [method-name & r]]
  (let [args (map (fn [item]
                    (if (= clojure.lang.PersistentList (type item))
                      (generate-expression lang item)
                      (generate-value lang item)))
                  r)]
    (cond
      (= method-name 'not) (generate-unary lang 'not (first args))
      (= method-name 'def) (generate-def lang (first args) (last args))
      :else (generate-call lang method-name args))))

(defn prettify-expression [lang expression]
  (str (generate-expression lang expression) (get-terminator lang)))

(defn prettify-code [lang clojure-code]
  (clojure.string/join "\n" (map #(prettify-expression lang %) clojure-code)))
