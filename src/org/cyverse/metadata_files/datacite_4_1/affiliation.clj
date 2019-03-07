(ns org.cyverse.metadata-files.datacite-4-1.affiliation
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The affiliation element

(deftype Affiliation [affiliation]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/affiliation {} affiliation)))

(deftype AffiliationGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "affiliation")
  (min-occurs [_] 0)
  (max-occurs [_] "unbounded")
  (child-element-factories [_] [])
  (get-location [_] (str parent-location ".affiliation"))

  (validate [self {affiliation :value}]
    (util/validate-non-blank-string-attribute-value (mdf/get-location self) affiliation))

  (generate-nested [_ {affiliation :value :as attribute}]
    (Affiliation. affiliation)))

(defn new-affiliation-generator [location]
  (AffiliationGenerator. location))
