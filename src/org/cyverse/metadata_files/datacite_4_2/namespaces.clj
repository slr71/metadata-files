(ns org.cyverse.metadata-files.datacite-4-2.namespaces
  (:use [clojure.data.xml :only [alias-uri]]))

(defn alias-uris []
  (alias-uri :datacite "http://datacite.org/schema/kernel-4")
  (alias-uri :xml "http://www.w3.org/XML/1998/namespace")
  (alias-uri :xsi "http://www.w3.org/2001/XMLSchema-instance"))
