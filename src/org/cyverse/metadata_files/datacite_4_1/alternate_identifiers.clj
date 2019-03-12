(ns org.cyverse.metadata-files.datacite-4-1.alternate-identifiers
  (:use [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.simple-nested-element :as sne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The alternateIdentifier element

(defn- get-alternate-identifier-type [location {:keys [avus]}]
  (util/get-required-attribute-value location avus "alternateIdentifierType"))

(defn- get-alternate-identifier-attrs [location attribute]
  {:alternateIdentifierType (get-alternate-identifier-type location attribute)})

(defn new-alternate-identifier-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "alternateIdentifier"
    :min-occurs      0
    :max-occurs      "unbounded"
    :attrs-fn        get-alternate-identifier-attrs
    :tag             ::datacite/alternateIdentifier
    :parent-location location}))

;; The alternateIdentifiers element

(defn new-alternate-identifiers-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-alternate-identifier-generator]
    :tag                 ::datacite/alternateIdentifiers
    :parent-location     location}))
