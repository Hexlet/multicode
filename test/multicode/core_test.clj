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

(deftest let-python-test
  (is (= "arr = ['a', 'b', 'c']\nassert(=('b', fetch(arr, 1, 'd')))\nassert(=('d', fetch(arr, 5, 'd')))\nassert(=('c', fetch(arr, -1, 'd')))\nassert(=('d', fetch(arr, -5, 'd')))" 
         (prettify-code :python 
                        ['(let [arr [\a \b \c]])
                         '(assert (= \b (fetch arr 1 \d)))
                         '(assert (= \d (fetch arr 5 \d)))
                         '(assert (= \c (fetch arr -1 \d)))
                         '(assert (= \d (fetch arr -5 \d)))]))))

(deftest let-ruby-test
  (is (= "arr = ['a', 'b', 'c']\nassert(=('b', fetch(arr, 1, 'd')))\nassert(=('d', fetch(arr, 5, 'd')))\nassert(=('c', fetch(arr, -1, 'd')))\nassert(=('d', fetch(arr, -5, 'd')))" 
         (prettify-code :ruby 
                        ['(let [arr [\a \b \c]])
                         '(assert (= \b (fetch arr 1 \d)))
                         '(assert (= \d (fetch arr 5 \d)))
                         '(assert (= \c (fetch arr -1 \d)))
                         '(assert (= \d (fetch arr -5 \d)))])))) 

(deftest let-java-scritp-test
  (is (= "var arr = ['a', 'b', 'c'];\nassert(=('b', fetch(arr, 1, 'd')));\nassert(=('d', fetch(arr, 5, 'd')));\nassert(=('c', fetch(arr, -1, 'd')));\nassert(=('d', fetch(arr, -5, 'd')));" 
         (prettify-code :javascript 
                        ['(let [arr [\a \b \c]])
                         '(assert (= \b (fetch arr 1 \d)))
                         '(assert (= \d (fetch arr 5 \d)))
                         '(assert (= \c (fetch arr -1 \d)))
                         '(assert (= \d (fetch arr -5 \d)))]))))

(deftest let-php-test
  (is (= "$arr = array('a', 'b', 'c');\nassert(=('b', fetch(arr, 1, 'd')));\nassert(=('d', fetch(arr, 5, 'd')));\nassert(=('c', fetch(arr, -1, 'd')));\nassert(=('d', fetch(arr, -5, 'd')));" 
         (prettify-code :php 
                        ['(let [arr [\a \b \c]])
                         '(assert (= \b (fetch arr 1 \d)))
                         '(assert (= \d (fetch arr 5 \d)))
                         '(assert (= \c (fetch arr -1 \d)))
                         '(assert (= \d (fetch arr -5 \d)))]))))

(deftest let-few-variable-ruby
  (is (= "arr = ['a', 'b', 'c']\nx = 5\nz = 8\nassert(=('b', fetch(arr, 1, 'd')))\nassert(=('d', fetch(arr, 5, 'd')))\nassert(=('c', fetch(arr, -1, 'd')))\nassert(=('d', fetch(arr, -5, 'd')))"
         (prettify-code :ruby 
                        ['(let [arr [\a \b \c] x 5 z 8])
                         '(assert (= \b (fetch arr 1 \d)))
                         '(assert (= \d (fetch arr 5 \d)))
                         '(assert (= \c (fetch arr -1 \d)))
                         '(assert (= \d (fetch arr -5 \d)))]))))

(deftest let-with-asserts-ruby
  (is (= "arr = ['a', 'b', 'c']\nx = 5\nz = 8\nassert(=('b', fetch(arr, 1, 'd')))\nassert(=('d', fetch(arr, 5, 'd')))\nassert(=('c', fetch(arr, -1, 'd')))\nassert(=('d', fetch(arr, -5, 'd')))"
         (prettify-code :ruby 
                        [ '(let [arr [\a \b \c] x 5 z 8] 
                            (assert (= \b (fetch arr 1 \d)))
                            (assert (= \d (fetch arr 5 \d)))
                            (assert (= \c (fetch arr -1 \d)))
                            (assert (= \d (fetch arr -5 \d))))])))) 

(deftest let-with-asserts-php
  (is (=  "$arr = array('a', 'b', 'c');\n$x = 5;\n$z = 8;\nassert(=('b', fetch(arr, 1, 'd')));\nassert(=('d', fetch(arr, 5, 'd')));\nassert(=('c', fetch(arr, -1, 'd')));\nassert(=('d', fetch(arr, -5, 'd')));"
         (prettify-code :php 
                        [ '(let [arr [\a \b \c] x 5 z 8] 
                            (assert (= \b (fetch arr 1 \d)))
                            (assert (= \d (fetch arr 5 \d)))
                            (assert (= \c (fetch arr -1 \d)))
                            (assert (= \d (fetch arr -5 \d))))])))) 

(deftest let-with-asserts-javascript
  (is (=  "var arr = ['a', 'b', 'c'];\nvar x = 5;\nvar z = 8;\nassert(=('b', fetch(arr, 1, 'd')));\nassert(=('d', fetch(arr, 5, 'd')));\nassert(=('c', fetch(arr, -1, 'd')));\nassert(=('d', fetch(arr, -5, 'd')));"
         (prettify-code :javascript 
                        [ '(let [arr [\a \b \c] x 5 z 8] 
                            (assert (= \b (fetch arr 1 \d)))
                            (assert (= \d (fetch arr 5 \d)))
                            (assert (= \c (fetch arr -1 \d)))
                            (assert (= \d (fetch arr -5 \d))))])))) 

(deftest let-with-asserts-python
  (is (= "arr = ['a', 'b', 'c']\nx = 5\nz = 8\nassert(=('b', fetch(arr, 1, 'd')))\nassert(=('d', fetch(arr, 5, 'd')))\nassert(=('c', fetch(arr, -1, 'd')))\nassert(=('d', fetch(arr, -5, 'd')))" 
         (prettify-code :python 
                        ['(let [arr [\a \b \c] x 5 z 8] 
                           (assert (= \b (fetch arr 1 \d)))
                           (assert (= \d (fetch arr 5 \d)))
                           (assert (= \c (fetch arr -1 \d)))
                           (assert (= \d (fetch arr -5 \d))))]))))