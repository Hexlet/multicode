(ns multicode.core-test
  (:require [clojure.test :refer :all]
            [multicode.core :refer :all]))

(def src
  '(assert-equal 3 (fib 4)))

(def ruby-ast
  '(send nil :assert_equal (int 3) (send nil :fib (int 4))))

(deftest ast-test
  (is (= ruby-ast (ast-generator src))))
