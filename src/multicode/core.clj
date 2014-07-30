(ns multicode.core
  (:require [multicode.lang :refer :all]
            [multicode.ruby :refer :all]
            [multicode.php :refer :all]
            [multicode.javascript :refer :all]
            [multicode.python :refer :all]
            [multicode.coffeescript :refer :all]
            [clojure.string :as s]
            [clojure.set :as sets]))

(defn to-args [coll]
  (reduce #(str (str %1) ", " (str %2)) coll))

(defn generate-call [lang method-name args]
  (format "%s(%s)"
          (transform-method-name lang method-name)
          (if args (to-args args) "")))

(defn generate-unary [lang operator value]
  (format "!%s" value))

(defn generate-assignment [lang assignmentes get-args-func]
  (s/join
    (str (get-terminator lang) "\n")
    (concat
      (map (fn [args]
             (apply
               #(generate-def lang (str %1) (generate-value lang %2))
               args))
           (partition 2 assignmentes))
      (get-args-func))))

(defn- valid-method-name? [method-name]
  (= 0 (count (sets/intersection
                (set (str (last (str method-name))))
                #{\+ \= \- \*}))))

(defn- validate-method-name [method-name]
  (if-not (valid-method-name? method-name)
    (throw (Exception. "Wrong method name"))))

(defn generate-expression [lang [method-name & r]]
  (letfn [(get-args [code]
            (map (fn [item]
                   (if (= clojure.lang.PersistentList (type item))
                     (generate-expression lang item)
                     (generate-value lang item)))
                 code))]

    (validate-method-name method-name)

    (if (and method-name (not= clojure.lang.PersistentList (type method-name)))
      (case method-name
        not (generate-unary lang not (first (get-args r)))
        def (generate-def lang (first r) (first (get-args (drop 1 r))))
        let (generate-assignment lang (first r) #(get-args (drop 1 r))  )
        quote (generate-value lang (first r))
        (generate-call lang method-name (get-args r)))
      (s/join
        (str (get-terminator lang) "\n")
        (concat
          (get-args (conj '() method-name))
          (get-args r))))))

(defn prettify-expression [lang expression]
  (str (generate-expression lang expression) (get-terminator lang)))

(defn prettify-code [lang clojure-code]
  (s/join "\n" (map #(prettify-expression lang %) clojure-code)))
