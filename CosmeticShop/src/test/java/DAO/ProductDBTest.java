package DAO;

import Model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class cho ProductDB
 * 
 * Note: Các test này yêu cầu database connection thực tế.
 * Để chạy integration tests, cần cấu hình test database.
 * Có thể sử dụng Testcontainers hoặc in-memory database cho unit tests.
 */
@DisplayName("ProductDB DAO Tests")
class ProductDBTest {

    private ProductDB productDB;

    @BeforeEach
    void setUp() {
        // Lưu ý: Cần cấu hình test database hoặc mock connection
        // productDB = new ProductDB();
    }

    @Test
    @DisplayName("Test getAllProducts - requires database")
    @Disabled("Requires database connection - enable for integration tests")
    void testGetAllProducts() {
        // Given
        // When
        List<Product> products = productDB.getAllProducts();

        // Then
        assertThat(products).isNotNull();
        // Kiểm tra nếu có dữ liệu trong database
        // assertThat(products).isNotEmpty();
    }

    @Test
    @DisplayName("Test getProductById với ID hợp lệ")
    @Disabled("Requires database connection - enable for integration tests")
    void testGetProductById_ValidId() {
        // Given
        int productId = 1;

        // When
        Product product = productDB.getProductById(productId);

        // Then
        // Nếu có dữ liệu trong database
        // assertNotNull(product);
        // assertThat(product.getProductId()).isEqualTo(productId);
    }

    @Test
    @DisplayName("Test getProductById với ID không tồn tại")
    @Disabled("Requires database connection - enable for integration tests")
    void testGetProductById_InvalidId() {
        // Given
        int invalidId = 99999;

        // When
        Product product = productDB.getProductById(invalidId);

        // Then
        assertNull(product);
    }

    @Test
    @DisplayName("Test searchProducts")
    @Disabled("Requires database connection - enable for integration tests")
    void testSearchProducts() {
        // Given
        String searchTerm = "kem";

        // When
        // List<Product> products = productDB.searchProducts(searchTerm);

        // Then
        // assertThat(products).isNotNull();
        // Kiểm tra kết quả tìm kiếm
    }

    // TODO: Thêm các test cases khác:
    // - testAddProduct
    // - testUpdateProduct
    // - testDeleteProduct
    // - testGetProductsByCategory
    // - testUpdateStock
}



