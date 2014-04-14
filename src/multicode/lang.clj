(ns multicode.lang)

(defmulti transform-method-name (fn [lang _] lang))
(defmethod transform-method-name :default [_ method-name] method-name)

(defmulti get-terminator identity)
(defmethod get-terminator :default [_] ";")

(defmulti transform-var-name (fn [lang _] lang))
(defmethod transform-var-name :default [_ var-name] var-name)

(defmulti generate-value (fn [lang _] lang))
(defmethod generate-value :default [_ var-name] var-name)

(defmulti generate-def (fn [lang _ _] lang))
(defmethod generate-def :default [lang var-name value]
  (format "%s = %s" (transform-var-name lang var-name) value))
