(def bob (atom 200000))
(def joe (atom 300000))
(def inconsistencies (atom 0))

(defn transfer [source destination amount]
  (if (not= (+ @bob @joe) 500000) (swap! inconsistencies inc))
  (swap! source - amount)
  (swap! destination + amount))

(defn first-transfer []
  (dotimes [cnt 100000]
    (transfer bob joe 2)))

(defn second-transfer []
  (dotimes [cnt 100000]
    (transfer joe bob 1)))

(def first-future (future (first-transfer)))
(def second-future (future (second-transfer)))
@first-future
@second-future
(println "Bob has in account: " @bob)
(println "Joe has in account: " @joe)
(println "Inconsistencies while transfer: " @inconsistencies)
