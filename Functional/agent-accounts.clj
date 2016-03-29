(def bob (ref 200000))
(def joe (ref 300000))
(def inconsistency (atom 0))

(defn transfer [source destination amount]
  (send source - amount)
  (send destination + amount)
