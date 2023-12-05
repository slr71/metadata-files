(ns org.cyverse.metadata-files.container-nested-element
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-2.namespaces :only [alias-uris]])
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
  "This function creates and returns an element generator that supports metadata attributes with child attributes but
  no values of their own.

  The `attr-name` parameter takes the name of the metadata template attribute that is used to generate the
  element. Some elements (for example, top-level container elements) do not consume a metadata attribute. The
  `attr-name` parameter can be omitted in these cases.

  The `min-occurs` parameter takes the minimum number of occurrences of the element in the generated document.
  The default value for this parameter is 1.

  The `max-occurs` parameter takes the maximum number of occurrences of the element in the generated document.
  The default value for this parameter is 1.

  The `element-factory-fns` parameter contains a sequence of functions that will be used to create the generators for
  child elements.

  The `tag` parameter is used to specify the element tag.

  The `parent-location` parameter points to the relative location of the parent attribute in the metadata attribute
  hierarchy. The location of the current element is derived from the value of this parameter and the `attr-name`
  parameter."
  [{:keys [attr-name min-occurs max-occurs element-factory-fns tag parent-location]
    :or   {min-occurs 1
           max-occurs 1}}]
  (ContainerNestedElementGenerator. attr-name min-occurs max-occurs element-factory-fns tag parent-location))
