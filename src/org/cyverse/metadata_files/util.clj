(ns org.cyverse.metadata-files.util
  (:require [clojure.string :as string]))

(defn missing-required-attributes [attribute-names]
  (throw (ex-info (str "Missing required attributes: " (string/join ", " attribute-names))
                  {:attributes attribute-names})))

(defn attribute-set [attributes]
  (set (map :attr (remove (comp string/blank? :value) attributes))))

(defn missing-attributes [required-attributes attributes]
  (set (remove (attribute-set attributes) required-attributes)))

(defn attr-values [attributes attribute-name]
  (map :value (filter (comp (partial = attribute-name) :attr) attributes)))

(defn attr-value [attributes attribute-name]
  (first (attr-values attributes attribute-name)))

(defn associated-attr-values [attributes attribute-names]
  (let [vs (map (partial attr-values attributes) attribute-names)]
    (when-not (apply = (map count vs))
      (throw
       (ex-info (str "These attributes must have the same number of values: " (string/join ", " attribute-names))
                {:attributes attribute-names})))
    (map (partial apply vector) vs)))
