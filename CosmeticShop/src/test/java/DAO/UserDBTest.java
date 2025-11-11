package DAO;

import Model.user;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class cho UserDB
 * 
 * Note: Các test này yêu cầu database connection thực tế.
 * Để chạy integration tests, cần cấu hình test database.
 */
@DisplayName("UserDB DAO Tests")
class UserDBTest {

    private UserDB userDB;

    @BeforeEach
    void setUp() {
        // Lưu ý: Cần cấu hình test database hoặc mock connection
        // userDB = new UserDB();
    }

    @Test
    @DisplayName("Test getUserByEmail với email hợp lệ")
    @Disabled("Requires database connection - enable for integration tests")
    void testGetUserByEmail_ValidEmail() {
        // Given
        String email = "test@example.com";

        // When
        // user user = userDB.getUserByEmail(email);

        // Then
        // assertNotNull(user);
        // assertThat(user.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Test getUserByEmail với email không tồn tại")
    @Disabled("Requires database connection - enable for integration tests")
    void testGetUserByEmail_InvalidEmail() {
        // Given
        String invalidEmail = "nonexistent@example.com";

        // When
        // user user = userDB.getUserByEmail(invalidEmail);

        // Then
        // assertNull(user);
    }

    @Test
    @DisplayName("Test addUser")
    @Disabled("Requires database connection - enable for integration tests")
    void testAddUser() {
        // Given
        // user newUser = new user(0, "testuser", "newuser@example.com", 
        //         "0123456789", "password123", "USER", LocalDateTime.now());

        // When
        // boolean result = userDB.addUser(newUser);

        // Then
        // assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Test updateUser")
    @Disabled("Requires database connection - enable for integration tests")
    void testUpdateUser() {
        // Given
        // user user = userDB.getUserByEmail("test@example.com");
        // user.setPhone("0999999999");

        // When
        // boolean result = userDB.updateUser(user);

        // Then
        // assertThat(result).isTrue();
    }

    // TODO: Thêm các test cases khác:
    // - testGetUserById
    // - testGetAllUsers
    // - testDeleteUser
    // - testChangePassword
    // - testUpdateAvatar
}



