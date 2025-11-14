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

/**
 * End-to-End tests cho chức năng Admin.
 * 
 * Yêu cầu:
 * - Ứng dụng web phải đang chạy
 * - Có ít nhất 1 admin user trong database
 */
@DisplayName("E2E: Admin Flow Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminE2ETest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080/CosmeticShop";
    private static final long STEP_DELAY_MS = Long.getLong("e2e.stepDelay", 1200L);
    private static Model.user adminUser;
    
    @BeforeAll
    static void setUpAll() {
        try {
            // Tự động tải và cấu hình ChromeDriver
            WebDriverManager.chromedriver().setup();
            
            ChromeOptions options = new ChromeOptions();
            // Headless mode - chạy browser ở background (không hiện cửa sổ)
            // Comment dòng dưới để browser tự động mở và hiển thị
            // options.addArguments("--headless=new"); // Chrome 109+ (version 142)
            
            // Các options cần thiết
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-extensions");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-web-security");
            options.addArguments("--allow-running-insecure-content");
            
            // Thêm options cho Windows và Chrome mới
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-features=VizDisplayCompositor");
            options.addArguments("--start-maximized");
            
            // Bỏ qua màn hình chọn profile - Dùng guest mode (tốt nhất)
            // Guest mode bỏ qua hoàn toàn profile selection và không lưu data
            options.addArguments("--guest");
            
            // Hoặc dùng user data directory riêng (nếu guest mode không hoạt động)
            // String userDataDir = System.getProperty("java.io.tmpdir") + "chrome-test-profile-" + System.currentTimeMillis();
            // options.addArguments("--user-data-dir=" + userDataDir);
            // options.addArguments("--profile-directory=Default");
            
            // Set binary path cụ thể cho Chrome
            String chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
            String chromePathX86 = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
            java.io.File chromeFile = new java.io.File(chromePath);
            if (!chromeFile.exists()) {
                chromeFile = new java.io.File(chromePathX86);
            }
            if (chromeFile.exists()) {
                options.setBinary(chromeFile.getAbsolutePath());
                System.out.println("[AdminE2ETest] Sử dụng Chrome tại: " + chromeFile.getAbsolutePath());
            }
            
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            driver.manage().window().maximize();
            
            // Lấy admin user từ database
            adminUser = TestDataHelper.getRandomAdmin();
            if (adminUser == null) {
                System.out.println("[AdminE2ETest] Không tìm thấy admin user trong database!");
            }
        } catch (Exception e) {
            System.err.println("[AdminE2ETest] Không thể khởi động ChromeDriver: " + e.getMessage());
            System.err.println("[AdminE2ETest] E2E tests sẽ được skip. Để chạy E2E tests, đảm bảo:");
            System.err.println("  - Chrome browser đã được cài đặt");
            System.err.println("  - WebDriverManager có thể tải ChromeDriver");
            System.err.println("  - Web server đang chạy tại " + BASE_URL);
            // Skip toàn bộ test class nếu không thể khởi động Chrome
            Assumptions.assumeTrue(false, "ChromeDriver không thể khởi động. Skip E2E tests.");
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
    
    private static boolean isLoggedIn = false;
    
    @BeforeEach
    void setUp() {
        if (adminUser == null) {
            System.out.println("[AdminE2ETest] Skip test vì không có admin user");
            return;
        }
        
        // Chỉ đăng nhập 1 lần, tái sử dụng session cho các test tiếp theo
        if (!isLoggedIn) {
            System.out.println("\n[AdminE2ETest] Đăng nhập admin lần đầu...");
            // Đăng nhập admin
            driver.get(BASE_URL + "/login");
            pause();
            
            WebElement emailInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("email"))
            );
            emailInput.clear();
            emailInput.sendKeys(adminUser.getEmail());
            System.out.println("[AdminE2ETest] Email: " + adminUser.getEmail());
            pause(1000); // Đợi 1 giây để xem email được nhập
            
            WebElement passwordInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("password"))
            );
            passwordInput.clear();
            passwordInput.sendKeys(adminUser.getPassword());
            pause(1000); // Đợi 1 giây để xem password được nhập
            
            WebElement submitButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit'], input[type='submit']")
                )
            );
            // Scroll vào view trước khi click
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
            pause(500);
            submitButton.click();
            pause(2000); // Đợi đăng nhập xong
            
            // Đợi đăng nhập thành công
            wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/home"),
                ExpectedConditions.urlContains("/View/home")
            ));
            System.out.println("[AdminE2ETest] Đăng nhập thành công! Session sẽ được tái sử dụng cho các test tiếp theo.");
            isLoggedIn = true;
        } else {
            // Đã đăng nhập rồi, chỉ cần đảm bảo vẫn ở trang home hoặc navigate về home
            try {
                String currentUrl = driver.getCurrentUrl();
                if (!currentUrl.contains("/home") && !currentUrl.contains("/admin")) {
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
    @DisplayName("E2E: Admin truy cập trang quản lý")
    void shouldAccessAdminPage() {
        if (adminUser == null) {
            return;
        }
        
        // Truy cập trang admin
        driver.get(BASE_URL + "/admin");
        System.out.println("\n========================================");
        System.out.println("[AdminE2ETest] TEST 1: Truy cập trang quản lý Admin");
        System.out.println("========================================");
        System.out.println("[AdminE2ETest] URL: " + driver.getCurrentUrl());
        System.out.println("[AdminE2ETest] Page Title: " + driver.getTitle());
        pause();
        
        // Kiểm tra đã vào trang admin
        wait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("/admin"),
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".admin-panel, [class*='admin'], h1, h2")
            )
        ));
        
        // Tìm và in ra các menu/button admin có sẵn
        try {
            java.util.List<WebElement> adminLinks = driver.findElements(
                By.cssSelector("a[href*='admin'], .admin-menu a, nav a, .sidebar a")
            );
            System.out.println("[AdminE2ETest] Tìm thấy " + adminLinks.size() + " menu items:");
            for (int i = 0; i < Math.min(adminLinks.size(), 10); i++) {
                String text = adminLinks.get(i).getText().trim();
                if (!text.isEmpty()) {
                    System.out.println("  - " + text);
                }
            }
        } catch (Exception e) {
            System.out.println("[AdminE2ETest] Không tìm thấy menu items (có thể UI khác)");
        }
        
        pause(3000); // Đợi 3 giây để xem rõ
    }
    
    @Test
    @Order(2)
    @DisplayName("E2E: Admin xem danh sách sản phẩm")
    void shouldViewProductsList() {
        if (adminUser == null) {
            return;
        }
        
        // Truy cập trang quản lý sản phẩm
        driver.get(BASE_URL + "/admin?action=products");
        System.out.println("\n========================================");
        System.out.println("[AdminE2ETest] TEST 2: Xem danh sách sản phẩm");
        System.out.println("========================================");
        System.out.println("[AdminE2ETest] URL: " + driver.getCurrentUrl());
        pause();
        
        // Kiểm tra có danh sách sản phẩm
        WebElement productList = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("table, .product-list, [class*='product'], tbody")
        ));
        
        // Đếm số sản phẩm trong bảng
        try {
            java.util.List<WebElement> productRows = driver.findElements(
                By.cssSelector("table tbody tr, .product-item, [class*='product-row']")
            );
            System.out.println("[AdminE2ETest] Tìm thấy " + productRows.size() + " sản phẩm trong danh sách");
            
            // In ra 5 sản phẩm đầu tiên
            for (int i = 0; i < Math.min(productRows.size(), 5); i++) {
                String rowText = productRows.get(i).getText().trim();
                if (!rowText.isEmpty() && rowText.length() < 200) {
                    System.out.println("  Sản phẩm " + (i + 1) + ": " + rowText.substring(0, Math.min(100, rowText.length())));
                }
            }
        } catch (Exception e) {
            System.out.println("[AdminE2ETest] Không thể đếm sản phẩm (có thể UI khác)");
        }
        
        pause(3000); // Đợi 3 giây để xem rõ
    }
    
    @Test
    @Order(3)
    @DisplayName("E2E: Admin xem danh sách đơn hàng")
    void shouldViewOrdersList() {
        if (adminUser == null) {
            return;
        }
        
        // Truy cập trang quản lý đơn hàng
        driver.get(BASE_URL + "/admin?action=orders");
        System.out.println("\n========================================");
        System.out.println("[AdminE2ETest] TEST 3: Xem danh sách đơn hàng");
        System.out.println("========================================");
        System.out.println("[AdminE2ETest] URL: " + driver.getCurrentUrl());
        pause();
        
        // Kiểm tra có danh sách đơn hàng
        WebElement orderList = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("table, .order-list, [class*='order'], tbody")
        ));
        
        // Đếm số đơn hàng trong bảng
        try {
            java.util.List<WebElement> orderRows = driver.findElements(
                By.cssSelector("table tbody tr, .order-item, [class*='order-row']")
            );
            System.out.println("[AdminE2ETest] Tìm thấy " + orderRows.size() + " đơn hàng trong danh sách");
            
            // In ra 5 đơn hàng đầu tiên
            for (int i = 0; i < Math.min(orderRows.size(), 5); i++) {
                String rowText = orderRows.get(i).getText().trim();
                if (!rowText.isEmpty() && rowText.length() < 200) {
                    System.out.println("  Đơn hàng " + (i + 1) + ": " + rowText.substring(0, Math.min(100, rowText.length())));
                }
            }
        } catch (Exception e) {
            System.out.println("[AdminE2ETest] Không thể đếm đơn hàng (có thể UI khác)");
        }
        
        pause(3000); // Đợi 3 giây để xem rõ
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

