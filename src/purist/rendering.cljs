(ns purist.rendering
  (:require [domina :as dom]
            [io.pedestal.app.render.push :as render]
            [io.pedestal.app.render.push.handlers :as h]
            [io.pedestal.app.render.push.templates :as templates]
            [io.pedestal.app.render.push.handlers.automatic :as d])
  (:require-macros [purist.html-templates :as html-templates]))

(def templates (html-templates/purist-templates))

(defn render-template [template-name initial-value-fn]
  (fn [renderer [_ path :as delta] input-queue]
    (let [parent (render/get-parent-id renderer path)
          id (render/new-id! renderer path)
          html (templates/add-template renderer path (template-name templates))
          content (html (assoc (initial-value-fn delta) :id id))]
      (.log js/console content)
      (.log js/console parent)
      (dom/append! (dom/by-id parent) content))))

(defn render-value [renderer [_ path _ new-value] input-queue]
  (let [key (last path)]
    (templates/update-t renderer [:tutorial] {key (str new-value)})))

(defn render-other-counters-element [renderer [_ path] _]
  (render/new-id! renderer path "other-counters"))

(defn render-other-counter-value [renderer [_ path _ new-value] input-queue]
  (let [key (last path)]
    (templates/update-t renderer path {:count (str new-value)})))

(defn render-config []
  [[:node-create [:tutorial] (render-template :purist-page
                                              (constantly {:my-counter "0"}))]
   [:node-destroy [:tutorial] h/default-destroy]
   [:transform-enable [:tutorial :my-counter] (h/add-send-on-click "inc-button")]
   [:transform-disable [:tutorial :my-counter] (h/remove-send-on-click "inc-button")]
   [:value [:tutorial :*] render-value]
   [:value [:pedestal :debug :*] render-value]

   [:node-create [:tutorial :other-counters] render-other-counters-element]
   [:node-create [:tutorial :other-counters :*]
    (render-template :other-counter
                     (fn [[_ path]] {:counter-id (last path)}))]
   [:value [:tutorial :other-counters :*] render-other-counter-value]])


