(defproject org.cyverse/metadata-files "2.0.0"
  :description "Library for generating metadata files."
  :url "https://github.com/cyverse-de/metadata-files"
  :license {:name "BSD"
            :url "http://www.cyverse.org/license"}
  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :plugins [[jonase/eastwood "0.3.5"]
            [test2junit "1.2.2"]]
  :eastwood {:exclude-linters [:implicit-dependencies]}
  :dependencies [[medley "1.1.0"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/data.xml "0.2.0-alpha6"]
                 [org.clojure/tools.logging "0.4.1"]]
  :profiles {:dev {:resource-paths ["test-resources"]}})
