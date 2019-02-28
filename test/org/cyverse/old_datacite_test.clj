(ns org.cyverse.old-datacite-test
  (:require [clojure.data.xml :refer :all]
            [clojure.string :as string]
            [clojure.test :refer :all]
            [org.cyverse.old-metadata-files :refer :all]
            [org.cyverse.old-metadata-files.datacite :refer [build-datacite]]
            [org.cyverse.old-metadata-files.test-utils :refer [build-attributes test-xml]])
  (:import [java.util.regex Pattern]))

(def ^:private min-attrs
  {"Identifier"               "   doi:10.1000/182"
   "identifierType"           "DOI"
   "datacite.creator"         "Nobody Inparticular"
   "creatorAffiliation"       "The University of Nowhere"
   "datacite.title"           "A Very Important Data Set"
   "datacite.publisher"       "CyVerse Data Commons"
   "datacite.publicationyear" "2018"
   "datacite.resourcetype"    "Data Set"})

(def ^:private schema-url
  "https://schema.datacite.org/meta/kernel-3.1/metadata.xsd")

(defn- test-datacite [file attrs]
  (test-xml file (build-datacite (build-attributes attrs)) schema-url))

(defn- debug-datacite [file attrs]
  (test-xml file (build-datacite (build-attributes attrs)) schema-url true))

(defn- test-missing-fields* [field-names attrs]
  (let [spec (str "(:?" (string/join "|" (map #(Pattern/quote %) field-names)) "(?:,\\s*)?{" (count field-names) "})")]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          (re-pattern (str "Missing required attributes: " spec))
                          (build-datacite (build-attributes attrs))))))

(defn- test-missing-fields [& field-names]
  (test-missing-fields* field-names (apply dissoc min-attrs field-names)))

(defn- test-blank-fields [& field-names]
  (test-missing-fields* field-names (apply assoc min-attrs (mapcat #(vector % "") field-names))))

(defn- test-exception [msg-regex attrs]
  (is (thrown-with-msg? clojure.lang.ExceptionInfo msg-regex (build-datacite (build-attributes attrs)))))

(deftest test-minimal
  (testing "Minimal DataCite file."
    (test-datacite "old-datacite/minimal.xml" min-attrs)))

(deftest test-missing-identifier
  (testing "DataCite file generation with missing identifier."
    (test-missing-fields "Identifier")
    (test-blank-fields "Identifier")))

(deftest test-missing-identifier-type
  (testing "DataCite file generation with missing identifier type."
    (test-missing-fields "identifierType")
    (test-blank-fields "identifierType")))

(deftest test-missing-creator
  (testing "DataCite file generation with missing creator."
    (test-missing-fields "datacite.creator")
    (test-blank-fields "datacite.creator")))

(deftest test-missing-creator-affiliation
  (testing "DataCite file generation with missing creator affiliation."
    (test-missing-fields "creatorAffiliation")
    (test-blank-fields "creatorAffiliation")))

(deftest test-missing-title
  (testing "DataCite file generation with missing title."
    (test-missing-fields "datacite.title")
    (test-blank-fields "datacite.title")))

(deftest test-missing-publisher
  (testing "DataCite file generation with missing publisher."
    (test-missing-fields "datacite.publisher")
    (test-blank-fields "datacite.publisher")))

(deftest test-missing-publication-year
  (testing "DataCite file generation with missing publication year."
    (test-missing-fields "datacite.publicationyear")
    (test-blank-fields "datacite.publicationyear")))

(deftest test-missing-resource-type
  (testing "DataCite file generation with missing resource type."
    (test-missing-fields "datacite.resourcetype")
    (test-blank-fields "datacite.resourcetype")))

(deftest test-all-missing-fields
  (testing "DataCite file generateion with all required fields missing."
    (apply test-missing-fields (keys min-attrs))
    (apply test-blank-fields (keys min-attrs))))

(deftest test-creator-with-name-id
  (testing "DataCite file with creator name identifier."
    (test-datacite "old-datacite/creator-name-id.xml" (assoc min-attrs "creatorNameIdentifier" "0000-0000-0000-0000"))))

(deftest test-multiple-creators-without-name-id
  (testing "DataCite file with multiple creators without the name identifier."
    (->> [["datacite.creator" "Somebody Else"]
          ["creatorAffiliation" "University of Somewhere"]]
         (concat min-attrs)
         (test-datacite "old-datacite/multiple-creators.xml"))))

(deftest test-group-with-different-numbers-of-required-attr-values
  (testing "DataCite file for metadata group with different numbers of required attribute values"
    (->> [["datacite.creator" "Somebody Else"]]
         (concat min-attrs)
         (test-exception #"^These attributes must have the same number of values"))))

(deftest test-group-with-more-optional-attr-values-than-required-attr-values
  (testing "DataCite file for metadata group with more optional attribute values than required attribute values."
    (->> [["creatorNameIdentifier" "0000-0000-0000-0000"]
          ["creatorNameIdentifier" "0000-0000-0000-0001"]]
         (concat min-attrs)
         (test-exception #"^None of the attributes, (?:[^,]+, )+may have more values than any of the attributes"))))

(deftest test-subject
  (testing "DataCite file generation with subjects."
    (->> [["Subject" "foo,bar,baz"]
          ["Subject" "quux"]
          ["Subject" "blrfl"]]
         (concat min-attrs)
         (test-datacite "old-datacite/subjects.xml"))))

(deftest test-contributors
  (testing "DataCite file generation with contributors."
    (->> [["contributorName" "Somebody Else"]
          ["contributorType" "DataCollector"]
          ["contributorName" "Nobody Else"]
          ["contributorType" "Editor"]]
         (concat min-attrs)
         (test-datacite "old-datacite/contributors.xml"))))

(deftest test-alternate-identifiers
  (testing "DataCite file generation with alternate identifiers."
    (->> [["AlternateIdentifier" "the-alternate-id"]
          ["alternateIdentifierType" "ARK"]
          ["AlternateIdentifier" "the-other-alternate-id"]
          ["alternateIdentifierType" "FOOID"]]
         (concat min-attrs)
         (test-datacite "old-datacite/alternate-ids.xml"))))

(deftest test-empty-optional-repeating-field
  (testing "DataCite file generation with empty optional repeating fields."
    (->> [["AlternateIdentifier" ""]
          ["alternateIdentifierType" ""]
          ["AlternateIdentifier" ""]
          ["alternateIdentifierType" ""]]
         (concat min-attrs)
         (test-datacite "old-datacite/minimal.xml"))))

(deftest test-related-identifiers
  (testing "DataCite file generation with related identifiers."
    (->> [["RelatedIdentifier" "the-related-id"]
          ["relatedIdentifierType" "ARK"]
          ["relationType" "Continues"]
          ["RelatedIdentifier" "the-other-related-id"]
          ["relatedIdentifierType" "arXiv"]
          ["relationType" "IsDocumentedBy"]]
         (concat min-attrs)
         (test-datacite "old-datacite/related-ids.xml"))))

(deftest test-rights-list
  (testing "DataCite file generation with rights."
    (->> [["Rights" "CC0"]
          ["Rights" "ODC PDDL"]]
         (concat min-attrs)
         (test-datacite "old-datacite/rights.xml"))))

(deftest test-descriptions
  (testing "DataCite file generation with descriptions."
    (->> [["Description" "The description"]
          ["descriptionType" "Abstract"]
          ["Description" "The other description"]
          ["descriptionType" "Other"]]
         (concat min-attrs)
         (test-datacite "old-datacite/descriptions.xml"))))

(deftest test-geo-locations
  (testing "DataCite file generation with geographic locations."
    (->> [["geoLocationBox" "70.2944 -155.7153 72.2944 -157.7153"]
          ["geoLocationPoint" "71.2944 -156.7153"]
          ["geoLocationPlace" "The Place"]]
         (concat min-attrs)
         (test-datacite "old-datacite/geolocations.xml"))))

(deftest test-group-with-different-numbers-of-optional-attribute-values
  (testing "DataCite file for metadata group with different numbers of optional attribute values."
    (->> [["geoLocationBox" "70.2944 -155.7153 72.2944 -157.7153"]
          ["geoLocationPoint" "71.2944 -156.7153"]
          ["geoLocationPlace" "The Place"]
          ["geoLocationPlace" "The County"]
          ["geoLocationPlace" "The State"]]
         (concat min-attrs)
         (test-datacite "old-datacite/multiple-geolocations.xml"))))
