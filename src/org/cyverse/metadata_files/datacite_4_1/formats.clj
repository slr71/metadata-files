(ns org.cyverse.metadata-files.datacite-4-1.formats
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
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

(deftype Formats [formats]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/formats {} (mapv mdf/to-xml formats))))

(deftype FormatsGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] nil)
  (min-occurs [_] 0)
  (max-occurs [_] 1)
  (get-location [_] parent-location)

  (child-element-factories [self]
    [(new-format-generator (mdf/get-location self))])

  (validate [self attributes]
    (let [element-factories (mdf/child-element-factories self)]
      (util/validate-attr-counts self attributes)
      (util/validate-child-elements element-factories attributes)))

  (generate-nested [self attributes]
    (when-let [formats (seq (util/build-child-elements (mdf/child-element-factories self) attributes))]
      (Formats. formats))))

(defn new-formats-generator [location]
  (FormatsGenerator. location))
