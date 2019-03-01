(ns org.cyverse.metadata-files.datacite-4-1.identifier
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

(deftype Identifier [id type]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/identifier {:identifierType type} (string/replace id #"^doi:" ""))))

(defn- get-identifier-type [location {:keys [avus]}]
  (util/get-required-attribute-value location avus "identifierType"))

(deftype IdentifierGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "identifier")
  (min-occurs [_] 1)
  (max-occurs [_] 1)
  (child-element-factories [_] [])
  (get-location [_] (str parent-location ".identifier"))

  (validate [self attribute]
    (get-identifier-type (mdf/get-location self) attribute))

  (generate-nested [self attribute]
    (mdf/validate self attribute)
    (Identifier. (:value attribute)
                 (get-identifier-type (mdf/get-location self) attribute))))

(defn new-identifier-generator [location]
  (IdentifierGenerator. location))
