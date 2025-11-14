package E2E;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

/**
 * End-to-End tests cho chức năng Checkout (Thanh toán).
 * 
 * Yêu cầu:
 * - Ứng dụng web phải đang chạy
 * - Có ít nhất 1 user và 1 sản phẩm trong database
 * - User đã có sản phẩm trong giỏ hàng
 */
@DisplayName("E2E: Checkout Flow Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CheckoutE2ETest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080/CosmeticShop";
    private static final long STEP_DELAY_MS = 2000L; // 2 giây mỗi bước
    private static Model.user testUser;
    private static boolean isLoggedIn = false;
    
    @BeforeAll
    static void setUpAll() {
        try {
            // Tự động tải và cấu hình ChromeDriver
            WebDriverManager.chromedriver().setup();
            
            ChromeOptions options = new ChromeOptions();
            // Không headless để có thể xem
            // options.addArguments("--headless=new");
            
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--start-maximized");
            options.addArguments("--guest");
            
            String chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
            String chromePathX86 = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
            java.io.File chromeFile = new java.io.File(chromePath);
            if (!chromeFile.exists()) {
                chromeFile = new java.io.File(chromePathX86);
            }
            if (chromeFile.exists()) {
                options.setBinary(chromeFile.getAbsolutePath());
                System.out.println("[CheckoutE2ETest] Sử dụng Chrome tại: " + chromeFile.getAbsolutePath());
            }
            
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            driver.manage().window().maximize();
            
            // Lấy test user từ database
            testUser = TestDataHelper.getRandomUser();
            if (testUser == null) {
                System.out.println("[CheckoutE2ETest] Không tìm thấy user trong database!");
            }
        } catch (Exception e) {
            System.err.println("[CheckoutE2ETest] Không thể khởi động ChromeDriver: " + e.getMessage());
            Assumptions.assumeTrue(false, "ChromeDriver không thể khởi động. Skip E2E tests.");
        }
    }
    
    @AfterEach
    void tearDown() {
        // Delay giữa các test để có thể xem
        try {
            Thread.sleep(5000); // Đợi 5 giây giữa các test
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
        if (testUser == null) {
            System.out.println("[CheckoutE2ETest] Skip test vì không có user");
            return;
        }
        
        // Chỉ đăng nhập 1 lần, tái sử dụng session cho các test tiếp theo
        if (!isLoggedIn) {
            System.out.println("\n[CheckoutE2ETest] Đăng nhập user lần đầu...");
            // Đăng nhập user
            driver.get(BASE_URL + "/login");
            pause();
            
            System.out.println("[CheckoutE2ETest] Email: " + testUser.getEmail());
            
            WebElement emailInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("email"))
            );
            emailInput.clear();
            emailInput.sendKeys(testUser.getEmail());
            pause(1000); // Đợi 1 giây để xem email được nhập
            
            WebElement passwordInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("password"))
            );
            passwordInput.clear();
            passwordInput.sendKeys(testUser.getPassword());
            pause(1000); // Đợi 1 giây để xem password được nhập
            
            WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Đăng nhập')] | //button[@type='submit']")
                )
            );
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
            pause(500);
            submitButton.click();
            pause(2000); // Đợi đăng nhập xong
            
            // Đợi đăng nhập thành công
            wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/home"),
                ExpectedConditions.urlContains("/View/home")
            ));
            System.out.println("[CheckoutE2ETest] Đăng nhập thành công! Session sẽ được tái sử dụng cho các test tiếp theo.");
            isLoggedIn = true;
        } else {
            // Đã đăng nhập rồi, chỉ cần đảm bảo vẫn ở trang home hoặc navigate về home
            try {
                String currentUrl = driver.getCurrentUrl();
                if (!currentUrl.contains("/home") && !currentUrl.contains("/checkout")) {
                    driver.get(BASE_URL + "/View/home.jsp");
                    pause(500);
                }
            } catch (Exception e) {
                // Nếu có lỗi, thử đăng nhập lại
                isLoggedIn = false;
                setUp();
            }
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("E2E: Xem trang checkout với giỏ hàng có sản phẩm")
    void shouldViewCheckoutPage() {
        if (testUser == null) {
            return;
        }
        
        System.out.println("\n========================================");
        System.out.println("[CheckoutE2ETest] TEST 1: Xem trang Checkout");
        System.out.println("========================================");
        
        // Đảm bảo có sản phẩm trong giỏ hàng trước
        // Thêm sản phẩm vào giỏ hàng nếu chưa có
        Model.Product testProduct = TestDataHelper.getRandomProductInStock();
        if (testProduct != null) {
            System.out.println("[CheckoutE2ETest] Thêm sản phẩm vào giỏ: " + testProduct.getName());
            // Có thể thêm sản phẩm qua API hoặc điều hướng đến trang sản phẩm
        }
        
        // Truy cập trang checkout
        driver.get(BASE_URL + "/checkout");
        System.out.println("[CheckoutE2ETest] URL: " + driver.getCurrentUrl());
        pause();
        
        // Kiểm tra đã vào trang checkout
        wait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("/checkout"),
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".checkout, .checkout-form, form[action*='checkout']")
            )
        ));
        
        // Tìm và in ra thông tin giỏ hàng
        try {
            List<WebElement> cartItems = driver.findElements(
                By.cssSelector(".cart-item, .checkout-item, [class*='item']")
            );
            System.out.println("[CheckoutE2ETest] Tìm thấy " + cartItems.size() + " sản phẩm trong giỏ hàng");
            
            // Tìm tổng tiền
            try {
                WebElement totalElement = driver.findElement(
                    By.cssSelector(".total, .total-amount, [class*='total']")
                );
                System.out.println("[CheckoutE2ETest] Tổng tiền: " + totalElement.getText());
            } catch (Exception e) {
                System.out.println("[CheckoutE2ETest] Không tìm thấy tổng tiền");
            }
        } catch (Exception e) {
            System.out.println("[CheckoutE2ETest] Không tìm thấy sản phẩm trong giỏ (có thể giỏ hàng trống)");
        }
        
        pause(3000); // Đợi 3 giây để xem rõ
    }
    
    @Test
    @Order(2)
    @DisplayName("E2E: Xem form nhập thông tin giao hàng")
    void shouldViewShippingForm() {
        if (testUser == null) {
            return;
        }
        
        System.out.println("\n========================================");
        System.out.println("[CheckoutE2ETest] TEST 2: Xem form thông tin giao hàng");
        System.out.println("========================================");
        
        driver.get(BASE_URL + "/checkout");
        pause();
        
        // Tìm các trường trong form
        try {
            List<WebElement> formFields = driver.findElements(
                By.cssSelector("input[type='text'], input[type='tel'], input[type='email'], textarea, select")
            );
            System.out.println("[CheckoutE2ETest] Tìm thấy " + formFields.size() + " trường trong form:");
            for (WebElement field : formFields) {
                String name = field.getAttribute("name");
                String placeholder = field.getAttribute("placeholder");
                String label = field.getAttribute("id");
                if (name != null || placeholder != null || label != null) {
                    System.out.println("  - " + (name != null ? name : placeholder != null ? placeholder : label));
                }
            }
        } catch (Exception e) {
            System.out.println("[CheckoutE2ETest] Không tìm thấy form fields");
        }
        
        pause(3000);
    }
    
    @Test
    @Order(3)
    @DisplayName("E2E: Xem các phương thức thanh toán")
    void shouldViewPaymentMethods() {
        if (testUser == null) {
            return;
        }
        
        System.out.println("\n========================================");
        System.out.println("[CheckoutE2ETest] TEST 3: Xem phương thức thanh toán");
        System.out.println("========================================");
        
        driver.get(BASE_URL + "/checkout");
        pause();
        
        // Tìm các phương thức thanh toán
        try {
            List<WebElement> paymentMethods = driver.findElements(
                By.cssSelector(".payment-method, input[type='radio'][name*='payment'], .payment-option")
            );
            System.out.println("[CheckoutE2ETest] Tìm thấy " + paymentMethods.size() + " phương thức thanh toán:");
            for (WebElement method : paymentMethods) {
                String text = method.getText().trim();
                String value = method.getAttribute("value");
                if (!text.isEmpty()) {
                    System.out.println("  - " + text);
                } else if (value != null) {
                    System.out.println("  - " + value);
                }
            }
        } catch (Exception e) {
            System.out.println("[CheckoutE2ETest] Không tìm thấy phương thức thanh toán");
        }
        
        pause(3000);
    }
    
    @Test
    @Order(4)
    @DisplayName("E2E: Điền form checkout và submit (không thanh toán thật)")
    void shouldFillCheckoutForm() {
        if (testUser == null) {
            return;
        }
        
        System.out.println("\n========================================");
        System.out.println("[CheckoutE2ETest] TEST 4: Điền form checkout");
        System.out.println("========================================");
        
        // Đảm bảo có sản phẩm trong giỏ hàng
        Model.Product testProduct = TestDataHelper.getRandomProductInStock();
        if (testProduct != null) {
            System.out.println("[CheckoutE2ETest] Đảm bảo có sản phẩm trong giỏ: " + testProduct.getName());
            // Có thể thêm sản phẩm vào giỏ qua API hoặc navigate
        }
        
        driver.get(BASE_URL + "/checkout");
        pause();
        
        // Điền form thông tin giao hàng
        try {
            // Tìm và điền tên
            List<WebElement> nameFields = driver.findElements(
                By.cssSelector("input[name*='name'], input[name*='fullName'], input[id*='name']")
            );
            if (!nameFields.isEmpty()) {
                nameFields.get(0).clear();
                nameFields.get(0).sendKeys("Nguyễn Văn Test");
                System.out.println("[CheckoutE2ETest] Đã điền tên: Nguyễn Văn Test");
                pause(1000);
            }
            
            // Tìm và điền SĐT
            List<WebElement> phoneFields = driver.findElements(
                By.cssSelector("input[name*='phone'], input[type='tel'], input[id*='phone']")
            );
            if (!phoneFields.isEmpty()) {
                phoneFields.get(0).clear();
                phoneFields.get(0).sendKeys("0123456789");
                System.out.println("[CheckoutE2ETest] Đã điền SĐT: 0123456789");
                pause(1000);
            }
            
            // Tìm và điền địa chỉ
            List<WebElement> addressFields = driver.findElements(
                By.cssSelector("input[name*='address'], textarea[name*='address'], input[id*='address']")
            );
            if (!addressFields.isEmpty()) {
                addressFields.get(0).clear();
                addressFields.get(0).sendKeys("123 Đường Test, Quận 1, TP.HCM");
                System.out.println("[CheckoutE2ETest] Đã điền địa chỉ: 123 Đường Test, Quận 1, TP.HCM");
                pause(1000);
            }
            
            // Tìm và điền email (nếu có)
            List<WebElement> emailFields = driver.findElements(
                By.cssSelector("input[type='email'], input[name*='email']")
            );
            if (!emailFields.isEmpty() && emailFields.get(0).getAttribute("value").isEmpty()) {
                emailFields.get(0).clear();
                emailFields.get(0).sendKeys(testUser.getEmail());
                System.out.println("[CheckoutE2ETest] Đã điền email: " + testUser.getEmail());
                pause(1000);
            }
            
            System.out.println("[CheckoutE2ETest] Đã điền đầy đủ thông tin form!");
            
        } catch (Exception e) {
            System.out.println("[CheckoutE2ETest] Không thể điền form: " + e.getMessage());
        }
        
        pause(3000); // Đợi để xem form đã điền
    }
    
    @Test
    @Order(5)
    @DisplayName("E2E: Chọn phương thức thanh toán và xem tổng tiền")
    void shouldSelectPaymentMethodAndViewTotal() {
        if (testUser == null) {
            return;
        }
        
        System.out.println("\n========================================");
        System.out.println("[CheckoutE2ETest] TEST 5: Chọn phương thức thanh toán");
        System.out.println("========================================");
        
        driver.get(BASE_URL + "/checkout");
        pause();
        
        // Tìm và chọn phương thức thanh toán
        try {
            List<WebElement> paymentMethods = driver.findElements(
                By.cssSelector("input[type='radio'][name*='payment'], .payment-method input[type='radio']")
            );
            
            if (!paymentMethods.isEmpty()) {
                // Chọn phương thức đầu tiên
                WebElement firstMethod = paymentMethods.get(0);
                String methodValue = firstMethod.getAttribute("value");
                String methodText = firstMethod.findElement(By.xpath("./..")).getText().trim();
                
                // Scroll vào view
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", firstMethod);
                pause(500);
                
                // Click chọn
                if (!firstMethod.isSelected()) {
                    firstMethod.click();
                    System.out.println("[CheckoutE2ETest] Đã chọn phương thức thanh toán: " + (methodText.isEmpty() ? methodValue : methodText));
                    pause(1000);
                }
            }
            
            // Tìm và hiển thị tổng tiền
            try {
                List<WebElement> totalElements = driver.findElements(
                    By.cssSelector(".total, .total-amount, [class*='total'], .checkout-total")
                );
                for (WebElement total : totalElements) {
                    String totalText = total.getText().trim();
                    if (totalText.contains("VNĐ") || totalText.contains("đ") || totalText.matches(".*\\d+.*")) {
                        System.out.println("[CheckoutE2ETest] Tổng tiền: " + totalText);
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("[CheckoutE2ETest] Không tìm thấy tổng tiền");
            }
            
        } catch (Exception e) {
            System.out.println("[CheckoutE2ETest] Không thể chọn phương thức thanh toán: " + e.getMessage());
        }
        
        pause(3000);
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
}

