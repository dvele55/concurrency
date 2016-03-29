(def bob (ref 200000))
(def joe (ref 300000))
(def inconsistencies (atom 0))
(def attmepts (atom 0))
(def transfers (agent 0))

(defn transfer [source destination amount]
  (dosync
   (swap! attmepts inc) ; side effect DO NOT DO THIS
   (send transfers inc)
   (when (not= (+ @bob @joe) 500000)
     (swap! inconsistencies inc)) ; side effect DO NOT DO THIS
   (alter source - amount)
   (alter destination + amount)))

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
(await transfers)
(println "Bob has in account: " @bob)
(println "Joe has in account: " @joe)
(println "Inconsistencies while transfer: " @inconsistencies)
(println "Attempts: " @attmepts)
(println "Transfers: " @transfers)
