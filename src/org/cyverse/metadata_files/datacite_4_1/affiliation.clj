(ns org.cyverse.metadata-files.datacite-4-1.affiliation
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.simple-nested-element :as sne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The affiliation element

(defn new-affiliation-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "affiliation"
    :min-occurs      0
    :max-occurs      "unbounded"
    :tag             ::datacite/affiliation
    :parent-location location}))
