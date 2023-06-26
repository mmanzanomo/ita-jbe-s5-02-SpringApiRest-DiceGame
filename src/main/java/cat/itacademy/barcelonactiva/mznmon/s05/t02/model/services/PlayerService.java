package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.services;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.exceptions.EmailIsAlreadyExistsException;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.exceptions.NameIsAlreadyExistsException;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.game.Game;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.game.Player;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos.*;
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


    /**
     * This method checks if the player name does not exist and saves it.
     * @param registerPlayerDTO that contains the player's name.
     * @param id related to the registered user.
     * @return PlayerDTO - the player that was created or null if an exception NameIsAlreadyExistsException
     * or NameIsAlreadyExistsException was raised.
     */
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

            player.setUserId(id);
            Player savedPlayer = playerMongoDbRepository.save(player);
            return convertPlayerToPlayerDTO(savedPlayer);
        } catch (EmailIsAlreadyExistsException | NameIsAlreadyExistsException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * This method update the player's name.
     * @param playerNameDTO new name.
     * @param userId related to the registered user.
     * @return the updated name.
     */
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

    /**
     * This method get the player with specified user id.
     * @param id the user
     * @return the player if it has been found.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PlayerDTO> findPlayerById(Long id) {
        Optional<Player> playerByUserId = playerMongoDbRepository.findByUserId(id);

        return playerByUserId.map(this::convertPlayerToPlayerDTO);
    }

    /**
     * This method generated a new roll and save the game.
     * @param playerDTO the player who rolls the dice.
     * @return The saved game.
     */
    @Override
    @Transactional
    public GameDTO saveGame(PlayerDTO playerDTO) {
        Optional<Player> playerMongo = playerMongoDbRepository.findById(playerDTO.id());

        // If player exists roll the dice
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

    /**
     * This method returns the statistics of all players
     * @return a list of players with their statistics.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PlayerRateDTO> findAll() {
        List<Player> players = playerMongoDbRepository.findAll();
        return players.stream().map(this::convertPlayerToPlayerRateDTO).toList();
    }

    /**
     * This method clear the game list of a player.
     * @param id the user id.
     * @return the same player with updated info.
     */
    @Override
    @Transactional
    public PlayerDTO deletePlayerGames(Long id) {
        Optional<Player> playerByUserId = playerMongoDbRepository.findByUserId(id);

        if (playerByUserId.isPresent()) {
            playerByUserId.get().getGames().clear();
            playerByUserId.get().setTotalGames(0);
            playerByUserId.get().setGamesWon(0);
            playerByUserId.get().setSuccessRate();

            playerMongoDbRepository.save(playerByUserId.get());

            return convertPlayerToPlayerDTO(playerByUserId.get());
        }

        return null;
    }

    /**
     * This method returns the average of all wins made by players.
     * @return the total average of wins.
     */
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

    /**
     * This method obtains the player with the worst statistics.
     * @return the player with the worst statistics.
     */
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

    /**
     * This method obtains the player with the best statistics.
     * @return the player with the best statistics.
     */
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
        PlayerDTO playerDTO = new PlayerDTO(player.getId(), player.getUserId(), player.getName(),
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
