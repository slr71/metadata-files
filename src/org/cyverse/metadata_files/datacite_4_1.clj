(ns org.cyverse.metadata-files.datacite-4-1
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files.datacite-4-1
             [alternate-identifiers :as alternate-identifiers]
             [contributors :as contributors]
             [creators :as creators]
             [descriptions :as descriptions]
             [formats :as formats]
             [funding-references :as funding-references]
             [geo-locations :as geo-locations]
             [identifier :as identifier]
             [language :as language]
             [publication-year :as publication-year]
             [publisher :as publisher]
             [related-identifiers :as related-identifiers]
             [resource-type :as resource-type]
             [rights-list :as rights-list]
             [sizes :as sizes]
             [subjects :as subjects]
             [titles :as titles]
             [version :as version]]
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
       (language/new-language-generator location)
       (alternate-identifiers/new-alternate-identifiers-generator location)
       (related-identifiers/new-related-identifiers-generator location)
       (sizes/new-sizes-generator location)
       (formats/new-formats-generator location)
       (version/new-version-generator location)
       (rights-list/new-rights-list-generator location)
       (descriptions/new-descriptions-generator location)
       (geo-locations/new-geo-locations-generator location)
       (funding-references/new-funding-references-generator location)]))

  (get-location [_] "")

  (validate [self attributes]
    (let [element-factories (mdf/child-element-factories self)]
      (util/validate-attr-counts self attributes)
      (util/validate-child-elements element-factories attributes)))

  (generate-nested [self attributes]
    (Datacite. (util/build-child-elements (mdf/child-element-factories self) attributes))))

(defn build-datacite [attributes]
  (let [generator (DataciteGenerator.)]
    (mdf/validate generator attributes)
    (mdf/to-xml (mdf/generate-nested generator attributes))))
