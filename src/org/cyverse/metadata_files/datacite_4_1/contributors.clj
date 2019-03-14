(ns org.cyverse.metadata-files.datacite-4-1.contributors
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.compound-nested-element :as compound-ne]
            [org.cyverse.metadata-files.container-nested-element :as cne]
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

(defn- get-contributor-attrs [location attribute]
  {:contributorType (get-contributor-type location attribute)})

(defn- format-contributor-name [contributor-name]
  (element ::datacite/contributorName {} contributor-name))

(defn new-contributor-generator [location]
  (compound-ne/new-compound-nested-element-generator
   {:attr-name           "contributor"
    :min-occurs          0
    :max-occurs          "unbounded"
    :attrs-fn            get-contributor-attrs
    :format-fn           format-contributor-name
    :element-factory-fns [name-identifier/new-name-identifier-generator
                          affiliation/new-affiliation-generator]
    :tag                 ::datacite/contributor
    :parent-location     location}))

;; The contributors element

(defn new-contributors-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-contributor-generator]
    :tag                 ::datacite/contributors
    :parent-location     location}))
