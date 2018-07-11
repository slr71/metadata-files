(ns org.cyverse.metadata-files.datacite.geolocation
  (:require [clojure.string :as string]))

(defn- validate-latitude [latitude]
  (let [n (Double/parseDouble latitude)]
    (when-not (<= -90.0 n 90.0)
      (throw (ex-info (str "latitude " latitude " out of range")
                      {:min -90.0 :max 90.0 :latitude latitude})))))

(defn- validate-longitude [longitude]
  (let [n (Double/parseDouble longitude)]
    (when-not (<= -180.0 n 180.0)
      (throw (ex-info (str "longitude " longitude " out of range")
                      {:min -180.0 :max 180.0 :longitude longitude})))))

(defn- parse-point [point]
  (let [[latitude longitude] (string/split point #"\s+" 2)]
    (validate-latitude latitude)
    (validate-longitude longitude)
    [latitude longitude]))

(defn- parse-box [box]
  (let [[lat-1 long-1 lat-2 long-2] (string/split box #"\s+" 4)]
    (validate-latitude lat-1)
    (validate-longitude long-1)
    (validate-latitude lat-2)
    (validate-longitude long-2)
    (concat (sort-by #(Double/parseDouble %) [lat-1 lat-2])
            (sort-by #(Double/parseDouble %) [long-1 long-2]))))

(defn parse-attrs [[place point box :as attrs]]
  (when attrs
    [(when-not (string/blank? place) place)
     (when-not (string/blank? point) (parse-point point))
     (when-not (string/blank? box) (parse-box box))]))
