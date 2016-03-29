(def counter (agent 0))
(def attempts (atom 0))

(defn counter-increases[]
  (dotimes [cnt 500000]
    (send counter (fn [counter]
                    (swap! attempts inc)
                    (inc counter)))))

(def first-future (future (counter-increases)))
(def second-future (future (counter-increases)))
; wait for futures to complete
@first-future
@second-future
; wait for counter to be finished with updating
(await counter)
; print the value of the counter
(println "The counter is: " @counter)
(println "Number of attempts: " @attempts)
