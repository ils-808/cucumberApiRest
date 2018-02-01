Feature: Groupkt rest api tests

  Background: Endpoint Configuration
    Given The endpoint is configured

  @run
  Scenario: Get all countries
    When I query "get/all" countries
    Then I should have the status code "200"
    And body should contain
      | RestResponse.result.alpha2_code |
      | US                              |
      | DE                              |
      | GB                              |


  @run
  Scenario Outline: Get existing country
    When I input a "<country>"
    Then I should have the status code "200"
    And content type should be in "JSON" format with matching schema "ico2cod.json"
    And body should contain following attributes:
      | <RestResponse.messages> | <RestResponse.result.name> | <RestResponse.result.alpha2_code> | <RestResponse.result.alpha3_code> |
    Examples:
      | country | RestResponse.messages             | RestResponse.result.name                             | RestResponse.result.alpha2_code | RestResponse.result.alpha3_code |
      | US      | Country found matching code [US]. | United States of America                             | US                              | USA                             |
      | DE      | Country found matching code [DE]. | Germany                                              | DE                              | DEU                             |
      | GB      | Country found matching code [GB]. | United Kingdom of Great Britain and Northern Ireland | GB                              | GBR                             |

  @run
  Scenario Outline: Get nonexistent country
    When I input a "<country>"
    Then I should have the status code "200"
    And content type should be in "JSON" format with matching schema "ico2cod.json"
    And body should contain following attributes:
      | <RestResponse.messages> |
    Examples:
      | country | RestResponse.messages                               |
      | us      | No matching country found for requested code [us].  |
      | pew     | No matching country found for requested code [pew]. |

  @run
  Scenario Outline: Create new country
    When I create new country with following data:
      | <name> | <alpha2_code> | <alpha3_code> |
    Then  I should have the status code "200"
    Examples:
      | name         | alpha2_code | alpha3_code |
      | Test Country | TC          | TCY         |
      | Atlantis     | AT          | ATS         |

