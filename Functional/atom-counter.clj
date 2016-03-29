(def counter (atom 0))
(def attempts (atom 0))

(defn counter-increases[]
  (dotimes [cnt 500000]
    (swap! counter (fn [counter]
                     (swap! attempts inc) ; side effect DO NOT DO THIS
                     (inc counter)))))

(def first-future (future (counter-increases)))
(def second-future (future (counter-increases)))
; Wait for futures to complete
@first-future
@second-future
; Print value of the counter
(println "The counter is: " @counter)
(println "Number of attempts: " @attempts)
