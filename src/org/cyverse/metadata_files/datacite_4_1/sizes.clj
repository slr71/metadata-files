(ns org.cyverse.metadata-files.datacite-4-1.sizes
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; The size element

(deftype Size [size]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/size {} size)))

(deftype SizeGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "size")
  (min-occurs [_] 0)
  (max-occurs [_] "unbounded")
  (get-location [_] (str parent-location ".size"))
  (child-element-factories [_] [])

  (validate [self {size :value}]
    (util/validate-non-blank-string-attribute-value (mdf/get-location self) size))

  (generate-nested [_ {size :value}]
    (Size. size)))

(defn new-size-generator [location]
  (SizeGenerator. location))

;; The sizes element

(deftype Sizes [sizes]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/sizes {} (mapv mdf/to-xml sizes))))

(deftype SizesGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] nil)
  (min-occurs [_] 0)
  (max-occurs [_] 1)
  (get-location [_] parent-location)
  (child-element-factories [self] [(new-size-generator (mdf/get-location self))])

  (validate [self attributes]
    (let [element-factories (mdf/child-element-factories self)]
      (util/validate-attr-counts self attributes)
      (util/validate-child-elements element-factories attributes)))

  (generate-nested [self attributes]
    (when-let [sizes (seq (util/build-child-elements (mdf/child-element-factories self) attributes))]
      (Sizes. sizes))))

(defn new-sizes-generator [location]
  (SizesGenerator. location))
