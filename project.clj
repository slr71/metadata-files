(defproject org.cyverse/metadata-files "2.1.2-SNAPSHOT"
  :description "Library for generating metadata files."
  :url "https://github.com/cyverse-de/metadata-files"
  :license {:name "BSD"
            :url "http://www.cyverse.org/license"}
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :plugins [[jonase/eastwood "1.4.3"]
            [lein-ancient "1.0.0"]
            [test2junit "1.4.4"]]
  :eastwood {:exclude-linters [:implicit-dependencies]}
  ;; Fail the build on a new dependency conflict rather than printing a
  ;; warning nobody reads.
  :pedantic? :abort
  :dependencies [[medley "1.4.0"]
                 [org.clojure/clojure "1.12.5"]
                 [org.clojure/data.xml "0.2.0-alpha9"]
                 [org.clojure/tools.logging "1.3.1"]]
  :profiles {:dev {:resource-paths ["test-resources"]}})
