(ns org.cyverse.metadata-files.datacite-4-1.publication-year
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

(deftype PublicationYear [publication-year]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/publicationYear {} publication-year)))

(deftype PublicationYearGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "publicationYear")
  (min-occurs [_] 1)
  (max-occurs [_] 1)
  (child-element-factories [_] [])
  (get-location [_] (str parent-location ".publicationYear"))

  (validate [self {publication-year :value}]
    (util/validate-year-attribute-value (mdf/get-location self) publication-year))

  (generate-nested [self {publication-year :value}]
    (PublicationYear. publication-year)))

(defn new-publication-year-generator [location]
  (PublicationYearGenerator. location))
