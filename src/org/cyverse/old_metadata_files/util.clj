(ns org.cyverse.old-metadata-files.util
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
  (map string/trim (map :value (filter (comp (partial = attribute-name) :attr) attributes))))

(defn attr-value [attributes attribute-name]
  (first (attr-values attributes attribute-name)))

(defn- validate-associated-required-values
  "Verifies the number of required attribute values in a set of associated attributes. All required attributes in a set
  must have the same number of values. Furthermore, it is an error for any optional attributes to have more values than
  the required attributes have."
  [required-attribute-names optional-attribute-names required-values optional-values]
  (when-not (or (empty? required-values) (apply = (map count required-values)))
    (throw (ex-info (str "These attributes must have the same number of values: "
                         (string/join ", " required-attribute-names))
                    {:attributes required-attribute-names})))

  (let [max-rv-count (if (seq required-values) (apply max (map count required-values)) 0)
        max-ov-count (if (seq optional-values) (apply max (map count optional-values)) 0)]
    (when (and (seq required-attribute-names) (< max-rv-count max-ov-count))
      (throw (ex-info (str "None of the attributes, "
                           (string/join ", " optional-attribute-names)
                           ", may have more values than any of the attributes, "
                           (string/join ", " required-attribute-names)
                           ".")
                      {:required-attributes required-attribute-names
                       :optional-attributes optional-attribute-names})))))

(defn- extend-associated-optional-values
  "Extends the sequences of optional values in a set of associated attributes if necessary to ensure that all of the
  associated attributes in a set are processed correctly when calling clojure.core/map."
  [required-values optional-values]
  (let [max-count (apply max (map count (concat required-values optional-values)))]
    (mapv #(concat % (take (- max-count (count %)) (repeat nil)))
          optional-values)))

(defn associated-attr-values [attributes required-attribute-names optional-attribute-names]
  (let [get-values (partial attr-values attributes)
        rvs        (map get-values required-attribute-names)
        ovs        (map get-values optional-attribute-names)
        ovs        (extend-associated-optional-values rvs ovs)]
    (validate-associated-required-values required-attribute-names optional-attribute-names rvs ovs)
    (drop-while (partial every? string/blank?) (apply map vector (concat rvs ovs)))))
