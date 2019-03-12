(ns org.cyverse.metadata-files.simple-nested-element
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(deftype SimpleNestedElement [tag value]
  mdf/XmlSerializable
  (to-xml [_]
    (element tag {} value)))

(deftype SimpleNestedElementGenerator [attr-name min-repeat max-repeat validation-fn tag parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] attr-name)
  (min-occurs [_] min-repeat)
  (max-occurs [_] max-repeat)
  (get-location [_] (util/build-location parent-location attr-name))
  (child-element-factories [_] [])

  (validate [self {:keys [value]}]
    (validation-fn (mdf/get-location self) value))

  (generate-nested [_ {:keys [value]}]
    (when-not (string/blank? value)
      (SimpleNestedElement. tag value))))

(defn new-simple-nested-element-generator
  [{:keys [attr-name min-occurs max-occurs validation-fn tag parent-location]
    :or   {min-occurs    1
           max-occurs    1
           validation-fn util/validate-non-blank-string-attribute-value}}]
  (SimpleNestedElementGenerator. attr-name min-occurs max-occurs validation-fn tag parent-location))
