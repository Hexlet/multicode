(ns multicode.core-test
  (:require [clojure.test :refer :all]
            [multicode.core :refer :all]))

(deftest ruby-test
  (are [ruby clj] (= ruby (prettify-expression :ruby clj))
       "my_var = ['1', 2, [:inner, 'ha']]" '(def my-var ["1" 2 [:inner "ha"]])
       "their_var = ['1', 2, [:inner, 'he', ['innermost', 3]]]" '(def their-var '("1" 2 [:inner "he" ("innermost" 3)]))
       "your_var = [1, '2', nil, [nil, [nil]]]" '(def your-var '(1 "2" nil (nil [nil])))
       "x = {:a => 3, :b => 'u', :inner => {:key => :value}}" '(def x {:a 3, :b "u", :inner {:key :value}})
       "assert_equal(3, fib(4))" '(assert-equal 3 (fib 4))
       "assert(!false)" '(assert (not false))))

(deftest javascript-test
  (are [javascript clj] (= javascript (prettify-expression :javascript clj))
       "var myVar = ['1', 2, ['inner', 'ha']];" '(def my-var ["1" 2 [:inner "ha"]])
       "var theirVar = ['1', 2, ['inner', 'he', ['innermost', 3]]];" '(def their-var '("1" 2 [:inner "he" ("innermost" 3)]))
       "var yourVar = [1, '2', null, [null, [null]]];" '(def your-var '(1 "2" nil (nil [nil])))
       "var x = {a: 3, b: 'u', inner: {key: 'value'}};" '(def x {:a 3, :b :u, :inner {:key :value}})
       "assertEqual(3, fib(4));" '(assert-equal 3 (fib 4))
       "assert(!false);" '(assert (not false))))

(deftest php-test
  (are [php clj] (= php (prettify-expression :php clj))
       "$myVar = array('1', 2, array('inner', 'ha'));" '(def my-var ["1" 2 [:inner "ha"]])
       "$theirVar = array('1', 2, array('inner', 'he', array('innermost', 3)));" '(def their-var '("1" 2 [:inner "he" ("innermost" 3)]))
       "$yourVar = array(1, '2', nil, array(nil, array(nil)));" '(def your-var '(1 "2" nil (nil [nil])))
       "$x = array('a' => 3, 'b' => 'u', 'inner' => array('key' => 'value'));" '(def x {:a 3, :b "u", :inner {:key :value}})
       "assertEqual(3, fib(4));" '(assert-equal 3 (fib 4))
       "assert(!false);" '(assert (not false))))

(deftest python-test
  (are [python clj] (= python (prettify-expression :python clj))
       "my_var = ['1', 2, ['inner', 'ha']]" '(def my-var ["1" 2 [:inner "ha"]])
       "their_var = ['1', 2, ['inner', 'he', ['innermost', 3]]]" '(def their-var '("1" 2 [:inner "he" ("innermost" 3)]))
       "your_var = [1, '2', None, [None, [None]]]" '(def your-var '(1 "2" nil (nil [nil])))
       "x = {'a': 3, 'b': 'u', 'inner': {'key': 'value'}}" '(def x {:a 3, :b "u", :inner {:key :value}})
       "assert_equal(3, fib(4))" '(assert-equal 3 (fib 4))
       "assert(!False)" '(assert (not false))))

(deftest let-python-test
  (is (= "arr = ['a', 'b', 'c']\nassert_equal('b', fetch(arr, 1, 'd'))\nassert_equal('d', fetch(arr, 5, 'd'))\nassert_equal('c', fetch(arr, -1, 'd'))\nassert_equal('d', fetch(arr, -5, 'd'))"
         (prettify-code :python
                        ['(let [arr [\a \b \c]])
                         '(assert-equal \b (fetch arr 1 \d))
                         '(assert-equal \d (fetch arr 5 \d))
                         '(assert-equal \c (fetch arr -1 \d))
                         '(assert-equal \d (fetch arr -5 \d))]))))

(deftest let-ruby-test
  (is (= "arr = ['a', 'b', 'c']\nassert_equal('b', fetch(arr, 1, 'd'))\nassert_equal('d', fetch(arr, 5, 'd'))\nassert_equal('c', fetch(arr, -1, 'd'))\nassert_equal('d', fetch(arr, -5, 'd'))"
         (prettify-code :ruby
                        ['(let [arr [\a \b \c]])
                         '(assert-equal \b (fetch arr 1 \d))
                         '(assert-equal \d (fetch arr 5 \d))
                         '(assert-equal \c (fetch arr -1 \d))
                         '(assert-equal \d (fetch arr -5 \d))]))))

(deftest let-javascript-test
  (is (= "var arr = ['a', 'b', 'c'];\nassertEqual('b', fetch(arr, 1, 'd'));\nassertEqual('d', fetch(arr, 5, 'd'));\nassertEqual('c', fetch(arr, -1, 'd'));\nassertEqual('d', fetch(arr, -5, 'd'));"
         (prettify-code :javascript
                        ['(let [arr [\a \b \c]])
                         '(assert-equal \b (fetch arr 1 \d))
                         '(assert-equal \d (fetch arr 5 \d))
                         '(assert-equal \c (fetch arr -1 \d))
                         '(assert-equal \d (fetch arr -5 \d))]))))

(deftest let-php-test
  (is (= "$arr = array('a', 'b', 'c');\nassertEqual('b', fetch($arr, 1, 'd'));\nassertEqual('d', fetch($arr, 5, 'd'));\nassertEqual('c', fetch($arr, -1, 'd'));\nassertEqual('d', fetch($arr, -5, 'd'));"
         (prettify-code :php
                        ['(let [arr [\a \b \c]])
                         '(assert-equal \b (fetch arr 1 \d))
                         '(assert-equal \d (fetch arr 5 \d))
                         '(assert-equal \c (fetch arr -1 \d))
                         '(assert-equal \d (fetch arr -5 \d))]))))

(deftest let-few-variable-ruby
  (is (= "arr = ['a', 'b', 'c']\nx = 5\nz = 8\nassert_equal('b', fetch(arr, 1, 'd'))\nassert_equal('d', fetch(arr, 5, 'd'))\nassert_equal('c', fetch(arr, -1, 'd'))\nassert_equal('d', fetch(arr, -5, 'd'))"
         (prettify-code :ruby
                        ['(let [arr [\a \b \c] x 5 z 8])
                         '(assert-equal \b (fetch arr 1 \d))
                         '(assert-equal \d (fetch arr 5 \d))
                         '(assert-equal \c (fetch arr -1 \d))
                         '(assert-equal \d (fetch arr -5 \d))]))))

(deftest let-with-asserts-ruby
  (is (= "arr = ['a', 'b', 'c']\nx = 5\nz = 8\nassert_equal('b', fetch(arr, 1, 'd'))\nassert_equal('d', fetch(arr, 5, 'd'))\nassert_equal('c', fetch(arr, -1, 'd'))\nassert_equal('d', fetch(arr, -5, 'd'))"
         (prettify-code :ruby
                        [ '(let [arr [\a \b \c] x 5 z 8]
                            (assert-equal \b (fetch arr 1 \d))
                            (assert-equal \d (fetch arr 5 \d))
                            (assert-equal \c (fetch arr -1 \d))
                            (assert-equal \d (fetch arr -5 \d)))]))))

(deftest let-with-asserts-php
  (is (=  "$arr = array('a', 'b', 'c');\n$x = 5;\n$z = 8;\nassertEqual('b', fetch($arr, 1, 'd'));\nassertEqual('d', fetch($arr, 5, 'd'));\nassertEqual('c', fetch($arr, -1, 'd'));\nassertEqual('d', fetch($arr, -5, 'd'));"
         (prettify-code :php
                        [ '(let [arr [\a \b \c] x 5 z 8]
                            (assert-equal \b (fetch arr 1 \d))
                            (assert-equal \d (fetch arr 5 \d))
                            (assert-equal \c (fetch arr -1 \d))
                            (assert-equal \d (fetch arr -5 \d)))]))))

(deftest let-with-asserts-javascript
  (is (=  "var arr = ['a', 'b', 'c'];\nvar x = 5;\nvar z = 8;\nassertEqual('b', fetch(arr, 1, 'd'));\nassertEqual('d', fetch(arr, 5, 'd'));\nassertEqual('c', fetch(arr, -1, 'd'));\nassertEqual('d', fetch(arr, -5, 'd'));"
         (prettify-code :javascript
                        [ '(let [arr [\a \b \c] x 5 z 8]
                            (assert-equal \b (fetch arr 1 \d))
                            (assert-equal \d (fetch arr 5 \d))
                            (assert-equal \c (fetch arr -1 \d))
                            (assert-equal \d (fetch arr -5 \d)))]))))

(deftest let-with-asserts-python
  (is (= "arr = ['a', 'b', 'c']\nx = 5\nz = 8\nassert_equal('b', fetch(arr, 1, 'd'))\nassert_equal('d', fetch(arr, 5, 'd'))\nassert_equal('c', fetch(arr, -1, 'd'))\nassert_equal('d', fetch(arr, -5, 'd'))"
         (prettify-code :python
                        ['(let [arr [\a \b \c] x 5 z 8]
                           (assert-equal \b (fetch arr 1 \d))
                           (assert-equal \d (fetch arr 5 \d))
                           (assert-equal \c (fetch arr -1 \d))
                           (assert-equal \d (fetch arr -5 \d)))]))))

(deftest few-func-infunc
  (is (= "assert_equal('b', fetch(arr, 1, 'd'))\nassert_equal('d', fetch(arr, 5, 'd'))\nassert_equal('c', fetch(arr, -1, 'd'))\nassert_equal('d', fetch(arr, -5, 'd'))"
         (prettify-code :python
                        ['((assert-equal \b (fetch arr 1 \d))
                           (assert-equal \d (fetch arr 5 \d))
                           (assert-equal \c (fetch arr -1 \d))
                           (assert-equal \d (fetch arr -5 \d)))]))))

(deftest unary-operator-test
  (is (= "assert(!valid_credit_card?('440804l234567893'))"
         (prettify-code :ruby
                        ['(assert (not (valid-credit-card? "440804l234567893")))]))))
(deftest object-test
  (is (= "stack = Stack.new([5, 6, 7, 8])\nassert_equal(8, stack.pop())"
         (prettify-code :ruby
                       ['(let [stack (Stack. [5, 6, 7, 8])]
                          (assert-equal 8 (.pop stack)))]))))
