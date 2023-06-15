package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.mongo;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGameMongoDbRepository extends MongoRepository<Game, Long> {
}
