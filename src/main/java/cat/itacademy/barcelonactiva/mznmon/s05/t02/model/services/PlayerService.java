package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.services;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.exceptions.EmailIsAlreadyExistsException;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.exceptions.NameIsAlreadyExistsException;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.game.Game;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.game.Player;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos.*;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.mongo.IGameMongoDbRepository;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.mongo.IPlayerMongoDbRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
public class PlayerService implements IPlayerService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    private IPlayerMongoDbRepository playerMongoDbRepository;
    @Autowired
    private IGameMongoDbRepository gameMongoDbRepository;


    @Override
    @Transactional
    public PlayerDTO save(RegisterPlayerDTO registerPlayerDTO, Long id) {
        // Check if user have a player
        if (playerMongoDbRepository.existsByUserId(id))
            throw new NameIsAlreadyExistsException("A player is already exists.");

        // Else create a new player
        try {
            Player player = new Player();
            BeanUtils.copyProperties(registerPlayerDTO, player);

            if (player.getName().equals("")) {
                player.setName("Anonymous");
            } else {
                // Check if username already exists
                boolean nameExists = playerMongoDbRepository.existsByName(registerPlayerDTO.name());
                if (nameExists) throw new NameIsAlreadyExistsException("The username is already exists.");
            }

            playerMongoDbRepository.save(player);
            return convertPlayerToPlayerDTO(player);
        } catch (EmailIsAlreadyExistsException | NameIsAlreadyExistsException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public PlayerNameDTO update(PlayerNameDTO playerNameDTO, Long userId) {
        Optional<Player> playerMongo = playerMongoDbRepository.findByUserId(userId);

        if (playerMongo.isPresent()) {
            playerMongo.get().setName(playerNameDTO.name());
            playerMongoDbRepository.save(playerMongo.get());
        }
        return convertPlayerToPlayerNameDTO(playerMongo.get());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlayerDTO> findPlayerById(Long id) {
        Optional<Player> playerByUserId = playerMongoDbRepository.findByUserId(id);
        Optional<Player> player = playerMongoDbRepository.findById(playerByUserId.get().getId());
        return player.map(this::convertPlayerToPlayerDTO);
    }

    @Override
    @Transactional
    public GameDTO saveGame(PlayerDTO playerDTO) {
        Optional<Player> playerMongo = playerMongoDbRepository.findById(playerDTO.id());

        if (playerMongo.isPresent()) {
            Game game = new Game();
            playerMongo.get().getGames().add(game);
            playerMongo.get().increaseTotalGames();

            if (game.getScore() == 7) playerMongo.get().setGamesWon();
            playerMongo.get().setSuccessRate();

            playerMongoDbRepository.save(playerMongo.get());
            return convertGameToGameDTO(game);
        }
        return null;
    }

    @Override
    public void delete(String id) {
        // TODO: delete player
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerRateDTO> findAll() {
        List<Player> players = playerMongoDbRepository.findAll();
        return players.stream().map(this::convertPlayerToPlayerRateDTO).toList();
    }

    @Override
    @Transactional
    public PlayerDTO deletePlayerGames(Long id) {
        Optional<Player> playerByUserId = playerMongoDbRepository.findByUserId(id);
        // Find player id
        Optional<Player> playerMongo = playerMongoDbRepository.findById(playerByUserId.get().getId());

        if (playerMongo.isPresent()) {
            gameMongoDbRepository.deleteAll(playerMongo.get().getGames());
            playerMongo.get().getGames().clear();
            playerMongo.get().setTotalGames(0);
            playerMongo.get().setGamesWon(0);
            playerMongo.get().setSuccessRate();
        }

        return convertPlayerToPlayerDTO(playerMongo.get());
    }

    @Override
    @Transactional(readOnly = true)
    public RankingDTO getRanking() {
        List<Player> players = playerMongoDbRepository.findAll();

        // Calculate the total average of games won.
        double totalMeanRate = players.stream()
                .filter(player -> !player.getGames().isEmpty())
                .mapToDouble(Player::getSuccessRate)
                .average()
                .orElse(0.0); // Default value in case there are no elements

        return convertDoubleToRankingDTO(totalMeanRate);
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerRateDTO getRankingLoserPlayer() {
        List<Player> players = playerMongoDbRepository.findAll();
        Player worstPlayer = players.stream()
                .filter(player -> !player.getGames().isEmpty())
                .min(Comparator.comparing(Player::getSuccessRate))
                .orElse(null);
        return convertPlayerToPlayerRateDTO(worstPlayer);
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerRateDTO getRankingWinnerPlayer() {
        List<Player> players = playerMongoDbRepository.findAll();
        Player bestPlayer = players.stream()
                .filter(player -> !player.getGames().isEmpty())
                .max(Comparator.comparing(Player::getSuccessRate))
                .orElse(null);
        return convertPlayerToPlayerRateDTO(bestPlayer);
    }


    /*
        UTILS TO COMVERT TO DTO
     */
    private PlayerDTO convertPlayerToPlayerDTO(Player player) {
        PlayerDTO playerDTO = new PlayerDTO(player.getId(), null, player.getName(),
                player.getGamesWon(), player.getTotalGames(), player.getSuccessRate(),
                player.getGames().stream().map(this::convertGameToGameDTO).toList());
        return playerDTO;
    }

    private PlayerRateDTO convertPlayerToPlayerRateDTO(Player player) {
        PlayerRateDTO playerRateDTO = new PlayerRateDTO(
            player.getId(),
            player.getName(),
            player.getGamesWon(),
            player.getTotalGames(),
            player.getSuccessRate());
        return playerRateDTO;
    }

    private PlayerNameDTO convertPlayerToPlayerNameDTO(Player player) {
        PlayerNameDTO playerNameDTO = new PlayerNameDTO(player.getName());
        return playerNameDTO;
    }

    private GameDTO convertGameToGameDTO(Game game) {
        GameDTO gameDTO = new GameDTO(
            game.getId(),
            game.getDice1(),
            game.getDice2(),
            game.getScore(),
            game.isWinner(),
            game.getPlayDate());
        return gameDTO;
    }

    private RankingDTO convertDoubleToRankingDTO(double meanRate) {
        return new RankingDTO(meanRate);
    }

}
