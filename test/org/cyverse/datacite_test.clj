(ns org.cyverse.datacite-test
  (:require [clojure.data.xml :refer :all]
            [clojure.test :refer :all]
            [org.cyverse.metadata-files :refer :all]
            [org.cyverse.metadata-files.datacite :refer [build-datacite]]
            [org.cyverse.metadata-files.test-utils :refer [build-attributes test-xml]])
  (:import [java.net URL]))

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

(defn- test-missing-field [field-name]
  (is (thrown-with-msg? clojure.lang.ExceptionInfo
                        (re-pattern (str "Missing required attributes: " field-name))
                        (build-datacite (build-attributes (dissoc min-attrs field-name))))))

(deftest test-minimal
  (testing "Minimal DataCite file."
    (test-xml "datacite/minimal.xml" (build-datacite (build-attributes min-attrs)) schema-url)))

(deftest test-missing-identifier
  (testing "DataCite file generation with missing identifier."
    (test-missing-field "Identifier")))

(deftest test-missing-identifier-type
  (testing "DataCite file generation with missing identifier type."
    (test-missing-field "identifierType")))

(deftest test-missing-creator
  (testing "DataCite file generation with missing creator."
    (test-missing-field "datacite.creator")))

(deftest test-missing-creator-affiliation
  (testing "DataCite file generation with missing creator affiliation."
    (test-missing-field "creatorAffiliation")))

(deftest test-missing-title
  (testing "DataCite file generation with missing title."
    (test-missing-field "datacite.title")))

(deftest test-missing-publisher
  (testing "DataCite file generation with missing publisher."
    (test-missing-field "datacite.publisher")))

(deftest test-missing-publication-year
  (testing "DataCite file generation with missing publication year."
    (test-missing-field "datacite.publicationyear")))

(deftest test-missing-resource-type
  (testing "DataCite file generation with missing resource type."
    (test-missing-field "datacite.resourcetype")))
