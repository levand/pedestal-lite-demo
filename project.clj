(defproject purist "0.1.0-SNAPSHOT"
  :plugins [[lein-cljsbuild "0.3.2"]]
  :dependencies [[io.pedestal/pedestal.app "0.1.9"]
                 [domina "1.0.1"]
                 [org.clojure/google-closure-library-third-party "0.0-2029-2"]
                 [org.clojure/clojurescript "0.0-1586"]
                 [org.clojure/clojure "1.5.1"]]
  :cljsbuild {:crossovers [purist.behavior
                           io.pedestal.app
                           io.pedestal.app.data.change
                           io.pedestal.app.dataflow
                           io.pedestal.app.messages
                           io.pedestal.app.protocols
                           io.pedestal.app.query
                           io.pedestal.app.queue
                           io.pedestal.app.render
                           io.pedestal.app.render.push
                           io.pedestal.app.tree
                           io.pedestal.app.util.adapters
                           io.pedestal.app.util.observers]
              :builds [{:id "whitespace"
                        :source-paths ["src"]
                        :compiler {:output-to "resources/public/generated/whitespace.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}
                       {:id "advanced"
                        :source-paths ["src"]
                        :compiler {:output-to "resources/public/generated/advanced.js"
                                   :optimizations :advanced}}]})

