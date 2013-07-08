(ns clojure-sandbox.lamina-testing)

(use 'lamina.executor 'lamina.core 'lamina.viz)
;(import 'java.util.concurrent.ScheduledThreadPoolExecutor 'java.util.concurrent.TimeUnit 'java.util.concurrent.Executors)

; build a scheduled thread pool
;(def sched-pool (java.util.concurrent.ScheduledThreadPoolExecutor. 2))

; build a couple functions that use lamina channels
;(def test-chan (channel))
; I don't need to use the exector pool - there is a lamina function called 'probably'
;(.scheduleAtFixedRate sched-pool #(enqueue test-chan "go go!") 0 3 TimeUnit/SECONDS)

; try using the "callback" mechanism...
;(receive test-chan #(println (.toUpperCase %))) ; interesting - it runs once...


;(.scheduleAtFixedRate sched-pool #(println (read-channel test-chan)) 0 3 TimeUnit/SECONDS)

; test to emulate Pancakes-like functionality
(def robot-chan (channel))
(def robot-chan-clone (fork robot-chan)) ; copy of channel for multiple callbacks

(def p #(println %)) ; silly function for callback!
(def p2 #(println % " dos!")) ; another silly function for callback!

; register them to the single robot channel!
(receive-all robot-chan p)
(receive-all robot-chan p2)

; now enqueue something!
(enqueue robot-chan {:x 1 :y 1 :theta 30})





      