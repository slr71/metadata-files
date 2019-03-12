(ns org.cyverse.metadata-files.datacite-4-1.descriptions
  (:use [medley.core :only [remove-vals]]
        [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.container-nested-element :as cne]
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

(defn new-descriptions-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-description-generator]
    :tag                 ::datacite/descriptions
    :parent-location     location}))
