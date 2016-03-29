(let [a (future
          (println "Started A")
          (Thread/sleep 1000)
          (println "Finished A")
          (+ 1 2))
      b (future
          (println "Started B")
          (Thread/sleep 2000)
          (println "Finished B")
          (+ 3 4))]
  (println "Waiting for futures")
  (+ @a @b))
