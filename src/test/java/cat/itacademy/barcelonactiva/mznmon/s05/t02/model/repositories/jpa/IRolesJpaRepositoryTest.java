package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.jpa;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.users.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IRolesJpaRepositoryTest {

    @Autowired
    private IRolesJpaRepository rolesRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findByName() {
        // Given
        String roleName = "ADMIN";

        // When
        Optional<Role> foundRole = rolesRepository.findByName(roleName);

        // Then
        assertTrue(foundRole.isPresent());
        assertEquals(roleName, foundRole.get().getName());
    }
}