(ns org.cyverse.metadata-files.datacite-4-1.formats
  (:use [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.simple-nested-element :as sne]))

(alias-uris)

;; The format element

(defn new-format-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "format"
    :min-occurs      0
    :max-occurs      "unbounded"
    :tag             ::datacite/format
    :parent-location location}))

;; The formats element

(defn new-formats-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-format-generator]
    :tag                 ::datacite/formats
    :parent-location     location}))
