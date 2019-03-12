(ns org.cyverse.metadata-files.datacite-4-1.identifier
  (:use [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files.simple-nested-element :as sne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

(defn- get-identifier-type [location {:keys [avus]}]
  (util/get-required-attribute-value location avus "identifierType"))

(defn- get-identifier-attrs [location attribute]
  {:identifierType (get-identifier-type location attribute)})

(defn new-identifier-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "identifier"
    :attrs-fn        get-identifier-attrs
    :format-fn       (fn [id] (string/replace (string/trim id) #"^doi:" ""))
    :tag             ::datacite/identifier
    :parent-location location}))
