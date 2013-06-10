(ns clojure-sandbox.basic)

(require '(clojure-sandbox [concurrency-util :as cutil]))

; Working through the conurrency section in Clojure Programming by Emerick.

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

;(set-validator! sarah #(or (:age %) 
;                              (throw (IllegalStateException. "You need to have an age!"))))

; note this anonymous function passed as the validator function! what is passed into %? the atom?
; Answer: Well, it appears that dissoc would get tested, then (:age @atom) is called and or'ed. The or will
; fail, making the exception get thrown.

;(swap! sarah dissoc :age)
