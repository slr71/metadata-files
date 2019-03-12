(ns org.cyverse.metadata-files.datacite-4-1.sizes
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.container-nested-element :as cne]
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

(defn new-sizes-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-size-generator]
    :tag                 ::datacite/sizes
    :parent-location     location}))
