(ns org.cyverse.datacite-4-1-test
  (:require [clojure.data.xml :refer :all]
            [clojure.string :as string]
            [clojure.test :refer :all]
            [org.cyverse.metadata-files :refer :all]
            [org.cyverse.metadata-files.datacite-4-1 :refer [build-datacite]]
            [org.cyverse.metadata-files.test-utils :refer [test-xml]])
  (:import [java.util.regex Pattern]))

(def ^:private min-attrs
  [{:attr "identifier" :value "   doi:10.1000/182" :avus [{:attr "identifierType" :value "DOI"}]}
   {:attr "creator" :value "the-creator"}
   {:attr "title" :value "the-title"}
   {:attr "publisher" :value "CyVerse"}
   {:attr "publicationYear" :value "2019"}
   {:attr "resourceType" :value "XML" :avus [{:attr "resourceTypeGeneral" :value "Dataset"}]}])

(def ^:private schema-url
  "http://schema.datacite.org/meta/kernel-4.1/metadata.xsd")

(defn- test-datacite [file attrs]
  (test-xml file (build-datacite attrs) schema-url))

(defn- debug-datacite [file attrs]
  (test-xml file (build-datacite attrs) schema-url true))

(defn- test-metadata-validation-failure [attrs]
  (is (thrown-with-msg? clojure.lang.ExceptionInfo
                        #"Metadata validation failed."
                        (build-datacite attrs))))

(defn- test-missing-attribute [attrs]
  (is (thrown-with-msg? clojure.lang.ExceptionInfo
                        #"Missing required attribute."
                        (build-datacite attrs))))

(defn- test-invalid-attribute [attrs invalid-attr]
  (is (thrown-with-msg? clojure.lang.ExceptionInfo
                        (re-pattern (str "Invalid " invalid-attr " value."))
                        (build-datacite attrs))))

(defn- test-longitude-validation-failure [attrs]
  (is (thrown-with-msg? clojure.lang.ExceptionInfo
                        #"Longitudes must be between -180.0 and 180.0"
                        (build-datacite attrs))))

(defn- test-latitude-validation-failure [attrs]
  (is (thrown-with-msg? clojure.lang.ExceptionInfo
                        #"Latitudes must be between -90.0 and 90.0"
                        (build-datacite attrs))))

(deftest test-minimal
  (testing "Minimal DataCite file."
    (test-datacite "datacite-4.1/minimal.xml" min-attrs)))

(deftest test-missing-top-level-element-attributes
  (testing "Missing top-level attributes."
    (doseq [attr-name (mapv :attr min-attrs)]
      (test-metadata-validation-failure (remove (comp (partial = attr-name) :attr) min-attrs)))))

(deftest test-affiliation
  (testing "DataCite file with creator affiliation."
    (test-datacite "datacite-4.1/affiliation.xml"
                   (assoc-in min-attrs [1 :avus] [{:attr "affiliation" :value "CyVerse"}]))))

(deftest test-name-identifier
  (testing "DataCite file with name identifier."
    (test-datacite "datacite-4.1/name-identifier.xml"
                   (assoc-in min-attrs [1 :avus] [{:attr  "nameIdentifier"
                                                   :value "foo"
                                                   :avus  [{:attr  "nameIdentifierScheme"
                                                            :value "ORCID"}]}]))))
(deftest test-scheme-uri
  (testing "DataCite file with a name identifier and a scheme URI."
    (test-datacite "datacite-4.1/scheme-uri.xml"
                   (assoc-in min-attrs [1 :avus] [{:attr  "nameIdentifier"
                                                   :value "foo"
                                                   :avus  [{:attr  "nameIdentifierScheme"
                                                            :value "ORCID"}
                                                           {:attr  "schemeURI"
                                                            :value "https://orcid.org"}]}]))))
(deftest test-missing-name-identifier-scheme
  (testing "DataCite file with a name identifier missing its scheme."
    (test-missing-attribute (assoc-in min-attrs [1 :avus] [{:attr  "nameIdentifier"
                                                            :value "foo"}]))))

(deftest test-title-type
  (testing "DataCite file with a title type."
    (test-datacite "datacite-4.1/title-type.xml"
                   (assoc-in min-attrs [2 :avus] [{:attr "titleType" :value "Other"}]))))

(deftest test-invalid-title-type
  (testing "DataCite file with an invalid title type."
    (test-invalid-attribute (assoc-in min-attrs [2 :avus] [{:attr "titleType" :value "Foo"}])
                            "titleType")))

(deftest test-invalid-resource-type-general
  (testing "DataCite file with an invalid resourceTypeGeneral attribute."
    (test-invalid-attribute (assoc-in min-attrs [5 :avus 0 :value] "Foo") "resourceTypeGeneral")))

(deftest test-subject
  (testing "DataCite file with a subject."
    (test-datacite "datacite-4.1/subject.xml"
                   (conj min-attrs {:attr "subject" :value "the-subject"}))))

(deftest test-subject-scheme
  (testing "DataCite file with a subject scheme."
    (test-datacite "datacite-4.1/subject-scheme.xml"
                   (conj min-attrs {:attr  "subject"
                                    :value "the-subject"
                                    :avus  [{:attr "subjectScheme" :value "scheme"}]}))))

(deftest test-subject-scheme-uri
  (testing "DataCite file with a subject scheme URI."
    (test-datacite "datacite-4.1/subject-scheme-uri.xml"
                   (conj min-attrs {:attr  "subject"
                                    :value "the-subject"
                                    :avus  [{:attr "subjectScheme" :value "scheme"}
                                            {:attr "schemeURI" :value "https://scheme.org"}]}))))

(deftest test-subject-value-uri
  (testing "DataCite file with a subject value URI."
    (test-datacite "datacite-4.1/subject-value-uri.xml"
                   (conj min-attrs {:attr  "subject"
                                    :value "the-subject"
                                    :avus  [{:attr "valueURI" :value "https://example.org"}]}))))

(deftest test-custom-subject-language
  (testing "DataCite file with a custom subject language."
    (test-datacite "datacite-4.1/subject-custom-lang.xml"
                   (conj min-attrs {:attr  "subject"
                                    :value "the-subject"
                                    :avus  [{:attr "xml:lang" :value "IT"}]}))))

(deftest test-contributor
  (testing "DataCite file with a contributor."
    (test-datacite "datacite-4.1/contributor.xml"
                   (conj min-attrs {:attr  "contributor"
                                    :value "the-contributor"
                                    :avus  [{:attr "contributorType" :value "Editor"}]}))))

(deftest test-contributor-affiliation
  (testing "DataCite file with a contributor affiliation."
    (test-datacite "datacite-4.1/contributor-affiliation.xml"
                   (conj min-attrs {:attr  "contributor"
                                    :value "the-contributor"
                                    :avus  [{:attr "contributorType" :value "Editor"}
                                            {:attr "affiliation" :value "CyVerse"}]}))))

(deftest test-contributor-name-identifier
  (testing "DataCite file with a contributor name identifier."
    (test-datacite "datacite-4.1/contributor-name-identifier.xml"
                   (conj min-attrs {:attr  "contributor"
                                    :value "the-contributor"
                                    :avus  [{:attr "contributorType" :value "Editor"}
                                            {:attr "nameIdentifier"
                                             :value "the-name-identifier"
                                             :avus  [{:attr "nameIdentifierScheme" :value "ORCID"}]}]}))))

(deftest test-invalid-contributor-type
  (testing "DataCite file with an invalid contributor type."
    (test-invalid-attribute (conj min-attrs {:attr  "contributor"
                                             :value "the-contributor"
                                             :avus  [{:attr "contributorType" :value "Fiddler"}]})
                            "contributorType")))

(deftest test-missing-contributor-type
  (testing "DataCite file with a missing contributor type."
    (test-missing-attribute (conj min-attrs {:attr  "contributor" :value "the-contributor"}))))

(deftest test-language
  (testing "DataCite file with a specified language."
    (test-datacite "datacite-4.1/language.xml" (conj min-attrs {:attr "language" :value "en-us"}))))

(deftest test-alternate-identifier
  (testing "DataCite file with an alternate identifier."
    (test-datacite "datacite-4.1/alternate-identifier.xml"
                   (conj min-attrs {:attr  "alternateIdentifier"
                                    :value "the-alt-id"
                                    :avus  [{:attr "alternateIdentifierType" :value "the-alt-id-type"}]}))))

(deftest test-missing-alternate-identifier-type
  (testing "DataCite file with a missing alternate identifier type."
    (test-missing-attribute (conj min-attrs {:attr  "alternateIdentifier"
                                             :value "the-alt-id"}))))

(deftest test-related-identifier
  (testing "DataCite file with a related identifier."
    (test-datacite "datacite-4.1/related-identifier.xml"
                   (conj min-attrs {:attr "relatedIdentifier"
                                    :value "https://example.org"
                                    :avus [{:attr "relatedIdentifierType" :value "URL"}
                                           {:attr "relationType" :value "IsDescribedBy"}]}))))

(deftest test-missing-related-identifier-type
  (testing "DataCite file with a missing related identifier type."
    (test-missing-attribute (conj min-attrs {:attr "relatedIdentifier"
                                             :value "https://example.org"
                                             :avus [{:attr "relationType" :value "IsDescribedBy"}]}))))

(deftest test-missing-relation-type
  (testing "DataCite file with a missing relation type."
    (test-missing-attribute (conj min-attrs {:attr "relatedIdentifier"
                                             :value "https://example.org"
                                             :avus [{:attr "relatedIdentifierType" :value "URL"}]}))))

(deftest test-size
  (testing "DataCite file with a size."
    (test-datacite "datacite-4.1/size.xml"
                   (conj min-attrs {:attr "size" :value "All the gigabytes!"}))))

(deftest test-format
  (testing "DataCite file with a format."
    (test-datacite "datacite-4.1/format.xml"
                   (conj min-attrs {:attr "format" :value "text/plain"}))))

(deftest test-version
  (testing "DataCite file with a version."
    (test-datacite "datacite-4.1/version.xml"
                   (conj min-attrs {:attr "version" :value "1"}))))

(deftest test-multiple-versions
  (testing "DataCite file with too many versions."
    (test-metadata-validation-failure (concat min-attrs [{:attr "version" :value "1"}
                                                         {:attr "version" :value "2"}]))))

(deftest test-rights
  (testing "DataCite file with a rights list."
    (test-datacite "datacite-4.1/rights-list.xml"
                   (conj min-attrs {:attr "rights" :value "ODC PDDL"}))))

(deftest test-rights-uri
  (testing "DataCite file with a rights URI."
    (test-datacite "datacite-4.1/rights-uri.xml"
                   (conj min-attrs {:attr  "rights"
                                    :value "ODC PDDL"
                                    :avus  [{:attr "rightsURI" :value "https://example.org"}]}))))

(deftest test-desription
  (testing "DataCite file with a description."
    (test-datacite "datacite-4.1/description.xml"
                   (conj min-attrs {:attr  "description"
                                    :value "the-description"
                                    :avus  [{:attr "descriptionType" :value "Abstract"}]}))))

(deftest test-custom-description-language
  (testing "DataCite file with a custom description language."
    (test-datacite "datacite-4.1/description-language.xml"
                   (conj min-attrs {:attr  "description"
                                    :value "the-description"
                                    :avus  [{:attr "descriptionType" :value "Abstract"}
                                            {:attr "xml:lang" :value "it"}]}))))

(deftest test-missing-description-type
  (testing "DataCite file with a missing description type."
    (test-missing-attribute (conj min-attrs {:attr "description" :value "the-description"}))))

(deftest test-invalid-description-type
  (testing "DataCite file with an invalid description type."
    (test-invalid-attribute (conj min-attrs {:attr  "description"
                                             :value "the-description"
                                             :avus  [{:attr "descriptionType" :value "Blah"}]})
                            "descriptionType")))

(deftest test-geo-location-place
  (testing "DataCite file with a geographic location place."
    (test-datacite "datacite-4.1/geo-location-place.xml"
                   (conj min-attrs {:attr "geoLocation"
                                    :avus [{:attr "geoLocationPlace" :value "Tucson"}]}))))

(deftest test-geo-location-point
  (testing "DataCite file with a geographic location point."
    (test-datacite "datacite-4.1/geo-location-point.xml"
                   (conj min-attrs {:attr "geoLocation"
                                    :avus [{:attr "geoLocationPoint"
                                            :avus [{:attr "pointLongitude" :value "132.2"}
                                                   {:attr "pointLatitude" :value "88.1"}]}]}))))

(deftest test-invalid-location-point-longitude
  (testing "DataCite file with invalid location point longitude."
    (test-longitude-validation-failure
     (conj min-attrs {:attr "geoLocation"
                      :avus [{:attr "geoLocationPoint"
                              :avus [{:attr "pointLongitude" :value "-180.1"}
                                     {:attr "pointLatitude" :value "88.1"}]}]}))
    (test-longitude-validation-failure
     (conj min-attrs {:attr "geoLocation"
                      :avus [{:attr "geoLocationPoint"
                              :avus [{:attr "pointLongitude" :value "180.1"}
                                     {:attr "pointLatitude" :value "88.1"}]}]}))))

(deftest test-invalid-location-point-latitude
  (testing "DataCite file with invalid location point latitude."
    (test-latitude-validation-failure
     (conj min-attrs {:attr "geoLocation"
                      :avus [{:attr "geoLocationPoint"
                              :avus [{:attr "pointLongitude" :value "-179.9"}
                                     {:attr "pointLatitude" :value "-90.1"}]}]}))
    (test-latitude-validation-failure
     (conj min-attrs {:attr "geoLocation"
                      :avus [{:attr "geoLocationPoint"
                              :avus [{:attr "pointLongitude" :value "179.9"}
                                     {:attr "pointLatitude" :value "90.1"}]}]}))))

(deftest test-incomplete-location-point
  (testing "DataCite files with incomplete geographic location points"
    (test-metadata-validation-failure
     (conj min-attrs {:attr "geoLocation"
                      :avus [{:attr "geoLocationPoint"
                              :avus [{:attr "pointLongitude" :value "179.9"}]}]}))
    (test-metadata-validation-failure
     (conj min-attrs {:attr "geoLocation"
                      :avus [{:attr "geoLocationPoint"
                              :avus [{:attr "pointLatitude" :value "89.9"}]}]}))))

(deftest test-geo-location-box
  (testing "DataCite file with a geographic location box."
    (test-datacite "datacite-4.1/geo-location-box.xml"
                   (conj min-attrs {:attr "geoLocation"
                                    :avus [{:attr "geoLocationBox"
                                            :avus [{:attr "westBoundLongitude" :value "-180.0"}
                                                   {:attr "eastBoundLongitude" :value "180.0"}
                                                   {:attr "southBoundLatitude" :value "-90.0"}
                                                   {:attr "northBoundLatitude" :value "90.0"}]}]}))))

(deftest test-geo-location-box-validation-failures
  (testing "DataCite file with invalid geographic box longitudes and latitudes."
    (test-longitude-validation-failure
     (conj min-attrs {:attr "geoLocation"
                      :avus [{:attr "geoLocationBox"
                              :avus [{:attr "westBoundLongitude" :value "-180.1"}
                                     {:attr "eastBoundLongitude" :value "180.0"}
                                     {:attr "southBoundLatitude" :value "-90.0"}
                                     {:attr "northBoundLatitude" :value "90.0"}]}]}))
    (test-longitude-validation-failure
     (conj min-attrs {:attr "geoLocation"
                      :avus [{:attr "geoLocationBox"
                              :avus [{:attr "westBoundLongitude" :value "-180.0"}
                                     {:attr "eastBoundLongitude" :value "180.1"}
                                     {:attr "southBoundLatitude" :value "-90.0"}
                                     {:attr "northBoundLatitude" :value "90.0"}]}]}))
    (test-latitude-validation-failure
     (conj min-attrs {:attr "geoLocation"
                      :avus [{:attr "geoLocationBox"
                              :avus [{:attr "westBoundLongitude" :value "-180.0"}
                                     {:attr "eastBoundLongitude" :value "180.0"}
                                     {:attr "southBoundLatitude" :value "-90.1"}
                                     {:attr "northBoundLatitude" :value "90.0"}]}]}))
    (test-latitude-validation-failure
     (conj min-attrs {:attr "geoLocation"
                      :avus [{:attr "geoLocationBox"
                              :avus [{:attr "westBoundLongitude" :value "-180.0"}
                                     {:attr "eastBoundLongitude" :value "180.0"}
                                     {:attr "southBoundLatitude" :value "-90.0"}
                                     {:attr "northBoundLatitude" :value "90.1"}]}]}))))

(deftest test-incomplete-location-box
  (testing "DataCite file with incomplete geographic location boxes."
    (test-metadata-validation-failure
     (conj min-attrs {:attr "geoLocation"
                      :avus [{:attr "geoLocationBox"
                              :avus [{:attr "eastBoundLongitude" :value "180.0"}
                                     {:attr "southBoundLatitude" :value "-90.0"}
                                     {:attr "northBoundLatitude" :value "90.0"}]}]}))
    (test-metadata-validation-failure
     (conj min-attrs {:attr "geoLocation"
                      :avus [{:attr "geoLocationBox"
                              :avus [{:attr "westBoundLongitude" :value "-180.0"}
                                     {:attr "southBoundLatitude" :value "-90.0"}
                                     {:attr "northBoundLatitude" :value "90.0"}]}]}))
    (test-metadata-validation-failure
     (conj min-attrs {:attr "geoLocation"
                      :avus [{:attr "geoLocationBox"
                              :avus [{:attr "westBoundLongitude" :value "-180.0"}
                                     {:attr "eastBoundLongitude" :value "180.0"}
                                     {:attr "northBoundLatitude" :value "90.0"}]}]}))
    (test-metadata-validation-failure
     (conj min-attrs {:attr "geoLocation"
                      :avus [{:attr "geoLocationBox"
                              :avus [{:attr "westBoundLongitude" :value "-180.0"}
                                     {:attr "eastBoundLongitude" :value "180.0"}
                                     {:attr "southBoundLatitude" :value "-90.0"}]}]}))))
