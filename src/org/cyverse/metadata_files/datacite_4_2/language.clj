(ns org.cyverse.metadata-files.datacite-4-2.language
  (:use [org.cyverse.metadata-files.datacite-4-2.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.simple-nested-element :as sne]))

(alias-uris)

(defn new-language-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "language"
    :min-occurs      0
    :validation-fn   (constantly nil)
    :tag             ::datacite/language
    :parent-location location}))
