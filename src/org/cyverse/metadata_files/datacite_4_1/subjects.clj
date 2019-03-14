(ns org.cyverse.metadata-files.datacite-4-1.subjects
  (:use [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.simple-nested-element :as sne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The subject element

(defn- get-subject-attrs [_ {:keys [avus]}]
  {:subjectScheme (util/attr-value avus "subjectScheme")
   :schemeURI     (util/attr-value avus "schemeURI")
   :valueURI      (util/attr-value avus "valueURI")
   ::xml/lang     (util/get-language avus)})

(defn new-subject-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "subject"
    :min-occurs      0
    :max-occurs      "unbounded"
    :attrs-fn        get-subject-attrs
    :tag             ::datacite/subject
    :parent-location location}))

;; The subjects element

(defn new-subjects-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-subject-generator]
    :tag                 ::datacite/subjects
    :parent-location     location}))
