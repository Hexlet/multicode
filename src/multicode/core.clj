(ns multicode.core
  (:require [multicode.lang :refer :all]
            [multicode.ruby :refer :all]
            [multicode.php :refer :all]
            [multicode.javascript :refer :all]))

(defn to-args [coll] (reduce #(str (str %1) ", " (str %2)) coll))

(defn generate-call [lang method-name args]
  (format "%s(%s)"
          (transform-method-name lang method-name)
          (if args (to-args args) "")))

(defn generate-unary [lang operator value]
  (format "!%s" (generate-value lang value)))

(defn generate-expression [lang [method-name & r]]
  (let [args (map (fn [item]
                    (if (= clojure.lang.PersistentList (type item))
                      (generate-expression lang item)
                      (generate-value lang item)))
                  r)]
    (case method-name
      not (generate-unary lang not (first args))
      def (generate-def lang (first args) (last args))
      (generate-call lang method-name args))))

(defn prettify-expression [lang expression]
  (str (generate-expression lang expression) (get-terminator lang)))

(defn prettify-code [lang clojure-code]
  (clojure.string/join "\n" (map #(prettify-expression lang %) clojure-code)))
