(ns org.cyverse.metadata-files.datacite-4-1.rights-list
  (:use [medley.core :only [remove-vals]]
        [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The rights element

(defn- get-rights-uri [location {:keys [avus]}]
  (util/attr-value avus "rightsURI"))

(deftype Rights [rights rights-uri]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/rights (remove-vals string/blank? {:rightsURI rights-uri})
      rights)))

(deftype RightsGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "rights")
  (min-occurs [_] 0)
  (max-occurs [_] "unbounded")
  (get-location [_] (str parent-location ".rights"))
  (child-element-factories [_] [])

  (validate [self {rights :value :as attribute}]
    (let [location (mdf/get-location self)]
      (util/validate-non-blank-string-attribute-value location rights)
      (get-rights-uri location attribute)))

  (generate-nested [self {rights :value :as attribute}]
    (Rights. rights (get-rights-uri (mdf/get-location self) attribute))))

(defn new-rights-generator [location]
  (RightsGenerator. location))

;; The rightsList element

(defn new-rights-list-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-rights-generator]
    :tag                 ::datacite/rightsList
    :parent-location     location}))
