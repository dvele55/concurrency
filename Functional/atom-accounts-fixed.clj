(def accounts (atom {:bob 200000, :joe 300000}))
(def inconsistencies (atom 0))

(defn transfer [source destination amount]
  (let [deref-accounts @accounts]
    (if (not= (+ (get deref-accounts :bob) (get deref-accounts :joe)) 500000)
      (swap! inconsistencies inc))
    (swap! accounts
           (fn [accs]
             (update (update accs source - amount) destination + amount)))))

(defn first-transfer []
  (dotimes [cnt 100000]
    (transfer :bob :joe 2)))


(defn second-transfer []
  (dotimes [cnt 100000]
    (transfer :joe :bob 1)))

(def first-future (future (first-transfer)))
(def second-future (future (second-transfer)))
@first-future
@second-future
(println "Bob has in account: " (get @accounts :bob))
(println "Joe has in account: " (get @accounts :joe))
(println "Inconsistencies while transfer: " @inconsistencies)
