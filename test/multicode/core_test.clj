(ns multicode.core-test
  (:require [clojure.test :refer :all]
            [multicode.core :refer :all]))

(deftest ruby-test
  (are [ruby clj] (= ruby (prettify-expression clj))
       "x = [1, 2, 3]" '(def x [1 2 3])
       "x = {:a => 3, :b => :u}" '(def x {:a 3, :b :u})
       "assert_equal(3, fib(4))" '(assert-equal 3 (fib 4))
       "assert(!false)" '(assert (not false))))
