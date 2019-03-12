(ns org.cyverse.metadata-files.datacite-4-1.geo-locations
  (:use [clojure.data.xml :only [element]]
        [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files :as mdf]
            [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.util :as util]))

(alias-uris)

;; General longitude elements

(defn- validate-longitude [location longitude]
  (try
    (let [longitude (Double/parseDouble longitude)]
      (when-not (<= -180.0 longitude 180.0)
        (throw (ex-info "Longitudes must be between -180.0 and 180.0" {:location location :longitude longitude}))))
    (catch NumberFormatException _
      (throw (ex-info "Invalid longitude" {:location location :longitude longitude})))))

(deftype Longitude [tag longitude]
  mdf/XmlSerializable
  (to-xml [_]
    (element tag {} longitude)))

(deftype LongitudeGenerator [attr-name tag parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] attr-name)
  (min-occurs [_] 1)
  (max-occurs [_] 1)
  (get-location [_] (str parent-location "." attr-name))
  (child-element-factories [_] [])

  (validate [self {longitude :value}]
    (validate-longitude (mdf/get-location self) longitude))

  (generate-nested [_ {longitude :value}]
    (Longitude. tag longitude)))

(defn new-longitude-generator [attr-name tag location]
  (LongitudeGenerator. attr-name tag location))

;; General latitude elements

(defn- validate-latitude [location latitude]
  (try
    (let [latitude (Double/parseDouble latitude)]
      (when-not (<= -90.0 latitude 90.0)
        (throw (ex-info "Latitudes must be between -90.0 and 90.0" {:location location :latitude latitude}))))
    (catch NumberFormatException _
      (throw (ex-info "Invalid latitude" {:location location :latitude :latitude})))))

(deftype Latitude [tag latitude]
  mdf/XmlSerializable
  (to-xml [_]
    (element tag {} latitude)))

(deftype LatitudeGenerator [attr-name tag parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] attr-name)
  (min-occurs [_] 1)
  (max-occurs [_] 1)
  (get-location [_] (str parent-location "." attr-name))
  (child-element-factories [_] [])

  (validate [self {latitude :value}]
    (validate-latitude (mdf/get-location self) latitude))

  (generate-nested [_ {latitude :value}]
    (Latitude. tag latitude)))

(defn new-latitude-generator [attr-name tag location]
  (LatitudeGenerator. attr-name tag location))

;; The geoLocationPlace element

(deftype GeoLocationPlace [place]
  mdf/XmlSerializable
  (to-xml [_]
    (element ::datacite/geoLocationPlace {} place)))

(deftype GeoLocationPlaceGenerator [parent-location]
  mdf/NestedElementFactory
  (attribute-name [_] "geoLocationPlace")
  (min-occurs [_] 0)
  (max-occurs [_] 1)
  (get-location [_] (str parent-location ".geoLocationPlace"))
  (child-element-factories [_] [])

  (validate [self {place :value}]
    (util/validate-non-blank-string-attribute-value (mdf/get-location self) place))

  (generate-nested [self {place :value}]
    (GeoLocationPlace. place)))

(defn new-geo-location-place-generator [location]
  (GeoLocationPlaceGenerator. location))

;; The geoLocationPoint element

(defn new-geo-location-point-generator [location]
  (cne/new-container-nested-element-generator
   {:attr-name           "geoLocationPoint"
    :min-occurs          0
    :element-factory-fns [(partial new-longitude-generator "pointLongitude" ::datacite/pointLongitude)
                          (partial new-latitude-generator "pointLatitude" ::datacite/pointLatitude)]
    :tag                 ::datacite/geoLocationPoint
    :parent-location     location}))

;; The geoLocationBox element

(defn new-geo-location-box-generator [location]
  (cne/new-container-nested-element-generator
   {:attr-name           "geoLocationBox"
    :min-occurs          0
    :max-occurs          "unbounded"
    :element-factory-fns [(partial new-longitude-generator "westBoundLongitude" ::datacite/westBoundLongitude)
                          (partial new-longitude-generator "eastBoundLongitude" ::datacite/eastBoundLongitude)
                          (partial new-latitude-generator "southBoundLatitude" ::datacite/southBoundLatitude)
                          (partial new-latitude-generator "northBoundLatitude" ::datacite/northBoundLatitude)]
    :tag                 ::datacite/geoLocationBox
    :parent-location     location}))

;; The geoLocation element

(defn new-geo-location-generator [location]
  (cne/new-container-nested-element-generator
   {:attr-name           "geoLocation"
    :min-occurs          0
    :max-occurs          "unbounded"
    :element-factory-fns [new-geo-location-place-generator
                          new-geo-location-point-generator
                          new-geo-location-box-generator]
    :tag                 ::datacite/geoLocation
    :parent-location     location}))

;; The geoLocations element

(defn new-geo-locations-generator [location]
  (cne/new-container-nested-element-generator
   {:min-occurs          0
    :element-factory-fns [new-geo-location-generator]
    :tag                 ::datacite/geoLocations
    :parent-location     location}))
