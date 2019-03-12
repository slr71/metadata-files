(ns org.cyverse.metadata-files.compound-nested-element
  (:use [clojure.data.xml :only [element]]
        [medley.core :only [remove-vals]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
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
  [{:keys [attr-name min-occurs max-occurs validation-fn attrs-fn format-fn element-factory-fns tag parent-location]
    :or   {min-occurs          1
           max-occurs          1
           validation-fn       util/validate-non-blank-string-attribute-value
           element-factory-fns []
           attrs-fn            (constantly {})
           format-fn           identity}}]
  (CompoundNestedElementGenerator. attr-name min-occurs max-occurs validation-fn attrs-fn format-fn
                                   element-factory-fns tag parent-location))
