(ns clojure-sandbox.macros)

; this file contains all practice code with macros

; basic example from book: reverse-it macro
(require '(clojure [string :as str]
                   [walk :as walk]))

(defmacro reverse-it
  [form]
  (walk/postwalk #(if (symbol? %) 
                    (symbol (str/reverse (name %)))
                    %)
                 form))