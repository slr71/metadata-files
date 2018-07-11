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

(defn- validate-required-values
  "Verifies that the same number of values is defined for each member of set of required associated attribute names. If
  the number of values is different then the metadata is not defined correctly."
  [required-attribute-names required-values]
  (when-not (or (empty? required-values) (apply = (map count required-values)))
    (throw (ex-info (str "These attributes must have the same number of values: "
                         (string/join ", " required-attribute-names))
                    {:attributes required-attribute-names}))))

(defn associated-attr-values [attributes required-attribute-names optional-attribute-names]
  (let [get-values (partial attr-values attributes)
        extend-ovs (fn [ovs] (if (seq required-attribute-names) (concat ovs (repeat nil)) ovs))
        rvs        (map get-values required-attribute-names)
        ovs        (map (comp extend-ovs get-values) optional-attribute-names)]
    (validate-required-values required-attribute-names rvs)
    (apply map vector (concat rvs ovs))))
