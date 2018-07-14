(defproject org.cyverse/metadata-files "1.0.1"
  :description "Library for generating metadata files."
  :url "https://github.com/cyverse-de/metadata-files"
  :license {:name "BSD"
            :url "http://www.cyverse.org/license"}
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :plugins [[jonase/eastwood "0.2.3"]
            [test2junit "1.2.2"]]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.xml "0.2.0-alpha5"]]
  :profiles {:dev {:resource-paths ["test-resources"]}})
