(ns clojure-sandbox.lamina-testing)

(use 'lamina.executor 'lamina.core 'lamina.viz)
(import 'java.util.concurrent.ScheduledThreadPoolExecutor 'java.util.concurrent.TimeUnit 'java.util.concurrent.Executors)

; build a scheduled thread pool
(def sched-pool (java.util.concurrent.ScheduledThreadPoolExecutor. 2))

; helper functions - scheduling and re-scheduling tasks
(defn schedule-task [pool task period unit]
  (.scheduleAtFixedRate pool task 0 period unit))

(defn reschedule-task [pool fut f period unit]
  (future-cancel fut)
  (.scheduleAtFixedRate pool f 0 period unit))

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

; Let's put the threading and lamina together!
(def robot-update-task
  (schedule-task sched-pool #(enqueue robot-chan {:x 6 :y 7 :theta 90}) 1 TimeUnit/SECONDS))


      