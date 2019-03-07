(ns org.cyverse.metadata-files.datacite-4-1.contributors
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.datacite-4-1.affiliation :as affiliation]
            [org.cyverse.metadata-files.datacite-4-1.name-identifier :as name-identifier]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The contributor element

(def ^:private valid-contributor-types
  #{"ContactPerson"
    "DataCollector"
    "DataCurator"
    "DataManager"
    "Distributor"
    "Editor"
    "HostingInstitution"
    "Other"
    "Producer"
    "ProjectLeader"
    "ProjectManager"
    "ProjectMember"
    "RegistrationAgency"
    "RegistrationAuthority"
    "RelatedPerson"
    "ResearchGroup"
    "RightsHolder"
    "Researcher"
    "Sponsor"
    "Supervisor"
    "WorkPackageLeader"})

(defn- get-contributor-type [location {:keys [avus]}]
  (let [attribute-name   "contributorType"
        contributor-type (util/get-required-attribute-value location avus attribute-name)]
    (when-not (valid-contributor-types contributor-type)
      (throw (ex-info (str "Invalid " attribute-name " value.")
                      {:location     location
                       :attribute    attribute-name
                       :value        contributor-type
                       :valid-values valid-contributor-types})))
    contributor-type))

(deftype Contributor [contributor-name contributor-type elements]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/contributor {:contributorType contributor-type}
      (concat [(element ::datacite/contributorName {} contributor-name)] (mapv mdf/to-xml elements)))))

(deftype ContributorGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "contributor")
  (min-occurs [_] 0)
  (max-occurs [_] "unbounded")

  (child-element-factories [self]
    (let [location (mdf/get-location self)]
      [(name-identifier/new-name-identifier-generator location)
       (affiliation/new-affiliation-generator location)]))

  (get-location [_] (str parent-location ".contributor"))

  (validate [self {contributor-name :value avus :avus :as attribute}]
    (let [location (mdf/get-location self)]
      (util/validate-non-blank-string-attribute-value location contributor-name)
      (get-contributor-type location attribute)
      (util/validate-attr-counts self avus)
      (util/validate-child-elements (mdf/child-element-factories self) avus)))

  (generate-nested [self {contributor-name :value avus :avus :as attribute}]
    (let [location (mdf/get-location self)]
      (Contributor. contributor-name
                    (get-contributor-type location attribute)
                    (util/build-child-elements (mdf/child-element-factories self) avus)))))

(defn- new-contributor-generator [location]
  (ContributorGenerator. location))

;; The contributors element

(deftype Contributors [contributors]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/contributors {} (mapv mdf/to-xml contributors))))

(deftype ContributorsGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] nil)
  (min-occurs [_] 0)
  (max-occurs [_] 1)

  (child-element-factories [self]
    [(new-contributor-generator (mdf/get-location self))])

  (get-location [_] parent-location)

  (validate [self attributes]
    (let [element-factories (mdf/child-element-factories self)]
      (util/validate-attr-counts self attributes)
      (util/validate-child-elements element-factories attributes)))

  (generate-nested [self attributes]
    (when-let [contributors (seq (util/build-child-elements (mdf/child-element-factories self) attributes))]
      (Contributors. contributors))))

(defn new-contributors-generator [location]
  (ContributorsGenerator. location))
