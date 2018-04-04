Feature:Queue Features

  Scenario: Should not be able to create Application with empty name
    When invalid name is given as ""
    Then Request for create application should return '400' status
  Scenario: Should be able to create Application with empty name
    When valid name is given as "new application"
    Then Request for create application should return '201' status
  Scenario: Login should fail for wrong credentials
    When Given invalid application id "invalid id" and "invalid secret"
    Then Api should return response with status '403'
  Scenario: Login should pass for correct credentials
    When Given valid application id and secret
    Then Api should return response with status '200'
  Scenario: Queue should be created
    When a set of events like "feed-created" "feed-deleted" and "feed-approved" used for creating a queue
    Then api should return '201' as status
  Scenario: Queue should add subscriber
    When events is subscribed with given queue id
    Then subscriber endpoint should return '200' as status
  Scenario: Queue should broadcast completed event
    When Subscribed event is marked as completed and server returns '200' as status
    Then it should be bradcasted to subscriber