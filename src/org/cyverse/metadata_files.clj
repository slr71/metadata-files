(ns org.cyverse.metadata-files)

(defprotocol FlatElementFactory
  (required-attributes [_] "Returns the set of required attributes.")
  (missing-attributes [_] "Returns the set of required attributes that are missing."))

(defprotocol NestedElementFactory
  (attribute-name [_] "The name of the primary attribute used to generate the element.")
  (min-occurs [_] "Returns the minimum number of occurrences of the element.")
  (max-occurs [_] "Returns the maximum number of occurrences of the element.")
  (child-element-factories [_] "Returns the list of element factories for child elements.")
  (validate [_] "Validates the attributes used to generate the element."))

(defprotocol ElementFactory
  (generate [_] "Generates the element."))

(defprotocol XmlSerializable
  (to-xml [_] "Serializes the object as XML. This function returns an instance of Clojure.data.xml.Element."))
