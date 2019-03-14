(ns org.cyverse.metadata-files.util
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]))

(def default-language "en-us")

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

(defn get-language [attributes]
  (or (attr-value attributes "xml:lang") default-language))

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

(defn- get-attr-counts [attributes]
  (reduce #(update %1 %2 (fnil inc 0)) {} (map :attr attributes)))

(defn- validate-attr-count [attr-counts element-factory]
  (when-let [attr-name (mdf/attribute-name element-factory)]
    (let [occurs     (attr-counts attr-name 0)
          min-occurs (mdf/min-occurs element-factory)
          max-occurs (mdf/max-occurs element-factory)
          in-range   (and (<= min-occurs occurs) (or (= max-occurs "unbounded") (<= occurs max-occurs)))]
      (if-not in-range
        [(str "Found " occurs " instances of " attr-name "; expected between " min-occurs " and " max-occurs ".")]
        []))))

(defn validate-attr-counts [element-factory attributes]
  (let [child-element-factories (mdf/child-element-factories element-factory)
        location                (mdf/get-location element-factory)
        attr-counts             (get-attr-counts attributes)]
    (when-let [msgs (seq (mapcat (partial validate-attr-count attr-counts) child-element-factories))]
      (throw (ex-info "Metadata validation failed." {:location location :reasons msgs})))))

(defn validate-non-blank-string-attribute-value [location value]
  (when (string/blank? value)
    (throw (ex-info "Missing or empty required string attribute value." {:location location}))))

(defn validate-year-attribute-value [location value]
  (validate-non-blank-string-attribute-value location value)
  (when-not (re-matches #"\d{4}" value)
    (throw (ex-info "Incorrect year format; expected YYYY." {:location location :value value}))))

(defn get-required-attribute-value [location attributes attribute-name]
  (let [attribute-value (attr-value attributes attribute-name)]
    (if (string/blank? attribute-value)
      (throw (ex-info "Missing required attribute." {:location location :attribute attribute-name}))
      attribute-value)))

(defn- attribute-arg-fn [attributes]
  (let [attributes-named (group-by :attr attributes)]
    (fn [factory]
      (if-let [name (mdf/attribute-name factory)]
        (attributes-named name)
        [attributes]))))

(defn validate-child-elements [child-element-factories attributes]
  (let [get-attribute-arg (attribute-arg-fn attributes)]
    (doseq [factory   child-element-factories
            attribute (get-attribute-arg factory)]
      (mdf/validate factory attribute))))

(defn build-child-elements [child-element-factories attributes]
  (let [get-attribute-arg (attribute-arg-fn attributes)]
    (->> (for [factory   child-element-factories
               attribute (get-attribute-arg factory)]
           (mdf/generate-nested factory attribute))
         (remove nil?))))

(defn get-child-attributes
  "The attribute argument given to methods in a nested element generator may either be a single attribute or a list of
   attributes. If a list of attributes is given then child elements can be generated from that list. If a single
   attribute is given then child elements will be obtained from the sub-attributes."
  [attribute-arg]
  (if (sequential? attribute-arg)
    attribute-arg
    (:avus attribute-arg)))

(defn build-location
  "Builds the location of the current nested attribute. Each location has potentially two components: a parent
   location and a potentially empty attribute name. If the attribute name is empty then the parent location
   should be used. Otherwise, the the two locations are joined with a periods and returned."
  [parent-location attribute-name]
  (if (string/blank? attribute-name)
    parent-location
    (str parent-location "." attribute-name)))
