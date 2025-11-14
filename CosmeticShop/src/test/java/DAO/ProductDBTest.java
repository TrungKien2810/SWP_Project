package DAO;

import E2E.TestDataHelper;
import Model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Assumptions;

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
 * Sử dụng TestDataHelper để lấy data động từ database thay vì hardcode.
 */
@DisplayName("ProductDB DAO Tests")
class ProductDBTest {

    private ProductDB productDB;

    @BeforeEach
    void setUp() {
        productDB = new ProductDB();
    }

    @Test
    @DisplayName("Test getAllProducts - lấy tất cả products từ database")
    void testGetAllProducts() {
        // Given
        // When
        List<Product> products = productDB.getAllProducts();

        // Then
        assertThat(products).isNotNull();
        // Kiểm tra nếu có dữ liệu trong database
        if (!products.isEmpty()) {
            assertThat(products).isNotEmpty();
            System.out.println("[ProductDBTest] Tìm thấy " + products.size() + " products trong database");
        } else {
            System.out.println("[ProductDBTest] Database chưa có products nào");
        }
    }

    @Test
    @DisplayName("Test getProductById với ID hợp lệ - lấy từ database")
    void testGetProductById_ValidId() {
        // Given - Lấy product ID động từ database
        Integer productId = TestDataHelper.getRandomProductId();
        Assumptions.assumeTrue(productId != null, "Database phải có ít nhất 1 product để test");

        // When
        Product product = productDB.getProductById(productId);

        // Then
        assertNotNull(product, "Product với ID " + productId + " phải tồn tại trong database");
        assertThat(product.getProductId()).isEqualTo(productId);
        System.out.println("[ProductDBTest] Tìm thấy product: " + product.getName() + " (ID: " + productId + ")");
    }

    @Test
    @DisplayName("Test getProductById với ID không tồn tại")
    void testGetProductById_InvalidId() {
        // Given - ID không tồn tại
        int invalidId = 99999;

        // When
        Product product = productDB.getProductById(invalidId);

        // Then
        assertNull(product, "Product với ID không tồn tại phải trả về null");
    }

    @Test
    @DisplayName("Test searchProducts - tìm kiếm với keyword từ database")
    void testSearchProducts() {
        // Given - Lấy keyword từ product thực tế trong DB
        Product randomProduct = TestDataHelper.getRandomProduct();
        Assumptions.assumeTrue(randomProduct != null, "Database phải có ít nhất 1 product để test");
        
        // Lấy một phần tên product làm search term
        String productName = randomProduct.getName();
        String searchTerm = productName.length() > 3 ? productName.substring(0, 3) : productName;

        // When
        List<Product> products = productDB.searchProducts(searchTerm);

        // Then
        assertThat(products).isNotNull();
        if (!products.isEmpty()) {
            System.out.println("[ProductDBTest] Tìm thấy " + products.size() + " products với keyword: " + searchTerm);
            // Kiểm tra ít nhất 1 product chứa search term trong tên hoặc mô tả
            boolean found = products.stream()
                .anyMatch(p -> p.getName().toLowerCase().contains(searchTerm.toLowerCase()) 
                    || (p.getDescription() != null && p.getDescription().toLowerCase().contains(searchTerm.toLowerCase())));
            assertThat(found).isTrue();
        } else {
            System.out.println("[ProductDBTest] Không tìm thấy products với keyword: " + searchTerm);
        }
    }

    // TODO: Thêm các test cases khác:
    // - testAddProduct
    // - testUpdateProduct
    // - testDeleteProduct
    // - testGetProductsByCategory
    // - testUpdateStock
}



