(ns org.cyverse.metadata-files.datacite-4-1.titles
  (:use [medley.core :only [remove-vals]]
        [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The title element

(def ^:private valid-title-types #{"AlternativeTitle" "Subtitle" "TranslatedTitle" "Other"})

(defn- get-title-type [location {:keys [avus]}]
  (let [attribute-name "titleType"
        title-type     (util/attr-value avus attribute-name)]
    (when-not (string/blank? title-type)
      (when-not (valid-title-types title-type)
        (throw (ex-info (str "Invalid " attribute-name " value.")
                        {:location     location
                         :attribute    attribute-name
                         :value        title-type
                         :valid-values valid-title-types})))
      title-type)))

(deftype Title [title title-type language]
  mdf/XmlSerializable
  (to-xml [_]
    (let [attrs (remove-vals string/blank? {:titleType title-type ::xml/lang language})]
      (element ::datacite/title attrs title))))

(deftype TitleGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "title")
  (min-occurs [_] 1)
  (max-occurs [_] "unbounded")
  (child-element-factories [_] [])

  (get-location [_]
    (str parent-location ".title"))

  (validate [self {title :value avus :avus :as attribute}]
    (let [location (mdf/get-location self)]
      (util/validate-non-blank-string-attribute-value location title)
      (get-title-type location attribute)
      (util/get-language avus)))

  (generate-nested [self {title :value avus :avus :as attribute}]
    (let [location (mdf/get-location self)]
      (Title. title
              (get-title-type location attribute)
              (util/get-language avus)))))

(defn new-title-generator [location]
  (TitleGenerator. location))

;; The titles element

(deftype Titles [titles]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/titles {} (mapv mdf/to-xml titles))))

(deftype TitlesGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] nil)
  (min-occurs [_] 1)
  (max-occurs [_] 1)

  (child-element-factories [self]
    [(new-title-generator (mdf/get-location self))])

  (get-location [_] (str parent-location))

  (validate [self attributes]
    (let [element-factories (mdf/child-element-factories self)]
      (util/validate-attr-counts self attributes)
      (util/validate-child-elements element-factories attributes)))

  (generate-nested [self attributes]
    (Titles. (util/build-child-elements (mdf/child-element-factories self) attributes))))

(defn new-titles-generator [location]
  (TitlesGenerator. location))
