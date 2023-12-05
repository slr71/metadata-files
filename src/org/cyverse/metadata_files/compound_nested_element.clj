(ns org.cyverse.metadata-files.compound-nested-element
  (:use [clojure.data.xml :only [element]]
        [medley.core :only [remove-vals]]
        [org.cyverse.metadata-files.datacite-4-2.namespaces :only [alias-uris]])
  (:require [clojure.string :as string]
            [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(deftype CompoundNestedElement [tag attrs value-element child-elements]
  mdf/XmlSerializable
  (to-xml [_]
    (element tag (remove-vals string/blank? attrs)
      (concat [value-element] (mapv mdf/to-xml child-elements)))))

(deftype CompoundNestedElementGenerator
    [attr-name min-repeat max-repeat validation-fn attrs-fn format-fn element-factory-fns tag parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] attr-name)
  (min-occurs [_] min-repeat)
  (max-occurs [_] max-repeat)
  (get-location [_] (util/build-location parent-location attr-name))

  (child-element-factories [self]
    (let [location (mdf/get-location self)]
      (mapv #(% location) element-factory-fns)))

  (validate [self {:keys [value] :as attribute}]
    (validation-fn (mdf/get-location self) value)
    (attrs-fn self attribute))

  (generate-nested [self {:keys [value avus] :as attribute}]
    (when-not (string/blank? value)
      (let [child-elements (seq (util/build-child-elements (mdf/child-element-factories self) avus))]
        (CompoundNestedElement. tag (attrs-fn self attribute) (format-fn value) child-elements)))))

(defn new-compound-nested-element-generator
  "This function creates and returns an element generator that supports metadata attributes with both a value
  and child attributes.

  The `attr-name` parameter takes the name of the metadata template attribute that is used to generate the
  element. Some elements (for example, top-level container elements) do not consume a metadata attribute. The
  `attr-name` parameter can be omitted in these cases.

  The `min-occurs` parameter takes the minimum number of occurrences of the element in the generated document.
  The default value for this parameter is 1.

  The `max-occurs` parameter takes the maximum number of occurrences of the element in the generated document.
  The default value for this parameter is 1.

  The `validation-fn` parameter takes a function that will be called to verify that the attribute value is valid.
  If left unspecified, the default validation function will simply verify that the attribute contains a string
  that is not blank.

  The `attrs-fn` parameter takes a function that will generate the attributes of the element. The result of calling
  this function should be a map from attribute name to attribute value (for example, `{:attr-name \"attr-value\"}`).
  This parameter may be left unspecified if the element has no attributes.

  The `format-fn` parameter takes a function that will be called to format the value before placing it in the element.
  If this parameter is left unspecified, the value will be placed in the element, unmodified.

  The `element-factory-fns` parameter contains a sequence of functions that will be used to create the generators for
  child elements.

  The `tag` parameter is used to specify the element tag.

  The `parent-location` parameter points to the relative location of the parent attribute in the metadata attribute
  hierarchy. The location of the current element is derived from the value of this parameter and the `attr-name`
  parameter."
  [{:keys [attr-name min-occurs max-occurs validation-fn attrs-fn format-fn element-factory-fns tag parent-location]
    :or   {min-occurs          1
           max-occurs          1
           validation-fn       util/validate-non-blank-string-attribute-value
           element-factory-fns []
           attrs-fn            (constantly {})
           format-fn           identity}}]
  (CompoundNestedElementGenerator. attr-name min-occurs max-occurs validation-fn attrs-fn format-fn
                                   element-factory-fns tag parent-location))
