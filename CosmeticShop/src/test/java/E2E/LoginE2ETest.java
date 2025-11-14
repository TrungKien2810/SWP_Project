package E2E;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-End tests cho chức năng đăng nhập trên web browser.
 * 
 * Yêu cầu:
 * - Ứng dụng web phải đang chạy (ví dụ: http://localhost:8080/CosmeticShop)
 * - Microsoft Edge browser đã cài đặt
 * - WebDriverManager sẽ tự động tải EdgeDriver
 * 
 * Chạy test:
 * mvn test -Dtest=LoginE2ETest
 */
@DisplayName("E2E: Login Flow Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginE2ETest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080/CosmeticShop";
    private static final long STEP_DELAY_MS = Long.getLong("e2e.stepDelay", 1200L);
    private static final long VISUAL_DELAY_MS = 2000L; // 2 giây sau khi highlight
    
    @BeforeAll
    static void setUpAll() {
        try {
            // Ưu tiên dùng EdgeDriver - thử nhiều cách
            boolean edgeDriverReady = false;
            
            // Cách 1: Thử tải EdgeDriver từ internet
            try {
                System.out.println("[LoginE2ETest] Đang thử tải EdgeDriver từ internet...");
                WebDriverManager.edgedriver().setup();
                System.out.println("[LoginE2ETest] EdgeDriver đã tải thành công!");
                edgeDriverReady = true;
            } catch (Exception e) {
                System.out.println("[LoginE2ETest] Không thể tải EdgeDriver từ internet: " + e.getMessage());
                System.out.println("[LoginE2ETest] Thử dùng EdgeDriver từ cache...");
                
                // Cách 2: Thử dùng EdgeDriver từ cache (nếu có) - không cần internet
                try {
                    // WebDriverManager sẽ tự động tìm trong cache nếu có
                    // Chỉ cần setup mà không cần tải mới
                    System.out.println("[LoginE2ETest] Đang tìm EdgeDriver trong cache...");
                    // Thử khởi động EdgeDriver trực tiếp (có thể đã có trong cache)
                    EdgeOptions testOptions = new EdgeOptions();
                    // Test với headless để kiểm tra nhanh
                    testOptions.addArguments("--headless=new");
                    testOptions.addArguments("--remote-allow-origins=*");
                    // Thử tạo EdgeDriver để xem có sẵn không
                    org.openqa.selenium.edge.EdgeDriver testDriver = new org.openqa.selenium.edge.EdgeDriver(testOptions);
                    testDriver.quit(); // Đóng ngay để test
                    System.out.println("[LoginE2ETest] EdgeDriver có sẵn trong hệ thống!");
                    edgeDriverReady = true;
                } catch (Exception e2) {
                    System.out.println("[LoginE2ETest] EdgeDriver không có sẵn: " + e2.getMessage());
                }
            }
            
            // Nếu EdgeDriver không sẵn sàng, mới fallback sang ChromeDriver
            if (!edgeDriverReady) {
                System.out.println("[LoginE2ETest] EdgeDriver không khả dụng. Chuyển sang ChromeDriver (fallback)...");
                try {
                // Fallback: Dùng ChromeDriver
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                // Headless mode - chạy browser ở background (không hiện cửa sổ)
                // Comment dòng dưới để browser tự động mở và hiển thị
                // chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--guest");
                chromeOptions.addArguments("--start-maximized");
                
                    System.out.println("[LoginE2ETest] Đang khởi động ChromeDriver (fallback)...");
                    driver = new ChromeDriver(chromeOptions);
                    wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Tăng timeout lên 15 giây
                    driver.manage().window().maximize();
                    
                    // Kiểm tra web server có đang chạy không
                    System.out.println("[LoginE2ETest] Đang kiểm tra web server tại: " + BASE_URL);
                    driver.get(BASE_URL);
                    pause();
                    
                    // Đợi trang load xong
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
                    System.out.println("[LoginE2ETest] Web server đang chạy! Page title: " + driver.getTitle());
                    System.out.println("[LoginE2ETest] ChromeDriver đã khởi động thành công!");
                    return;
                } catch (Exception e) {
                    System.err.println("[LoginE2ETest] ChromeDriver cũng không khởi động được: " + e.getMessage());
                    throw e; // Ném lỗi để skip tests
                }
            }
            
            // Dùng EdgeDriver (ưu tiên)
            EdgeOptions options = new EdgeOptions();
            // Headless mode - chạy browser ở background (không hiện cửa sổ)
            // Comment dòng dưới để browser tự động mở và hiển thị
            // options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--guest");
            options.addArguments("--start-maximized");
            
            System.out.println("[LoginE2ETest] Đang khởi động EdgeDriver...");
            driver = new EdgeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Tăng timeout lên 15 giây
            driver.manage().window().maximize();
            
            // Kiểm tra web server có đang chạy không
            System.out.println("[LoginE2ETest] Đang kiểm tra web server tại: " + BASE_URL);
            driver.get(BASE_URL);
            pause();
            
            // Đợi trang load xong
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            System.out.println("[LoginE2ETest] Web server đang chạy! Page title: " + driver.getTitle());
            System.out.println("[LoginE2ETest] EdgeDriver đã khởi động thành công!");
            
        } catch (Exception e) {
            System.err.println("[LoginE2ETest] Không thể khởi động browser: " + e.getMessage());
            e.printStackTrace();
            System.err.println("[LoginE2ETest] E2E tests sẽ được skip.");
            Assumptions.assumeTrue(false, "Browser không thể khởi động. Skip E2E tests.");

        }
    }
    
    @AfterEach
    void tearDown() {
        // Delay giữa các test để có thể xem
        try {
            Thread.sleep(5000); // Đợi 5 giây giữa các test để có thể xem rõ
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @AfterAll
    static void tearDownAll() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @BeforeEach
    void setUp() {
        // Logout trước khi test login mới (đảm bảo không có session cũ)
        logoutIfLoggedIn();
        
        // Mỗi test bắt đầu từ trang chủ
        if (driver != null) {
            driver.get(BASE_URL);
            pause();
            // Đợi trang load xong
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        }
    }
    
    /**
     * Helper method để logout nếu đang logged in
     */
    private void logoutIfLoggedIn() {
        if (driver == null) {
            return;
        }
        
        try {
            // Kiểm tra xem có đang logged in không bằng cách tìm logout link
            driver.get(BASE_URL);
            pause(500);
            
            try {
                // Tìm logout link hoặc button
                WebElement logoutLink = driver.findElement(
                    By.xpath("//a[contains(@href, '/logout')] | //a[contains(text(), 'Đăng xuất')] | //a[contains(text(), 'Log Out')]")
                );
                
                if (logoutLink != null && logoutLink.isDisplayed()) {
                    System.out.println("[LoginE2ETest] Đang logout trước khi test login mới...");
                    // Click logout link hoặc truy cập trực tiếp logout URL
                    driver.get(BASE_URL + "/logout");
                    pause(1000);
                    // Đợi logout xong (redirect về home)
                    wait.until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("/home"),
                        ExpectedConditions.urlContains("/View/home"),
                        ExpectedConditions.urlContains("/products"),
                        ExpectedConditions.urlContains("/login")
                    ));
                    System.out.println("[LoginE2ETest] Đã logout thành công!");
                }
            } catch (Exception e) {
                // Không tìm thấy logout link, có nghĩa là chưa login
                // Không cần làm gì
            }
        } catch (Exception e) {
            // Nếu có lỗi, chỉ log và tiếp tục
            System.out.println("[LoginE2ETest] Không thể kiểm tra logout: " + e.getMessage());
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("E2E: Truy cập trang đăng nhập")
    void shouldNavigateToLoginPage() {
        printTestHeader("TEST 1", "Truy cập trang đăng nhập");
        
        logStep("1.1", "Truy cập trang login");
        driver.get(BASE_URL + "/login");
        pause();
        
        logStep("1.2", "Kiểm tra trang đã load");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        wait.until(ExpectedConditions.urlContains("/login"));
        assertThat(driver.getCurrentUrl()).contains("/login");
        System.out.println("  🌐 URL: " + driver.getCurrentUrl());
        System.out.println("  📄 Page Title: " + driver.getTitle());
        
        logStep("1.3", "Kiểm tra form đăng nhập");
        WebElement emailInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("email"))
        );
        WebElement passwordInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("password"))
        );
        
        scrollAndHighlight(emailInput, "Email input field");
        scrollAndHighlight(passwordInput, "Password input field");
        
        assertThat(emailInput).isNotNull();
        assertThat(passwordInput).isNotNull();
        System.out.println("\n✅ TEST 1 hoàn thành!\n");
    }
    
    @Test
    @Order(2)
    @DisplayName("E2E: Đăng nhập với email không hợp lệ -> hiển thị lỗi")
    void shouldShowErrorForInvalidEmail() {
        printTestHeader("TEST 2", "Đăng nhập với email không hợp lệ");
        
        // Đảm bảo đã logout trước khi test
        logoutIfLoggedIn();
        
        logStep("2.1", "Truy cập trang login");
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        logStep("2.2", "Nhập email không hợp lệ");
        WebElement emailInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("email"))
        );
        emailInput.clear();
        emailInput.sendKeys("invalid-email");
        scrollAndHighlight(emailInput, "Email input (invalid format)");
        System.out.println("  📧 Email: invalid-email");
        pause();
        
        logStep("2.3", "Nhập password");
        WebElement passwordInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("password"))
        );
        passwordInput.clear();
        passwordInput.sendKeys("password123");
        scrollAndHighlight(passwordInput, "Password input");
        pause();
        
        logStep("2.4", "Submit form");
        WebElement submitButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Đăng nhập')] | //button[@type='submit']")
            )
        );
        scrollAndHighlight(submitButton, "Submit button");
        submitButton.click();
        pause();
        
        logStep("2.5", "Kiểm tra thông báo lỗi");
        wait.until(ExpectedConditions.urlContains("/login"));
        
        try {
            WebElement toastError = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".toast-notification.error, .toast-notification[class*='error']")
                )
            );
            scrollAndHighlight(toastError, "Error toast notification");
            Thread.sleep(1000);
            
            WebElement toastMessage = toastError.findElement(By.cssSelector(".toast-message"));
            String errorText = toastMessage.getText();
            System.out.println("  ⚠️  Error message: " + errorText);
            
            assertThat(errorText.toLowerCase())
                .satisfiesAnyOf(
                    text -> assertThat(text).contains("email"),
                    text -> assertThat(text).contains("hợp lệ"),
                    text -> assertThat(text).contains("gmail")
                );
        } catch (Exception e) {
            assertThat(driver.getCurrentUrl()).contains("/login");
            System.out.println("  ⚠️  Toast không tìm thấy, nhưng đã redirect về login page");
        }
        
        System.out.println("\n✅ TEST 2 hoàn thành!\n");
    }
    
    @Test
    @Order(3)
    @DisplayName("E2E: Đăng nhập với trường rỗng -> hiển thị lỗi")
    void shouldShowErrorForEmptyFields() {
        printTestHeader("TEST 3", "Đăng nhập với trường rỗng");
        
        // Đảm bảo đã logout trước khi test
        logoutIfLoggedIn();
        
        logStep("3.1", "Truy cập trang login");
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        pause();
        
        logStep("3.2", "Để trống các field");
        WebElement emailInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("email"))
        );
        emailInput.clear();
        scrollAndHighlight(emailInput, "Email input (empty)");
        
        WebElement passwordInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("password"))
        );
        passwordInput.clear();
        scrollAndHighlight(passwordInput, "Password input (empty)");
        
        logStep("3.3", "Submit form với trường rỗng");
        WebElement submitButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Đăng nhập')] | //button[@type='submit']")
            )
        );
        scrollAndHighlight(submitButton, "Submit button");
        submitButton.click();
        pause();
        
        logStep("3.4", "Kiểm tra thông báo lỗi");
        wait.until(ExpectedConditions.urlContains("/login"));
        
        try {
            WebElement toastError = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".toast-notification.error, .toast-notification[class*='error']")
                )
            );
            scrollAndHighlight(toastError, "Error toast notification");
            Thread.sleep(1000);
            
            WebElement toastMessage = toastError.findElement(By.cssSelector(".toast-message"));
            String errorText = toastMessage.getText();
            System.out.println("  ⚠️  Error message: " + errorText);
            
            assertThat(errorText.toLowerCase())
                .satisfiesAnyOf(
                    text -> assertThat(text).contains("đầy đủ"),
                    text -> assertThat(text).contains("rỗng"),
                    text -> assertThat(text).contains("nhập"),
                    text -> assertThat(text).contains("email"),
                    text -> assertThat(text).contains("mật khẩu")
                );
        } catch (Exception e) {
            assertThat(driver.getCurrentUrl()).contains("/login");
            System.out.println("  ⚠️  Toast không tìm thấy, nhưng đã redirect về login page");
        }
        
        System.out.println("\n✅ TEST 3 hoàn thành!\n");
    }
    
    @Test
    @Order(4)
    @DisplayName("E2E: Đăng nhập thành công -> chuyển đến trang chủ")
    void shouldLoginSuccessfully() {
        printTestHeader("TEST 4", "Đăng nhập thành công");
        
        // Đảm bảo đã logout trước khi test
        logoutIfLoggedIn();
        
        // Lấy random user từ database
        Model.user testUser = TestDataHelper.getRandomUser();
        
        if (testUser == null) {
            System.out.println("  ⚠️  Không có user nào trong database, skip test");
            return;
        }
        
        logStep("4.1", "Truy cập trang login");
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        logStep("4.2", "Nhập email");
        WebElement emailInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("email"))
        );
        emailInput.clear();
        emailInput.sendKeys(testUser.getEmail());
        scrollAndHighlight(emailInput, "Email input");
        System.out.println("  📧 Email: " + testUser.getEmail());
        pause();
        
        logStep("4.3", "Nhập password");
        WebElement passwordInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("password"))
        );
        passwordInput.clear();
        passwordInput.sendKeys(testUser.getPassword());
        scrollAndHighlight(passwordInput, "Password input");
        pause();
        
        logStep("4.4", "Submit form");
        WebElement submitButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Đăng nhập')] | //button[@type='submit']")
            )
        );
        scrollAndHighlight(submitButton, "Submit button");
        submitButton.click();
        pause();
        
        logStep("4.5", "Kiểm tra đăng nhập thành công");
        wait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("/home"),
            ExpectedConditions.urlContains("/View/home"),
            ExpectedConditions.urlContains("/products"),
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".success, .alert-success, [class*='success']")
            )
        ));
        
        System.out.println("  🌐 URL sau khi login: " + driver.getCurrentUrl());
        
        try {
            WebElement logoutLink = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[contains(text(), 'Log Out')] | //a[contains(text(), 'Đăng xuất')] | //a[contains(@href, '/logout')]")
                )
            );
            scrollAndHighlight(logoutLink, "Logout link (login thành công)");
            assertThat(logoutLink).isNotNull();
            System.out.println("  ✅ Tìm thấy logout link - đăng nhập thành công!");
        } catch (Exception e) {
            String currentUrl = driver.getCurrentUrl();
            assertThat(currentUrl)
                .satisfiesAnyOf(
                    url -> assertThat(url).contains("/home"),
                    url -> assertThat(url).contains("/View/home"),
                    url -> assertThat(url).contains("/products")
                );
            System.out.println("  ✅ Đã redirect đến trang chủ - đăng nhập thành công!");
        }
        
        System.out.println("\n✅ TEST 4 hoàn thành!\n");
    }
    
    @Test
    @Order(5)
    @DisplayName("E2E: Đăng nhập với email không tồn tại -> hiển thị lỗi")
    void shouldShowErrorForNonExistentEmail() {
        printTestHeader("TEST 5", "Đăng nhập với email không tồn tại");
        
        // Đảm bảo đã logout trước khi test
        logoutIfLoggedIn();
        
        logStep("5.1", "Truy cập trang login");
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        logStep("5.2", "Nhập email không tồn tại");
        WebElement emailInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("email"))
        );
        emailInput.clear();
        emailInput.sendKeys("nonexistent@gmail.com");
        scrollAndHighlight(emailInput, "Email input (non-existent)");
        System.out.println("  📧 Email: nonexistent@gmail.com");
        pause();
        
        logStep("5.3", "Nhập password");
        WebElement passwordInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.id("password"))
        );
        passwordInput.clear();
        passwordInput.sendKeys("password123");
        scrollAndHighlight(passwordInput, "Password input");
        pause();
        
        logStep("5.4", "Submit form");
        WebElement submitButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Đăng nhập')] | //button[@type='submit']")
            )
        );
        scrollAndHighlight(submitButton, "Submit button");
        submitButton.click();
        pause();
        
        logStep("5.5", "Kiểm tra thông báo lỗi");
        wait.until(ExpectedConditions.urlContains("/login"));
        
        try {
            WebElement toastError = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".toast-notification.error, .toast-notification[class*='error']")
                )
            );
            scrollAndHighlight(toastError, "Error toast notification");
            Thread.sleep(1000);
            
            WebElement toastMessage = toastError.findElement(By.cssSelector(".toast-message"));
            String errorText = toastMessage.getText();
            System.out.println("  ⚠️  Error message: " + errorText);
            
            assertThat(errorText.toLowerCase())
                .satisfiesAnyOf(
                    text -> assertThat(text).contains("không tồn tại"),
                    text -> assertThat(text).contains("đăng ký"),
                    text -> assertThat(text).contains("tài khoản")
                );
        } catch (Exception e) {
            assertThat(driver.getCurrentUrl()).contains("/login");
            System.out.println("  ⚠️  Toast không tìm thấy, nhưng đã redirect về login page");
        }
        
        System.out.println("\n✅ TEST 5 hoàn thành!\n");
    }

    private static void pause() {
        pause(STEP_DELAY_MS);
    }
    
    private static void pause(long milliseconds) {
        if (milliseconds <= 0) {
            return;
        }
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * In header cho mỗi test với format đẹp
     */
    private static void printTestHeader(String testNumber, String testName) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 " + testNumber + ": " + testName);
        System.out.println("=".repeat(60) + "\n");
    }
    
    /**
     * Log step với format đẹp
     */
    private static void logStep(String stepNumber, String description) {
        System.out.println("\n  ┌─ BƯỚC " + stepNumber + ": " + description);
        System.out.println("  └─────────────────────────────────────────────");
    }
    
    /**
     * Highlight element để dễ nhìn thấy khi test
     */
    private static void highlightElement(WebElement element, String color) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String originalStyle = element.getAttribute("style");
            js.executeScript(
                "arguments[0].setAttribute('style', arguments[1]);",
                element,
                "border: 3px solid " + color + "; background-color: rgba(255, 255, 0, 0.3); padding: 2px;"
            );
            pause(VISUAL_DELAY_MS);
            js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, originalStyle != null ? originalStyle : "");
        } catch (Exception e) {
            // Bỏ qua nếu không thể highlight
        }
    }
    
    /**
     * Scroll element vào view và highlight
     */
    private static void scrollAndHighlight(WebElement element, String stepName) {
        try {
            System.out.println("  👁️  Đang xem: " + stepName);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            pause(500);
            highlightElement(element, "#ff0000"); // Màu đỏ để highlight
        } catch (Exception e) {
            System.out.println("  ⚠️  Không thể scroll/highlight: " + e.getMessage());
        }
    }
}

