package Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class cho Model.Product
 */
@DisplayName("Product Model Tests")
class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1, "Kem dưỡng ẩm", 250000.0, 50, 
                "Kem dưỡng ẩm cho da khô", "image1.jpg", 1);
    }

    @Test
    @DisplayName("Test constructor với single image")
    void testConstructorWithSingleImage() {
        // Given & When
        Product p = new Product(1, "Test Product", 100000.0, 10, 
                "Description", "test.jpg", 1);

        // Then
        assertThat(p.getProductId()).isEqualTo(1);
        assertThat(p.getName()).isEqualTo("Test Product");
        assertThat(p.getPrice()).isEqualTo(100000.0);
        assertThat(p.getStock()).isEqualTo(10);
        assertThat(p.getDescription()).isEqualTo("Description");
        assertThat(p.getImageUrl()).isEqualTo("test.jpg");
        assertThat(p.getCategoryId()).isEqualTo(1);
        assertThat(p.getImageUrls()).hasSize(1);
        assertThat(p.getImageUrls().get(0)).isEqualTo("test.jpg");
    }

    @Test
    @DisplayName("Test constructor với multiple images")
    void testConstructorWithMultipleImages() {
        // Given
        List<String> images = new ArrayList<>();
        images.add("image1.jpg");
        images.add("image2.jpg");
        images.add("image3.jpg");

        // When
        Product p = new Product(1, "Test Product", 100000.0, 10, 
                "Description", images, 1);

        // Then
        assertThat(p.getImageUrls()).hasSize(3);
        assertThat(p.getImageUrl()).isEqualTo("image1.jpg");
    }

    @Test
    @DisplayName("Test getter và setter methods")
    void testGettersAndSetters() {
        // When
        product.setProductId(2);
        product.setName("New Name");
        product.setPrice(300000.0);
        product.setStock(100);
        product.setDescription("New Description");
        product.setImageUrl("new-image.jpg");
        product.setCategoryId(2);

        // Then
        assertThat(product.getProductId()).isEqualTo(2);
        assertThat(product.getName()).isEqualTo("New Name");
        assertThat(product.getPrice()).isEqualTo(300000.0);
        assertThat(product.getStock()).isEqualTo(100);
        assertThat(product.getDescription()).isEqualTo("New Description");
        assertThat(product.getImageUrl()).isEqualTo("new-image.jpg");
        assertThat(product.getCategoryId()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test addImageUrl method")
    void testAddImageUrl() {
        // Given
        assertThat(product.getImageUrls()).hasSize(1);

        // When
        product.addImageUrl("image2.jpg");
        product.addImageUrl("image3.jpg");

        // Then
        assertThat(product.getImageUrls()).hasSize(3);
        assertThat(product.getImageUrls()).contains("image1.jpg", "image2.jpg", "image3.jpg");
    }

    @Test
    @DisplayName("Test setImageUrls method")
    void testSetImageUrls() {
        // Given - product đã có "image1.jpg" từ setUp()
        assertThat(product.getImageUrls()).hasSize(1);
        assertThat(product.getImageUrl()).isEqualTo("image1.jpg");
        
        List<String> newImages = new ArrayList<>();
        newImages.add("new1.jpg");
        newImages.add("new2.jpg");

        // When
        product.setImageUrls(newImages);

        // Then
        assertThat(product.getImageUrls()).hasSize(2);
        assertThat(product.getImageUrls()).containsExactly("new1.jpg", "new2.jpg");
        assertThat(product.getImageUrl()).isEqualTo("new1.jpg");
    }
    
    @Test
    @DisplayName("Test setImageUrls với empty list")
    void testSetImageUrls_WithEmptyList() {
        // Given
        List<String> emptyImages = new ArrayList<>();
        
        // When
        product.setImageUrls(emptyImages);
        
        // Then - imageUrls sẽ là empty, nhưng imageUrl vẫn giữ nguyên (không được clear)
        assertThat(product.getImageUrls()).isEmpty();
        // Note: imageUrl không được clear khi set empty list (theo implementation hiện tại)
        assertThat(product.getImageUrl()).isEqualTo("image1.jpg");
    }
    
    @Test
    @DisplayName("Test setImageUrls với null")
    void testSetImageUrls_WithNull() {
        // Given
        // When
        product.setImageUrls(null);
        
        // Then - imageUrls sẽ là null (theo implementation), imageUrl vẫn giữ nguyên
        assertThat(product.getImageUrls()).isNull();
        assertThat(product.getImageUrl()).isEqualTo("image1.jpg");
    }

    @Test
    @DisplayName("Test constructor với null imageUrl")
    void testConstructorWithNullImageUrl() {
        // When - Cast null to String để tránh ambiguous constructor
        Product p = new Product(1, "Test", 100000.0, 10, 
                "Desc", (String) null, 1);

        // Then
        assertThat(p.getImageUrls()).isEmpty();
        assertThat(p.getImageUrl()).isNull();
    }
}

