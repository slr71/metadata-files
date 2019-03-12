(ns org.cyverse.metadata-files.container-nested-element
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(deftype ContainerNestedElement [tag child-elements]
  mdf/XmlSerializable
  (to-xml [_]
    (element tag {} (mapv mdf/to-xml child-elements))))

(deftype ContainerNestedElementGenerator [attr-name min-repeat max-repeat element-factory-fns tag parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] attr-name)
  (min-occurs [_] min-repeat)
  (max-occurs [_] max-repeat)
  (get-location [_] (if (string/blank? attr-name) parent-location (str parent-location "." attr-name)))

  (child-element-factories [self]
    (let [location (mdf/get-location self)]
      (mapv #(% location) element-factory-fns)))

  (validate [self attribute-arg]
    (let [attributes (if (string/blank? attr-name) attribute-arg (:avus attribute-arg))]
      (util/validate-attr-counts self attributes)
      (util/validate-child-elements (mdf/child-element-factories self) attributes)))

  (generate-nested [self attribute-arg]
    (let [attributes (if (string/blank? attr-name) attribute-arg (:avus attribute-arg))]
      (when-let [child-elements (seq (util/build-child-elements (mdf/child-element-factories self) attributes))]
        (ContainerNestedElement. tag child-elements)))))

(defn new-container-nested-element-generator [attr-name min-repeat max-repeat element-factory-fns tag parent-location]
  (ContainerNestedElementGenerator. attr-name min-repeat max-repeat element-factory-fns tag parent-location))
