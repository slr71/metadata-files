(ns org.cyverse.metadata-files.datacite-4-1.geo-locations
  (:use [org.cyverse.metadata-files.datacite-4-1.namespaces :only [alias-uris]])
  (:require [org.cyverse.metadata-files.container-nested-element :as cne]
            [org.cyverse.metadata-files.simple-nested-element :as sne]))

(alias-uris)

;; General longitude elements

(defn- validate-longitude [location longitude]
  (try
    (let [longitude (Double/parseDouble longitude)]
      (when-not (<= -180.0 longitude 180.0)
        (throw (ex-info "Longitudes must be between -180.0 and 180.0" {:location location :longitude longitude}))))
    (catch NumberFormatException _
      (throw (ex-info "Invalid longitude" {:location location :longitude longitude})))))

(defn new-longitude-generator [attr-name tag location]
  (sne/new-simple-nested-element-generator
   {:attr-name       attr-name
    :validation-fn   validate-longitude
    :tag             tag
    :parent-location location}))

;; General latitude elements

(defn- validate-latitude [location latitude]
  (try
    (let [latitude (Double/parseDouble latitude)]
      (when-not (<= -90.0 latitude 90.0)
        (throw (ex-info "Latitudes must be between -90.0 and 90.0" {:location location :latitude latitude}))))
    (catch NumberFormatException _
      (throw (ex-info "Invalid latitude" {:location location :latitude :latitude})))))

(defn new-latitude-generator [attr-name tag location]
  (sne/new-simple-nested-element-generator
   {:attr-name       attr-name
    :validation-fn   validate-latitude
    :tag             tag
    :parent-location location}))

;; The geoLocationPlace element

(defn new-geo-location-place-generator [location]
  (sne/new-simple-nested-element-generator
   {:attr-name       "geoLocationPlace"
    :min-occurs      0
    :tag             ::datacite/geoLocationPlace
    :parent-location location}))

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
