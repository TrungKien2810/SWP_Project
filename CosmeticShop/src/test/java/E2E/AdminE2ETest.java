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

import static org.assertj.core.api.Assertions.assertThat;

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
    
    @BeforeEach
    void setUp() {
        if (adminUser == null) {
            System.out.println("[AdminE2ETest] Skip test vì không có admin user");
            return;
        }
        
        // Đăng nhập admin trước mỗi test
        driver.get(BASE_URL + "/login");
        
        WebElement emailInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.name("email"))
        );
        emailInput.sendKeys(adminUser.getEmail());
        
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys(adminUser.getPassword());
        
        WebElement submitButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='submit'], input[type='submit']")
            )
        );
        // Scroll vào view trước khi click
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        submitButton.click();
        
        // Đợi đăng nhập thành công
        wait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("/home"),
            ExpectedConditions.urlContains("/View/home")
        ));
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
        
        // Kiểm tra đã vào trang admin
        wait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("/admin"),
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".admin-panel, [class*='admin'], h1, h2")
            )
        ));
        
        // Kiểm tra có các chức năng admin (tùy theo UI)
        // Ví dụ: Quản lý sản phẩm, Quản lý đơn hàng, etc.
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
        
        // Kiểm tra có danh sách sản phẩm
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("table, .product-list, [class*='product']")
        ));
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
        
        // Kiểm tra có danh sách đơn hàng
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector("table, .order-list, [class*='order']")
        ));
    }
}

