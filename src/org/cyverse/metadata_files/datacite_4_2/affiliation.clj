(ns org.cyverse.metadata-files.datacite-4-2.affiliation
  (:use [org.cyverse.metadata-files.datacite-4-2.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.simple-nested-element :as sne]))

(alias-uris)

;; The affiliation element

(defn new-affiliation-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "affiliation"
    :min-occurs      0
    :max-occurs      "unbounded"
    :tag             ::datacite/affiliation
    :parent-location location}))
