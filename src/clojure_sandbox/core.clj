(ns clojure-sandbox.core)

; Working through the conurrency section in Clojure Programming by Emerick.

; Useful testing macro for concurrency work:
(defmacro futures
  [n & exprs]
  (vec (for [_ (range n)
             expr exprs]
         `(future ~expr))))

(defmacro wait-futures
  [& args]
  `(doseq [f# (futures ~@args)]
     @f#))

; Adding a watch to an atom - atom uses an anonymous function
(defn watch-func
  [key id old new]
  (println old "=>" new))

(def xfunc (atom (fn [n] (* 2 n))))

; utilize the xfunc atom
(@xfunc 5) ; outputs 10

; add a watch function to see what happens
(add-watch xfunc :watch-func watch-func)

; reset the function to multiply by 3
(reset! xfunc (fn [n] (* 3 n)))

(@xfunc 5) ; outputs 15

; Following the example on validators here...
(def sarah (atom {:name "Sarah" :age 34}))
; make the :age key required for a valid atom operation
(set-validator! sarah #(or (:age %) 
                              (throw (IllegalStateException. "You need to have an age!"))))
; note this anonymous function passed as the validator function! what is passed into %? the atom?
; Answer: Well, it appears that dissoc would get tested, then (:age @atom) is called and or'ed. The or will
; fail, making the exception get thrown.
(swap! sarah dissoc :age)

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
(defn loot
  [from to]
  (dosync ; needed to set up a transaction
    (when-let [item (first (:items @from))]
      (alter to update-in [:items] conj item)
      (alter from update-in [:items] disj item))))

; have the characters loot Smaug!
(wait-futures 1
                 (while (loot smaug bilbo))
                 (while (loot smaug gandalf)))

