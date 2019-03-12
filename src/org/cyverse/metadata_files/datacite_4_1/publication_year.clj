(ns org.cyverse.metadata-files.datacite-4-1.publication-year
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.simple-nested-element :as sne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

(defn new-publication-year-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "publicationYear"
    :validation-fn   util/validate-year-attribute-value
    :tag             ::datacite/publicationYear
    :parent-location location}))
