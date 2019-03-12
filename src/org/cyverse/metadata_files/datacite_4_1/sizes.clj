(ns org.cyverse.metadata-files.datacite-4-1.sizes
  (:use [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.simple-nested-element :as sne]))

(alias-uris)

;; The size element

(defn new-size-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "size"
    :min-occurs      0
    :max-occurs      "unbounded"
    :tag             ::datacite/size
    :parent-location location}))

;; The sizes element

(defn new-sizes-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-size-generator]
    :tag                 ::datacite/sizes
    :parent-location     location}))
