(ns pedestal-lite-demo.behavior
    (:require [clojure.string :as string]
              [io.pedestal.app :as app]
              [io.pedestal.app.messages :as msg]
              [io.pedestal.app.dataflow :as dataflow]))

(defn round [n places]
  (let [p (Math/pow 10 places)]
    (/ (Math/round (* p n)) p)))

(defn round-number-post [[op path n]]
  [[op path (round n 2)]])

(defn total-count [_ nums]
  (reduce + nums))

(defn max-count [old nums]
  (apply max (or old 0) nums))

(defn average-count [_ {:keys [total nums]}]
  (/ total (count nums)))

(defn publish-counter  [count]
  [{msg/type :swap msg/topic [:other-counters] :value count}])

(defn inc-transform [old _]
  ((fnil inc 0) old))

(defn swap-transform
  [_ message]
  (:value message))

(defn cumulative-average [debug key x]
  (let [k (last key)
        i (inc (or (::avg-count debug) 0))
        avg (or (::avg-raw debug) 0)
        new-avg (+ avg (/ (- x avg) i))]
    (assoc debug
      ::avg-count i
      ::avg-raw new-avg
      (keyword (str (name k) "-avg")) (int new-avg))))

(defn init-emitter [_]
  [[:transform-enable [:tutorial :my-counter] :inc [{msg/topic [:my-counter]}]]])

(def example-app
  {:version 2
   :debug true
   :transform [[:inc [:*] inc-transform]
               [:swap [:other-counters :*] swap-transform]
               [:debug [:pedestal :**] swap-transform]]
   :derive #{[#{[:my-counter] [:other-counters :*]} [:total-count] total-count :vals]
             [#{[:my-counter] [:other-counters :*]} [:max-count] max-count :vals]

             [{[:my-counter] :nums
               [:other-counters :*] :nums
               [:total-count] :total}
              [:average-count] average-count :map]

             [#{[:pedestal :debug :dataflow-time]} [:pedestal :debug :dataflow-time-max] max-count :vals]
             [#{[:pedestal :debug :dataflow-time]} [:pedestal :debug] cumulative-average :map-seq]
}

   :effect #{[#{[:my-counter]} publish-counter :single-val]}
   :emit [[#{[:pedestal :debug :dataflow-time]
             [:pedestal :debug :dataflow-time-max]
             [:pedestal :debug :dataflow-time-avg]} (app/default-emitter [])]
          {:in #{[:my-counter]
                 [:other-counters :*]
                 [:total-count]
                 [:max-count]
                 [:average-count]}
           :fn (app/default-emitter :tutorial)
           :init init-emitter}]})
