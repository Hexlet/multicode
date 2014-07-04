# multicode
[![Build Status](https://travis-ci.org/mokevnin/multicode.svg?branch=travis)](https://travis-ci.org/mokevnin/multicode)

## Description

Transforms code written on Clojure to another languages

## Accessible languages

PHP, Python, Ruby, JavaScript

## Develoment

* Install leinengen

## Usage

~~~Clojure
(prettify-code :ruby 
                        [ '(let [arr [\a \b \c] x 5 z 8] 
                            (assert-equal \b (fetch arr 1 \d))
                            (assert-equal \d (fetch arr 5 \d))
                            (assert-equal \c (fetch arr -1 \d))
                            (assert-equal \d (fetch arr -5 \d)))])

#=>"arr = ['a', 'b', 'c']\nx = 5\nz = 8\nassert_equal('b', fetch(arr, 1, 'd'))\nassert_equal('d', fetch(arr, 5, 'd'))\nassert_equal('c', fetch(arr, -1, 'd'))\nassert_equal('d', fetch(arr, -5, 'd'))"  
~~~

~~~Clojure
(prettify-expression :javascript '(assert-equal 3 (fib 4)))

#=>"assertEqual(3, fib(4));"
~~~
