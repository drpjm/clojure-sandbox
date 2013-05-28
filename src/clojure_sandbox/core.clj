(ns clojure-sandbox.core)

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