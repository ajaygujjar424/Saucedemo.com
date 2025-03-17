import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.FileWriter;
import java.io.IOException;

public class BaseTest {
    protected WebDriver driver;

    @BeforeMethod
    public void setup() {
        try {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
        } catch (Exception e) {
            System.out.println("Error setting up WebDriver: " + e.getMessage());
        }
    }

    @AfterMethod
    public void teardown() {
        try {
            if (driver != null) {
                driver.findElement(By.xpath("//button[@id='react-burger-menu-btn']")).click();
                Thread.sleep(2000);
                driver.findElement(By.xpath("//a[@id='logout_sidebar_link']")).click();
                Thread.sleep(2000);
                driver.quit();
            }
        } catch (Exception e) {
            System.out.println("Error during logout: " + e.getMessage());
            if (driver != null) {
                driver.quit();
            }
        }
    }
}

class LoginPage {
    private WebDriver driver;
    private By usernameField = By.xpath("//input[@id='user-name']");
    private By passwordField = By.xpath("//input[@id='password']");
    private By loginButton = By.xpath("//input[@id='login-button']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void login() {
        try {
            driver.findElement(usernameField).sendKeys("standard_user");
            driver.findElement(passwordField).sendKeys("secret_sauce");
            driver.findElement(loginButton).click();
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    public boolean isLoginSuccessful() {
        try {
            return driver.findElements(By.xpath("//div[@class='inventory_list']")).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}

class ProductsPage {
    private WebDriver driver;
    private By firstProductName = By.xpath("//div[normalize-space()='Sauce Labs Backpack']");
    private By firstProductPrice = By.xpath("//div[@class='inventory_list']//div[1]//div[2]//div[2]//div[1]");
    private By addToCartButton = By.xpath("//button[@id='add-to-cart-sauce-labs-backpack']");
    private By cartIcon = By.xpath("//a[@class='shopping_cart_link']");

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getFirstProductName() {
        return driver.findElement(firstProductName).getText();
    }

    public String getFirstProductPrice() {
        return driver.findElement(firstProductPrice).getText();
    }

    public void addToCart() {
        driver.findElement(addToCartButton).click();
    }

    public void goToCart() {
        driver.findElement(cartIcon).click();
    }
}

class CartPage {
    private WebDriver driver;
    private By cartItemName = By.xpath("//div[@class='inventory_item_name']");
    private By checkoutButton = By.xpath("//button[@id='checkout']");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public String getCartItemName() {
        return driver.findElement(cartItemName).getText();
    }

    public void clickCheckoutButton() {
        driver.findElement(checkoutButton).click();
    }
}

import org.testng.Assert;
import org.testng.annotations.Test;

public class AddToCartTest extends BaseTest {
    @Test
    public void testAddToCartFunctionality() {
        try {
            driver.get("https://www.saucedemo.com");
            
            LoginPage loginPage = new LoginPage(driver);
            loginPage.login();

            if (!loginPage.isLoginSuccessful()) {
                System.out.println("Login failed. Test aborted.");
                return;
            }

            ProductsPage productsPage = new ProductsPage(driver);
            String productName = productsPage.getFirstProductName();
            String productPrice = productsPage.getFirstProductPrice();

            try (FileWriter writer = new FileWriter("productDetails.txt")) {
                writer.write("Product Name: " + productName + "\n");
                writer.write("Product Price: " + productPrice + "\n");
            } catch (IOException e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }

            productsPage.addToCart();
            productsPage.goToCart();

            CartPage cartPage = new CartPage(driver);
            String cartItemName = cartPage.getCartItemName();

            Assert.assertEquals(cartItemName, productName, "Product in cart does not match!");

        } catch (Exception e) {
            System.out.println("Test execution error: " + e.getMessage());
        }
    }
}
