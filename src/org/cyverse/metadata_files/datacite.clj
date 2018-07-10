(ns org.cyverse.metadata-files.datacite
  (:use [clojure.data.xml :only [alias-uri element]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

;; Define aliases for XML namespaces.

(alias-uri :datacite "http://datacite.org/schema/kernel-4")
(alias-uri :xml "http://www.w3.org/XML/1998/namespace")
(alias-uri :xsi "http://www.w3.org/2001/XMLSchema-instance")

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

(deftype Creator [name affiliation name-id]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/creator {}
      (remove nil? [(element ::datacite/creatorName {} name)
                    (when-not (string/blank? name-id) (element ::datacite/nameIdentifier {} name-id))
                    (element ::datacite/affiliation {} affiliation)]))))

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
      (let [values (util/associated-attr-values
                    attributes ["datacite.creator" "creatorAffiliation"] ["creatorNameIdentifier"])]
        (Creators. (mapv (fn [[name affiliation name-id]] (Creator. name affiliation name-id)) values))))))

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

;; Optional field: subjects

(deftype Subject [subject]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/subject {::xml/lang "en"} subject)))

(deftype Subjects [subjects]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/subjects {}
      (mapv mdf/to-xml subjects))))

(deftype SubjectsGenerator [attributes]
  mdf/ElementFactory
  (required-attributes [_]
    #{})

  (missing-attributes [_]
    #{})

  (generate [self]
    (when-let [values (seq (util/attr-values attributes "Subject"))]
      (Subjects. (->> (mapcat (fn [s] (string/split s #"\s*,\s*")) values)
                      (mapv (fn [subject] (Subject. subject))))))))

;; Optional field: contributors

(deftype Contributor [name type]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/contributor {}
      [(element ::datacite/contributorName {} name)
       (element ::datacite/contributorType {} type)])))

(deftype Contributors [contributors]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/contributors {}
      (mapv mdf/to-xml contributors))))

(deftype ContributorsGenerator [attributes]
  mdf/ElementFactory
  (required-attributes [_]
    #{})

  (missing-attributes [_]
    #{})

  (generate [self]
    (when-let [values (seq (util/associated-attr-values attributes ["contributorName" "contributorType"] []))]
      (Contributors. (mapv (fn [[name type]] (Contributor. name type)) values)))))

;; Optional field: alternate identifiers

(deftype AlternateId [id type]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/alternateIdentifier {::datacite/alternateIdentifierType type} id)))

(deftype AlternateIds [alternate-ids]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/alternateIdentifiers {}
      (mapv mdf/to-xml alternate-ids))))

(deftype AlternateIdsGenerator [attributes]
  mdf/ElementFactory
  (required-attributes [_]
    #{})

  (missing-attributes [_]
    #{})

  (generate [self]
    (let [required-attrs ["alternateIdentifier" "alternateIdentifierType"]]
      (when-let [vs (seq (util/associated-attr-values attributes required-attrs []))]
        (AlternateIds. (mapv (fn [[id type]] (AlternateId. id type)) vs))))))

;; Optional field: related identifiers

(deftype RelatedId [id type relation-type]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/relatedIdentifier {::datacite/relatedIdentifierType type ::datacite/relationType relation-type}
      id)))

(deftype RelatedIds [related-ids]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/relatedIdentifiers {}
      (mapv mdf/to-xml related-ids))))

(deftype RelatedIdsGenerator [attributes]
  mdf/ElementFactory
  (required-attributes [_]
    #{})

  (missing-attributes [_]
    #{})

  (generate [self]
    (let [required-attrs ["relatedIdentifier" "relatedIdentifierType" "relationType"]]
      (when-let [vs (seq (util/associated-attr-values attributes required-attrs []))]
        (RelatedIds. (mapv (fn [[id type relation-type]] (RelatedId. id type relation-type)) vs))))))

;; The datacite document itself.

(def ^:private schema-locations
  (->> ["https://schema.datacite.org/meta/kernel-4.1 https://schema.datacite.org/meta/kernel-4.1/metadata.xsd"]
       (string/join " ")))

(deftype Datacite [elements]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/resource {:xmlns/datacite      "http://datacite.org/schema/kernel-4"
                                  :xmlns/xsi           "http://www.w3.org/2001/XMLSchema-instance"
                                  ::xsi/schemaLocation schema-locations}
      (mapv mdf/to-xml elements))))

(defn- required-element-factories [attributes]
  [(IdentifierGenerator. attributes)
   (CreatorsGenerator. attributes)
   (TitlesGenerator. attributes)
   (PublisherGenerator. attributes)
   (PublicationYearGenerator. attributes)
   (ResourceTypeGenerator. attributes)])

(defn- optional-element-factories [attributes]
  [(SubjectsGenerator. attributes)
   (ContributorsGenerator. attributes)
   (AlternateIdsGenerator. attributes)
   (RelatedIdsGenerator. attributes)])

(defn- element-factories [attributes]
  (concat (required-element-factories attributes)
          (optional-element-factories attributes)))

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
