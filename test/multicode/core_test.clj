(ns multicode.core-test
  (:require [clojure.test :refer :all]
            [multicode.core :refer :all]))

(def src
  '[(assert-equal 3 (fib 4))])

(def ruby-code
  "assert_equal(3, fib(4))")

(deftest ruby-test
  (is (= ruby-code (prettify-code src))))

;TODO assert assert_not ==
