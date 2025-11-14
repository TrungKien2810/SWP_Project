package DAO;

import E2E.TestDataHelper;
import Model.user;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assumptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class cho UserDB
 * 
 * Note: Các test này yêu cầu database connection thực tế.
 * Sử dụng TestDataHelper để lấy data động từ database thay vì hardcode.
 */
@DisplayName("UserDB DAO Tests")
class UserDBTest {

    private UserDB userDB;

    @BeforeEach
    void setUp() {
        userDB = new UserDB();
    }

    @Test
    @DisplayName("Test getUserByEmail với email hợp lệ - lấy từ database")
    void testGetUserByEmail_ValidEmail() {
        // Given - Lấy user động từ database
        user randomUser = TestDataHelper.getRandomUser();
        Assumptions.assumeTrue(randomUser != null, "Database phải có ít nhất 1 user để test");
        String email = randomUser.getEmail();

        // When
        user user = userDB.getUserByEmail(email);

        // Then
        assertNotNull(user, "User với email " + email + " phải tồn tại trong database");
        assertThat(user.getEmail()).isEqualTo(email);
        System.out.println("[UserDBTest] Tìm thấy user: " + user.getEmail() + " (ID: " + user.getUser_id() + ")");
    }

    @Test
    @DisplayName("Test getUserByEmail với email không tồn tại")
    void testGetUserByEmail_InvalidEmail() {
        // Given
        String invalidEmail = "nonexistent_" + System.currentTimeMillis() + "@example.com";

        // When
        user user = userDB.getUserByEmail(invalidEmail);

        // Then
        assertNull(user, "User với email không tồn tại phải trả về null");
    }

    @Test
    @DisplayName("Test getUserById với ID hợp lệ - lấy từ database")
    void testGetUserById_ValidId() {
        // Given - Lấy user động từ database
        user randomUser = TestDataHelper.getRandomUser();
        Assumptions.assumeTrue(randomUser != null, "Database phải có ít nhất 1 user để test");
        int userId = randomUser.getUser_id();

        // When
        user user = userDB.getUserById(userId);

        // Then
        assertNotNull(user, "User với ID " + userId + " phải tồn tại trong database");
        assertThat(user.getUser_id()).isEqualTo(userId);
        System.out.println("[UserDBTest] Tìm thấy user ID " + userId + ": " + user.getEmail());
    }

    @Test
    @DisplayName("Test getUserById với ID không tồn tại")
    void testGetUserById_InvalidId() {
        // Given - ID không tồn tại
        int invalidId = 99999;

        // When
        user user = userDB.getUserById(invalidId);

        // Then
        assertNull(user, "User với ID không tồn tại phải trả về null");
    }

    // TODO: Thêm các test cases khác:
    // - testAddUser (cần cleanup sau test)
    // - testUpdateUser (cần restore sau test)
    // - testGetAllUsers
    // - testGetUsersByRole
    // - testDeleteUser
    // - testChangePassword
    // - testUpdateAvatar
}



