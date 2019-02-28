(ns org.cyverse.metadata-files.datacite-4-1
  (:use [clojure.data.xml :only [alias-uri element]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

;; Define aliases for XML namespaces.

(alias-uri :datacite "http://datacite.org/schema/kernel-4")
(alias-uri :xml "http://www.w3.org/XML/1998/namespace")
(alias-uri :xsi "http://www.w3.org/2001/XMLSchema-instance")

;; The datacite document itself.

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

(defn- required-element-factories [attributes]
  [])

(defn- optional-element-factories [attributes]
  [])

(defn- element-factories [attributes]
  (concat (required-element-factories attributes)
          (optional-element-factories attributes)))

(deftype DataciteGenerator [attributes]
  mdf/ElementFactory
  (required-attributes [_]
    (->> (required-element-factories attributes)
         (mapcat mdf/required-attributes)
         set))

  (missing-attributes [self]
    (util/missing-attributes (mdf/required-attributes self) attributes))

  (generate [self]
    (if-let [missing (seq (mdf/missing-attributes self))]
      (util/missing-required-attributes missing)
      (Datacite. (remove nil? (mapv mdf/generate (element-factories attributes)))))))

(defn build-datacite [attributes]
  (mdf/to-xml (mdf/generate (DataciteGenerator. attributes))))
