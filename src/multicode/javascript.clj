(ns multicode.javascript)

(defn transform-method-name [method-name]
  (let [[first & rest] (clojure.string/split (str method-name) #"-")]
    (str first (clojure.string/join "" (map #(clojure.string/capitalize %) rest)))))

(defn transform-var-name [var-name]
  (let [[first & rest] (clojure.string/split (str var-name) #"-")]
    (str first (clojure.string/join "" (map #(clojure.string/capitalize %) rest)))))

(defn generate-def [var-name value]
  (format "var %s = %s" (transform-var-name var-name) value))

(defn generate-array [value]
  (format "[%s]" (clojure.string/join ", " value)))

(defn generate-string [value] (format "'%s'" (name value)))

(defn generate-hash [value]
  (let [parts (reverse (map #(str (name (first %)) ": " (last %))
                            value))]
    (format "{%s}" (clojure.string/join ", " parts))))

(defn generate-value [value]
  (cond
    (string? value) (generate-string value)
    (keyword? value) (generate-string value)
    (= clojure.lang.PersistentVector (type value)) (generate-array (map generate-value value))
    (= clojure.lang.PersistentArrayMap (type value)) (generate-hash
                                                       (reduce #(merge %1 {(name (first %2)), (generate-value (last %2))})
                                                               {}
                                                               value))
    :else value))

