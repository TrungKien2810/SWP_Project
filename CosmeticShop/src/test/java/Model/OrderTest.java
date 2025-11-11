package Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class cho Model.Order
 */
@DisplayName("Order Model Tests")
class OrderTest {

    private Order order;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.now();
        order = new Order();
        order.setOrderId(1);
        order.setUserId(100);
        order.setOrderDate(testDate);
        order.setTotalAmount(500000.0);
    }

    @Test
    @DisplayName("Test getter và setter methods")
    void testGettersAndSetters() {
        // When
        order.setOrderId(2);
        order.setUserId(200);
        order.setTotalAmount(750000.0);
        order.setShippingAddressId(10);
        order.setShippingMethodId(1);
        order.setShippingCost(30000.0);
        order.setPaymentMethod("VNPay");
        order.setPaymentStatus("PAID");
        order.setOrderStatus("PROCESSING");
        order.setTrackingNumber("TRACK123");
        order.setDiscountId(5);
        order.setDiscountAmount(50000.0);
        order.setNotes("Giao hàng nhanh");

        // Then
        assertThat(order.getOrderId()).isEqualTo(2);
        assertThat(order.getUserId()).isEqualTo(200);
        assertThat(order.getTotalAmount()).isEqualTo(750000.0);
        assertThat(order.getShippingAddressId()).isEqualTo(10);
        assertThat(order.getShippingMethodId()).isEqualTo(1);
        assertThat(order.getShippingCost()).isEqualTo(30000.0);
        assertThat(order.getPaymentMethod()).isEqualTo("VNPay");
        assertThat(order.getPaymentStatus()).isEqualTo("PAID");
        assertThat(order.getOrderStatus()).isEqualTo("PROCESSING");
        assertThat(order.getTrackingNumber()).isEqualTo("TRACK123");
        assertThat(order.getDiscountId()).isEqualTo(5);
        assertThat(order.getDiscountAmount()).isEqualTo(50000.0);
        assertThat(order.getNotes()).isEqualTo("Giao hàng nhanh");
    }

    @Test
    @DisplayName("Test order details list")
    void testOrderDetails() {
        // Given
        OrderDetail detail1 = new OrderDetail();
        detail1.setProductId(1);
        detail1.setQuantity(2);
        detail1.setPrice(250000.0);

        OrderDetail detail2 = new OrderDetail();
        detail2.setProductId(2);
        detail2.setQuantity(1);
        detail2.setPrice(300000.0);

        // When
        order.getDetails().add(detail1);
        order.getDetails().add(detail2);

        // Then
        assertThat(order.getDetails()).hasSize(2);
        assertThat(order.getDetails().get(0).getProductId()).isEqualTo(1);
        assertThat(order.getDetails().get(1).getProductId()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test UI summary fields")
    void testUISummaryFields() {
        // When
        order.setFirstProductName("Kem dưỡng ẩm");
        order.setFirstProductImageUrl("product.jpg");

        // Then
        assertThat(order.getFirstProductName()).isEqualTo("Kem dưỡng ẩm");
        assertThat(order.getFirstProductImageUrl()).isEqualTo("product.jpg");
    }

    @Test
    @DisplayName("Test null values handling")
    void testNullValues() {
        // When
        order.setShippingAddressId(null);
        order.setShippingMethodId(null);
        order.setDiscountId(null);

        // Then
        assertThat(order.getShippingAddressId()).isNull();
        assertThat(order.getShippingMethodId()).isNull();
        assertThat(order.getDiscountId()).isNull();
    }
}



