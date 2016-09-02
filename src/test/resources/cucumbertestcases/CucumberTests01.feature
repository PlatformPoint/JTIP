Feature: Human-Belly

  Scenario: TFS_Cocumber Integration -Scenario01
    Given I have 42 cukes in my belly
    When I wait 1 hour
    Then my belly should growl

  Scenario: TFS_Cocumber Integration -Scenario02
    Given I have 100 cukes in my belly
    When I wait 1 hour
    Then my belly should silent