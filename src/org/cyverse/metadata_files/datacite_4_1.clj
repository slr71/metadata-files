(ns org.cyverse.metadata-files.datacite-4-1
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files.datacite-4-1
             [contributors :as contributors]
             [creators :as creators]
             [identifier :as identifier]
             [language :as language]
             [publication-year :as publication-year]
             [publisher :as publisher]
             [resource-type :as resource-type]
             [subjects :as subjects]
             [titles :as titles]]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

(def ^:private schema-locations
  (->> ["http://datacite.org/schema/kernel-4 http://schema.datacite.org/meta/kernel-4.1/metadata.xsd"]
       (string/join " ")))

(deftype Datacite [elements]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/resource {:xmlns/datacite      "http://datacite.org/schema/kernel-4"
                                  :xmlns/xsi           "http://www.w3.org/2001/XMLSchema-instance"
                                  ::xsi/schemaLocation schema-locations}
      (mapv mdf/to-xml elements))))

(deftype DataciteGenerator []
  mdf/NestedElementFactory
  (attribute-name [_] nil)
  (min-occurs [_] 1)
  (max-occurs [_] 1)

  (child-element-factories [self]
    (let [location (mdf/get-location self)]
      [(identifier/new-identifier-generator location)
       (creators/new-creators-generator location)
       (titles/new-titles-generator location)
       (publisher/new-publisher-generator location)
       (publication-year/new-publication-year-generator location)
       (resource-type/new-resource-type-generator location)
       (subjects/new-subjects-generator location)
       (contributors/new-contributors-generator location)
       (language/new-language-generator location)]))

  (get-location [_] "")

  (validate [self attributes]
    (let [element-factories (mdf/child-element-factories self)]
      (util/validate-attr-counts self attributes)
      (util/validate-child-elements (mdf/child-element-factories self) attributes)))

  (generate-nested [self attributes]
    (Datacite. (util/build-child-elements (mdf/child-element-factories self) attributes))))

(defn build-datacite [attributes]
  (let [generator (DataciteGenerator.)]
    (mdf/validate generator attributes)
    (mdf/to-xml (mdf/generate-nested generator attributes))))
