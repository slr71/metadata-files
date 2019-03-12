(ns org.cyverse.metadata-files.datacite-4-1.related-identifiers
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The relatedIdentifier attribute

(def ^:private valid-related-identifier-types
  #{"ARK" "arXiv" "bibcode" "DOI" "EAN13" "EISSN" "Handle" "IGSN" "ISBN" "ISSN" "ISTC" "LISSN" "LSID" "PMID"
    "PURL" "UPC" "URL" "URN"})

(def ^:private valid-relation-types
  #{"IsCitedBy" "Cites" "IsSupplementTo" "IsSupplementedBy" "IsContinuedBy" "Continues" "IsNewVersionOf"
    "IsPreviousVersionOf" "IsPartOf" "HasPart" "IsReferencedBy" "References" "IsDocumentedBy" "Documents"
    "IsCompiledBy" "Compiles" "IsVariantFormOf" "IsOriginalFormOf" "IsIdenticalTo" "HasMetadata" "IsMetadataFor"
    "Reviews" "IsReviewedBy" "IsDerivedFrom" "IsSourceOf" "Describes" "IsDescribedBy" "HasVersion" "IsVersionOf"
    "Requires" "IsRequiredBy"})

(defn- get-related-identifier-type [location {:keys [avus]}]
  (let [attribute-name          "relatedIdentifierType"
        related-identifier-type (util/get-required-attribute-value location avus attribute-name)]
    (when-not (valid-related-identifier-types related-identifier-type)
      (throw (ex-info (str "Invalid " attribute-name " value.")
                      {:location     location
                       :attribute    attribute-name
                       :value        related-identifier-type
                       :valid-values valid-related-identifier-types})))
    related-identifier-type))

(defn- get-relation-type [location {:keys [avus]}]
  (let [attribute-name "relationType"
        relation-type  (util/get-required-attribute-value location avus attribute-name)]
    (when-not (valid-relation-types relation-type)
      (throw (ex-info (str "Invalid " attribute-name " value.")
                      {:location     location
                       :attribute    attribute-name
                       :value        relation-type
                       :valid-values valid-relation-types})))
    relation-type))

(deftype RelatedIdentifier [related-identifier related-identifier-type relation-type]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/relatedIdentifier {:relatedIdentifierType related-identifier-type :relationType relation-type}
      related-identifier)))

(deftype RelatedIdentifierGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "relatedIdentifier")
  (min-occurs [_] 0)
  (max-occurs [_] "unbounded")
  (get-location [self] (str parent-location ".relatedIdentifier"))
  (child-element-factories [_] [])

  (validate [self {related-identifier :value :as attribute}]
    (let [location (mdf/get-location self)]
      (util/validate-non-blank-string-attribute-value location related-identifier)
      (get-related-identifier-type location attribute)
      (get-relation-type location attribute)))

  (generate-nested [self {related-identifier :value :as attribute}]
    (let [location (mdf/get-location self)]
      (RelatedIdentifier. related-identifier
                          (get-related-identifier-type location attribute)
                          (get-relation-type location attribute)))))

(defn new-related-identifier-generator [location]
  (RelatedIdentifierGenerator. location))

;; The relatedIdentifiers attribute

(defn new-related-identifiers-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-related-identifier-generator]
    :tag                 ::datacite/relatedIdentifiers
    :parent-location     location}))
