(ns org.cyverse.metadata-files.container-nested-element
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
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
  (get-location [_] (util/build-location parent-location attr-name))

  (child-element-factories [self]
    (let [location (mdf/get-location self)]
      (mapv #(% location) element-factory-fns)))

  (validate [self attribute-arg]
    (let [attributes (util/get-child-attributes attribute-arg)]
      (util/validate-attr-counts self attributes)
      (util/validate-child-elements (mdf/child-element-factories self) attributes)))

  (generate-nested [self attribute-arg]
    (let [attributes (util/get-child-attributes attribute-arg)]
      (when-let [child-elements (seq (util/build-child-elements (mdf/child-element-factories self) attributes))]
        (ContainerNestedElement. tag child-elements)))))

(defn new-container-nested-element-generator
  [{:keys [attr-name min-occurs max-occurs element-factory-fns tag parent-location]
    :or   {min-occurs 1
           max-occurs 1}}]
  (ContainerNestedElementGenerator. attr-name min-occurs max-occurs element-factory-fns tag parent-location))
