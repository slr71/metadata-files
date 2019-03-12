(ns org.cyverse.metadata-files.datacite-4-1.descriptions
  (:use [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.simple-nested-element :as sne]
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

(defn- get-description-attrs [location {:keys [avus] :as attribute}]
  {:descriptionType (get-description-type location attribute)
   ::xml/lang       (util/get-language avus)})

(defn new-description-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "description"
    :min-occurs      0
    :max-occurs      "unbounded"
    :attrs-fn        get-description-attrs
    :tag             ::datacite/description
    :parent-location location}))

;; The desriptions element

(defn new-descriptions-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-description-generator]
    :tag                 ::datacite/descriptions
    :parent-location     location}))
