package DAO;

import E2E.TestDataHelper;
import Model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests cho CategoryDB
 * 
 * Note: Các test này yêu cầu database connection thực tế.
 * Sử dụng TestDataHelper để lấy data động từ database.
 */
@DisplayName("CategoryDB DAO Tests")
class CategoryDBTest {

    private CategoryDB categoryDB;

    @BeforeEach
    void setUp() {
        categoryDB = new CategoryDB();
    }

    @Test
    @DisplayName("Test listAll - lấy tất cả categories từ database")
    void testListAll() {
        // When
        List<Category> categories = categoryDB.listAll();

        // Then
        assertThat(categories).isNotNull();
        // Kiểm tra nếu có dữ liệu trong database
        if (!categories.isEmpty()) {
            assertThat(categories).isNotEmpty();
            System.out.println("[CategoryDBTest] Tìm thấy " + categories.size() + " categories trong database");
            // Kiểm tra category đầu tiên có đầy đủ thông tin
            Category first = categories.get(0);
            assertThat(first.getCategoryId()).isGreaterThan(0);
            assertThat(first.getName()).isNotNull();
        } else {
            System.out.println("[CategoryDBTest] Database chưa có categories nào");
        }
    }

    @Test
    @DisplayName("Test getById với ID hợp lệ - lấy từ database")
    void testGetById_ValidId() {
        // Given - Lấy category ID động từ database
        List<Category> allCategories = categoryDB.listAll();
        Assumptions.assumeTrue(!allCategories.isEmpty(), "Database phải có ít nhất 1 category để test");
        
        int categoryId = allCategories.get(0).getCategoryId();

        // When
        Category category = categoryDB.getById(categoryId);

        // Then
        assertThat(category).isNotNull();
        assertThat(category.getCategoryId()).isEqualTo(categoryId);
        System.out.println("[CategoryDBTest] Tìm thấy category ID " + categoryId + ": " + category.getName());
    }

    @Test
    @DisplayName("Test getById với ID không tồn tại")
    void testGetById_InvalidId() {
        // Given - ID không tồn tại
        int invalidId = 99999;

        // When
        Category category = categoryDB.getById(invalidId);

        // Then
        assertThat(category).isNull();
    }

    @Test
    @DisplayName("Test existsByName - kiểm tra tên category đã tồn tại")
    void testExistsByName() {
        // Given - Lấy category từ database
        List<Category> allCategories = categoryDB.listAll();
        Assumptions.assumeTrue(!allCategories.isEmpty(), "Database phải có ít nhất 1 category để test");
        
        String existingName = allCategories.get(0).getName();

        // When
        boolean exists = categoryDB.existsByName(existingName);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Test existsByName với tên không tồn tại")
    void testExistsByName_NonExistent() {
        // Given
        String nonExistentName = "NonExistentCategory_" + System.currentTimeMillis();

        // When
        boolean exists = categoryDB.existsByName(nonExistentName);

        // Then
        assertThat(exists).isFalse();
    }
}

