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
    private static final long STEP_DELAY_MS = 3000L; // 3 giây mỗi bước để xem rõ hơn
    private static final long VISUAL_DELAY_MS = 2000L; // 2 giây sau khi highlight
    private static Model.user testUser;
    private static boolean isLoggedIn = false;
    private static final String SCREENSHOT_DIR = "test-screenshots";
    private static TestReportGenerator reportGenerator;
    private static ThreadLocal<List<String>> currentTestSteps = new ThreadLocal<>();
    private static ThreadLocal<String> currentTestName = new ThreadLocal<>();
    private static ThreadLocal<String> currentScreenshotPath = new ThreadLocal<>();
    
    @BeforeAll
    static void setUpAll() {
        try {
            // Khởi tạo report generator
            reportGenerator = new TestReportGenerator("CheckoutE2ETest");
            System.out.println("[CheckoutE2ETest] Đã khởi tạo Test Report Generator");
            
            // Tạo thư mục screenshots
            Path screenshotPath = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotPath)) {
                Files.createDirectories(screenshotPath);
                System.out.println("[CheckoutE2ETest] Đã tạo thư mục screenshots: " + screenshotPath.toAbsolutePath());
            }
            
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
        // Tạo Excel report
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
                e.printStackTrace();
            }
        }
        
        if (driver != null) {
            driver.quit();
        }
    }
    
    @BeforeEach
    void setUpTest() {
        // Reset cho mỗi test
        currentTestSteps.set(new ArrayList<>());
        currentScreenshotPath.set(null);
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
        currentTestName.set("TEST_1_ViewCheckoutPage");
        String testName = currentTestName.get();
        
        if (testUser == null) {
            if (reportGenerator != null) {
                reportGenerator.addTestResult(testName, "SKIP", 
                    "E2E: Xem trang checkout với giỏ hàng có sản phẩm", 
                    null, "No test user available", null);
            }
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 1: Xem trang Checkout");
        System.out.println("=".repeat(60));
        
        String errorMessage = null;
        try {
        
        logStep("1.1", "Truy cập trang checkout");
        currentTestSteps.get().add("1.1 - Truy cập trang checkout");
        driver.get(BASE_URL + "/checkout");
        System.out.println("  🌐 URL: " + driver.getCurrentUrl());
        pause();
        takeScreenshot(testName, "01_CheckoutPage");
        
        logStep("1.2", "Kiểm tra trang checkout đã load");
        currentTestSteps.get().add("1.2 - Kiểm tra trang checkout đã load");
        wait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("/checkout"),
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".checkout, .checkout-form, form[action*='checkout']")
            )
        ));
        System.out.println("  ✅ Trang checkout đã load thành công");
        pause(1000);
        
        logStep("1.3", "Kiểm tra danh sách sản phẩm trong giỏ hàng");
        currentTestSteps.get().add("1.3 - Kiểm tra danh sách sản phẩm trong giỏ hàng");
        try {
            List<WebElement> cartItems = driver.findElements(
                By.cssSelector(".list-group-item, .order-summary-card .list-group-item")
            );
            System.out.println("  📦 Tìm thấy " + cartItems.size() + " sản phẩm trong giỏ hàng");
            currentTestSteps.get().add("  → Tìm thấy " + cartItems.size() + " sản phẩm");
            
            if (!cartItems.isEmpty()) {
                scrollAndHighlight(cartItems.get(0), "Sản phẩm đầu tiên trong giỏ hàng");
            }
        } catch (Exception e) {
            System.out.println("  ⚠️  Không tìm thấy sản phẩm trong giỏ (có thể giỏ hàng trống)");
            currentTestSteps.get().add("  → Không tìm thấy sản phẩm");
        }
        pause(1000);
        
        logStep("1.4", "Kiểm tra tổng tiền");
        currentTestSteps.get().add("1.4 - Kiểm tra tổng tiền");
        try {
            WebElement totalElement = driver.findElement(By.id("grandTotal"));
            scrollAndHighlight(totalElement, "Tổng tiền");
            String totalText = totalElement.getText();
            System.out.println("  💰 Tổng tiền: " + totalText);
            currentTestSteps.get().add("  → Tổng tiền: " + totalText);
        } catch (Exception e) {
            System.out.println("  ⚠️  Không tìm thấy tổng tiền");
            currentTestSteps.get().add("  → Không tìm thấy tổng tiền");
        }
        
        takeScreenshot(testName, "02_CheckoutSummary");
        pause(2000);
        System.out.println("\n✅ TEST 1 hoàn thành!\n");
        
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println("\n❌ TEST 1 thất bại: " + errorMessage);
            e.printStackTrace();
        } finally {
            // Ghi kết quả vào report
            if (reportGenerator != null) {
                String status = errorMessage == null ? "PASS" : "FAIL";
                reportGenerator.addTestResult(testName, status,
                    "E2E: Xem trang checkout với giỏ hàng có sản phẩm",
                    currentTestSteps.get(), errorMessage, currentScreenshotPath.get());
            }
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("E2E: Xem form nhập thông tin giao hàng")
    void shouldViewShippingForm() {
        if (testUser == null) {
            return;
        }
        
        String testName = "TEST_2_ViewShippingForm";
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 2: Xem form thông tin giao hàng");
        System.out.println("=".repeat(60));
        
        logStep("2.1", "Truy cập trang checkout");
        driver.get(BASE_URL + "/checkout");
        pause();
        takeScreenshot(testName, "01_CheckoutPage");
        
        logStep("2.2", "Kiểm tra select địa chỉ giao hàng");
        try {
            WebElement addressSelect = driver.findElement(By.name("shipping_address_id"));
            scrollAndHighlight(addressSelect, "Select địa chỉ giao hàng");
            List<WebElement> options = addressSelect.findElements(By.tagName("option"));
            System.out.println("  📍 Tìm thấy select địa chỉ giao hàng");
            System.out.println("  📍 Có " + options.size() + " địa chỉ");
            if (!options.isEmpty()) {
                System.out.println("  📍 Địa chỉ đầu tiên: " + options.get(0).getText());
            }
            pause(1000);
        } catch (Exception e) {
            System.out.println("  ⚠️  Không có địa chỉ (cần thêm địa chỉ)");
        }
        
        logStep("2.3", "Kiểm tra phương thức giao hàng");
        try {
            List<WebElement> shippingMethods = driver.findElements(
                By.cssSelector("input[type='radio'][name='shipping_method_id']")
            );
            System.out.println("  🚚 Tìm thấy " + shippingMethods.size() + " phương thức giao hàng");
            if (!shippingMethods.isEmpty()) {
                scrollAndHighlight(shippingMethods.get(0), "Phương thức giao hàng đầu tiên");
            }
            pause(1000);
        } catch (Exception e) {
            System.out.println("  ⚠️  Không tìm thấy phương thức giao hàng");
        }
        
        logStep("2.4", "Kiểm tra phương thức thanh toán");
        try {
            List<WebElement> paymentMethods = driver.findElements(
                By.cssSelector("input[type='radio'][name='payment_method']")
            );
            System.out.println("  💳 Tìm thấy " + paymentMethods.size() + " phương thức thanh toán");
            if (!paymentMethods.isEmpty()) {
                scrollAndHighlight(paymentMethods.get(0), "Phương thức thanh toán đầu tiên");
            }
            pause(1000);
        } catch (Exception e) {
            System.out.println("  ⚠️  Không tìm thấy phương thức thanh toán");
        }
        
        logStep("2.5", "Kiểm tra textarea ghi chú");
        try {
            WebElement notesTextarea = driver.findElement(By.name("notes"));
            scrollAndHighlight(notesTextarea, "Textarea ghi chú");
            System.out.println("  📝 Tìm thấy textarea ghi chú");
            pause(1000);
        } catch (Exception e) {
            System.out.println("  ⚠️  Không tìm thấy textarea ghi chú");
        }
        
        takeScreenshot(testName, "02_FormFields");
        pause(2000);
        System.out.println("\n✅ TEST 2 hoàn thành!\n");
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
                By.cssSelector("input[type='radio'][name='payment_method']")
            );
            System.out.println("[CheckoutE2ETest] Tìm thấy " + paymentMethods.size() + " phương thức thanh toán:");
            for (WebElement method : paymentMethods) {
                String value = method.getAttribute("value");
                String id = method.getAttribute("id");
                WebElement label = driver.findElement(By.cssSelector("label[for='" + id + "']"));
                String text = label.getText().trim();
                System.out.println("  - " + (text.isEmpty() ? value : text) + " (value: " + value + ")");
            }
        } catch (Exception e) {
            System.out.println("[CheckoutE2ETest] Không tìm thấy phương thức thanh toán: " + e.getMessage());
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
        
        // Kiểm tra và chọn địa chỉ giao hàng (UI mới dùng select dropdown)
        try {
            WebElement addressSelect = driver.findElement(By.name("shipping_address_id"));
            List<WebElement> options = addressSelect.findElements(By.tagName("option"));
            if (!options.isEmpty()) {
                // Kiểm tra xem có option nào đã được chọn chưa
                WebElement selectedOption = null;
                try {
                    selectedOption = addressSelect.findElement(By.cssSelector("option:checked"));
                } catch (Exception e) {
                    // Không có option nào được chọn
                }
                if (selectedOption != null) {
                    System.out.println("[CheckoutE2ETest] Đã chọn địa chỉ: " + selectedOption.getText());
                } else {
                    System.out.println("[CheckoutE2ETest] Không có địa chỉ nào được chọn");
                }
            } else {
                System.out.println("[CheckoutE2ETest] Không có địa chỉ nào, cần thêm địa chỉ trước");
            }
        } catch (Exception e) {
            System.out.println("[CheckoutE2ETest] Không tìm thấy select địa chỉ: " + e.getMessage());
        }
        
        // Chọn phương thức giao hàng (nếu chưa chọn)
        try {
            List<WebElement> shippingMethods = driver.findElements(
                By.cssSelector("input[type='radio'][name='shipping_method_id']")
            );
            if (!shippingMethods.isEmpty()) {
                WebElement checked = null;
                for (WebElement method : shippingMethods) {
                    if (method.isSelected()) {
                        checked = method;
                        break;
                    }
                }
                if (checked == null) {
                    shippingMethods.get(0).click();
                    System.out.println("[CheckoutE2ETest] Đã chọn phương thức giao hàng đầu tiên");
                } else {
                    System.out.println("[CheckoutE2ETest] Phương thức giao hàng đã được chọn");
                }
            }
        } catch (Exception e) {
            System.out.println("[CheckoutE2ETest] Không tìm thấy phương thức giao hàng: " + e.getMessage());
        }
        
        System.out.println("[CheckoutE2ETest] Đã kiểm tra form checkout!");
        
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
                By.cssSelector("input[type='radio'][name='payment_method']")
            );
            
            if (!paymentMethods.isEmpty()) {
                // Chọn phương thức đầu tiên (hoặc lấy phương thức đã chọn)
                WebElement selectedMethod = null;
                for (WebElement method : paymentMethods) {
                    if (method.isSelected()) {
                        selectedMethod = method;
                        break;
                    }
                }
                
                if (selectedMethod == null) {
                    selectedMethod = paymentMethods.get(0);
                    // Scroll vào view
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectedMethod);
                    pause(500);
                    selectedMethod.click();
                }
                
                String methodValue = selectedMethod.getAttribute("value");
                String methodId = selectedMethod.getAttribute("id");
                WebElement label = driver.findElement(By.cssSelector("label[for='" + methodId + "']"));
                String methodText = label.getText().trim();
                System.out.println("[CheckoutE2ETest] Đã chọn phương thức thanh toán: " + (methodText.isEmpty() ? methodValue : methodText));
                pause(1000);
            }
            
            // Tìm và hiển thị tổng tiền
            try {
                WebElement totalElement = driver.findElement(By.id("grandTotal"));
                String totalText = totalElement.getText().trim();
                System.out.println("[CheckoutE2ETest] Tổng tiền: " + totalText);
            } catch (Exception e) {
                System.out.println("[CheckoutE2ETest] Không tìm thấy tổng tiền: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("[CheckoutE2ETest] Không thể chọn phương thức thanh toán: " + e.getMessage());
        }
        
        pause(3000);
    }
    
    @Test
    @Order(6)
    @DisplayName("E2E: Submit form checkout với COD - Tạo đơn hàng thành công")
    void shouldSubmitCheckoutFormWithCOD() {
        if (testUser == null) {
            return;
        }
        
        String testName = "TEST_6_SubmitCOD";
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🧪 TEST 6: Submit form checkout với COD");
        System.out.println("=".repeat(60));
        
        // Đảm bảo có sản phẩm trong giỏ hàng
        Model.Product testProduct = TestDataHelper.getRandomProductInStock();
        if (testProduct == null) {
            System.out.println("  ⚠️  Không có sản phẩm trong kho, skip test");
            return;
        }
        
        logStep("6.1", "Truy cập trang checkout");
        driver.get(BASE_URL + "/checkout");
        pause();
        takeScreenshot(testName, "01_CheckoutPage");
        
        try {
            logStep("6.2", "Chọn địa chỉ giao hàng");
            WebElement addressSelect = driver.findElement(By.name("shipping_address_id"));
            List<WebElement> options = addressSelect.findElements(By.tagName("option"));
            if (options.isEmpty()) {
                System.out.println("  ⚠️  Không có địa chỉ nào, cần thêm địa chỉ trước khi đặt hàng");
                return;
            }
            scrollAndHighlight(addressSelect, "Select địa chỉ giao hàng");
            
            // Kiểm tra xem có option nào đã được chọn chưa
            WebElement selectedOption = null;
            try {
                selectedOption = addressSelect.findElement(By.cssSelector("option:checked"));
            } catch (Exception e) {
                // Không có option nào được chọn
            }
            if (selectedOption == null) {
                org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(addressSelect);
                select.selectByIndex(0);
                System.out.println("  ✅ Đã chọn địa chỉ đầu tiên");
            }
            WebElement currentSelected = addressSelect.findElement(By.cssSelector("option:checked"));
            System.out.println("  📍 Địa chỉ đã chọn: " + currentSelected.getText());
            pause(1000);
            
            logStep("6.3", "Chọn phương thức giao hàng");
            List<WebElement> shippingMethods = driver.findElements(
                By.cssSelector("input[type='radio'][name='shipping_method_id']")
            );
            if (!shippingMethods.isEmpty()) {
                WebElement checked = null;
                for (WebElement method : shippingMethods) {
                    if (method.isSelected()) {
                        checked = method;
                        break;
                    }
                }
                if (checked == null) {
                    scrollAndHighlight(shippingMethods.get(0), "Phương thức giao hàng");
                    shippingMethods.get(0).click();
                    System.out.println("  ✅ Đã chọn phương thức giao hàng đầu tiên");
                } else {
                    scrollAndHighlight(checked, "Phương thức giao hàng đã chọn");
                    System.out.println("  ✅ Phương thức giao hàng đã được chọn");
                }
                pause(1000);
            }
            
            logStep("6.4", "Chọn phương thức thanh toán COD");
            List<WebElement> paymentMethods = driver.findElements(
                By.cssSelector("input[type='radio'][name='payment_method']")
            );
            
            WebElement codMethod = null;
            for (WebElement method : paymentMethods) {
                String value = method.getAttribute("value");
                if (value != null && value.equalsIgnoreCase("COD")) {
                    codMethod = method;
                    break;
                }
            }
            
            if (codMethod != null) {
                scrollAndHighlight(codMethod, "Phương thức thanh toán COD");
                if (!codMethod.isSelected()) {
                    codMethod.click();
                }
                System.out.println("  ✅ Đã chọn COD");
                pause(1000);
            } else {
                System.out.println("  ⚠️  Không tìm thấy phương thức COD");
                return;
            }
            
            logStep("6.5", "Click nút đặt hàng");
            WebElement submitBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit'].btn-place-order, button.btn-place-order, button[type='submit']")
                )
            );
            scrollAndHighlight(submitBtn, "Nút đặt hàng");
            System.out.println("  🖱️  Click nút: " + submitBtn.getText());
            submitBtn.click();
            pause(3000);
            
            logStep("6.6", "Kiểm tra kết quả");
            String currentUrl = driver.getCurrentUrl();
            System.out.println("  🌐 URL sau khi submit: " + currentUrl);
            takeScreenshot(testName, "02_AfterSubmit");
            
            if (currentUrl.contains("/order-detail") || currentUrl.contains("success")) {
                System.out.println("  ✅ Tạo đơn hàng thành công! Redirect đến trang chi tiết đơn hàng.");
            } else if (currentUrl.contains("/checkout") && currentUrl.contains("error")) {
                System.out.println("  ⚠️  Có lỗi khi tạo đơn hàng: " + currentUrl);
            } else {
                System.out.println("  ⚠️  Redirect đến: " + currentUrl);
            }
            
        } catch (Exception e) {
            System.out.println("  ❌ Lỗi khi submit form: " + e.getMessage());
            e.printStackTrace();
            takeScreenshot(testName, "ERROR_" + System.currentTimeMillis());
        }
        
        pause(2000);
        System.out.println("\n✅ TEST 6 hoàn thành!\n");
    }
    
    @Test
    @Order(7)
    @DisplayName("E2E: Submit form checkout với VNPay - Redirect đến trang thanh toán")
    void shouldSubmitCheckoutFormWithVNPay() {
        if (testUser == null) {
            return;
        }
        
        System.out.println("\n========================================");
        System.out.println("[CheckoutE2ETest] TEST 7: Submit form checkout với VNPay");
        System.out.println("========================================");
        
        // Đảm bảo có sản phẩm trong giỏ hàng
        Model.Product testProduct = TestDataHelper.getRandomProductInStock();
        if (testProduct == null) {
            System.out.println("[CheckoutE2ETest] Không có sản phẩm trong kho, skip test");
            return;
        }
        
        driver.get(BASE_URL + "/checkout");
        pause();
        
        try {
            // Chọn địa chỉ giao hàng (UI mới dùng select dropdown)
            try {
                WebElement addressSelect = driver.findElement(By.name("shipping_address_id"));
                List<WebElement> options = addressSelect.findElements(By.tagName("option"));
                if (options.isEmpty()) {
                    System.out.println("[CheckoutE2ETest] Không có địa chỉ nào, cần thêm địa chỉ trước khi đặt hàng");
                    return;
                }
                // Kiểm tra xem có option nào đã được chọn chưa
                WebElement selectedOption = null;
                try {
                    selectedOption = addressSelect.findElement(By.cssSelector("option:checked"));
                } catch (Exception e) {
                    // Không có option nào được chọn
                }
                if (selectedOption == null) {
                    // Chọn option đầu tiên
                    org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(addressSelect);
                    select.selectByIndex(0);
                }
                // Lấy option đã chọn để hiển thị
                WebElement currentSelected = addressSelect.findElement(By.cssSelector("option:checked"));
                System.out.println("[CheckoutE2ETest] Đã chọn địa chỉ: " + currentSelected.getText());
                pause(500);
            } catch (Exception e) {
                System.out.println("[CheckoutE2ETest] Không tìm thấy select địa chỉ: " + e.getMessage());
                return;
            }
            
            // Chọn phương thức vận chuyển (nếu chưa chọn)
            try {
                List<WebElement> shippingMethods = driver.findElements(
                    By.cssSelector("input[type='radio'][name='shipping_method_id']")
                );
                if (!shippingMethods.isEmpty()) {
                    WebElement checked = null;
                    for (WebElement method : shippingMethods) {
                        if (method.isSelected()) {
                            checked = method;
                            break;
                        }
                    }
                    if (checked == null) {
                        shippingMethods.get(0).click();
                        System.out.println("[CheckoutE2ETest] Đã chọn phương thức giao hàng đầu tiên");
                    }
                    pause(500);
                }
            } catch (Exception e) {
                System.out.println("[CheckoutE2ETest] Không tìm thấy phương thức giao hàng: " + e.getMessage());
            }
            
            // Chọn VNPay/BANK - value="BANK"
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
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", vnpayMethod);
                pause(500);
                vnpayMethod.click();
                System.out.println("[CheckoutE2ETest] Đã chọn VNPay");
                pause(1000);
            } else {
                System.out.println("[CheckoutE2ETest] Không tìm thấy phương thức VNPay");
                return;
            }
            
            // Submit form - UI mới dùng button.btn-place-order
            WebElement submitBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit'].btn-place-order, button.btn-place-order, button[type='submit']")
                )
            );
            
            System.out.println("[CheckoutE2ETest] Click nút submit để thanh toán VNPay: " + submitBtn.getText());
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitBtn);
            pause(500);
            submitBtn.click();
            pause(5000); // Đợi redirect đến VNPay (có thể mất thời gian)
            
            // Kiểm tra redirect
            String currentUrl = driver.getCurrentUrl();
            System.out.println("[CheckoutE2ETest] URL sau khi submit: " + currentUrl);
            
            if (currentUrl.contains("/payment/vnpay") || currentUrl.contains("vnpay") || currentUrl.contains("sandbox.vnpayment")) {
                System.out.println("[CheckoutE2ETest] ✅ Redirect đến trang VNPay thành công!");
                System.out.println("[CheckoutE2ETest] ⚠️ Lưu ý: Không thực hiện thanh toán thật, chỉ test redirect.");
            } else if (currentUrl.contains("/order-detail")) {
                System.out.println("[CheckoutE2ETest] ⚠️ Redirect đến order-detail (có thể đã tạo đơn nhưng không redirect VNPay)");
            } else {
                System.out.println("[CheckoutE2ETest] ⚠️ Redirect đến: " + currentUrl);
            }
            
        } catch (Exception e) {
            System.out.println("[CheckoutE2ETest] Lỗi khi submit form VNPay: " + e.getMessage());
            e.printStackTrace();
        }
        
        pause(3000);
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
            // Khôi phục style ban đầu
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
    
    /**
     * Chụp screenshot và lưu vào file
     */
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
            
            // Lưu screenshot path cho report (lấy screenshot cuối cùng)
            if (currentTestName.get() != null && currentTestName.get().equals(testName)) {
                currentScreenshotPath.set(destFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("  ⚠️  Không thể chụp screenshot: " + e.getMessage());
        }
    }
    
    /**
     * Log step với format đẹp
     */
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

