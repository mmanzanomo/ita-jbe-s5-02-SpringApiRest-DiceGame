package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.jpa;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserJpaRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
