package DAO;

import E2E.TestDataHelper;
import Model.Cart;
import Model.CartItems;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests cho CartDB
 * 
 * Note: Các test này yêu cầu database connection thực tế.
 * Sử dụng TestDataHelper để lấy data động từ database.
 */
@DisplayName("CartDB DAO Tests")
class CartDBTest {

    private CartDB cartDB;

    @BeforeEach
    void setUp() {
        cartDB = new CartDB();
    }

    @Test
    @DisplayName("Test getCartByUserId với user ID hợp lệ - lấy từ database")
    void testGetCartByUserId_ValidUserId() {
        // Given - Lấy user từ database
        Model.user randomUser = TestDataHelper.getRandomUser();
        Assumptions.assumeTrue(randomUser != null, "Database phải có ít nhất 1 user để test");
        int userId = randomUser.getUser_id();

        // When
        Cart cart = cartDB.getCartByUserId(userId);

        // Then
        // Cart có thể null nếu user chưa có cart
        if (cart != null) {
            assertThat(cart.getUser_id()).isEqualTo(userId);
            assertThat(cart.getCart_id()).isGreaterThan(0);
            System.out.println("[CartDBTest] Tìm thấy cart ID " + cart.getCart_id() + " cho user ID " + userId);
        } else {
            System.out.println("[CartDBTest] User ID " + userId + " chưa có cart");
        }
    }

    @Test
    @DisplayName("Test getCartByUserId với user ID không tồn tại")
    void testGetCartByUserId_InvalidUserId() {
        // Given - ID không tồn tại
        int invalidUserId = 99999;

        // When
        Cart cart = cartDB.getCartByUserId(invalidUserId);

        // Then
        assertThat(cart).isNull();
    }

    @Test
    @DisplayName("Test getCartItemsByCartId - lấy items từ cart")
    void testGetCartItemsByCartId() {
        // Given - Lấy user có cart
        Model.user randomUser = TestDataHelper.getRandomUser();
        Assumptions.assumeTrue(randomUser != null, "Database phải có ít nhất 1 user để test");
        
        Cart cart = cartDB.getCartByUserId(randomUser.getUser_id());
        Assumptions.assumeTrue(cart != null, "User phải có cart để test");

        // When
        List<CartItems> items = cartDB.getCartItemsByCartId(cart.getCart_id());

        // Then
        assertThat(items).isNotNull();
        System.out.println("[CartDBTest] Tìm thấy " + items.size() + " items trong cart ID " + cart.getCart_id());
        
        // Nếu có items, kiểm tra structure
        if (!items.isEmpty()) {
            CartItems firstItem = items.get(0);
            assertThat(firstItem.getId()).isGreaterThan(0);
            assertThat(firstItem.getProduct_id()).isGreaterThan(0);
            assertThat(firstItem.getQuantity()).isGreaterThan(0);
        }
    }

    @Test
    @DisplayName("Test getCartItemsByCartId với cart ID không tồn tại")
    void testGetCartItemsByCartId_InvalidCartId() {
        // Given
        int invalidCartId = 99999;

        // When
        List<CartItems> items = cartDB.getCartItemsByCartId(invalidCartId);

        // Then
        assertThat(items).isNotNull();
        assertThat(items).isEmpty();
    }

    @Test
    @DisplayName("Test calculateSelectedTotal - tính tổng giá trị items đã chọn")
    void testCalculateSelectedTotal() {
        // Given - Lấy user có cart
        Model.user randomUser = TestDataHelper.getRandomUser();
        Assumptions.assumeTrue(randomUser != null, "Database phải có ít nhất 1 user để test");
        
        Cart cart = cartDB.getCartByUserId(randomUser.getUser_id());
        Assumptions.assumeTrue(cart != null, "User phải có cart để test");

        // When
        double total = cartDB.calculateSelectedTotal(cart.getCart_id());

        // Then
        assertThat(total).isGreaterThanOrEqualTo(0);
        System.out.println("[CartDBTest] Tổng giá trị cart ID " + cart.getCart_id() + ": " + total);
    }
}

