package steps;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.hasItem;

import java.util.List;

import Entities.City;
import com.Mock;
import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import gherkin.deps.com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Steps {

    private Mock mockedMethods = new Mock();
    private Response response;
    private RequestSpecification request;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://services.groupkt.com/";
        RestAssured.basePath = "/country";
    }

    @Given("^The endpoint is configured$")
    public void configure_endpoint() throws Throwable {
        request = given();
    }

    @When("^I query \"([^\"]*)\" countries$")
    public void request_countries(String url) throws Throwable {
        response = request.when().get(url);
        response.then().log().all();
    }

    @Then("^I should have the status code \"([^\"]*)\"$")
    public void response_should_have_the_status_code(String statusCode) throws Throwable {
        response.then().statusCode(Integer.parseInt(statusCode));
    }

    @Then("^content type should be in \"([^\"]*)\" format with matching schema \"([^\"]*)\"$")
    public void content_type_should_be_in_format_with_matching_schema(String format, String fileName) throws Throwable {
        if (format.equals("JSON")) {
            response.then().assertThat().contentType(ContentType.JSON).and()
                    .body(matchesJsonSchemaInClasspath(fileName));
        }
    }

    @Then("^body should contain$")
    public void body_should_contain(List<String> data) throws Throwable {
        for (int i = 1; i < data.size(); i++) {
            response.then().assertThat().body(data.get(0), hasItem(data.get(i)));
        }
    }

    @And("^body should contain following attributes:$")
    public void body_should_contain_attributes(DataTable table) throws Throwable {
        //List<Map<String, String>> table = arg.asMaps(String.class, String.class);

        List<List<String>> data = table.raw();
        for (int i = 1; i < data.size(); i++) {
            response.then().assertThat().body(data.get(i).get(0), hasItem(data.get(i).get(1)));
        }
    }

    @When("^I input a \"(.*)?\"$")
    public void input_country(String countryCode) throws Throwable {
        response = request.when().get("get/iso2code/" + countryCode);
        response.then().log().all();
    }

    @When("I create new country with following data:$")
    public void create_new_country(List<String> attributes) throws Throwable {

        mockedMethods.initAndWireStubs();

        City city = new City();
        city.name = attributes.get(0);
        city.alpha2_code = attributes.get(1);
        city.alpha3_code = attributes.get(2);

        Gson gson = new Gson();
        String jsonString = gson.toJson(city);

        response = given()
                .body(jsonString.replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", ""))
                .contentType("application/json")
                .when()
                .post("http://localhost:8083/api/create/country");
        response.then().log().all();

        mockedMethods.stop();
    }
}
