(ns org.cyverse.metadata-files.util
  (:require [clojure.string :as string]
            [clojure.pprint :refer [pprint]]))

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

(defn associated-attr-values [attributes required-attribute-names optional-attribute-names]
  (let [get-values (partial attr-values attributes)
        extend-ovs (fn [ovs] (concat ovs (repeat nil)))
        rvs        (map get-values required-attribute-names)
        ovs        (map (comp extend-ovs get-values) optional-attribute-names)]
    (when-not (apply = (map count rvs))
      (throw
       (ex-info (str "These attributes must have the same number of values: "
                     (string/join ", " required-attribute-names))
                {:attributes required-attribute-names})))
    (apply map vector (concat rvs ovs))))
