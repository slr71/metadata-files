(ns org.cyverse.datacite-test
  (:require [clojure.data.xml :refer :all]
            [clojure.test :refer :all]
            [org.cyverse.metadata-files :refer :all]
            [org.cyverse.metadata-files.datacite :refer [build-datacite]]
            [org.cyverse.metadata-files.test-utils :refer [build-attributes test-xml]])
  (:import [java.net URL]))

(def ^:private min-attrs
  (build-attributes {"Identifier"             "the-identifier"
                     "identifierType"           "DOI"
                     "datacite.creator"         "Nobody Inparticular"
                     "creatorAffiliation"       "The University of Nowhere"
                     "datacite.title"           "A Very Important Data Set"
                     "datacite.publisher"       "CyVerse Data Commons"
                     "datacite.publicationyear" "2018"
                     "datacite.resourcetype"    "Data Set"}))

(def ^:private schema-url
  (URL. "https://schema.datacite.org/meta/kernel-4.1/metadata.xsd"))

(deftest test-minimal
  (testing "Minimal DataCite file."
    (test-xml "datacite/minimal.xml" (build-datacite min-attrs) schema-url)))
