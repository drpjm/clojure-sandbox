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

; basic agent error handling if the "continue" option is used.
(def c (agent nil
                    :error-mode :continue
                    :error-handler (fn [the-agent exception]
                                     (.println java.lang.System/out (.getMessage exception)))))

(send c (fn [_] (throw (Exception. "Something went awry!"))))

; dealing with I/O using agents - uses the fantasy game stuff!
(require '[clojure.java.io :as io])

(def console (agent *out*))
(def character-log (agent (io/writer "character-states.log" :append true)))
; this function will perform the write, uses a type hint to boost performance
(defn write
     [^java.io.Writer w & content]
     (doseq [x (interpose " " content)]
       (.write w (str x)))
     (doto w 
       (.write "\n")
       .flush))
       
; this function allows us to log data when something changes in the reference
(defn log-reference
     [reference & writer-agents]
     (add-watch reference :log
                (fn [_ reference old new]
                  (doseq [writer-agent writer-agents]
                    (send-off writer-agent write new)))))
       

(require '[clojure-sandbox.fantasygame :as game])
(require '[clojure-sandbox.concurrency-util :as cutil])

(def smaug (game/character "Smaug" :health 500 :strength 400))
(def bilbo (game/character "Bilbo" :health 100 :strength 100))
(def gandalf (game/character "Gandalf" :health 75 :mana 1000))
; here we attach the character-log agent and log-reference function to each player
(log-reference bilbo console character-log)
(log-reference smaug console character-log)

; new attack/heal functions that add logging!
(defn attack
     [aggressor target]
     (dosync
       (let [damage (* (rand 0.1) (:strength @aggressor))]
         (send-off console write
                   (:name @aggressor) "hits" (:name @target) "for" damage)
         (commute target update-in [:health] #(max 0 (- % damage))))))

(defn heal
     [healer target]
     (dosync
       (let [aid (* (rand 0.1) (:mana @healer))]
         (when (pos? aid)
           (send-off console write
                     (:name @healer) "heals" (:name @target) "for" aid)
           (commute healer update-in [:mana] - (max 5 (/ aid 5)))
           (commute target update-in [:health] + aid)))))

(cutil/wait-futures 1
                    (game/play bilbo attack smaug)
                    (game/play smaug attack bilbo)
                    (game/play gandalf heal bilbo))
