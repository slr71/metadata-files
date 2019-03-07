(ns org.cyverse.metadata-files.datacite-4-1.language
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

(deftype Language [language]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/language {} language)))

(deftype LanguageGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "language")
  (min-occurs [_] 0)
  (max-occurs [_] 1)
  (child-element-factories [_] [])
  (get-location [_] (str parent-location ".language"))
  (validate [_ _])

  (generate-nested [_ {language :value}]
    (when-not (string/blank? language)
      (Language. language))))

(defn new-language-generator [location]
  (LanguageGenerator. location))
