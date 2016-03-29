(def result (promise))
(future (println "The result is: " @result))
(Thread/sleep 2000)
(deliver result 42)
