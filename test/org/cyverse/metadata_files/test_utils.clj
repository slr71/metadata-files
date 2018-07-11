(ns org.cyverse.metadata-files.test-utils
  (:require [clj-xml-validation.core :refer [create-validation-fn]]
            [clojure.data.xml :as xml]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.test :refer :all]))

(defn build-attributes
  "Provides a convenient way to generate metadata attributes in the format returned by the metadata service. The
   attributes can be defined as a map if there are no duplicate attribute names. Otherwise, a sequence of vectors
   must be used."
  [attrs]
  (mapv (fn [[attr value]] {:attr attr :value value}) attrs))

(defn- build-validator
  "Builds a schema validator for one or more schema locations"
  [schema-locations]
  (when (seq schema-locations)
    (apply create-validation-fn schema-locations)))

;; Comparing generated XML to parsed XML requires us to serialize and parse the generated XML.
(defn test-xml
  "Compares XML in a file to generated XML. The XML in the file is assumed to be the expected value and the
   generated XML is assumed to be the actual value to compare to the expected value. The optional schema-locations
   parameter can be used to validate the generated XML against one or more XML schemas as well."
  [filename xml & [schema-locations]]
  (println (xml/indent-str xml))
  (println (xml/indent-str (xml/parse (io/reader (io/resource filename)))))
  (let [xml-str (xml/emit-str xml)]
    (when-let [valid-xml? (build-validator schema-locations)]
      (is (valid-xml? (string/replace xml-str #"<\?[^?]*\?>" ""))))
    (is (= (xml/parse (io/reader (io/resource filename)))
           (xml/parse-str xml-str)))))
