(ns org.cyverse.metadata-files.datacite-4-1.resource-type
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

(def valid-resource-types
  #{"Audiovisual"
    "Collection"
    "DataPaper"
    "Dataset"
    "Event"
    "Image"
    "InteractiveResource"
    "Model"
    "PhysicalObject"
    "Service"
    "Software"
    "Sound"
    "Text"
    "Workflow"
    "Other"})

(defn- get-resource-type-general [location {:keys [avus]}]
  (let [attribute-name "resourceTypeGeneral"
        value          (util/get-required-attribute-value location avus attribute-name)]
    (when-not (valid-resource-types value)
      (throw (ex-info (str "Invalid " attribute-name " value.")
                      {:location     location
                       :attribute    attribute-name
                       :value        value
                       :valid-values valid-resource-types})))
    value))

(deftype ResourceType [resource-type resource-type-general]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/resourceType {:resourceTypeGeneral resource-type-general} resource-type)))

(deftype ResourceTypeGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "resourceType")
  (min-occurs [_] 1)
  (max-occurs [_] 1)
  (child-element-factories [_] [])

  (get-location [_]
    (str parent-location ".resourceType"))

  (validate [self {resource-type :value :as attribute}]
    (let [location (mdf/get-location self)]
      (util/validate-non-blank-string-attribute-value location resource-type)
      (get-resource-type-general location attribute)))

  (generate-nested [self {resource-type :value :as attribute}]
    (ResourceType. resource-type (get-resource-type-general (mdf/get-location self) attribute))))

(defn new-resource-type-generator [location]
  (ResourceTypeGenerator. location))
