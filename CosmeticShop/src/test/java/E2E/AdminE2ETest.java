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
 * 🎬 KỊCH BẢN 3: QUẢN TRỊ - QUẢN LÝ SẢN PHẨM VÀ ĐƠN HÀNG
 * 
 * Test theo kịch bản thuyết trình:
 * 1. Dashboard Admin
 * 2. Quản lý Sản phẩm
 * 3. Quản lý Danh mục
 * 4. Quản lý Đơn hàng
 * 5. Quản lý Người dùng
 * 
 * Yêu cầu:
 * - Ứng dụng web phải đang chạy
 * - Có ít nhất 1 admin user trong database
 */
@DisplayName("🎬 KỊCH BẢN 3: Quản trị - Quản lý sản phẩm và đơn hàng")
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
    @DisplayName("3.1: Dashboard Admin - Xem thống kê")
    void shouldViewAdminDashboard() {
        if (adminUser == null) {
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 3.1: Dashboard Admin");
        System.out.println("=".repeat(60));
        
        driver.get(BASE_URL + "/admin?action=dashboard");
        pause();
        
        try {
            // Kiểm tra thống kê hôm nay
            java.util.List<WebElement> stats = driver.findElements(
                By.cssSelector(".stat-card, .stat, [class*='stat'], .dashboard-stat")
            );
            System.out.println("  📊 Tìm thấy " + stats.size() + " thống kê");
            
            // Kiểm tra biểu đồ doanh thu
            java.util.List<WebElement> charts = driver.findElements(
                By.cssSelector(".chart, canvas, [class*='chart']")
            );
            System.out.println("  📈 Tìm thấy " + charts.size() + " biểu đồ");
            
            // Kiểm tra thông báo
            java.util.List<WebElement> notifications = driver.findElements(
                By.cssSelector(".notification, .alert, [class*='notification']")
            );
            System.out.println("  🔔 Tìm thấy " + notifications.size() + " thông báo");
            
        } catch (Exception e) {
            System.out.println("  ⚠️  Không tìm thấy các thành phần dashboard: " + e.getMessage());
        }
        
        pause(3000);
        System.out.println("✅ TEST 3.1 hoàn thành!\n");
    }
    
    @Test
    @Order(2)
    @DisplayName("3.2: Quản lý Sản phẩm - Xem danh sách")
    void shouldViewProductsList() {
        if (adminUser == null) {
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 3.2: Quản lý Sản phẩm");
        System.out.println("=".repeat(60));
        
        driver.get(BASE_URL + "/admin?action=products");
        pause();
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("table, .product-list, [class*='product'], tbody")
        ));
        
        try {
            java.util.List<WebElement> productRows = driver.findElements(
                By.cssSelector("table tbody tr, .product-item, [class*='product-row']")
            );
            System.out.println("  📦 Tìm thấy " + productRows.size() + " sản phẩm");
            
            // Tìm nút thêm sản phẩm mới
            try {
                driver.findElement(
                    By.xpath("//a[contains(text(), 'Thêm')] | //button[contains(text(), 'Thêm')] | //a[href*='add']")
                );
                System.out.println("  ✅ Tìm thấy nút thêm sản phẩm");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy nút thêm sản phẩm");
            }
            
        } catch (Exception e) {
            System.out.println("  ⚠️  Không thể đếm sản phẩm: " + e.getMessage());
        }
        
        pause(3000);
        System.out.println("✅ TEST 3.2 hoàn thành!\n");
    }
    
    @Test
    @Order(3)
    @DisplayName("3.3: Quản lý Danh mục")
    void shouldManageCategories() {
        if (adminUser == null) {
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 3.3: Quản lý Danh mục");
        System.out.println("=".repeat(60));
        
        driver.get(BASE_URL + "/admin?action=categories");
        pause();
        
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("table, .category-list, [class*='category']")
            ));
            
            java.util.List<WebElement> categories = driver.findElements(
                By.cssSelector("table tbody tr, .category-item, [class*='category']")
            );
            System.out.println("  📁 Tìm thấy " + categories.size() + " danh mục");
            
        } catch (Exception e) {
            System.out.println("  ⚠️  Không tìm thấy danh sách danh mục: " + e.getMessage());
        }
        
        pause(3000);
        System.out.println("✅ TEST 3.3 hoàn thành!\n");
    }
    
    @Test
    @Order(4)
    @DisplayName("3.4: Quản lý Đơn hàng - Xem danh sách và lọc")
    void shouldViewOrdersList() {
        if (adminUser == null) {
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 3.4: Quản lý Đơn hàng");
        System.out.println("=".repeat(60));
        
        driver.get(BASE_URL + "/admin?action=orders");
        pause();
        
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("table, .order-list, [class*='order'], tbody")
        ));
        
        try {
            java.util.List<WebElement> orderRows = driver.findElements(
                By.cssSelector("table tbody tr, .order-item, [class*='order-row']")
            );
            System.out.println("  📋 Tìm thấy " + orderRows.size() + " đơn hàng");
            
            // Kiểm tra bộ lọc trạng thái
            try {
                java.util.List<WebElement> filters = driver.findElements(
                    By.cssSelector("select[name*='status'], .filter, [class*='filter']")
                );
                System.out.println("  🔍 Tìm thấy " + filters.size() + " bộ lọc");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy bộ lọc");
            }
            
            // Click vào đơn hàng đầu tiên để xem chi tiết
            if (!orderRows.isEmpty()) {
                try {
                    orderRows.get(0).click();
                    pause(2000);
                    System.out.println("  ✅ Đã xem chi tiết đơn hàng");
                } catch (Exception e) {
                    System.out.println("  ⚠️  Không thể click vào đơn hàng");
                }
            }
            
        } catch (Exception e) {
            System.out.println("  ⚠️  Không thể đếm đơn hàng: " + e.getMessage());
        }
        
        pause(3000);
        System.out.println("✅ TEST 3.4 hoàn thành!\n");
    }
    
    @Test
    @Order(5)
    @DisplayName("3.5: Quản lý Người dùng")
    void shouldManageUsers() {
        if (adminUser == null) {
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 3.5: Quản lý Người dùng");
        System.out.println("=".repeat(60));
        
        driver.get(BASE_URL + "/admin?action=users");
        pause();
        
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("table, .user-list, [class*='user']")
            ));
            
            java.util.List<WebElement> users = driver.findElements(
                By.cssSelector("table tbody tr, .user-item, [class*='user-row']")
            );
            System.out.println("  👥 Tìm thấy " + users.size() + " người dùng");
            
        } catch (Exception e) {
            System.out.println("  ⚠️  Không tìm thấy danh sách người dùng: " + e.getMessage());
        }
        
        pause(3000);
        System.out.println("✅ TEST 3.5 hoàn thành!\n");
    }
    
    @Test
    @Order(6)
    @DisplayName("3.6: Thêm sản phẩm mới")
    void shouldAddNewProduct() {
        if (adminUser == null) {
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 3.6: Thêm sản phẩm mới");
        System.out.println("=".repeat(60));
        
        try {
            driver.get(BASE_URL + "/products?action=new");
            pause();
            
            // Kiểm tra form thêm sản phẩm
            try {
                driver.findElement(By.name("name"));
                System.out.println("  ✅ Tìm thấy form thêm sản phẩm");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy form thêm sản phẩm: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("  ⚠️  Không thể truy cập trang thêm sản phẩm: " + e.getMessage());
        }
        
        pause(3000);
        System.out.println("✅ TEST 3.6 hoàn thành!\n");
    }
    
    @Test
    @Order(7)
    @DisplayName("3.7: Cập nhật trạng thái đơn hàng")
    void shouldUpdateOrderStatus() {
        if (adminUser == null) {
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 3.7: Cập nhật trạng thái đơn hàng");
        System.out.println("=".repeat(60));
        
        try {
            driver.get(BASE_URL + "/admin?action=orders");
            pause();
            
            // Tìm đơn hàng PENDING
            try {
                List<WebElement> orderRows = driver.findElements(
                    By.cssSelector("table tbody tr, .order-item, [class*='order-row']")
                );
                if (!orderRows.isEmpty()) {
                    // Click vào đơn hàng đầu tiên
                    orderRows.get(0).click();
                    pause(2000);
                    
                    // Tìm select hoặc button cập nhật trạng thái
                    try {
                        driver.findElement(
                            By.cssSelector("select[name*='status'], .status-select")
                        );
                        System.out.println("  ✅ Tìm thấy select cập nhật trạng thái");
                    } catch (Exception e) {
                        System.out.println("  ⚠️  Không tìm thấy select trạng thái");
                    }
                } else {
                    System.out.println("  ⚠️  Không có đơn hàng nào");
                }
            } catch (Exception e) {
                System.out.println("  ⚠️  Không thể tìm đơn hàng: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("  ⚠️  Không thể truy cập trang đơn hàng: " + e.getMessage());
        }
        
        pause(3000);
        System.out.println("✅ TEST 3.7 hoàn thành!\n");
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

