(ns org.cyverse.metadata-files.datacite-4-2.titles
  (:use [org.cyverse.metadata-files.datacite-4-2.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.simple-nested-element :as sne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The title element

(def ^:private valid-title-types #{"AlternativeTitle" "Subtitle" "TranslatedTitle" "Other"})

(defn- get-title-type [location {:keys [avus]}]
  (let [attribute-name "titleType"
        title-type     (util/attr-value avus attribute-name)]
    (when-not (string/blank? title-type)
      (when-not (valid-title-types title-type)
        (throw (ex-info (str "Invalid " attribute-name " value.")
                        {:location     location
                         :attribute    attribute-name
                         :value        title-type
                         :valid-values valid-title-types})))
      title-type)))

(defn- get-title-attrs [location {:keys [avus] :as attribute}]
  {:titleType (get-title-type location attribute)
   ::xml/lang (util/get-language avus)})

(defn new-title-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "title"
    :max-occurs      "unbounded"
    :attrs-fn        get-title-attrs
    :tag             ::datacite/title
    :parent-location location}))

;; The titles element

(defn new-titles-generator [location]
  (cne/new-container-nested-element-generator
   {:element-factory-fns [new-title-generator]
    :tag                 ::datacite/titles
    :parent-location     location}))
