(ns org.cyverse.metadata-files.datacite-4-1.name-identifier
  (:use [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.simple-nested-element :as sne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The nameIdentifier element

(defn- get-name-identifier-scheme [location {:keys [avus]}]
  (util/get-required-attribute-value location avus "nameIdentifierScheme"))

(defn- get-name-identifier-scheme-uri [_ {:keys [avus]}]
  (util/attr-value avus "schemeURI"))

(defn- get-name-identifier-attrs [location attribute]
  {:nameIdentifierScheme (get-name-identifier-scheme location attribute)
   :schemeURI            (get-name-identifier-scheme-uri location attribute)})

(defn new-name-identifier-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "nameIdentifier"
    :min-occurs      0
    :max-occurs      "unbounded"
    :attrs-fn        get-name-identifier-attrs
    :tag             ::datacite/nameIdentifier
    :parent-location location}))
