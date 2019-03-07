(ns org.cyverse.metadata-files.datacite-4-1.creators
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.datacite-4-1.affiliation :as affiliation]
            [org.cyverse.metadata-files.datacite-4-1.name-identifier :as name-identifier]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The creator element

(deftype Creator [creator-name elements]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/creator {}
      (concat [(element ::datacite/creatorName {} creator-name)] (mapv mdf/to-xml elements)))))

(deftype CreatorGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "creator")
  (min-occurs [_] 1)
  (max-occurs [_] "unbounded")

  (child-element-factories [self]
    [(name-identifier/new-name-identifier-generator (mdf/get-location self))
     (affiliation/new-affiliation-generator (mdf/get-location self))])

  (get-location [_] (str parent-location ".creator"))

  (validate [self {avus :avus creator-name :value}]
    (let [location (mdf/get-location self)]
      (util/validate-non-blank-string-attribute-value location creator-name)
      (util/validate-attr-counts self avus)
      (util/validate-child-elements (mdf/child-element-factories self) avus)))

  (generate-nested [self {avus :avus creator-name :value :as attribute}]
    (Creator. creator-name (util/build-child-elements (mdf/child-element-factories self) avus))))

(defn new-creator-generator [location]
  (CreatorGenerator. location))

;; The creators element

(deftype Creators [creators]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/creators {} (mapv mdf/to-xml creators))))

(deftype CreatorsGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] nil)
  (min-occurs [_] 1)
  (max-occurs [_] 1)

  (child-element-factories [self]
    [(new-creator-generator (mdf/get-location self))])

  (get-location [_] (str parent-location))

  (validate [self attributes]
    (let [element-factories (mdf/child-element-factories self)]
      (util/validate-attr-counts self attributes)
      (util/validate-child-elements element-factories attributes)))

  (generate-nested [self attributes]
    (Creators. (util/build-child-elements (mdf/child-element-factories self) attributes))))

(defn new-creators-generator [location]
  (CreatorsGenerator. location))
