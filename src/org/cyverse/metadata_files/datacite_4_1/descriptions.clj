(ns org.cyverse.metadata-files.datacite-4-1.descriptions
  (:use [medley.core :only [remove-vals]]
        [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The description element

(def ^:prviate valid-description-types
  #{"Abstract" "Methods" "SeriesInformation" "TableOfContents" "TechnicalInfo" "Other"})

(defn- get-description-type [location {:keys [avus]}]
  (let [attribute-name   "descriptionType"
        description-type (util/get-required-attribute-value location avus attribute-name)]
    (when-not (valid-description-types description-type)
      (throw (ex-info (str "Invalid " attribute-name " value.")
                      {:location     location
                       :attribute    attribute-name
                       :value        description-type
                       :valid-values valid-description-types})))
    description-type))

(deftype Description [description description-type language]
  mdf/XmlSerializable
  (to-xml [_]
    (let [attrs (remove-vals string/blank? {:descriptionType description-type ::xml/lang language})]
      (element ::datacite/description attrs description))))

(deftype DescriptionGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "description")
  (min-occurs [_] 0)
  (max-occurs [_] "unbounded")
  (get-location [_] (str parent-location ".description"))
  (child-element-factories [_] [])

  (validate [self {description :value :as attribute}]
    (let [location (mdf/get-location self)]
      (util/validate-non-blank-string-attribute-value location description)
      (get-description-type location attribute)))

  (generate-nested [self {description :value avus :avus :as attribute}]
    (Description. description
                  (get-description-type (mdf/get-location self) attribute)
                  (util/get-language avus))))

(defn new-description-generator [location]
  (DescriptionGenerator. location))

;; The desriptions element

(deftype Descriptions [descriptions]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/descriptions {} (mapv mdf/to-xml descriptions))))

(deftype DescriptionsGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] nil)
  (min-occurs [_] 0)
  (max-occurs [_] 1)
  (get-location [_] parent-location)

  (child-element-factories [self]
    [(new-description-generator (mdf/get-location self))])

  (validate [self attributes]
    (let [element-factories (mdf/child-element-factories self)]
      (util/validate-attr-counts self attributes)
      (util/validate-child-elements element-factories attributes)))

  (generate-nested [self attributes]
    (when-let [descriptions (seq (util/build-child-elements (mdf/child-element-factories self) attributes))]
      (Descriptions. descriptions))))

(defn new-descriptions-generator [location]
  (DescriptionsGenerator. location))
