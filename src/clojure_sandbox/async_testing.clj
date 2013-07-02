(ns clojure-sandbox.async-testing)

(require '[clojure.core.async :as async :refer :all])
(import 'java.util.concurrent.ScheduledThreadPoolExecutor 'java.util.concurrent.TimeUnit 'java.util.concurrent.Executors)

; making a scheduled thread pool...
;(let [sched-pool (java.util.concurrent.ScheduledThreadPoolExecutor. 1)]
;        (.scheduleAtFixedRate sched-pool (fn [] (println "Hello! from pool")) 0 1 TimeUnit/SECONDS))

;(let [executor (Executors/newSingleThreadScheduledExecutor)]
;        (.scheduleAtFixedRate executor (fn [] (println "Hello! from single thread")) 0 1 TimeUnit/SECONDS))

(defn pub-msg
        "This function sends a msg over the pub-chan"
        [pub-chan]
        (go (>! pub-chan "--Greetings--") ))

(defn recv-msg
        "This function reads a msg from sub-chan and capitalizes it!"
        [sub-chan]
        (println (.toUpperCase (<!! sub-chan))))

(def test-chan (chan 10))
(def receiver (future (while true (recv-msg test-chan) )))
; this works!
(>!! test-chan "awesomesauce")

; and this too!
(let [sched-pool (java.util.concurrent.ScheduledThreadPoolExecutor. 1)]
        (.scheduleAtFixedRate sched-pool (fn [] (>!! test-chan "go go!")) 0 1 TimeUnit/SECONDS))