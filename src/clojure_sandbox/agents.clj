(ns clojure-sandbox.agents)

; agents are asynchronous reference types
; use send and send-off to submit an action

(def a (agent 5000))
(def b (agent 10000))

(send-off a #(java.lang.Thread/sleep %))
(send-off b #(java.lang.Thread/sleep %))

(def a (agent nil))
(send a (fn [_] (throw (Exception. "uh oh!"))))
(agent-error a)