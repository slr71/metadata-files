(ns org.cyverse.metadata-files.datacite-4-1.publisher
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

(deftype Publisher [publisher]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/publisher {} publisher)))

(deftype PublisherGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "publisher")
  (min-occurs [_] 1)
  (max-occurs [_] 1)
  (child-element-factories [_] [])
  (get-location [_] (str parent-location ".publisher"))

  (validate [self {publisher :value}]
    (util/validate-non-blank-string-attribute-value (mdf/get-location self) publisher))

  (generate-nested [self {publisher :value :as attribute}]
    (mdf/validate self attribute)
    (Publisher. publisher)))

(defn new-publisher-generator [location]
  (PublisherGenerator. location))
