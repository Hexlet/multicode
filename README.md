# multicode
[![Build Status](https://travis-ci.org/Hexlet/multicode.svg?branch=travis)](https://travis-ci.org/mokevnin/multicode)

## Description

Transforms code written in Clojure into other languages

## Supported languages

PHP, Python, Ruby, JavaScript

## Develoment

* Install leiningen

## Usage


    (prettify-code :ruby
                   [ '(let [arr [\a \b \c] x 5 z 8]
                   (assert-equal \b (fetch arr 1 \d))
                   (assert-equal \d (fetch arr 5 \d))
                   (assert-equal \c (fetch arr -1 \d))
                   (assert-equal \d (fetch arr -5 \d)))])

    #=>"arr = ['a', 'b', 'c']
    x = 5
    z = 8
    assert_equal('b', fetch(arr, 1, 'd'))
    assert_equal('d', fetch(arr, 5, 'd'))
    assert_equal('c', fetch(arr, -1, 'd'))
    assert_equal('d', fetch(arr, -5, 'd'))"

    (prettify-expression :javascript '(assert-equal 3 (fib 4)))

    #=>"assertEqual(3, fib(4));"
