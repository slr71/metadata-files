(ns org.cyverse.metadata-files.datacite-4-2.publication-year
  (:use [org.cyverse.metadata-files.datacite-4-2.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.simple-nested-element :as sne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

(defn new-publication-year-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "publicationYear"
    :validation-fn   util/validate-year-attribute-value
    :tag             ::datacite/publicationYear
    :parent-location location}))
