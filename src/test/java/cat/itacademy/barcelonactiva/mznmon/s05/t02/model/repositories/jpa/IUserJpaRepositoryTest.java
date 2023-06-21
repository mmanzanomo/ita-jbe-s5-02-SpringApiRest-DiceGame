package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.jpa;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.dtos.RegisterRequest;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.users.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class IUserJpaRepositoryTest {

    @Autowired
    private IUserJpaRepository userRepository;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        // Clear database after each test
        userRepository.deleteAll();
    }

    @Test
    void existsByEmail() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByEmail(email);

        // Then
        assertTrue(exists);
    }

    @Test
    void findByEmail() {
        // Given
        String email = "test2@example.com";
        String password = "password1234";
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail(email);

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(email, foundUser.get().getEmail());
    }
}