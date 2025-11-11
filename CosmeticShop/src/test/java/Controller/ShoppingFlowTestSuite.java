package Controller;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite để chạy các flow tests theo trình tự nghiệp vụ
 * 
 * Flow chính (chạy theo thứ tự trong @SelectClasses):
 * 1. Login Flow - Đăng nhập
 * 2. Add To Cart Flow - Thêm sản phẩm vào giỏ hàng
 * 3. Cart Flow - Xem và quản lý giỏ hàng
 * 4. Checkout Flow - Thanh toán và tạo đơn hàng
 * 
 * Cách sử dụng:
 * mvn test -Dtest=ShoppingFlowTestSuite
 */
@Suite
@SuiteDisplayName("Shopping Flow Test Suite - End to End Flow")
@SelectClasses({
    LoginFlowTest.class,        // Order 1
    AddToCartFlowTest.class,    // Order 2
    CartFlowTest.class,         // Order 3
    CheckoutFlowTest.class      // Order 4
})
public class ShoppingFlowTestSuite {
    // Test suite class - không cần test methods
    // Các test classes sẽ được chạy theo thứ tự trong @SelectClasses
    // Mỗi test class đã có @Order annotation để đảm bảo thứ tự
}

