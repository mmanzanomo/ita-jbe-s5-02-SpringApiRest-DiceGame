package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.jpa;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRolesJpaRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}