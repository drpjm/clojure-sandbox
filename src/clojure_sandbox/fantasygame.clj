(ns clojure-sandbox.fantasygame)

; note - I needed to first reference the "root" package clojure-sandbox before mapping it
; to the name "cutil"
(require '(clojure-sandbox [concurrency-util :as cutil]))

; Work on Refs - for coordinated, synchronous access to state
; Example used is a super basic fantasy game.
(defn character
     [name & {:as opts}]
     (ref (merge {:name name :items #{} :health 500}
                 opts)))
; this function generates characters with opts, which is another map
; The characters!
(def smaug (character "Smaug" :health 500 :strength 400 :items (set (range 50))))
(def bilbo (character "Bilbo" :health 100 :strength 100))
(def gandalf (character "Gandalf" :health 75 :mana 750))
; loot a character!
(defn fixedloot
  [from to]
  (dosync ; needed to set up a transaction
    (when-let [item (first (:items @from))]
      (commute to update-in [:items] conj item) ; replaced alter with commute
      (alter from update-in [:items] disj item))))

; commute does not cause a conflict with the ref - but it must be used carefully!
; alter ensures that the in-transaction and committed ref values are the SAME.

; have the characters loot Smaug!
(time (cutil/wait-futures 1
                 (while (fixedloot smaug bilbo))
                 (while (fixedloot smaug gandalf))))

(defn attack
     [aggressor target]
     (dosync
       (let [damage (* (rand 0.1) (:strength @aggressor))]
         (commute target update-in [:health] #(max 0 (- % damage))))))

(defn heal
     [healer target]
     (dosync
       (let [aid (* (rand 0.1) (:mana @healer))]
         (when (pos? aid)
           (commute healer update-in [:mana] - (max 5 (/ aid 5)))
           (commute target update-in [:health] + aid)))))

(def alive? (comp pos? :health))

(defn play
     [char action other]
     (while (and (alive? @char)
                 (alive? @other)
                 (action char other))
       (java.lang.Thread/sleep (rand-int 50))))

