(ns org.cyverse.metadata-files.datacite-4-2.publisher
  (:use [org.cyverse.metadata-files.datacite-4-2.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.simple-nested-element :as sne]))

(alias-uris)

(defn new-publisher-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "publisher"
    :tag             ::datacite/publisher
    :parent-location location}))
