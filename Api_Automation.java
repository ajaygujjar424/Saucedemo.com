import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ApiAutomationTest {
    
    @Test
    public void testGetRequest() {
        // Define the endpoint
        String url = "https://reqres.in/api/users?page=2";
        
        // Send the GET request
        Response response = RestAssured.get(url);
        
        // Validate the status code
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200 but got " + response.getStatusCode());
        
        // Validate that the response body is not empty
        Assert.assertFalse(response.getBody().asString().isEmpty(), "Response body should not be empty");
        
        // Print response for debugging
        System.out.println("Response Body: " + response.getBody().asString());
        System.out.println("Test Passed: Status code is 200 and response body is not empty");
    }
}
