(ns org.cyverse.metadata-files.datacite-4-2.creators
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-2.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.compound-nested-element :as compound-ne]
            [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.datacite-4-2.affiliation :as affiliation]
            [org.cyverse.metadata-files.datacite-4-2.name-identifier :as name-identifier]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The creator element

(defn- format-creator-name [creator-name]
  (element ::datacite/creatorName {} creator-name))

(defn new-creator-generator [location]
  (compound-ne/new-compound-nested-element-generator
   {:attr-name           "creator"
    :min-occurs          1
    :max-occurs          "unbounded"
    :format-fn           format-creator-name
    :element-factory-fns [name-identifier/new-name-identifier-generator
                          affiliation/new-affiliation-generator]
    :tag                 ::datacite/creator
    :parent-location     location}))

;; The creators element

(defn new-creators-generator [location]
  (cne/new-container-nested-element-generator
   {:element-factory-fns [new-creator-generator]
    :tag                 ::datacite/creators
    :parent-location     location}))
