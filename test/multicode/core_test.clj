(ns multicode.core-test
  (:require [clojure.test :refer :all]
            [multicode.core :refer :all]))

(deftest ruby-test
  (are [ruby clj] (= ruby (prettify-expression :ruby clj))
       "my_var = ['1', 2, [:inner, 'ha']]" '(def my-var ["1" 2 [:inner "ha"]])
       "x = {:a => 3, :b => 'u', :inner => {:key => :value}}" '(def x {:a 3, :b "u", :inner {:key :value}})
       "assert_equal(3, fib(4))" '(assert-equal 3 (fib 4))
       "assert(!false)" '(assert (not false))))

(deftest javascript-test
  (are [javascript clj] (= javascript (prettify-expression :javascript clj))
       "var myVar = ['1', 2, ['inner', 'ha']];" '(def my-var ["1" 2 [:inner "ha"]])
       "var x = {a: 3, b: 'u', inner: {key: 'value'}};" '(def x {:a 3, :b :u, :inner {:key :value}})
       "assertEqual(3, fib(4));" '(assert-equal 3 (fib 4))
       "assert(!false);" '(assert (not false))))

(deftest php-test
  (are [php clj] (= php (prettify-expression :php clj))
       "$myVar = array('1', 2, array('inner', 'ha'));" '(def my-var ["1" 2 [:inner "ha"]])
       "$x = array('a' => 3, 'b' => 'u', 'inner' => array('key' => 'value'));" '(def x {:a 3, :b "u", :inner {:key :value}})
       "assertEqual(3, fib(4));" '(assert-equal 3 (fib 4))
       "assert(!false);" '(assert (not false))))

(deftest python-test
  (are [python clj] (= python (prettify-expression :python clj))
    "my_var = ['1', 2, ['inner', 'ha']]" '(def my-var ["1" 2 [:inner "ha"]])
    "x = {'a': 3, 'b': 'u', 'inner': {'key': 'value'}}" '(def x {:a 3, :b "u", :inner {:key :value}})
    "assertEqual(3, fib(4))" '(assert-equal 3 (fib 4))
    "assert(!False)" '(assert (not false))))
