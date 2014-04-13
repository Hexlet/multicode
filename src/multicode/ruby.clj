(ns multicode.ruby
  (:gen-class))

(defn get-terminator [] "")

(defn transform-method-name [method-name]
  (clojure.string/replace method-name #"-" "_"))

(defn transform-var-name [var-name]
  (clojure.string/replace var-name #"-" "_"))

(defn generate-def [var-name value]
  (format "%s = %s" (transform-var-name var-name) value))

(defn generate-array [value]
  (format "[%s]" (clojure.string/join ", " value)))

(defn generate-string [value] (format "'%s'" value))

(defn generate-hash [value]
  (let [parts (reverse (map #(str (first %) " => " (last %))
                            value))]
    (format "{%s}" (clojure.string/join ", " parts))))

(defn generate-value [value]
  (cond
    (string? value) (generate-string value)
    (= clojure.lang.PersistentVector (type value)) (generate-array (map generate-value value))
    (= clojure.lang.PersistentArrayMap (type value)) (generate-hash
                                                       (reduce #(merge %1 {(generate-value (first %2)), (generate-value (last %2))})
                                                               {}
                                                               value))
    :else value))

