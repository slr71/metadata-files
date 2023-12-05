(ns org.cyverse.metadata-files.datacite-4-2.funding-references
  (:use [org.cyverse.metadata-files.datacite-4-2.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.simple-nested-element :as sne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The funderIdentifier element

(def ^:private valid-funder-identifier-types #{"ISNI" "GRID" "Crossref Funder ID" "Other"})

(defn- get-funder-identifier-type [location {:keys [avus]}]
  (let [attribute-name         "funderIdentifierType"
        funder-identifier-type (util/get-required-attribute-value location avus attribute-name)]
    (when-not (valid-funder-identifier-types funder-identifier-type)
      (throw (ex-info (str "Invalid " attribute-name " value.")
                      {:location     location
                       :attribute    attribute-name
                       :value        funder-identifier-type
                       :valid-values valid-funder-identifier-types})))
    funder-identifier-type))

(defn- get-funder-identifier-attrs [location attribute]
  {:funderIdentifierType (get-funder-identifier-type location attribute)})

(defn new-funder-identifier-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "funderIdentifier"
    :min-occurs      0
    :attrs-fn        get-funder-identifier-attrs
    :tag             ::datacite/funderIdentifier
    :parent-location location}))

;; The funderName element

(defn new-funder-name-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "funderName"
    :tag             ::datacite/funderName
    :parent-location location}))

;; The fundingReference element

(defn new-funding-reference-generator [location]
  (cne/new-container-nested-element-generator
   {:attr-name           "fundingReference"
    :min-occurs          0
    :max-occurs          "unbounded"
    :element-factory-fns [new-funder-name-generator
                          new-funder-identifier-generator]
    :tag                 ::datacite/fundingReference
    :parent-location     location}))

;; The fundingReferences element

(defn new-funding-references-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-funding-reference-generator]
    :tag                 ::datacite/fundingReferences
    :parent-location     location}))
