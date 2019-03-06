(ns org.cyverse.metadata-files.datacite-4-1.creators
  (:use [medley.core :only [remove-vals]]
        [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The affiliation element

(deftype Affiliation [affiliation]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/affiliation {} affiliation)))

(deftype AffiliationGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "affiliation")
  (min-occurs [_] 0)
  (max-occurs [_] "unbounded")
  (child-element-factories [_] [])
  (get-location [_] (str parent-location ".affiliation"))

  (validate [self {affiliation :value}]
    (util/validate-non-blank-string-attribute-value (mdf/get-location self) affiliation))

  (generate-nested [_ {affiliation :value :as attribute}]
    (Affiliation. affiliation)))

(defn new-affiliation-generator [location]
  (AffiliationGenerator. location))

;; The nameIdentifier element

(defn- get-name-identifier-scheme [location {:keys [avus]}]
  (util/get-required-attribute-value location avus "nameIdentifierScheme"))

(defn- get-name-identifier-scheme-uri [_ {:keys [avus]}]
  (util/attr-value avus "schemeURI"))

(deftype NameIdentifier [name-identifier scheme scheme-uri]
  mdf/XmlSerializable
  (to-xml [_]
    (let [attrs (remove-vals string/blank? {:nameIdentifierScheme scheme :schemeURI scheme-uri})]
      (element ::datacite/nameIdentifier attrs name-identifier))))

(deftype NameIdentifierGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "nameIdentifier")
  (min-occurs [_] 0)
  (max-occurs [_] "unbounded")
  (child-element-factories [_] [])
  (get-location [_] (str parent-location ".nameIdentifier"))

  (validate [self {name-identifier :value :as attribute}]
    (let [location (mdf/get-location self)]
      (util/validate-non-blank-string-attribute-value location name-identifier)
      (get-name-identifier-scheme location attribute)
      (get-name-identifier-scheme-uri location attribute)))

  (generate-nested [self {name-identifier :value :as attribute}]
    (let [location (mdf/get-location self)]
      (NameIdentifier. name-identifier
                       (get-name-identifier-scheme location attribute)
                       (get-name-identifier-scheme-uri location attribute)))))

(defn new-name-identifier-generator [location]
  (NameIdentifierGenerator. location))

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
    [(new-name-identifier-generator (mdf/get-location self))
     (new-affiliation-generator (mdf/get-location self))])

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
