(ns org.cyverse.metadata-files.datacite-4-1.alternate-identifiers
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The alternateIdentifier element

(defn- get-alternate-identifier-type [location {:keys [avus]}]
  (util/get-required-attribute-value location avus "alternateIdentifierType"))

(deftype AlternateIdentifier [alternate-identifier alternate-identifier-type]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/alternateIdentifier {:alternateIdentifierType alternate-identifier-type}
      alternate-identifier)))

(deftype AlternateIdentifierGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "alternateIdentifier")
  (min-occurs [_] 0)
  (max-occurs [_] "unbounded")
  (get-location [_] (str parent-location ".alternateIdentifier"))
  (child-element-factories [_] [])

  (validate [self {alternate-identifier :value :as attribute}]
    (let [location (mdf/get-location self)]
      (util/validate-non-blank-string-attribute-value location alternate-identifier)
      (get-alternate-identifier-type location attribute)))

  (generate-nested [self {alternate-identifier :value :as attribute}]
    (let [location (mdf/get-location self)]
      (AlternateIdentifier. alternate-identifier (get-alternate-identifier-type location attribute)))))

(defn new-alternate-identifier-generator [location]
  (AlternateIdentifierGenerator. location))

;; The alternateIdentifiers element

(deftype AlternateIdentifiers [alternate-identifiers]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/alternateIdentifiers {} (mapv mdf/to-xml alternate-identifiers))))

(deftype AlternateIdentifiersGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] nil)
  (min-occurs [_] 0)
  (max-occurs [_] 1)
  (get-location [_] parent-location)

  (child-element-factories [self]
    [(new-alternate-identifier-generator (mdf/get-location self))])

  (validate [self attributes]
    (let [element-factories (mdf/child-element-factories self)]
      (util/validate-attr-counts self attributes)
      (util/validate-child-elements element-factories attributes)))

  (generate-nested [self attributes]
    (when-let [alternate-identifiers (seq (util/build-child-elements (mdf/child-element-factories self) attributes))]
      (AlternateIdentifiers. alternate-identifiers))))

(defn new-alternate-identifiers-generator [location]
  (AlternateIdentifiersGenerator. location))
