(ns purist.html-templates
  (:use [io.pedestal.app.templates :only [tfn dtfn tnodes]]))

(defmacro purist-templates
  []
  {:purist-page (dtfn (tnodes "templates/purist.html"
                                       "purist" [[:#other-counters]])
                               #{:id})
   :other-counter (dtfn (tnodes "templates/purist.html"
                                "other-counter") #{:id})})
