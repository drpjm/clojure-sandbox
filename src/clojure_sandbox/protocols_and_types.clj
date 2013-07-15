(ns clojure-sandbox.protocols-and-types)

; This file explores the protocol and type features of clojure

(deftype SchrodingerCat [^:unsynchronized-mutable state] ; this type of option is not thread safe! must use locking.
     clojure.lang.IDeref
     (deref [sc]
       (locking sc
         (or state
             (set! state (if (zero? (rand-int 2))
                           :dead
                           :alive))))))

; a factory that makes schrodinger cats!
(defn schrodinger-cat
     []
     (SchrodingerCat. nil))

; the Matrix protocol from pg. 265
(defprotocol Matrix
  "Protocol for working with 2D structures."
  (lookup [matrix i j])
  (update [matrix i j value])
  (rows [matrix])
  (cols [matrix])
  (dims [matrix]))

; let's extend this protocol to Points!
; note that records follow "value semantics" - thus, their values are immutable!
(defrecord Point [x y]
        Matrix
        (lookup [pt i j]
          (when (zero? j)
            (case i
              0 x
              1 y)))
        (update [pt i j value]
          (if (zero? j)
            (condp = i
              0 (Point. value y)
              1 (Point. x value))
            pt))
        (rows [pt] [[x] [y]])
        (cols [pt] [x y])
        (dims [pt] [2 1]))
