(defproject org.cyverse/metadata-files "1.0.3-SNAPSHOT"
  :description "Library for generating metadata files."
  :url "https://github.com/cyverse-de/metadata-files"
  :license {:name "BSD"
            :url "http://www.cyverse.org/license"}
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :plugins [[jonase/eastwood "0.3.5"]
            [test2junit "1.2.2"]]
  :eastwood {:exclude-linters [:implicit-dependencies]}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.xml "0.2.0-alpha6"]]
  :profiles {:dev {:resource-paths ["test-resources"]}})
