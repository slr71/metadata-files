(ns org.cyverse.metadata-files.datacite-4-1.rights-list
  (:use [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.simple-nested-element :as sne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The rights element

(defn- get-rights-attrs [_ {:keys [avus]}]
  {:rightsURI (util/attr-value avus "rightsURI")})

(defn new-rights-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "rights"
    :min-occurs      0
    :max-occurs      "unbounded"
    :attrs-fn        get-rights-attrs
    :tag             ::datacite/rights
    :parent-location location}))

;; The rightsList element

(defn new-rights-list-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-rights-generator]
    :tag                 ::datacite/rightsList
    :parent-location     location}))
