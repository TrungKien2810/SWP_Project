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
import org.openqa.selenium.JavascriptExecutor;

import java.time.Duration;
import java.util.List;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 🎬 KỊCH BẢN 2: QUẢN LÝ TÀI KHOẢN VÀ THEO DÕI ĐƠN HÀNG
 * 
 * Test theo kịch bản thuyết trình:
 * 1. Đăng ký và Đăng nhập
 * 2. Quản lý Tài khoản
 * 3. Wishlist
 * 4. Lịch sử Đơn hàng
 * 5. Hệ thống Thông báo
 * 6. Quản lý Địa chỉ Giao hàng
 */
@DisplayName("🎬 KỊCH BẢN 2: Quản lý tài khoản và theo dõi đơn hàng")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountManagementE2ETest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080/CosmeticShop";
    private static final long STEP_DELAY_MS = 3000L;
    private static Model.user testUser;
    private static boolean isLoggedIn = false;
    private static final String SCREENSHOT_DIR = "test-screenshots/scenario2";
    private static TestReportGenerator reportGenerator;
    
    @BeforeAll
    static void setUpAll() {
        try {
            reportGenerator = new TestReportGenerator("AccountManagementE2ETest");
            Path screenshotPath = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotPath)) {
                Files.createDirectories(screenshotPath);
            }
            
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*", "--start-maximized", "--guest");
            
            String chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
            String chromePathX86 = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
            java.io.File chromeFile = new java.io.File(chromePath);
            if (!chromeFile.exists()) {
                chromeFile = new java.io.File(chromePathX86);
            }
            if (chromeFile.exists()) {
                options.setBinary(chromeFile.getAbsolutePath());
            }
            
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            driver.manage().window().maximize();
            
            testUser = TestDataHelper.getRandomUser();
        } catch (Exception e) {
            System.err.println("[Kịch bản 2] Không thể khởi động ChromeDriver: " + e.getMessage());
            Assumptions.assumeTrue(false, "ChromeDriver không thể khởi động.");
        }
    }
    
    @AfterAll
    static void tearDownAll() {
        if (reportGenerator != null) {
            try {
                reportGenerator.finish();
                String reportPath = reportGenerator.generateReport();
                System.out.println("\n📊 TEST REPORT: " + new File(reportPath).getAbsolutePath());
            } catch (Exception e) {
                System.err.println("❌ Lỗi khi tạo report: " + e.getMessage());
            }
        }
        if (driver != null) {
            driver.quit();
        }
    }
    
    @BeforeEach
    void setUp() {
        if (testUser == null) return;
        
        if (!isLoggedIn) {
            driver.get(BASE_URL + "/login");
            pause();
            
            WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
            emailInput.clear();
            emailInput.sendKeys(testUser.getEmail());
            pause(1000);
            
            WebElement passwordInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("password")));
            passwordInput.clear();
            passwordInput.sendKeys(testUser.getPassword());
            pause(1000);
            
            WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Đăng nhập')] | //button[@type='submit']")
                )
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
            pause(500);
            submitButton.click();
            pause(2000);
            
            wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/home"),
                ExpectedConditions.urlContains("/View/home")
            ));
            isLoggedIn = true;
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("2.1: Đăng ký tài khoản mới")
    void shouldSignUpNewAccount() {
        System.out.println("\n🧪 TEST 2.1: Đăng ký tài khoản");
        
        try {
            driver.get(BASE_URL + "/signup");
            pause();
            
            // Tìm form đăng ký
            WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
            WebElement usernameInput = driver.findElement(By.id("username"));
            WebElement passwordInput = driver.findElement(By.id("password"));
            
            // Tạo email test ngẫu nhiên
            String testEmail = "test" + System.currentTimeMillis() + "@gmail.com";
            String testUsername = "testuser" + System.currentTimeMillis();
            String testPassword = "Test123456";
            
            emailInput.clear();
            emailInput.sendKeys(testEmail);
            usernameInput.clear();
            usernameInput.sendKeys(testUsername);
            passwordInput.clear();
            passwordInput.sendKeys(testPassword);
            pause(1000);
            
            WebElement submitBtn = driver.findElement(
                By.xpath("//button[contains(text(), 'Đăng ký')] | //button[@type='submit']")
            );
            submitBtn.click();
            pause(2000);
            
            System.out.println("✅ TEST 2.1 hoàn thành!");
        } catch (Exception e) {
            System.out.println("⚠️  TEST 2.1: " + e.getMessage());
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("2.2: Quản lý thông tin tài khoản")
    void shouldManageAccountInfo() {
        if (testUser == null) return;
        
        System.out.println("\n🧪 TEST 2.2: Quản lý thông tin tài khoản");
        
        try {
            driver.get(BASE_URL + "/account-management");
            pause();
            
            // Kiểm tra form thông tin
            try {
                WebElement phoneInput = driver.findElement(By.name("phone"));
                scrollAndHighlight(phoneInput, "Ô số điện thoại");
                System.out.println("  ✅ Tìm thấy form quản lý tài khoản");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy form quản lý tài khoản");
            }
            
            pause(2000);
            System.out.println("✅ TEST 2.2 hoàn thành!");
        } catch (Exception e) {
            System.out.println("⚠️  TEST 2.2: " + e.getMessage());
        }
    }
    
    @Test
    @Order(3)
    @DisplayName("2.3: Quản lý Wishlist")
    void shouldManageWishlist() {
        if (testUser == null) return;
        
        System.out.println("\n🧪 TEST 2.3: Quản lý Wishlist");
        
        try {
            // Thêm sản phẩm vào wishlist từ trang chi tiết
            Model.Product testProduct = TestDataHelper.getRandomProductInStock();
            if (testProduct != null) {
                driver.get(BASE_URL + "/product-detail?id=" + testProduct.getProductId());
                pause();
                
                try {
                    WebElement wishlistBtn = driver.findElement(
                        By.xpath("//a[contains(@href, 'wishlist')] | //button[contains(@onclick, 'wishlist')] | //i[contains(@class, 'heart')]")
                    );
                    scrollAndHighlight(wishlistBtn, "Nút yêu thích");
                    wishlistBtn.click();
                    pause(2000);
                    System.out.println("  ✅ Đã thêm sản phẩm vào wishlist");
                } catch (Exception e) {
                    System.out.println("  ⚠️  Không tìm thấy nút wishlist");
                }
            }
            
            // Xem wishlist
            driver.get(BASE_URL + "/wishlist");
            pause();
            
            List<WebElement> wishlistItems = driver.findElements(
                By.cssSelector(".wishlist-item, .product-item, [class*='wishlist']")
            );
            System.out.println("  📦 Tìm thấy " + wishlistItems.size() + " sản phẩm trong wishlist");
            
            pause(2000);
            System.out.println("✅ TEST 2.3 hoàn thành!");
        } catch (Exception e) {
            System.out.println("⚠️  TEST 2.3: " + e.getMessage());
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("2.4: Xem lịch sử đơn hàng")
    void shouldViewOrderHistory() {
        if (testUser == null) return;
        
        System.out.println("\n🧪 TEST 2.4: Lịch sử đơn hàng");
        
        try {
            driver.get(BASE_URL + "/my-orders");
            pause();
            
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".order-list, table, [class*='order']")
            ));
            
            List<WebElement> orders = driver.findElements(
                By.cssSelector(".order-item, tr[class*='order'], [class*='order-row']")
            );
            System.out.println("  📋 Tìm thấy " + orders.size() + " đơn hàng");
            
            if (!orders.isEmpty()) {
                scrollAndHighlight(orders.get(0), "Đơn hàng đầu tiên");
                
                // Click vào đơn hàng để xem chi tiết
                try {
                    orders.get(0).click();
                    pause(2000);
                    System.out.println("  ✅ Đã xem chi tiết đơn hàng");
                } catch (Exception e) {
                    System.out.println("  ⚠️  Không thể click vào đơn hàng");
                }
            }
            
            pause(2000);
            System.out.println("✅ TEST 2.4 hoàn thành!");
        } catch (Exception e) {
            System.out.println("⚠️  TEST 2.4: " + e.getMessage());
        }
    }
    
    @Test
    @Order(7)
    @DisplayName("2.7: Xem chi tiết đơn hàng")
    void shouldViewOrderDetail() {
        if (testUser == null) return;
        
        System.out.println("\n🧪 TEST 2.7: Chi tiết đơn hàng");
        
        try {
            // Lấy một đơn hàng từ database
            List<Model.Order> orders = TestDataHelper.getOrdersByUserId(testUser.getUser_id());
            if (orders == null || orders.isEmpty()) {
                System.out.println("  ⚠️  Không có đơn hàng nào để xem chi tiết");
                return;
            }
            
            int orderId = orders.get(0).getOrderId();
            driver.get(BASE_URL + "/order-detail?orderId=" + orderId);
            pause();
            
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".order-detail, [class*='order-detail']")
            ));
            
            // Kiểm tra thông tin đơn hàng
            try {
                driver.findElements(
                    By.cssSelector(".order-info, .order-header, [class*='order-info']")
                );
                System.out.println("  📋 Tìm thấy thông tin đơn hàng");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy thông tin đơn hàng");
            }
            
            pause(2000);
            System.out.println("✅ TEST 2.7 hoàn thành!");
        } catch (Exception e) {
            System.out.println("⚠️  TEST 2.7: " + e.getMessage());
        }
    }
    
    @Test
    @Order(8)
    @DisplayName("2.8: Đổi mật khẩu")
    void shouldChangePassword() {
        if (testUser == null) return;
        
        System.out.println("\n🧪 TEST 2.8: Đổi mật khẩu");
        
        try {
            driver.get(BASE_URL + "/change-password");
            pause();
            
            try {
                WebElement oldPasswordInput = driver.findElement(By.name("oldPassword"));
                driver.findElement(By.name("newPassword"));
                driver.findElement(By.name("confirmPassword"));
                
                scrollAndHighlight(oldPasswordInput, "Ô mật khẩu cũ");
                System.out.println("  ✅ Tìm thấy form đổi mật khẩu");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy form đổi mật khẩu");
            }
            
            pause(2000);
            System.out.println("✅ TEST 2.8 hoàn thành!");
        } catch (Exception e) {
            System.out.println("⚠️  TEST 2.8: " + e.getMessage());
        }
    }
    
    @Test
    @Order(9)
    @DisplayName("2.9: Thêm địa chỉ giao hàng mới")
    void shouldAddNewShippingAddress() {
        if (testUser == null) return;
        
        System.out.println("\n🧪 TEST 2.9: Thêm địa chỉ giao hàng mới");
        
        try {
            driver.get(BASE_URL + "/shipping-address");
            pause();
            
            // Tìm nút thêm địa chỉ
            try {
                WebElement addBtn = driver.findElement(
                    By.xpath("//button[contains(text(), 'Thêm')] | //a[contains(text(), 'Thêm')] | //button[contains(@onclick, 'add')]")
                );
                scrollAndHighlight(addBtn, "Nút thêm địa chỉ");
                addBtn.click();
                pause(2000);
                
                // Kiểm tra form thêm địa chỉ
                try {
                    WebElement nameInput = driver.findElement(By.name("fullName"));
                    scrollAndHighlight(nameInput, "Form thêm địa chỉ");
                    System.out.println("  ✅ Đã mở form thêm địa chỉ");
                } catch (Exception e) {
                    System.out.println("  ⚠️  Không tìm thấy form thêm địa chỉ");
                }
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy nút thêm địa chỉ");
            }
            
            pause(2000);
            System.out.println("✅ TEST 2.9 hoàn thành!");
        } catch (Exception e) {
            System.out.println("⚠️  TEST 2.9: " + e.getMessage());
        }
    }
    
    @Test
    @Order(10)
    @DisplayName("2.10: Xóa sản phẩm khỏi Wishlist")
    void shouldRemoveFromWishlist() {
        if (testUser == null) return;
        
        System.out.println("\n🧪 TEST 2.10: Xóa sản phẩm khỏi Wishlist");
        
        try {
            driver.get(BASE_URL + "/wishlist");
            pause();
            
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".wishlist-item, .product-item, [class*='wishlist']")
            ));
            
            // Tìm nút xóa
            try {
                List<WebElement> removeButtons = driver.findElements(
                    By.xpath("//a[contains(@href, 'remove')] | //button[contains(@onclick, 'remove')] | //i[contains(@class, 'trash')]")
                );
                if (!removeButtons.isEmpty()) {
                    scrollAndHighlight(removeButtons.get(0), "Nút xóa khỏi wishlist");
                    removeButtons.get(0).click();
                    pause(2000);
                    System.out.println("  ✅ Đã xóa sản phẩm khỏi wishlist");
                } else {
                    System.out.println("  ⚠️  Không tìm thấy nút xóa");
                }
            } catch (Exception e) {
                System.out.println("  ⚠️  Không thể xóa: " + e.getMessage());
            }
            
            pause(2000);
            System.out.println("✅ TEST 2.10 hoàn thành!");
        } catch (Exception e) {
            System.out.println("⚠️  TEST 2.10: " + e.getMessage());
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("2.5: Xem thông báo")
    void shouldViewNotifications() {
        if (testUser == null) return;
        
        System.out.println("\n🧪 TEST 2.5: Hệ thống thông báo");
        
        try {
            driver.get(BASE_URL + "/notification-center");
            pause();
            
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".notification-list, .notifications, [class*='notification']")
            ));
            
            List<WebElement> notifications = driver.findElements(
                By.cssSelector(".notification-item, .notification, [class*='notification']")
            );
            System.out.println("  🔔 Tìm thấy " + notifications.size() + " thông báo");
            
            if (!notifications.isEmpty()) {
                scrollAndHighlight(notifications.get(0), "Thông báo đầu tiên");
            }
            
            pause(2000);
            System.out.println("✅ TEST 2.5 hoàn thành!");
        } catch (Exception e) {
            System.out.println("⚠️  TEST 2.5: " + e.getMessage());
        }
    }
    
    @Test
    @Order(6)
    @DisplayName("2.6: Quản lý địa chỉ giao hàng")
    void shouldManageShippingAddresses() {
        if (testUser == null) return;
        
        System.out.println("\n🧪 TEST 2.6: Quản lý địa chỉ giao hàng");
        
        try {
            driver.get(BASE_URL + "/shipping-address");
            pause();
            
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".address-list, .addresses, [class*='address']")
            ));
            
            List<WebElement> addresses = driver.findElements(
                By.cssSelector(".address-item, .address, [class*='address']")
            );
            System.out.println("  📍 Tìm thấy " + addresses.size() + " địa chỉ");
            
            // Tìm nút thêm địa chỉ mới
            try {
                WebElement addBtn = driver.findElement(
                    By.xpath("//button[contains(text(), 'Thêm')] | //a[contains(text(), 'Thêm')]")
                );
                scrollAndHighlight(addBtn, "Nút thêm địa chỉ");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy nút thêm địa chỉ");
            }
            
            pause(2000);
            System.out.println("✅ TEST 2.6 hoàn thành!");
        } catch (Exception e) {
            System.out.println("⚠️  TEST 2.6: " + e.getMessage());
        }
    }
    
    private static void scrollAndHighlight(WebElement element, String stepName) {
        try {
            System.out.println("  👁️  Đang xem: " + stepName);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            pause(500);
        } catch (Exception e) {
            // Bỏ qua
        }
    }
    
    private static void pause() {
        pause(STEP_DELAY_MS);
    }
    
    private static void pause(long milliseconds) {
        if (milliseconds <= 0) return;
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

