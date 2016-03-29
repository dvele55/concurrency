(def promise-result (promise))
(def future-result
  (future
    (println "The result is: " + @promise-result)
    13))
(println "Future result is: " @future-result)
(deliver result 42)
