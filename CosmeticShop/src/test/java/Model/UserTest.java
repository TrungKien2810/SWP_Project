package Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class cho Model.user
 */
@DisplayName("User Model Tests")
class UserTest {

    private user user;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.now();
        user = new user(1, "testuser", "test@example.com", "0123456789", 
                "password123", "USER", testDate);
    }

    @Test
    @DisplayName("Test constructor với đầy đủ thông tin")
    void testConstructorWithAllFields() {
        // Given & When
        user u = new user(1, "john", "john@example.com", "0987654321", 
                "pass123", "ADMIN", testDate);

        // Then
        assertThat(u.getUser_id()).isEqualTo(1);
        assertThat(u.getUsername()).isEqualTo("john");
        assertThat(u.getEmail()).isEqualTo("john@example.com");
        assertThat(u.getPhone()).isEqualTo("0987654321");
        assertThat(u.getPassword()).isEqualTo("pass123");
        assertThat(u.getRole()).isEqualTo("ADMIN");
        assertThat(u.getDate_create()).isEqualTo(testDate);
    }

    @Test
    @DisplayName("Test constructor với null phone - should set empty string")
    void testConstructorWithNullPhone() {
        // When
        user u = new user(1, "test", "test@example.com", null, 
                "pass", "USER", testDate);

        // Then
        assertThat(u.getPhone()).isEmpty();
    }

    @Test
    @DisplayName("Test constructor với null role - should default to USER")
    void testConstructorWithNullRole() {
        // When
        user u = new user(1, "test", "test@example.com", "123", 
                "pass", null, testDate);

        // Then
        assertThat(u.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Test getter và setter methods")
    void testGettersAndSetters() {
        // When
        user.setUser_id(2);
        user.setUsername("newuser");
        user.setEmail("new@example.com");
        user.setPhone("0999999999");
        user.setPassword("newpass");
        user.setRole("ADMIN");
        user.setAvatarUrl("avatar.jpg");

        LocalDateTime newDate = LocalDateTime.now().minusDays(1);
        user.setDate_create(newDate);

        // Then
        assertThat(user.getUser_id()).isEqualTo(2);
        assertThat(user.getUsername()).isEqualTo("newuser");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getPhone()).isEqualTo("0999999999");
        assertThat(user.getPassword()).isEqualTo("newpass");
        assertThat(user.getRole()).isEqualTo("ADMIN");
        assertThat(user.getAvatarUrl()).isEqualTo("avatar.jpg");
        assertThat(user.getDate_create()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        // When
        user u = new user();

        // Then
        assertThat(u).isNotNull();
        assertThat(u.getUser_id()).isEqualTo(0);
        assertThat(u.getUsername()).isNull();
    }
}



