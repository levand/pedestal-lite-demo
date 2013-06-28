(ns pedestal-lite-demo.html-templates
  (:use [io.pedestal.app.templates :only [tfn dtfn tnodes]]))

(defmacro pedestal-lite-demo-templates
  []
  {:pedestal-lite-demo-page (dtfn (tnodes "templates/pedestal-lite-demo.html"
                                       "pedestal-lite-demo" [[:#other-counters]])
                               #{:id})
   :other-counter (dtfn (tnodes "templates/pedestal-lite-demo.html"
                                "other-counter") #{:id})})
