(ns org.cyverse.datacite-test
  (:require [clojure.data.xml :refer :all]
            [clojure.string :as string]
            [clojure.test :refer :all]
            [org.cyverse.metadata-files :refer :all]
            [org.cyverse.metadata-files.datacite :refer [build-datacite]]
            [org.cyverse.metadata-files.test-utils :refer [build-attributes test-xml]])
  (:import [java.net URL]
           [java.util.regex Pattern]))

(def ^:private min-attrs
  {"Identifier"               "the-identifier"
   "identifierType"           "DOI"
   "datacite.creator"         "Nobody Inparticular"
   "creatorAffiliation"       "The University of Nowhere"
   "datacite.title"           "A Very Important Data Set"
   "datacite.publisher"       "CyVerse Data Commons"
   "datacite.publicationyear" "2018"
   "datacite.resourcetype"    "Data Set"})

(def ^:private schema-url
  (URL. "https://schema.datacite.org/meta/kernel-4.1/metadata.xsd"))

(defn- test-datacite [file attrs]
  (test-xml file (build-datacite (build-attributes attrs)) [schema-url]))

(defn- test-missing-fields [& field-names]
  (let [spec (str "(:?" (string/join "|" (map #(Pattern/quote %) field-names)) "(?:,\\s*)?{" (count field-names) "})")]
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          (re-pattern (str "Missing required attributes: " spec))
                          (build-datacite (build-attributes (apply dissoc min-attrs field-names)))))))

(deftest test-minimal
  (testing "Minimal DataCite file."
    (test-datacite "datacite/minimal.xml" min-attrs)))

(deftest test-missing-identifier
  (testing "DataCite file generation with missing identifier."
    (test-missing-fields "Identifier")))

(deftest test-missing-identifier-type
  (testing "DataCite file generation with missing identifier type."
    (test-missing-fields "identifierType")))

(deftest test-missing-creator
  (testing "DataCite file generation with missing creator."
    (test-missing-fields "datacite.creator")))

(deftest test-missing-creator-affiliation
  (testing "DataCite file generation with missing creator affiliation."
    (test-missing-fields "creatorAffiliation")))

(deftest test-missing-title
  (testing "DataCite file generation with missing title."
    (test-missing-fields "datacite.title")))

(deftest test-missing-publisher
  (testing "DataCite file generation with missing publisher."
    (test-missing-fields "datacite.publisher")))

(deftest test-missing-publication-year
  (testing "DataCite file generation with missing publication year."
    (test-missing-fields "datacite.publicationyear")))

(deftest test-missing-resource-type
  (testing "DataCite file generation with missing resource type."
    (test-missing-fields "datacite.resourcetype")))

(deftest test-all-missing-fields
  (testing "DataCite file generateion with all required fields missing."
    (apply test-missing-fields (keys min-attrs))))

(deftest test-creator-with-name-id
  (testing "DataCite file with creator name identifier."
    (test-datacite "datacite/creator-name-id.xml" (assoc min-attrs "creatorNameIdentifier" "foo"))))
