(ns org.cyverse.metadata-files)

(defprotocol FlatElementFactory
  (required-attributes [_] "Returns the set of required attributes.")
  (missing-attributes [_] "Returns the set of required attributes that are missing.")
  (generate [_] "Generates the element."))

(defprotocol NestedElementFactory
  (attribute-name [_] "The name of the primary attribute used to generate the element.")
  (min-occurs [_] "Returns the minimum number of occurrences of the element.")
  (max-occurs [_] "Returns the maximum number of occurrences of the element.")
  (child-element-factories [_] "Returns the list of element factories for child elements.")
  (get-location [_] "Returns the location of the current attribute in the metadata template hierarchy.")
  (validate [_ attribute] "Validates the attributes used to generate the element.")
  (generate-nested [_ attribute] "Generates the element."))

(defprotocol XmlSerializable
  (to-xml [_] "Serializes the object as XML. This function returns an instance of Clojure.data.xml.Element."))
