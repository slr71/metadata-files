(ns org.cyverse.metadata-files.datacite-4-1.language
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.simple-nested-element :as sne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

(defn new-language-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "language"
    :min-occurs      0
    :validation-fn   (constantly nil)
    :tag             ::datacite/language
    :parent-location location}))
