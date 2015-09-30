(defproject multicode "0.1.1"
  :description "Asserts compiler for hexlet battle"
  :url "https://battle.hexlet.io"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[slamhound "1.5.4"]
                 [org.clojure/clojure "1.5.1"]]
  :plugins  [[lein-exec "0.3.3"]
             [lein-kibit "0.0.8"]]
  :main ^:skip-aot multicode.core
  :target-path "target/%s"
  :jvm-opts  ["-XX:+TieredCompilation" "-XX:TieredStopAtLevel=1"]
  :profiles {:uberjar {:aot :all}})
