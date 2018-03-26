Feature:Queue Features

  Scenario: Queue should be created
    When a set of events like "feed-created" "feed-deleted" and "feed-approved" used for creating a queue
    Then api should return '201' as status
  Scenario: Queue should add subscriber
    When events is subscribed with given queue id
    Then subscriber endpoint should return '200' as status
  Scenario: Queue should broadcast completed event
    When Subscribed event is marked as completed and server returns '200' as status
    Then it should be bradcasted to subscriber