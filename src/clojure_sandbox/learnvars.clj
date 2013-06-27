(ns clojure-sandbox.learnvars)

; all top level functions and values are stored as vars

; an example of a private var - only accessible within THIS ns
(def ^{:private true} everything 42)

; to get it from outside, need to dereference:
(in-ns 'temp-ns)
@#'clojure-sandbox.learnvars/everything

; and back to the learnvars...
(in-ns 'clojure-sandbox.learnvars)

; oh look - more meta data: the const
(def ^:const
   c 
  "awesome constant" 
  "Yeah buddy!!!")

; the binding form to do it.
(def ^:dynamic *maxvalue* 1024)
(defn valid-value?
     [v]
     (< v *maxvalue*))
(binding [*maxvalue* 256]
        (println (valid-value? 257) )
        (doto (Thread. #(println "in other thread: " (valid-value? 257)))
          .start
          .join))