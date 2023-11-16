(ns org.cyverse.metadata-files.datacite-4-2.related-identifiers
  (:use [org.cyverse.metadata-files.datacite-4-2.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.simple-nested-element :as sne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The relatedIdentifier attribute

(def ^:private valid-related-identifier-types
  #{"ARK" "arXiv" "bibcode" "DOI" "EAN13" "EISSN" "Handle" "IGSN" "ISBN" "ISSN" "ISTC" "LISSN" "LSID" "PMID"
    "PURL" "UPC" "URL" "URN" "w3id"})

(def ^:private valid-relation-types
  #{"IsCitedBy" "Cites" "IsSupplementTo" "IsSupplementedBy" "IsContinuedBy" "Continues" "IsNewVersionOf"
    "IsPreviousVersionOf" "IsPartOf" "HasPart" "IsReferencedBy" "References" "IsDocumentedBy" "Documents"
    "IsCompiledBy" "Compiles" "IsVariantFormOf" "IsOriginalFormOf" "IsIdenticalTo" "HasMetadata" "IsMetadataFor"
    "Reviews" "IsReviewedBy" "IsDerivedFrom" "IsSourceOf" "Describes" "IsDescribedBy" "HasVersion" "IsVersionOf"
    "Requires" "IsRequiredBy" "Obsoletes" "IsObsoletedBy"})

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

(defn- get-related-identifier-attrs [location attribute]
  {:relatedIdentifierType (get-related-identifier-type location attribute)
   :relationType          (get-relation-type location attribute)})

(defn new-related-identifier-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "relatedIdentifier"
    :min-occurs      0
    :max-occurs      "unbounded"
    :attrs-fn        get-related-identifier-attrs
    :tag             ::datacite/relatedIdentifier
    :parent-location location}))

;; The relatedIdentifiers attribute

(defn new-related-identifiers-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-related-identifier-generator]
    :tag                 ::datacite/relatedIdentifiers
    :parent-location     location}))
