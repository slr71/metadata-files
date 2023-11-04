(ns org.cyverse.metadata-files.datacite-4-2.resource-type
  (:use [org.cyverse.metadata-files.datacite-4-2.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.simple-nested-element :as sne]
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

(defn- get-resource-type-attrs [location attribute]
  {:resourceTypeGeneral (get-resource-type-general location attribute)})

(defn new-resource-type-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "resourceType"
    :attrs-fn        get-resource-type-attrs
    :tag             ::datacite/resourceType
    :parent-location location}))
