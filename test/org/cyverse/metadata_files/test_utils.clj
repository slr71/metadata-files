(ns org.cyverse.metadata-files.test-utils
  (:require [clojure.data.xml :as xml]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.test :refer :all])
  (:import [java.net URL]
           [javax.xml XMLConstants]
           [javax.xml.transform Source]
           [javax.xml.transform.stream StreamSource]
           [javax.xml.validation Schema SchemaFactory Validator]
           [org.xml.sax SAXException]))

(defn build-attributes
  "Provides a convenient way to generate metadata attributes in the format returned by the metadata service. The
   attributes can be defined as a map if there are no duplicate attribute names. Otherwise, a sequence of vectors
   must be used."
  [attrs]
  (mapv (fn [[attr value]] {:attr attr :value value}) attrs))

(defn- get-schema-validtor
  "Gets an XML schema validator for a schema URL."
  [schema-location]
  (-> (SchemaFactory/newInstance XMLConstants/W3C_XML_SCHEMA_NS_URI)
      (.newSchema (URL. schema-location))
      (.newValidator)))

(def build-validation-fn
  (memoize
   (fn [schema-location]
     (let [validator (get-schema-validtor schema-location)]
       (fn [xml-str]
         (let [source (StreamSource. (io/input-stream (.getBytes xml-str)))]
           (try
             (.validate validator source)
             true
             (catch SAXException e
               (println "XML schema validation failed: " (.getMessage e))
               false))))))))

;; Comparing generated XML to parsed XML requires us to serialize and parse the generated XML.
(defn test-xml
  "Compares XML in a file to generated XML. The XML in the file is assumed to be the expected value and the generated
  XML is assumed to be the actual value to compare to the expected value. The schema-location parameter is used to
  validate the generated XML against an XML schema."
  [filename xml & [schema-location debug]]
  (when debug
    (println "Actual:" (xml/indent-str xml))
    (println "Expected:" (xml/indent-str (xml/parse (io/reader (io/resource filename))))))
  (let [xml-str (xml/emit-str xml)]
    (is ((build-validation-fn schema-location) xml-str))
    (is (= (xml/parse (io/reader (io/resource filename)))
           (xml/parse-str xml-str)))))
