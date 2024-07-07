@demo
Feature: sample karate api test script
  for help, see: https://github.com/intuit/karate/wiki/ZIP-Release

  Background:
    * url 'https://jsonplaceholder.typicode.com'

  Scenario: get all users and then get the first user by id
    Given path 'users'
    When method get
    Then status 200

    * def first = response[0]

    Given path 'users', first.id
    When method get
    Then status 200

  Scenario: get all todos and then get the first todo item by id
    Given path 'todos'
    When method get
    Then status 200

    * def first = response[0]

    Given path 'todos', first.id
    When method get
    Then status 200
