(ns org.cyverse.metadata-files)

(defprotocol ElementFactory
  (required-attributes [_] "Returns the set of required attributes.")
  (missing-attributes [_] "Returns the set of required attributes that are missing.")
  (generate [_] "Generates the element."))

(defprotocol XmlSerializable
  (to-xml [_] "Serializes the object as XML. This function returns an instance of Clojure.data.xml.Element."))
