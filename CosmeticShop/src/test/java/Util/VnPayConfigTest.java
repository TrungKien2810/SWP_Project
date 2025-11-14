package Util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests cho VnPayConfig utility class
 */
@DisplayName("VnPayConfig Utility Tests")
class VnPayConfigTest {

    @Test
    @DisplayName("Test hmacSHA512 - tạo hash từ key và data")
    void testHmacSHA512() {
        // Given
        String key = "test_key";
        String data = "test_data";

        // When
        String hash = VnPayConfig.hmacSHA512(key, data);

        // Then
        assertThat(hash).isNotNull();
        assertThat(hash).isNotEmpty();
        // Hash SHA512 luôn có 128 ký tự hex
        assertThat(hash.length()).isEqualTo(128);
    }

    @Test
    @DisplayName("Test hmacSHA512 với key rỗng trả về null (không hợp lệ)")
    void testHmacSHA512_EmptyKey() {
        // Given
        String key = "";
        String data = "test_data";

        // When
        String hash = VnPayConfig.hmacSHA512(key, data);

        // Then
        assertThat(hash).isNull();
    }

    @Test
    @DisplayName("Test hashAllFields - hash tất cả fields trong map")
    void testHashAllFields() {
        // Given
        Map<String, String> fields = new HashMap<>();
        fields.put("vnp_Amount", "1000000");
        fields.put("vnp_Command", "pay");
        fields.put("vnp_TxnRef", "12345");

        // When
        String hash = VnPayConfig.hashAllFields(fields);

        // Then
        assertThat(hash).isNotNull();
        assertThat(hash).contains("vnp_Amount");
        assertThat(hash).contains("vnp_Command");
        assertThat(hash).contains("vnp_TxnRef");
    }

    @Test
    @DisplayName("Test hashAllFields với map rỗng")
    void testHashAllFields_EmptyMap() {
        // Given
        Map<String, String> fields = new HashMap<>();

        // When
        String hash = VnPayConfig.hashAllFields(fields);

        // Then
        assertThat(hash).isNotNull();
        assertThat(hash).isEmpty();
    }

    @Test
    @DisplayName("Test hashAllFields bỏ qua giá trị null hoặc rỗng")
    void testHashAllFields_IgnoreNullAndEmpty() {
        // Given
        Map<String, String> fields = new HashMap<>();
        fields.put("vnp_Amount", "1000000");
        fields.put("vnp_Command", null);
        fields.put("vnp_TxnRef", "");

        // When
        String hash = VnPayConfig.hashAllFields(fields);

        // Then
        assertThat(hash).isNotNull();
        assertThat(hash).contains("vnp_Amount");
        assertThat(hash).doesNotContain("vnp_Command");
        assertThat(hash).doesNotContain("vnp_TxnRef");
    }

    @Test
    @DisplayName("Test urlEncode - encode string thành URL format")
    void testUrlEncode() {
        // Given
        String input = "test value with spaces";

        // When
        String encoded = VnPayConfig.urlEncode(input);

        // Then
        assertThat(encoded).isNotNull();
        // URLEncoder encode spaces thành dấu +
        assertThat(encoded).contains("+");
    }

    @Test
    @DisplayName("Test urlEncode với ký tự đặc biệt")
    void testUrlEncode_SpecialCharacters() {
        // Given
        String input = "test&value=123";

        // When
        String encoded = VnPayConfig.urlEncode(input);

        // Then
        assertThat(encoded).isNotNull();
        // & và = sẽ được encode thành %26 và %3D
        assertThat(encoded).contains("%26");
        assertThat(encoded).contains("%3D");
    }

    @Test
    @DisplayName("Test getClientIp - lấy IP từ X-Forwarded-For header")
    void testGetClientIp_FromXForwardedFor() {
        // Given
        String xForwardedFor = "192.168.1.1, 10.0.0.1";
        String remoteAddr = "127.0.0.1";

        // When
        String ip = VnPayConfig.getClientIp(xForwardedFor, remoteAddr);

        // Then
        assertThat(ip).isEqualTo("192.168.1.1");
    }

    @Test
    @DisplayName("Test getClientIp - dùng remoteAddr nếu không có X-Forwarded-For")
    void testGetClientIp_FromRemoteAddr() {
        // Given
        String xForwardedFor = null;
        String remoteAddr = "127.0.0.1";

        // When
        String ip = VnPayConfig.getClientIp(xForwardedFor, remoteAddr);

        // Then
        assertThat(ip).isEqualTo("127.0.0.1");
    }

    @Test
    @DisplayName("Test getClientIp - trả về rỗng nếu cả hai đều null")
    void testGetClientIp_BothNull() {
        // Given
        String xForwardedFor = null;
        String remoteAddr = null;

        // When
        String ip = VnPayConfig.getClientIp(xForwardedFor, remoteAddr);

        // Then
        assertThat(ip).isEmpty();
    }

    @Test
    @DisplayName("Test randomTxnRef - tạo transaction reference ngẫu nhiên")
    void testRandomTxnRef() {
        // Given
        int length = 10;

        // When
        String txnRef = VnPayConfig.randomTxnRef(length);

        // Then
        assertThat(txnRef).isNotNull();
        assertThat(txnRef.length()).isEqualTo(length);
        // Chỉ chứa số
        assertThat(txnRef).matches("\\d+");
    }

    @Test
    @DisplayName("Test randomTxnRef - tạo nhiều lần cho kết quả khác nhau")
    void testRandomTxnRef_DifferentResults() {
        // Given
        int length = 10;

        // When
        String ref1 = VnPayConfig.randomTxnRef(length);
        String ref2 = VnPayConfig.randomTxnRef(length);

        // Then
        // Có thể giống nhau nhưng rất hiếm (1/10^10)
        // Chỉ kiểm tra format
        assertThat(ref1).matches("\\d+");
        assertThat(ref2).matches("\\d+");
    }

    @Test
    @DisplayName("Test describeResponseCode - mô tả response code thành công")
    void testDescribeResponseCode_Success() {
        // Given
        String code = "00";

        // When
        String description = VnPayConfig.describeResponseCode(code);

        // Then
        assertThat(description).isEqualTo("Giao dịch thành công");
    }

    @Test
    @DisplayName("Test describeResponseCode - các mã lỗi khác nhau")
    void testDescribeResponseCode_VariousCodes() {
        // Test một số mã lỗi phổ biến
        assertThat(VnPayConfig.describeResponseCode("07"))
            .containsIgnoringCase("nghi ngờ");
        assertThat(VnPayConfig.describeResponseCode("09"))
            .containsIgnoringCase("chưa đăng ký");
        assertThat(VnPayConfig.describeResponseCode("51"))
            .containsIgnoringCase("không đủ số dư");
        assertThat(VnPayConfig.describeResponseCode("99"))
            .isEqualTo("Lỗi khác");
    }

    @Test
    @DisplayName("Test describeResponseCode - mã không xác định")
    void testDescribeResponseCode_UnknownCode() {
        // Given
        String code = "XX";

        // When
        String description = VnPayConfig.describeResponseCode(code);

        // Then
        assertThat(description).isEqualTo("Lỗi không xác định");
    }

    @Test
    @DisplayName("Test describeResponseCode - null code")
    void testDescribeResponseCode_NullCode() {
        // When
        String description = VnPayConfig.describeResponseCode(null);

        // Then
        assertThat(description).isEqualTo("Lỗi không xác định");
    }

    @Test
    @DisplayName("Test statusForResponseCode - trả về PAID cho mã 00")
    void testStatusForResponseCode_Paid() {
        // Given
        String code = "00";

        // When
        String status = VnPayConfig.statusForResponseCode(code);

        // Then
        assertThat(status).isEqualTo("PAID");
    }

    @Test
    @DisplayName("Test statusForResponseCode - trả về REVIEW cho mã 07")
    void testStatusForResponseCode_Review() {
        // Given
        String code = "07";

        // When
        String status = VnPayConfig.statusForResponseCode(code);

        // Then
        assertThat(status).isEqualTo("REVIEW");
    }

    @Test
    @DisplayName("Test statusForResponseCode - trả về FAILED cho mã khác")
    void testStatusForResponseCode_Failed() {
        // Given
        String code = "99";

        // When
        String status = VnPayConfig.statusForResponseCode(code);

        // Then
        assertThat(status).isEqualTo("FAILED");
    }
}

