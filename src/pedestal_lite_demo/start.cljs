(ns pedestal-lite-demo.start
  (:require [io.pedestal.app.protocols :as p]
            [io.pedestal.app :as app]
            [io.pedestal.app.render.push :as push-render]
            [io.pedestal.app.render :as render]
            [io.pedestal.app.messages :as msg]
            [pedestal-lite-demo.behavior :as behavior]
            #_[pedestal-lite-demo.services :as services]
            [pedestal-lite-demo.simulated.services :as services]
            [pedestal-lite-demo.rendering :as rendering]))

(defn round [n places]
  (let [p (Math/pow 10 places)]
    (/ (Math/round (* p n)) p)))

(defn round-number-post [[op path n]]
  [[op path (round n 2)]])

(defn add-post-processors [dataflow]
  (assoc dataflow :post {:app-model [[:value [:tutorial :average-count] round-number-post]]}))

(defn create-app [render-config]
  (let [app (app/build (add-post-processors behavior/example-app))
        render-fn (push-render/renderer "content" render-config render/log-fn)
        app-model (render/consume-app-model app render-fn)]
    (app/begin app)
    (p/put-message (:input app) {msg/type :set-value msg/topic [:greeting] :value "Hello World!"})
    {:app app :app-model app-model}))

(defn ^:export main []
  (let [app (create-app (rendering/render-config))
        services (services/->Services (:app app))]
    (app/consume-effects (:app app) services/services-fn)
    (p/start services)
    app))

