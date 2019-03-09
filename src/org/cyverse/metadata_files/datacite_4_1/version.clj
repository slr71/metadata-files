(ns org.cyverse.metadata-files.datacite-4-1.version
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

(deftype Version [version]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/version {} version)))

(deftype VersionGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "version")
  (min-occurs [_] 0)
  (max-occurs [_] 1)
  (get-location [_] (str parent-location ".version"))
  (child-element-factories [_] [])

  (validate [self {version :value}]
    (util/validate-non-blank-string-attribute-value (mdf/get-location self) version))

  (generate-nested [_ {version :value}]
    (Version. version)))

(defn new-version-generator [location]
  (VersionGenerator. location))
