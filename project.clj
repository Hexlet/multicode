(defproject multicode "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.match "0.2.1"]]
  :plugins  [[lein-exec "0.3.3"]]
  :main ^:skip-aot multicode.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :main multicode.core)
