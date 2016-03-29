(defn fibonacci[a]
  (if (<= a 2)
    1
    (+ (fibonacci (- a 1)) (fibonacci (- a 2)))))

(println "Start serial calculation")
(time (println "The result is: " (+ (fibonacci 36) (fibonacci 36))))

(println "Start parallel calculation")
(defn parallel-fibonacci[]
  (def result-1 (future (fibonacci 36)))
  (def result-2 (future (fibonacci 36)))
  (+ @result-1 @result-2))

(time (println "The result is: " (parallel-fibonacci)))
