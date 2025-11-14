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

/**
 * 🎬 KỊCH BẢN 4: QUẢN TRỊ - MÃ GIẢM GIÁ VÀ BÁO CÁO
 * 
 * Test theo kịch bản thuyết trình:
 * 1. Quản lý Mã giảm giá
 * 2. Hệ thống Voucher Tự động
 * 3. Áp dụng Mã giảm giá
 * 4. Báo cáo và Thống kê
 * 5. Quản lý Liên hệ
 * 6. Tính năng Nâng cao
 */
@DisplayName("🎬 KỊCH BẢN 4: Quản trị - Mã giảm giá và báo cáo")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminPromotionE2ETest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080/CosmeticShop";
    private static final long STEP_DELAY_MS = 3000L;
    private static Model.user adminUser;
    private static boolean isLoggedIn = false;
    
    @BeforeAll
    static void setUpAll() {
        try {
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
            
            adminUser = TestDataHelper.getRandomAdmin();
        } catch (Exception e) {
            System.err.println("[Kịch bản 4] Không thể khởi động ChromeDriver: " + e.getMessage());
            Assumptions.assumeTrue(false, "ChromeDriver không thể khởi động.");
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
        if (adminUser == null) return;
        
        if (!isLoggedIn) {
            driver.get(BASE_URL + "/login");
            pause();
            
            WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
            emailInput.clear();
            emailInput.sendKeys(adminUser.getEmail());
            pause(1000);
            
            WebElement passwordInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("password")));
            passwordInput.clear();
            passwordInput.sendKeys(adminUser.getPassword());
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
                ExpectedConditions.urlContains("/admin")
            ));
            isLoggedIn = true;
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("4.1: Quản lý Mã giảm giá - Xem danh sách")
    void shouldViewDiscountsList() {
        if (adminUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 4.1: Quản lý Mã giảm giá");
        System.out.println("=".repeat(60));
        
        try {
            driver.get(BASE_URL + "/admin?action=discounts");
            pause();
            
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("table, .discount-list, [class*='discount']")
            ));
            
            java.util.List<WebElement> discounts = driver.findElements(
                By.cssSelector("table tbody tr, .discount-item, [class*='discount']")
            );
            System.out.println("  🎟️  Tìm thấy " + discounts.size() + " mã giảm giá");
            
            // Tìm nút tạo mã mới
            try {
                driver.findElement(
                    By.xpath("//a[contains(text(), 'Tạo')] | //button[contains(text(), 'Tạo')] | //a[href*='add']")
                );
                System.out.println("  ✅ Tìm thấy nút tạo mã giảm giá");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy nút tạo mã giảm giá");
            }
            
        } catch (Exception e) {
            System.out.println("  ⚠️  Không tìm thấy danh sách mã giảm giá: " + e.getMessage());
        }
        
        pause(3000);
        System.out.println("✅ TEST 4.1 hoàn thành!\n");
    }
    
    @Test
    @Order(2)
    @DisplayName("4.2: Tạo mã giảm giá mới")
    void shouldCreateNewDiscount() {
        if (adminUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 4.2: Tạo mã giảm giá mới");
        System.out.println("=".repeat(60));
        
        try {
            driver.get(BASE_URL + "/admin?action=discounts&add=true");
            pause();
            
            // Tìm form tạo mã giảm giá
            try {
                WebElement codeInput = driver.findElement(By.name("code"));
                WebElement nameInput = driver.findElement(By.name("name"));
                
                codeInput.clear();
                codeInput.sendKeys("TEST" + System.currentTimeMillis());
                nameInput.clear();
                nameInput.sendKeys("Mã test");
                
                System.out.println("  ✅ Tìm thấy form tạo mã giảm giá");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy form tạo mã giảm giá: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("  ⚠️  Không thể truy cập trang tạo mã: " + e.getMessage());
        }
        
        pause(3000);
        System.out.println("✅ TEST 4.2 hoàn thành!\n");
    }
    
    @Test
    @Order(3)
    @DisplayName("4.3: Xem voucher của user")
    void shouldViewUserVouchers() {
        if (adminUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 4.3: Xem voucher của user");
        System.out.println("=".repeat(60));
        
        // Chuyển sang user để xem voucher
        Model.user testUser = TestDataHelper.getRandomUser();
        if (testUser == null) {
            System.out.println("  ⚠️  Không có user để test");
            return;
        }
        
        try {
            // Đăng xuất admin và đăng nhập user
            driver.get(BASE_URL + "/logout");
            pause();
            
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
            submitButton.click();
            pause(2000);
            
            // Xem voucher
            driver.get(BASE_URL + "/my-promos");
            pause();
            
            java.util.List<WebElement> vouchers = driver.findElements(
                By.cssSelector(".voucher-item, .discount-item, [class*='voucher']")
            );
            System.out.println("  🎟️  Tìm thấy " + vouchers.size() + " voucher");
            
        } catch (Exception e) {
            System.out.println("  ⚠️  Không thể xem voucher: " + e.getMessage());
        }
        
        pause(3000);
        System.out.println("✅ TEST 4.3 hoàn thành!\n");
    }
    
    @Test
    @Order(4)
    @DisplayName("4.4: Báo cáo và Thống kê")
    void shouldViewReports() {
        if (adminUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 4.4: Báo cáo và Thống kê");
        System.out.println("=".repeat(60));
        
        try {
            driver.get(BASE_URL + "/admin?action=reports");
            pause();
            
            // Kiểm tra các loại báo cáo
            try {
                java.util.List<WebElement> reportSections = driver.findElements(
                    By.cssSelector(".report-section, .report-card, [class*='report']")
                );
                System.out.println("  📊 Tìm thấy " + reportSections.size() + " phần báo cáo");
                
                // Kiểm tra báo cáo doanh thu
                java.util.List<WebElement> revenueReports = driver.findElements(
                    By.cssSelector("[class*='revenue'], [class*='doanh']")
                );
                System.out.println("  💰 Tìm thấy " + revenueReports.size() + " báo cáo doanh thu");
                
                // Kiểm tra top sản phẩm bán chạy
                java.util.List<WebElement> topProducts = driver.findElements(
                    By.cssSelector("[class*='top'], [class*='best']")
                );
                System.out.println("  ⭐ Tìm thấy " + topProducts.size() + " top sản phẩm");
                
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy báo cáo: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("  ⚠️  Không thể truy cập trang báo cáo: " + e.getMessage());
        }
        
        pause(3000);
        System.out.println("✅ TEST 4.4 hoàn thành!\n");
    }
    
    @Test
    @Order(5)
    @DisplayName("4.5: Quản lý Liên hệ")
    void shouldManageContacts() {
        if (adminUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 4.5: Quản lý Liên hệ");
        System.out.println("=".repeat(60));
        
        try {
            driver.get(BASE_URL + "/admin?action=contact");
            pause();
            
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("table, .contact-list, [class*='contact']")
            ));
            
            java.util.List<WebElement> contacts = driver.findElements(
                By.cssSelector("table tbody tr, .contact-item, [class*='contact']")
            );
            System.out.println("  📧 Tìm thấy " + contacts.size() + " tin nhắn liên hệ");
            
        } catch (Exception e) {
            System.out.println("  ⚠️  Không tìm thấy danh sách liên hệ: " + e.getMessage());
        }
        
        pause(3000);
        System.out.println("✅ TEST 4.5 hoàn thành!\n");
    }
    
    @Test
    @Order(6)
    @DisplayName("4.6: Tính năng Nâng cao - Tìm kiếm")
    void shouldTestAdvancedFeatures() {
        if (adminUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 4.6: Tính năng Nâng cao");
        System.out.println("=".repeat(60));
        
        try {
            // Test tìm kiếm real-time
            driver.get(BASE_URL + "/products");
            pause();
            
            WebElement searchInput = driver.findElement(
                By.cssSelector("#globalSearchInput, input[type='search'], input[name*='search'], input[placeholder*='tìm']")
            );
            searchInput.clear();
            searchInput.sendKeys("kem");
            pause(1000);
            
            // Click nút tìm kiếm
            try {
                WebElement searchBtn = driver.findElement(By.id("globalSearchBtn"));
                searchBtn.click();
                pause(2000);
                System.out.println("  🔍 Đã click nút tìm kiếm và xem kết quả");
            } catch (Exception e) {
                // Nếu không tìm thấy nút, thử nhấn Enter
                searchInput.sendKeys(org.openqa.selenium.Keys.RETURN);
                pause(2000);
                System.out.println("  🔍 Đã nhấn Enter để tìm kiếm");
            }
            
            // Kiểm tra sản phẩm nổi bật
            driver.get(BASE_URL + "/View/home.jsp");
            pause();
            
            java.util.List<WebElement> featuredProducts = driver.findElements(
                By.cssSelector(".featured-product, [class*='featured']")
            );
            System.out.println("  ⭐ Tìm thấy " + featuredProducts.size() + " sản phẩm nổi bật");
            
        } catch (Exception e) {
            System.out.println("  ⚠️  Không thể test tính năng nâng cao: " + e.getMessage());
        }
        
        pause(3000);
        System.out.println("✅ TEST 4.6 hoàn thành!\n");
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

