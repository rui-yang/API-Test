package stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.messages.internal.com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

public class Steps {
    private static final String BASE_URL = "https://bookstore.toolsqa.com";
    private static final String USERNAME = "testtest";
    private static final String PASSWORD = "Test@@123";
    private static final String USER_ID = "0dc0c593-2226-42be-9336-97ccb9b0435a";
    private static String token;
    private static String bookId;
    private Response response;


    @Given("I am an authorized user")
    public void i_am_an_authorized_user() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        JSONObject requestParams = new JSONObject();
        requestParams.put("userName", USERNAME);
        requestParams.put("password", PASSWORD);
        
        request.header("Content-Type", "application/json");
        request.body(requestParams.toJSONString());

//        Response response1 = request.post("/Account/v1/User");
//        userId = response1.jsonPath().get("userId");


        Response response = request.post("/Account/v1/GenerateToken");

        int statusCode = response.getStatusCode();
        Assert.assertEquals(200, statusCode);

        token = response.jsonPath().get("token");

    }

    @Given("A list of books are available")
    public void a_list_of_books_are_available() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        Response response = request.get("/Bookstore/v1/Books");
        Assert.assertEquals(200, response.getStatusCode());
        List<Map<String, String>> books = response.jsonPath().get("books");
        Assert.assertTrue(books.size() > 0);
        bookId = books.get(2).get("isbn");
    }

    @When("I add a book to my reading list")
    public void i_add_a_book_to_my_reading_list() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        JSONObject requestParams = new JSONObject();
        requestParams.put("userId", USER_ID);

        JSONArray collectionArray = new JSONArray();
        JSONObject isbn = new JSONObject();
        isbn.put("isbn", bookId);
        collectionArray.add(isbn);
        requestParams.put("collectionOfIsbns", collectionArray);

        request.body(requestParams.toJSONString());

        response = request.post("/Bookstore/v1/Books");
    }

    @Then("the book is added")
    public void the_book_is_added() {
        Assert.assertEquals(201, response.getStatusCode());
    }

    @When("I remove a book from my reading list")
    public void i_remove_a_book_from_my_reading_list() {
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        request.header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");

        JSONObject requestParams = new JSONObject();
        requestParams.put("userId", USER_ID);
        requestParams.put("isbn", bookId);

        request.body(requestParams.toJSONString());

        response = request.delete("/Bookstore/v1/Book");

    }

    @Then("the book is removed")
    public void the_book_is_removed() {
        Assert.assertEquals(204, response.getStatusCode());
    }

}
