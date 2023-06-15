package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.mongo;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPlayerMongoDbRepository extends MongoRepository<Player, String> {
    boolean existsByName(String name);
    boolean existsByUserId(Long id);
    Optional<Player> findByUserId(Long id);
}
