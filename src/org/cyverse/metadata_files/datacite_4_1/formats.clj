(ns org.cyverse.metadata-files.datacite-4-1.formats
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The format element

(deftype Format [format]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/format {} format)))

(deftype FormatGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "format")
  (min-occurs [_] 0)
  (max-occurs [_] "unbounded")
  (get-location [_] (str parent-location ".format"))
  (child-element-factories [_] [])

  (validate [self {format :value}]
    (util/validate-non-blank-string-attribute-value (mdf/get-location self) format))

  (generate-nested [self {format :value}]
    (Format. format)))

(defn new-format-generator [location]
  (FormatGenerator. location))

;; The formats element

(defn new-formats-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-format-generator]
    :tag                 ::datacite/formats
    :parent-location     location}))
