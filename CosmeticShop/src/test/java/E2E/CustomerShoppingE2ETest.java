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
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 🎬 KỊCH BẢN 1: TRẢI NGHIỆM KHÁCH HÀNG - MUA SẮM VÀ THANH TOÁN
 * 
 * Test theo kịch bản thuyết trình:
 * 1. Trang chủ và Duyệt Sản phẩm
 * 2. Quản lý Giỏ hàng
 * 3. Checkout và Thanh toán
 * 4. Feedback Sản phẩm
 * 
 * Yêu cầu:
 * - Ứng dụng web phải đang chạy
 * - Có ít nhất 1 user và 1 sản phẩm trong database
 */
@DisplayName("🎬 KỊCH BẢN 1: Trải nghiệm khách hàng - Mua sắm và thanh toán")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerShoppingE2ETest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080/CosmeticShop";
    private static final long STEP_DELAY_MS = 3000L;
    private static final long VISUAL_DELAY_MS = 2000L;
    private static Model.user testUser;
    private static boolean isLoggedIn = false;
    private static final String SCREENSHOT_DIR = "test-screenshots/scenario1";
    private static TestReportGenerator reportGenerator;
    private static ThreadLocal<List<String>> currentTestSteps = new ThreadLocal<>();
    private static ThreadLocal<String> currentTestName = new ThreadLocal<>();
    private static ThreadLocal<String> currentScreenshotPath = new ThreadLocal<>();
    
    @BeforeAll
    static void setUpAll() {
        try {
            reportGenerator = new TestReportGenerator("CustomerShoppingE2ETest");
            System.out.println("[Kịch bản 1] Đã khởi tạo Test Report Generator");
            
            Path screenshotPath = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotPath)) {
                Files.createDirectories(screenshotPath);
                System.out.println("[Kịch bản 1] Đã tạo thư mục screenshots: " + screenshotPath.toAbsolutePath());
            }
            
            WebDriverManager.chromedriver().setup();
            
            ChromeOptions options = new ChromeOptions();
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
            }
            
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            driver.manage().window().maximize();
            
            testUser = TestDataHelper.getRandomUser();
            if (testUser == null) {
                System.out.println("[Kịch bản 1] Không tìm thấy user trong database!");
            }
        } catch (Exception e) {
            System.err.println("[Kịch bản 1] Không thể khởi động ChromeDriver: " + e.getMessage());
            Assumptions.assumeTrue(false, "ChromeDriver không thể khởi động. Skip E2E tests.");
        }
    }
    
    @AfterEach
    void tearDown() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @AfterAll
    static void tearDownAll() {
        if (reportGenerator != null) {
            try {
                reportGenerator.finish();
                String reportPath = reportGenerator.generateReport();
                System.out.println("\n" + "=".repeat(60));
                System.out.println("📊 TEST REPORT ĐÃ ĐƯỢC TẠO!");
                System.out.println("=".repeat(60));
                System.out.println("📁 File: " + new File(reportPath).getAbsolutePath());
                System.out.println("=".repeat(60) + "\n");
            } catch (Exception e) {
                System.err.println("❌ Lỗi khi tạo report: " + e.getMessage());
            }
        }
        
        if (driver != null) {
            driver.quit();
        }
    }
    
    @BeforeEach
    void setUpTest() {
        currentTestSteps.set(new ArrayList<>());
        currentScreenshotPath.set(null);
    }
    
    @BeforeEach
    void setUp() {
        if (testUser == null) {
            return;
        }
        
        if (!isLoggedIn) {
            System.out.println("\n[Kịch bản 1] Đăng nhập user...");
            driver.get(BASE_URL + "/login");
            pause();
            
            WebElement emailInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("email"))
            );
            emailInput.clear();
            emailInput.sendKeys(testUser.getEmail());
            pause(1000);
            
            WebElement passwordInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("password"))
            );
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
            System.out.println("[Kịch bản 1] Đăng nhập thành công!");
            isLoggedIn = true;
        } else {
            try {
                String currentUrl = driver.getCurrentUrl();
                if (!currentUrl.contains("/home") && !currentUrl.contains("/checkout")) {
                    driver.get(BASE_URL + "/View/home.jsp");
                    pause(500);
                }
            } catch (Exception e) {
                isLoggedIn = false;
                setUp();
            }
        }
    }
    
    // ========== PHẦN 1: TRANG CHỦ VÀ DUYỆT SẢN PHẨM ==========
    
    @Test
    @Order(1)
    @DisplayName("1.1: Xem trang chủ và sản phẩm nổi bật")
    void shouldViewHomePageAndFeaturedProducts() {
        currentTestName.set("TEST_1_1_HomePage");
        String testName = currentTestName.get();
        
        if (testUser == null) {
            if (reportGenerator != null) {
                reportGenerator.addTestResult(testName, "SKIP", 
                    "1.1: Xem trang chủ và sản phẩm nổi bật", 
                    null, "No test user available", null);
            }
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 1.1: Trang chủ và sản phẩm nổi bật");
        System.out.println("=".repeat(60));
        
        String errorMessage = null;
        try {
            logStep("1.1.1", "Truy cập trang chủ");
            currentTestSteps.get().add("1.1.1 - Truy cập trang chủ");
            driver.get(BASE_URL + "/View/home.jsp");
            System.out.println("  🌐 URL: " + driver.getCurrentUrl());
            pause();
            takeScreenshot(testName, "01_HomePage");
            
            logStep("1.1.2", "Kiểm tra banner và sản phẩm nổi bật");
            currentTestSteps.get().add("1.1.2 - Kiểm tra banner và sản phẩm nổi bật");
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("body, .container, .main-content")
            ));
            
            // Tìm banner
            try {
                List<WebElement> banners = driver.findElements(
                    By.cssSelector(".banner, .carousel, .slider, [class*='banner']")
                );
                System.out.println("  🖼️  Tìm thấy " + banners.size() + " banner");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy banner");
            }
            
            // Tìm sản phẩm nổi bật
            try {
                List<WebElement> featuredProducts = driver.findElements(
                    By.cssSelector(".featured-product, .product-card, [class*='product']")
                );
                System.out.println("  ⭐ Tìm thấy " + featuredProducts.size() + " sản phẩm nổi bật");
                if (!featuredProducts.isEmpty()) {
                    scrollAndHighlight(featuredProducts.get(0), "Sản phẩm nổi bật đầu tiên");
                }
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy sản phẩm nổi bật");
            }
            
            takeScreenshot(testName, "02_FeaturedProducts");
            pause(2000);
            System.out.println("\n✅ TEST 1.1 hoàn thành!\n");
            
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println("\n❌ TEST 1.1 thất bại: " + errorMessage);
            e.printStackTrace();
        } finally {
            if (reportGenerator != null) {
                String status = errorMessage == null ? "PASS" : "FAIL";
                reportGenerator.addTestResult(testName, status,
                    "1.1: Xem trang chủ và sản phẩm nổi bật",
                    currentTestSteps.get(), errorMessage, currentScreenshotPath.get());
            }
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("1.2: Xem bộ sưu tập và tìm kiếm sản phẩm")
    void shouldViewCollectionAndSearchProducts() {
        currentTestName.set("TEST_1_2_Collection");
        String testName = currentTestName.get();
        
        if (testUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 1.2: Bộ sưu tập và tìm kiếm");
        System.out.println("=".repeat(60));
        
        String errorMessage = null;
        try {
            logStep("1.2.1", "Truy cập bộ sưu tập");
            currentTestSteps.get().add("1.2.1 - Truy cập bộ sưu tập");
            driver.get(BASE_URL + "/products");
            pause();
            takeScreenshot(testName, "01_CollectionPage");
            
            logStep("1.2.2", "Kiểm tra danh sách sản phẩm");
            currentTestSteps.get().add("1.2.2 - Kiểm tra danh sách sản phẩm");
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".product-list, .products, table, [class*='product']")
            ));
            
            List<WebElement> products = driver.findElements(
                By.cssSelector(".product-item, .product-card, tr[class*='product']")
            );
            System.out.println("  📦 Tìm thấy " + products.size() + " sản phẩm");
            if (!products.isEmpty()) {
                scrollAndHighlight(products.get(0), "Sản phẩm đầu tiên");
            }
            
            logStep("1.2.3", "Tìm kiếm sản phẩm");
            currentTestSteps.get().add("1.2.3 - Tìm kiếm sản phẩm");
            try {
                // Tìm ô tìm kiếm trong header
                WebElement searchInput = driver.findElement(
                    By.cssSelector("#globalSearchInput, input[type='search'], input[name*='search'], input[placeholder*='tìm']")
                );
                searchInput.clear();
                searchInput.sendKeys("kem");
                pause(1000);
                scrollAndHighlight(searchInput, "Ô tìm kiếm");
                
                // Click nút tìm kiếm
                try {
                    WebElement searchBtn = driver.findElement(By.id("globalSearchBtn"));
                    scrollAndHighlight(searchBtn, "Nút tìm kiếm");
                    searchBtn.click();
                    pause(2000);
                    System.out.println("  ✅ Đã click nút tìm kiếm");
                } catch (Exception e) {
                    // Nếu không tìm thấy nút, thử nhấn Enter
                    searchInput.sendKeys(org.openqa.selenium.Keys.RETURN);
                    pause(2000);
                    System.out.println("  ✅ Đã nhấn Enter để tìm kiếm");
                }
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy ô tìm kiếm: " + e.getMessage());
            }
            
            takeScreenshot(testName, "02_SearchResults");
            pause(2000);
            System.out.println("\n✅ TEST 1.2 hoàn thành!\n");
            
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println("\n❌ TEST 1.2 thất bại: " + errorMessage);
            e.printStackTrace();
        } finally {
            if (reportGenerator != null) {
                String status = errorMessage == null ? "PASS" : "FAIL";
                reportGenerator.addTestResult(testName, status,
                    "1.2: Xem bộ sưu tập và tìm kiếm sản phẩm",
                    currentTestSteps.get(), errorMessage, currentScreenshotPath.get());
            }
        }
    }
    
    @Test
    @Order(3)
    @DisplayName("1.3: Xem chi tiết sản phẩm")
    void shouldViewProductDetail() {
        currentTestName.set("TEST_1_3_ProductDetail");
        String testName = currentTestName.get();
        
        if (testUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 1.3: Chi tiết sản phẩm");
        System.out.println("=".repeat(60));
        
        String errorMessage = null;
        try {
            logStep("1.3.1", "Truy cập trang sản phẩm");
            currentTestSteps.get().add("1.3.1 - Truy cập trang sản phẩm");
            Model.Product testProduct = TestDataHelper.getRandomProductInStock();
            if (testProduct == null) {
                System.out.println("  ⚠️  Không có sản phẩm trong kho");
                return;
            }
            
            driver.get(BASE_URL + "/product-detail?id=" + testProduct.getProductId());
            pause();
            takeScreenshot(testName, "01_ProductDetail");
            
            logStep("1.3.2", "Kiểm tra thông tin sản phẩm");
            currentTestSteps.get().add("1.3.2 - Kiểm tra thông tin sản phẩm");
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".product-detail, .product-info, [class*='product']")
            ));
            
            // Kiểm tra tên sản phẩm
            try {
                List<WebElement> productNames = driver.findElements(
                    By.cssSelector("h1, h2, .product-name, [class*='name']")
                );
                if (!productNames.isEmpty()) {
                    System.out.println("  📝 Tên sản phẩm: " + productNames.get(0).getText());
                    scrollAndHighlight(productNames.get(0), "Tên sản phẩm");
                }
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy tên sản phẩm");
            }
            
            // Kiểm tra gallery ảnh
            try {
                List<WebElement> images = driver.findElements(
                    By.cssSelector(".product-gallery img, .gallery img, img[src*='product']")
                );
                System.out.println("  🖼️  Tìm thấy " + images.size() + " ảnh trong gallery");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy gallery ảnh");
            }
            
            // Kiểm tra đánh giá
            try {
                List<WebElement> ratings = driver.findElements(
                    By.cssSelector(".rating, .stars, [class*='rating']")
                );
                System.out.println("  ⭐ Tìm thấy " + ratings.size() + " đánh giá");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy đánh giá");
            }
            
            takeScreenshot(testName, "02_ProductInfo");
            pause(2000);
            System.out.println("\n✅ TEST 1.3 hoàn thành!\n");
            
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println("\n❌ TEST 1.3 thất bại: " + errorMessage);
            e.printStackTrace();
        } finally {
            if (reportGenerator != null) {
                String status = errorMessage == null ? "PASS" : "FAIL";
                reportGenerator.addTestResult(testName, status,
                    "1.3: Xem chi tiết sản phẩm",
                    currentTestSteps.get(), errorMessage, currentScreenshotPath.get());
            }
        }
    }
    
    // ========== PHẦN 2: QUẢN LÝ GIỎ HÀNG ==========
    
    @Test
    @Order(4)
    @DisplayName("2.1: Thêm sản phẩm vào giỏ hàng")
    void shouldAddProductToCart() {
        currentTestName.set("TEST_2_1_AddToCart");
        String testName = currentTestName.get();
        
        if (testUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 2.1: Thêm sản phẩm vào giỏ hàng");
        System.out.println("=".repeat(60));
        
        String errorMessage = null;
        try {
            logStep("2.1.1", "Truy cập trang sản phẩm");
            currentTestSteps.get().add("2.1.1 - Truy cập trang sản phẩm");
            Model.Product testProduct = TestDataHelper.getRandomProductInStock();
            if (testProduct == null) {
                System.out.println("  ⚠️  Không có sản phẩm trong kho");
                return;
            }
            
            driver.get(BASE_URL + "/product-detail?id=" + testProduct.getProductId());
            pause();
            
            logStep("2.1.2", "Click nút Thêm vào giỏ hàng");
            currentTestSteps.get().add("2.1.2 - Click nút Thêm vào giỏ hàng");
            WebElement addToCartBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@href, 'addToCart')] | //button[contains(text(), 'Thêm vào giỏ')] | //a[contains(text(), 'Thêm vào giỏ')]")
                )
            );
            scrollAndHighlight(addToCartBtn, "Nút Thêm vào giỏ hàng");
            addToCartBtn.click();
            pause(2000);
            takeScreenshot(testName, "01_AddToCart");
            
            logStep("2.1.3", "Kiểm tra thông báo thành công");
            currentTestSteps.get().add("2.1.3 - Kiểm tra thông báo thành công");
            try {
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".success, .alert-success, [class*='success']")
                    ),
                    ExpectedConditions.urlContains("/cart")
                ));
                System.out.println("  ✅ Đã thêm sản phẩm vào giỏ hàng");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy thông báo thành công");
            }
            
            pause(2000);
            System.out.println("\n✅ TEST 2.1 hoàn thành!\n");
            
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println("\n❌ TEST 2.1 thất bại: " + errorMessage);
            e.printStackTrace();
        } finally {
            if (reportGenerator != null) {
                String status = errorMessage == null ? "PASS" : "FAIL";
                reportGenerator.addTestResult(testName, status,
                    "2.1: Thêm sản phẩm vào giỏ hàng",
                    currentTestSteps.get(), errorMessage, currentScreenshotPath.get());
            }
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("2.2: Xem và quản lý giỏ hàng")
    void shouldViewAndManageCart() {
        currentTestName.set("TEST_2_2_ManageCart");
        String testName = currentTestName.get();
        
        if (testUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 2.2: Quản lý giỏ hàng");
        System.out.println("=".repeat(60));
        
        String errorMessage = null;
        try {
            logStep("2.2.1", "Truy cập trang giỏ hàng");
            currentTestSteps.get().add("2.2.1 - Truy cập trang giỏ hàng");
            driver.get(BASE_URL + "/cart");
            pause();
            takeScreenshot(testName, "01_CartPage");
            
            logStep("2.2.2", "Kiểm tra danh sách sản phẩm trong giỏ");
            currentTestSteps.get().add("2.2.2 - Kiểm tra danh sách sản phẩm");
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".cart-item, .cart-product, table, [class*='cart']")
            ));
            
            List<WebElement> cartItems = driver.findElements(
                By.cssSelector(".cart-item, .cart-product, tr[class*='item']")
            );
            System.out.println("  📦 Tìm thấy " + cartItems.size() + " sản phẩm trong giỏ hàng");
            if (!cartItems.isEmpty()) {
                scrollAndHighlight(cartItems.get(0), "Sản phẩm đầu tiên trong giỏ");
            }
            
            logStep("2.2.3", "Kiểm tra tổng tiền");
            currentTestSteps.get().add("2.2.3 - Kiểm tra tổng tiền");
            try {
                WebElement totalElement = driver.findElement(
                    By.cssSelector("#grandTotal, .total, [class*='total']")
                );
                scrollAndHighlight(totalElement, "Tổng tiền");
                System.out.println("  💰 Tổng tiền: " + totalElement.getText());
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy tổng tiền");
            }
            
            takeScreenshot(testName, "02_CartSummary");
            pause(2000);
            System.out.println("\n✅ TEST 2.2 hoàn thành!\n");
            
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println("\n❌ TEST 2.2 thất bại: " + errorMessage);
            e.printStackTrace();
        } finally {
            if (reportGenerator != null) {
                String status = errorMessage == null ? "PASS" : "FAIL";
                reportGenerator.addTestResult(testName, status,
                    "2.2: Xem và quản lý giỏ hàng",
                    currentTestSteps.get(), errorMessage, currentScreenshotPath.get());
            }
        }
    }
    
    // ========== PHẦN 3: CHECKOUT VÀ THANH TOÁN ==========
    
    @Test
    @Order(6)
    @DisplayName("3.1: Checkout với mã giảm giá")
    void shouldCheckoutWithPromotionCode() {
        currentTestName.set("TEST_3_1_CheckoutWithPromotion");
        String testName = currentTestName.get();
        
        if (testUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 3.1: Checkout với mã giảm giá");
        System.out.println("=".repeat(60));
        
        String errorMessage = null;
        try {
            logStep("3.1.1", "Truy cập trang checkout");
            currentTestSteps.get().add("3.1.1 - Truy cập trang checkout");
            driver.get(BASE_URL + "/checkout");
            pause();
            takeScreenshot(testName, "01_CheckoutPage");
            
            logStep("3.1.2", "Chọn địa chỉ giao hàng");
            currentTestSteps.get().add("3.1.2 - Chọn địa chỉ giao hàng");
            try {
                WebElement addressSelect = driver.findElement(By.name("shipping_address_id"));
                List<WebElement> options = addressSelect.findElements(By.tagName("option"));
                if (!options.isEmpty()) {
                    org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(addressSelect);
                    select.selectByIndex(0);
                    System.out.println("  📍 Đã chọn địa chỉ giao hàng");
                }
            } catch (Exception e) {
                System.out.println("  ⚠️  Không có địa chỉ giao hàng");
            }
            
            logStep("3.1.3", "Chọn phương thức vận chuyển");
            currentTestSteps.get().add("3.1.3 - Chọn phương thức vận chuyển");
            try {
                List<WebElement> shippingMethods = driver.findElements(
                    By.cssSelector("input[type='radio'][name='shipping_method_id']")
                );
                if (!shippingMethods.isEmpty() && !shippingMethods.get(0).isSelected()) {
                    shippingMethods.get(0).click();
                    System.out.println("  🚚 Đã chọn phương thức vận chuyển");
                }
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy phương thức vận chuyển");
            }
            
            logStep("3.1.4", "Áp dụng mã giảm giá");
            currentTestSteps.get().add("3.1.4 - Áp dụng mã giảm giá");
            try {
                WebElement promoInput = driver.findElement(
                    By.cssSelector("input[name*='promo'], input[name*='discount'], input[name*='code']")
                );
                promoInput.clear();
                promoInput.sendKeys("SPRING10");
                pause(1000);
                
                WebElement applyBtn = driver.findElement(
                    By.xpath("//button[contains(text(), 'Áp dụng')] | //button[contains(text(), 'Apply')]")
                );
                applyBtn.click();
                pause(2000);
                System.out.println("  🎟️  Đã áp dụng mã giảm giá");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy ô nhập mã giảm giá");
            }
            
            takeScreenshot(testName, "02_CheckoutWithPromo");
            pause(2000);
            System.out.println("\n✅ TEST 3.1 hoàn thành!\n");
            
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println("\n❌ TEST 3.1 thất bại: " + errorMessage);
            e.printStackTrace();
        } finally {
            if (reportGenerator != null) {
                String status = errorMessage == null ? "PASS" : "FAIL";
                reportGenerator.addTestResult(testName, status,
                    "3.1: Checkout với mã giảm giá",
                    currentTestSteps.get(), errorMessage, currentScreenshotPath.get());
            }
        }
    }
    
    @Test
    @Order(7)
    @DisplayName("3.2: Thanh toán COD - Tạo đơn hàng")
    void shouldCheckoutWithCOD() {
        currentTestName.set("TEST_3_2_CheckoutCOD");
        String testName = currentTestName.get();
        
        if (testUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 3.2: Thanh toán COD");
        System.out.println("=".repeat(60));
        
        String errorMessage = null;
        try {
            logStep("3.2.1", "Truy cập trang checkout");
            currentTestSteps.get().add("3.2.1 - Truy cập trang checkout");
            driver.get(BASE_URL + "/checkout");
            pause();
            
            logStep("3.2.2", "Chọn địa chỉ và phương thức vận chuyển");
            currentTestSteps.get().add("3.2.2 - Chọn địa chỉ và phương thức vận chuyển");
            try {
                WebElement addressSelect = driver.findElement(By.name("shipping_address_id"));
                List<WebElement> options = addressSelect.findElements(By.tagName("option"));
                if (!options.isEmpty()) {
                    org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(addressSelect);
                    select.selectByIndex(0);
                }
                
                List<WebElement> shippingMethods = driver.findElements(
                    By.cssSelector("input[type='radio'][name='shipping_method_id']")
                );
                if (!shippingMethods.isEmpty() && !shippingMethods.get(0).isSelected()) {
                    shippingMethods.get(0).click();
                }
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy địa chỉ/phương thức vận chuyển");
            }
            
            logStep("3.2.3", "Chọn phương thức thanh toán COD");
            currentTestSteps.get().add("3.2.3 - Chọn phương thức thanh toán COD");
            WebElement codMethod = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[type='radio'][value='COD'], input[type='radio'][value='cod']")
                )
            );
            scrollAndHighlight(codMethod, "Phương thức thanh toán COD");
            if (!codMethod.isSelected()) {
                codMethod.click();
            }
            pause(1000);
            
            logStep("3.2.4", "Click nút đặt hàng");
            currentTestSteps.get().add("3.2.4 - Click nút đặt hàng");
            WebElement submitBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit'].btn-place-order, button.btn-place-order, button[type='submit']")
                )
            );
            scrollAndHighlight(submitBtn, "Nút đặt hàng");
            submitBtn.click();
            pause(3000);
            takeScreenshot(testName, "01_OrderPlaced");
            
            logStep("3.2.5", "Kiểm tra kết quả");
            currentTestSteps.get().add("3.2.5 - Kiểm tra kết quả");
            String currentUrl = driver.getCurrentUrl();
            System.out.println("  🌐 URL sau khi đặt hàng: " + currentUrl);
            
            if (currentUrl.contains("/order-detail") || currentUrl.contains("success")) {
                System.out.println("  ✅ Tạo đơn hàng thành công!");
            } else {
                System.out.println("  ⚠️  Redirect đến: " + currentUrl);
            }
            
            pause(2000);
            System.out.println("\n✅ TEST 3.2 hoàn thành!\n");
            
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println("\n❌ TEST 3.2 thất bại: " + errorMessage);
            e.printStackTrace();
        } finally {
            if (reportGenerator != null) {
                String status = errorMessage == null ? "PASS" : "FAIL";
                reportGenerator.addTestResult(testName, status,
                    "3.2: Thanh toán COD - Tạo đơn hàng",
                    currentTestSteps.get(), errorMessage, currentScreenshotPath.get());
            }
        }
    }
    
    @Test
    @Order(8)
    @DisplayName("3.3: Thanh toán VNPay - Redirect đến cổng thanh toán")
    void shouldCheckoutWithVNPay() {
        currentTestName.set("TEST_3_3_CheckoutVNPay");
        String testName = currentTestName.get();
        
        if (testUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 3.3: Thanh toán VNPay");
        System.out.println("=".repeat(60));
        
        String errorMessage = null;
        try {
            logStep("3.3.1", "Truy cập trang checkout");
            currentTestSteps.get().add("3.3.1 - Truy cập trang checkout");
            driver.get(BASE_URL + "/checkout");
            pause();
            takeScreenshot(testName, "01_CheckoutPage");
            
            logStep("3.3.2", "Chọn địa chỉ và phương thức vận chuyển");
            currentTestSteps.get().add("3.3.2 - Chọn địa chỉ và phương thức vận chuyển");
            try {
                WebElement addressSelect = driver.findElement(By.name("shipping_address_id"));
                List<WebElement> options = addressSelect.findElements(By.tagName("option"));
                if (options.isEmpty()) {
                    System.out.println("  ⚠️  Không có địa chỉ nào, cần thêm địa chỉ trước");
                    return;
                }
                org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(addressSelect);
                select.selectByIndex(0);
                
                List<WebElement> shippingMethods = driver.findElements(
                    By.cssSelector("input[type='radio'][name='shipping_method_id']")
                );
                if (!shippingMethods.isEmpty() && !shippingMethods.get(0).isSelected()) {
                    shippingMethods.get(0).click();
                }
                System.out.println("  ✅ Đã chọn địa chỉ và phương thức vận chuyển");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy địa chỉ/phương thức vận chuyển: " + e.getMessage());
            }
            
            logStep("3.3.3", "Chọn phương thức thanh toán VNPay");
            currentTestSteps.get().add("3.3.3 - Chọn phương thức thanh toán VNPay");
            List<WebElement> paymentMethods = driver.findElements(
                By.cssSelector("input[type='radio'][name='payment_method']")
            );
            
            WebElement vnpayMethod = null;
            for (WebElement method : paymentMethods) {
                String value = method.getAttribute("value");
                if (value != null && (value.equalsIgnoreCase("BANK") || value.equalsIgnoreCase("VNPAY"))) {
                    vnpayMethod = method;
                    break;
                }
            }
            
            if (vnpayMethod != null) {
                scrollAndHighlight(vnpayMethod, "Phương thức thanh toán VNPay");
                if (!vnpayMethod.isSelected()) {
                    vnpayMethod.click();
                }
                System.out.println("  ✅ Đã chọn VNPay");
                pause(1000);
            } else {
                System.out.println("  ⚠️  Không tìm thấy phương thức VNPay");
                return;
            }
            
            logStep("3.3.4", "Click nút đặt hàng");
            currentTestSteps.get().add("3.3.4 - Click nút đặt hàng");
            WebElement submitBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit'].btn-place-order, button.btn-place-order, button[type='submit']")
                )
            );
            scrollAndHighlight(submitBtn, "Nút đặt hàng");
            submitBtn.click();
            pause(5000); // Đợi redirect đến VNPay
            takeScreenshot(testName, "02_VNPayRedirect");
            
            logStep("3.3.5", "Kiểm tra redirect đến VNPay");
            currentTestSteps.get().add("3.3.5 - Kiểm tra redirect đến VNPay");
            String currentUrl = driver.getCurrentUrl();
            System.out.println("  🌐 URL sau khi submit: " + currentUrl);
            
            if (currentUrl.contains("/payment/vnpay") || currentUrl.contains("vnpay") || currentUrl.contains("sandbox.vnpayment")) {
                System.out.println("  ✅ Redirect đến trang VNPay thành công!");
                System.out.println("  ⚠️  Lưu ý: Không thực hiện thanh toán thật, chỉ test redirect");
            } else if (currentUrl.contains("/order-detail")) {
                System.out.println("  ⚠️  Redirect đến order-detail (có thể đã tạo đơn nhưng không redirect VNPay)");
            } else {
                System.out.println("  ⚠️  Redirect đến: " + currentUrl);
            }
            
            pause(2000);
            System.out.println("\n✅ TEST 3.3 hoàn thành!\n");
            
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println("\n❌ TEST 3.3 thất bại: " + errorMessage);
            e.printStackTrace();
        } finally {
            if (reportGenerator != null) {
                String status = errorMessage == null ? "PASS" : "FAIL";
                reportGenerator.addTestResult(testName, status,
                    "3.3: Thanh toán VNPay - Redirect đến cổng thanh toán",
                    currentTestSteps.get(), errorMessage, currentScreenshotPath.get());
            }
        }
    }
    
    // ========== PHẦN 4: FEEDBACK SẢN PHẨM ==========
    
    @Test
    @Order(9)
    @DisplayName("4.1: Xem đánh giá sản phẩm")
    void shouldViewProductReviews() {
        currentTestName.set("TEST_4_1_ViewReviews");
        String testName = currentTestName.get();
        
        if (testUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 4.1: Xem đánh giá sản phẩm");
        System.out.println("=".repeat(60));
        
        String errorMessage = null;
        try {
            logStep("4.1.1", "Truy cập trang chi tiết sản phẩm");
            currentTestSteps.get().add("4.1.1 - Truy cập trang chi tiết sản phẩm");
            Model.Product testProduct = TestDataHelper.getRandomProductInStock();
            if (testProduct == null) {
                System.out.println("  ⚠️  Không có sản phẩm trong kho");
                return;
            }
            
            driver.get(BASE_URL + "/product-detail?id=" + testProduct.getProductId());
            pause();
            
            logStep("4.1.2", "Kiểm tra đánh giá và bình luận");
            currentTestSteps.get().add("4.1.2 - Kiểm tra đánh giá và bình luận");
            try {
                List<WebElement> reviews = driver.findElements(
                    By.cssSelector(".review, .comment, [class*='review'], [class*='rating']")
                );
                System.out.println("  ⭐ Tìm thấy " + reviews.size() + " đánh giá");
                if (!reviews.isEmpty()) {
                    scrollAndHighlight(reviews.get(0), "Đánh giá đầu tiên");
                }
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy đánh giá");
            }
            
            takeScreenshot(testName, "01_ProductReviews");
            pause(2000);
            System.out.println("\n✅ TEST 4.1 hoàn thành!\n");
            
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println("\n❌ TEST 4.1 thất bại: " + errorMessage);
            e.printStackTrace();
        } finally {
            if (reportGenerator != null) {
                String status = errorMessage == null ? "PASS" : "FAIL";
                reportGenerator.addTestResult(testName, status,
                    "4.1: Xem đánh giá sản phẩm",
                    currentTestSteps.get(), errorMessage, currentScreenshotPath.get());
            }
        }
    }
    
    @Test
    @Order(10)
    @DisplayName("2.3: Cập nhật số lượng trong giỏ hàng")
    void shouldUpdateCartQuantity() {
        currentTestName.set("TEST_2_3_UpdateCartQuantity");
        String testName = currentTestName.get();
        
        if (testUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 2.3: Cập nhật số lượng trong giỏ hàng");
        System.out.println("=".repeat(60));
        
        String errorMessage = null;
        try {
            logStep("2.3.1", "Truy cập trang giỏ hàng");
            currentTestSteps.get().add("2.3.1 - Truy cập trang giỏ hàng");
            driver.get(BASE_URL + "/cart");
            pause();
            
            logStep("2.3.2", "Tìm và cập nhật số lượng sản phẩm");
            currentTestSteps.get().add("2.3.2 - Cập nhật số lượng");
            try {
                // Tìm input số lượng
                List<WebElement> quantityInputs = driver.findElements(
                    By.cssSelector("input[type='number'][name*='quantity'], input[name*='qty'], .quantity-input")
                );
                if (!quantityInputs.isEmpty()) {
                    WebElement qtyInput = quantityInputs.get(0);
                    scrollAndHighlight(qtyInput, "Ô số lượng");
                    qtyInput.clear();
                    qtyInput.sendKeys("2");
                    pause(1000);
                    
                    // Tìm nút cập nhật
                    try {
                        WebElement updateBtn = driver.findElement(
                            By.xpath("//button[contains(text(), 'Cập nhật')] | //button[contains(@onclick, 'update')]")
                        );
                        updateBtn.click();
                        pause(2000);
                        System.out.println("  ✅ Đã cập nhật số lượng");
                    } catch (Exception e) {
                        System.out.println("  ⚠️  Không tìm thấy nút cập nhật, có thể tự động cập nhật");
                    }
                } else {
                    System.out.println("  ⚠️  Không tìm thấy ô số lượng");
                }
            } catch (Exception e) {
                System.out.println("  ⚠️  Không thể cập nhật số lượng: " + e.getMessage());
            }
            
            takeScreenshot(testName, "01_UpdatedQuantity");
            pause(2000);
            System.out.println("\n✅ TEST 2.3 hoàn thành!\n");
            
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println("\n❌ TEST 2.3 thất bại: " + errorMessage);
            e.printStackTrace();
        } finally {
            if (reportGenerator != null) {
                String status = errorMessage == null ? "PASS" : "FAIL";
                reportGenerator.addTestResult(testName, status,
                    "2.3: Cập nhật số lượng trong giỏ hàng",
                    currentTestSteps.get(), errorMessage, currentScreenshotPath.get());
            }
        }
    }
    
    @Test
    @Order(12)
    @DisplayName("4.2: Thêm đánh giá sản phẩm")
    void shouldAddProductReview() {
        currentTestName.set("TEST_4_2_AddReview");
        String testName = currentTestName.get();
        
        if (testUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 4.2: Thêm đánh giá sản phẩm");
        System.out.println("=".repeat(60));
        
        String errorMessage = null;
        try {
            logStep("4.2.1", "Truy cập trang chi tiết sản phẩm đã mua");
            currentTestSteps.get().add("4.2.1 - Truy cập trang chi tiết sản phẩm");
            Model.Product testProduct = TestDataHelper.getRandomProductInStock();
            if (testProduct == null) {
                System.out.println("  ⚠️  Không có sản phẩm trong kho");
                return;
            }
            
            driver.get(BASE_URL + "/product-detail?id=" + testProduct.getProductId());
            pause();
            takeScreenshot(testName, "01_ProductDetail");
            
            logStep("4.2.2", "Tìm và click nút Đánh giá sản phẩm");
            currentTestSteps.get().add("4.2.2 - Click nút Đánh giá sản phẩm");
            try {
                WebElement reviewBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(), 'Đánh giá')] | //a[contains(text(), 'Đánh giá')] | //button[contains(@onclick, 'review')]")
                    )
                );
                scrollAndHighlight(reviewBtn, "Nút Đánh giá sản phẩm");
                reviewBtn.click();
                pause(2000);
                takeScreenshot(testName, "02_ReviewForm");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy nút đánh giá (có thể chưa đủ điều kiện)");
                errorMessage = "Không tìm thấy nút đánh giá: " + e.getMessage();
            }
            
            logStep("4.2.3", "Chọn số sao và viết bình luận");
            currentTestSteps.get().add("4.2.3 - Chọn số sao và viết bình luận");
            try {
                // Tìm và chọn 5 sao
                List<WebElement> stars = driver.findElements(
                    By.cssSelector(".star, .rating-star, input[type='radio'][name*='rating'], [class*='star']")
                );
                if (!stars.isEmpty()) {
                    WebElement fiveStar = stars.get(stars.size() - 1); // Sao cuối cùng (5 sao)
                    scrollAndHighlight(fiveStar, "Chọn 5 sao");
                    fiveStar.click();
                    pause(1000);
                    System.out.println("  ⭐ Đã chọn 5 sao");
                }
                
                // Tìm textarea để viết bình luận
                WebElement commentTextarea = driver.findElement(
                    By.cssSelector("textarea[name*='comment'], textarea[name*='review'], textarea[placeholder*='bình luận']")
                );
                scrollAndHighlight(commentTextarea, "Textarea bình luận");
                commentTextarea.clear();
                commentTextarea.sendKeys("Sản phẩm rất tốt, chất lượng cao, giao hàng nhanh!");
                pause(1000);
                System.out.println("  📝 Đã viết bình luận");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy form đánh giá: " + e.getMessage());
            }
            
            logStep("4.2.4", "Submit đánh giá");
            currentTestSteps.get().add("4.2.4 - Submit đánh giá");
            try {
                WebElement submitBtn = driver.findElement(
                    By.xpath("//button[contains(text(), 'Gửi')] | //button[contains(text(), 'Submit')] | //button[@type='submit']")
                );
                scrollAndHighlight(submitBtn, "Nút Submit đánh giá");
                submitBtn.click();
                pause(3000);
                takeScreenshot(testName, "03_ReviewSubmitted");
                System.out.println("  ✅ Đã submit đánh giá");
            } catch (Exception e) {
                System.out.println("  ⚠️  Không tìm thấy nút submit: " + e.getMessage());
            }
            
            pause(2000);
            System.out.println("\n✅ TEST 4.2 hoàn thành!\n");
            
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println("\n❌ TEST 4.2 thất bại: " + errorMessage);
            e.printStackTrace();
        } finally {
            if (reportGenerator != null) {
                String status = errorMessage == null ? "PASS" : "FAIL";
                reportGenerator.addTestResult(testName, status,
                    "4.2: Thêm đánh giá sản phẩm",
                    currentTestSteps.get(), errorMessage, currentScreenshotPath.get());
            }
        }
    }
    
    @Test
    @Order(11)
    @DisplayName("2.4: Xóa sản phẩm khỏi giỏ hàng")
    void shouldRemoveProductFromCart() {
        currentTestName.set("TEST_2_4_RemoveFromCart");
        String testName = currentTestName.get();
        
        if (testUser == null) return;
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 2.4: Xóa sản phẩm khỏi giỏ hàng");
        System.out.println("=".repeat(60));
        
        String errorMessage = null;
        try {
            logStep("2.4.1", "Truy cập trang giỏ hàng");
            currentTestSteps.get().add("2.4.1 - Truy cập trang giỏ hàng");
            driver.get(BASE_URL + "/cart");
            pause();
            
            logStep("2.4.2", "Tìm và click nút xóa sản phẩm");
            currentTestSteps.get().add("2.4.2 - Xóa sản phẩm");
            try {
                // Tìm nút xóa
                List<WebElement> removeButtons = driver.findElements(
                    By.xpath("//a[contains(@href, 'removeFromCart')] | //button[contains(@onclick, 'remove')] | //i[contains(@class, 'trash')] | //button[contains(text(), 'Xóa')]")
                );
                if (!removeButtons.isEmpty()) {
                    scrollAndHighlight(removeButtons.get(0), "Nút xóa sản phẩm");
                    removeButtons.get(0).click();
                    pause(2000);
                    System.out.println("  ✅ Đã click nút xóa sản phẩm");
                } else {
                    System.out.println("  ⚠️  Không tìm thấy nút xóa");
                }
            } catch (Exception e) {
                System.out.println("  ⚠️  Không thể xóa sản phẩm: " + e.getMessage());
            }
            
            takeScreenshot(testName, "01_AfterRemove");
            pause(2000);
            System.out.println("\n✅ TEST 2.4 hoàn thành!\n");
            
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println("\n❌ TEST 2.4 thất bại: " + errorMessage);
            e.printStackTrace();
        } finally {
            if (reportGenerator != null) {
                String status = errorMessage == null ? "PASS" : "FAIL";
                reportGenerator.addTestResult(testName, status,
                    "2.4: Xóa sản phẩm khỏi giỏ hàng",
                    currentTestSteps.get(), errorMessage, currentScreenshotPath.get());
            }
        }
    }
    
    // ========== HELPER METHODS ==========
    
    private static void scrollAndHighlight(WebElement element, String stepName) {
        try {
            System.out.println("  👁️  Đang xem: " + stepName);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            pause(500);
            highlightElement(element, "#ff0000");
        } catch (Exception e) {
            System.out.println("  ⚠️  Không thể scroll/highlight: " + e.getMessage());
        }
    }
    
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
    
    private static void takeScreenshot(String testName, String stepName) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File screenshotFile = screenshot.getScreenshotAs(OutputType.FILE);
            String fileName = String.format("%s/%s_%s_%d.png", 
                SCREENSHOT_DIR, 
                testName, 
                stepName.replaceAll("[^a-zA-Z0-9]", "_"),
                System.currentTimeMillis());
            File destFile = new File(fileName);
            screenshotFile.renameTo(destFile);
            System.out.println("  📸 Screenshot: " + destFile.getAbsolutePath());
            
            if (currentTestName.get() != null && currentTestName.get().equals(testName)) {
                currentScreenshotPath.set(destFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("  ⚠️  Không thể chụp screenshot: " + e.getMessage());
        }
    }
    
    private static void logStep(String stepNumber, String description) {
        System.out.println("\n  ┌─ BƯỚC " + stepNumber + ": " + description);
        System.out.println("  └─────────────────────────────────────────────");
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

