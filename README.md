# metadata-files

A Clojure library designed to generate metadata files for the CyVerse Data Commons repository.

## Usage

This library is intended to support the generation of multiple file formats. New formats can be added in the future as
necessary.

### DataCite 3.1

The suggested way to build a DataCite file is to use `org.cyverse.metadata-files.datacite/build-datacite`. This function
accepts a list of AVUs in the format returned by the metadata service and returns an instance of
`clojure.data.xml.Element` representing the metadata file, which can be serialized using any of the serialization
methods in `org.clojure/data.xml`.

The following metadata attributes are currently supported. Note that multiple CyVerse attributes might be associated
with a single DataCite element. For example the CyVerse attributes, `Identifier` and `identifierType` both map to
DataCite's `identifier` element. The `Identifier` attribute maps to the element's content whereas the `identifierType`
attribute maps to the element's `identifierType` attribute.

| CyVerse Attribute        | DataCite Element     |
| ------------------------ | -------------------- |
| Identifier               | identifier           |
| identifierType           | identifier           |
| datacite.creator         | creators             |
| creatorAffiliation       | creators             |
| creatorNameIdentifier    | creators             |
| datacite.title           | titles               |
| datacite.publisher       | publishers           |
| datacite.publicationyear | publicationYear      |
| datacite.resourcetype    | resourceType         |
| Subject                  | subjects             |
| contributorName          | contributors         |
| contributorType          | contributors         |
| AlternateIdentifier      | alternateIdentifiers |
| alternateIdentifierType  | alternateIdentifiers |
| RelatedIdentifier        | relatedIdentifiers   |
| relatedIdentifierType    | relatedIdentifiers   |
| relationType             | relatedIdentifiers   |
| Rights                   | rightsList           |
| Description              | descriptions         |
| descriptionType          | descriptions         |
| geoLocationPlace         | geoLocations         |
| geoLocationPoint         | geoLocations         |
| geoLocationBox           | geoLocations         |

Any attribute that is associated with the data set that is not in this list is ignored. Similarly, any attribute that is
in the list but either contains an empty value or is not associated with the data set is ignored.

Example:

```
user=> (defn build-attributes
  #_=>   "Provides a convenient way to generate metadata attributes in the format returned by the metadata service. The
  #_=>    attributes can be defined as a map if there are no duplicate attribute names. Otherwise, a sequence of vectors
  #_=>    must be used."
  #_=>   [attrs]
  #_=>   (mapv (fn [[attr value]] {:attr attr :value value}) attrs))

user=> (def ^:private min-attrs
  #_=>   {"Identifier"               "the-identifier"
  #_=>    "identifierType"           "DOI"
  #_=>    "datacite.creator"         "Nobody Inparticular"
  #_=>    "creatorAffiliation"       "The University of Nowhere"
  #_=>    "datacite.title"           "A Very Important Data Set"
  #_=>    "datacite.publisher"       "CyVerse Data Commons"
  #_=>    "datacite.publicationyear" "2018"
  #_=>    "datacite.resourcetype"    "Data Set"})

user=> (require '[clojure.data.xml :refer :all] '[org.cyverse.metadata-files.datacite :refer [build-datacite]])
nil

user=> (def xml (build-datacite (build-attributes min-attrs)))
#'user/xml

user=> (println (indent-str xml))
;; output omitted for brevity.
```

### DataCite 4.1

The primary interface for creating a DataCite 4.1 file is essentially the same as the interface for creating a DataCite
3.1 file. The function `org.cyverse.metadata-files.datacite-4-2/build-datacite` returns an instance of
`clojure.data.xml.Element` representing the metadata file. This metadata file generator works with the `DOI Request -
DataCite 4.1` template in the Discovery Environment, which is a nested metadata template. Because the template is
nested, the attributes can have the same names as the DataCite elements. The following attributes are supported.

- title
  - titleType
  - xml:lang
- creator
  - affiliation
  - nameIdentifier
    - nameIdentifierScheme
    - schemeURI
- publisher
- publicationYear
- resourceType
  - resourceTypeGeneral
- contributor
  - contributorType
  - affiliation
  - nameIdentifier
    - nameIdentifierScheme
    - schemeURI
- description
  - descriptionType
  - xml:lang
- subject
  - subjectScheme
  - schemeURI
  - xml:lang
- identifier
- rights
  - rightsURI
- version
- alternateIdentifier
  - alternateIdentifierType
- relatedIdentifier
  - relatedIdentifierType
  - relationType
- geoLocation
  - geoLocationPlace
  - geoLocationPoint
    - pointLongitude
    - pointLatitude
  - geoLocationBox
    - northBoundLatitude
    - southBoundLatitude
    - westBoundLongitude
    - eastBoundLongitude
- fundingreference
  - funderName
    - funderIdentifier
      - funderIdentifierType
- language
- size
- format

As with DataCite 3.1, any attribute that is not in this list is ignored.

Example:

```
user=> (require '[org.cyverse.metadata-files.datacite-4-2 :refer [build-datacite]]
  #_=>          '[clojure.data.xml :refer :all]
  #_=>          :reload-all)


user=> (def min-attrs
  #_=>   [{:attr  "identifier",
  #_=>     :value "   doi:10.1000/182",
  #_=>     :avus  [{:attr  "identifierType",
  #_=>              :value "DOI"}]}
  #_=>    {:attr  "creator",
  #_=>     :value "the-creator"}
  #_=>    {:attr  "title",
  #_=>     :value "the-title"}
  #_=>    {:attr  "publisher",
  #_=>     :value "CyVerse"}
  #_=>    {:attr  "publicationYear",
  #_=>     :value "2019"}
  #_=>    {:attr  "resourceType",
  #_=>     :value "XML",
  #_=>     :avus  [{:attr  "resourceTypeGeneral",
  #_=>              :value "Dataset"}]}])


user=> (println (indent-str (build-datacite min-attrs)))
;; output omitted for brevity.
```

## License

http://www.cyverse.org/license
