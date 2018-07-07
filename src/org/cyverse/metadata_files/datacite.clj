(ns org.cyverse.metadata-files.datacite
  (:use [clojure.data.xml :only [alias-uri element]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

;; Define aliases for XML namespaces.

(alias-uri :datacite "http://datacite.org/schema/kernel-4")
(alias-uri :xml "http://www.w3.org/XML/1998/namespace")

;; Required field: identifier

(deftype Identifier [type id]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/identifier {::datacite/identifierType type} id)))

(deftype IdentifierGenerator [attributes]
  mdf/ElementFactory
  (required-attributes [_]
    #{"Identifier" "identifierType"})

  (missing-attributes [self]
    (util/missing-attributes (mdf/required-attributes self) attributes))

  (generate [self]
    (if-let [missing (seq (mdf/missing-attributes self))]
      (util/missing-required-attributes missing)
      (Identifier. (util/attr-value attributes "identifierType") (util/attr-value attributes "identifier")))))

;; Required field: creators

(deftype Creator [name affiliation]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/creator {}
      [(element ::datacite/creatorName {} name)
       (element ::datacite/affiliation {} affiliation)])))

(deftype Creators [creators]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/creators {}
      (mapv mdf/to-xml creators))))

(deftype CreatorsGenerator [attributes]
  mdf/ElementFactory
  (required-attributes [_]
    #{"datacite.creator" "creatorAffiliation"})

  (missing-attributes [self]
    (util/missing-attributes (mdf/required-attributes self) attributes))

  (generate [self]
    (if-let [missing (seq (mdf/missing-attributes self))]
      (util/missing-required-attributes missing)
      (let [values (util/associated-attr-values attributes ["datacite.creator" "creatorAffiliation"])]
        (Creators. (mapv (fn [[name affiliation]] (Creator. name affiliation)) values))))))

;; Required field: title

(deftype Title [title]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/title {::xml/lang "en"} title)))

(deftype Titles [titles]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/titles {}
      (mapv mdf/to-xml titles))))

(deftype TitlesGenerator [attributes]
  mdf/ElementFactory
  (required-attributes [_]
    #{"datacite.title"})

  (missing-attributes [self]
    (util/missing-attributes (mdf/required-attributes self) attributes))

  (generate [self]
    (if-let [missing (seq (mdf/missing-attributes self))]
      (util/missing-required-attributes missing)
      (Titles. (mapv (fn [title] (Title. title)) (util/attr-values attributes "datacite.title"))))))

;; Required field: publisher

(deftype Publisher [publisher]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/publisher {} publisher)))

(deftype PublisherGenerator [attributes]
  mdf/ElementFactory
  (required-attributes [_]
    #{"datacite.publisher"})

  (missing-attributes [self]
    (util/missing-attributes (mdf/required-attributes self) attributes))

  (generate [self]
    (if-let [missing (seq (mdf/missing-attributes self))]
      (util/missing-required-attributes missing)
      (Publisher. (util/attr-value attributes "datacite.publisher")))))

;; Required field: publication year

(deftype PublicationYear [year]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/publicationYear {} year)))

(deftype PublicationYearGenerator [attributes]
  mdf/ElementFactory
  (required-attributes [_]
    #{"datacite.publicationyear"})

  (missing-attributes [self]
    (util/missing-attributes (mdf/required-attributes self) attributes))

  (generate [self]
    (if-let [missing (seq (mdf/missing-attributes self))]
      (util/missing-required-attributes missing)
      (PublicationYear. (util/attr-value attributes "datacite.publicationyear")))))

;; Required field: resource-type

(deftype ResourceType [resource-type]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/resourceType {} resource-type)))

(deftype ResourceTypeGenerator [attributes]
  mdf/ElementFactory
  (required-attributes [_]
    #{"datacite.resourcetype"})

  (missing-attributes [self]
    (util/missing-attributes (mdf/required-attributes self) attributes))

  (generate [self]
    (if-let [missing (seq (mdf/missing-attributes self))]
      (util/missing-required-attributes missing)
      (ResourceType. (util/attr-value attributes "datacite.resourcetype")))))

;; The datacite document itself.

(deftype Datacite [elements]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/resource {:xmlns/datacite "http://datacite.org/schema/kernel-4"}
      (mapv mdf/to-xml elements))))

(defn- required-element-factories [attributes]
  [(IdentifierGenerator. attributes)
   (CreatorsGenerator. attributes)
   (TitlesGenerator. attributes)
   (PublisherGenerator. attributes)
   (PublicationYearGenerator. attributes)
   (ResourceTypeGenerator. attributes)])

(defn- element-factories [attributes]
  (required-element-factories attributes))

(deftype DataciteGenerator [attributes]
  mdf/ElementFactory
  (required-attributes [_]
    (->> (required-element-factories attributes)
         (map mdf/required-attributes)
         (apply concat)
         set))

  (missing-attributes [self]
    (util/missing-attributes (mdf/required-attributes self) attributes))

  (generate [self]
    (if-let [missing (seq (mdf/missing-attributes self))]
      (util/missing-required-attributes missing)
      (Datacite. (remove nil? (mapv mdf/generate (element-factories attributes)))))))

(defn build-datacite [attributes]
  (mdf/to-xml (mdf/generate (DataciteGenerator. attributes))))
