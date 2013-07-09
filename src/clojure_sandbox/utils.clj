(ns clojure-sandbox.utils)

; helpful namespace cleaner from stack overflow post: http://stackoverflow.com/questions/3636364/can-i-clean-the-repl
(defn ns-clean
       "Remove all internal mappings from a given name space or the current one if no parameter given."
   ([] (ns-clean *ns*)) 
   ([ns] (map #(ns-unmap ns %) (keys (ns-interns ns)))))