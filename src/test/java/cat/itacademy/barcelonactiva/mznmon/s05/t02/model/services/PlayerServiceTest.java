package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.services;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.exceptions.NameIsAlreadyExistsException;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.game.Game;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.game.Player;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos.*;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.mongo.IPlayerMongoDbRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {
    @Mock
    private IPlayerMongoDbRepository playerMongoDbRepository;
    @InjectMocks
    private PlayerService playerService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void savePlayer() {
        // Arrange
        RegisterPlayerDTO registerPlayerDTO = new RegisterPlayerDTO("playerTest01");
        Long userId = 1L;

        Player player = new Player();
        player.setName("playerTest01");
        player.setUserId(userId);

        when(playerMongoDbRepository.existsByUserId(userId)).thenReturn(false);
        when(playerMongoDbRepository.existsByName(registerPlayerDTO.name())).thenReturn(false);
        when(playerMongoDbRepository.save(any(Player.class))).thenReturn(player);

        // Act
        PlayerDTO playerDTO = playerService.save(registerPlayerDTO, userId);

        // Assert
        assertNotNull(playerDTO);
        assertEquals(registerPlayerDTO.name(), playerDTO.name());
    }



    @Test
    void updatePlayerName() {
        // Arrange
        PlayerNameDTO playerNameDTO = new PlayerNameDTO("playerTest01");
        Long userId = 1L;

        Player player = new Player();
        player.setUserId(1L);
        player.setName("playerTest01");

        when(playerMongoDbRepository.findByUserId(userId)).thenReturn(Optional.of(player));

        // Act
        PlayerNameDTO result = playerService.update(playerNameDTO, userId);

        // Assert
        assertNotNull(result);
        assertEquals(result.name(), player.getName());
    }

    @Test
    void findPlayerById() {
        // Arrange
        Long userId = 1L;

        Player player = new Player();
        player.setUserId(userId);
        player.setName("John Doe");
        Optional<Player> playerOptional = Optional.of(player);
        when(playerMongoDbRepository.findByUserId(userId)).thenReturn(playerOptional);

        // Act
        Optional<PlayerDTO> result = playerService.findPlayerById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().userId());
        assertEquals(player.getName(), result.get().name());
        verify(playerMongoDbRepository, times(1)).findByUserId(userId);
        verifyNoMoreInteractions(playerMongoDbRepository);
    }

    @Test
    void saveGame() {
        // Arrange
        PlayerDTO playerDTO = new PlayerDTO("test000001", 1L, "playerTest001", 2, 5, 40.0, null);
        Player player = new Player();
        player.setGamesWon(playerDTO.gamesWon());
        player.setTotalGames(playerDTO.totalGames());
        player.setSuccessRate(playerDTO.successRate());
        when(playerMongoDbRepository.findById(playerDTO.id())).thenReturn(Optional.of(player));

        // Act
        GameDTO savedGameDTO = playerService.saveGame(playerDTO);

        // Assert
        assertNotNull(savedGameDTO);
        assertEquals(1, player.getGames().size());
        assertEquals(6, player.getTotalGames());
        if(savedGameDTO.score() == 7) {
            assertEquals(3, player.getGamesWon());
        }
        verify(playerMongoDbRepository, times(1)).save(player);
    }

    @Test
    void findAll() {
        // Arrange
        List<Player> players = new ArrayList<>();
        Player player1 = new Player();
        player1.setId("Test0001");
        player1.setName("playerTest01");
        player1.setGamesWon(2);
        player1.setTotalGames(5);
        player1.setSuccessRate();
        players.add(player1);

        Player player2 = new Player();
        player2.setId("Test0002");
        player2.setName("playerTest02");
        player2.setGamesWon(3);
        player2.setTotalGames(6);
        player2.setSuccessRate();
        players.add(player2);

        when(playerMongoDbRepository.findAll()).thenReturn(players);

        // Act
        List<PlayerRateDTO> playerRateDTOList = playerService.findAll();

        // Assert
        assertNotNull(playerRateDTOList);
        assertEquals(2, playerRateDTOList.size());

        PlayerRateDTO playerRateDTO1 = playerRateDTOList.get(0);
        assertEquals("Test0001", playerRateDTO1.id());
        assertEquals("playerTest01", playerRateDTO1.name());
        assertEquals(2, playerRateDTO1.gamesWon());
        assertEquals(5, playerRateDTO1.totalGames());
        assertEquals(40.0, playerRateDTO1.successRate());

        PlayerRateDTO playerRateDTO2 = playerRateDTOList.get(1);
        assertEquals("Test0002", playerRateDTO2.id());
        assertEquals("playerTest02", playerRateDTO2.name());
        assertEquals(3, playerRateDTO2.gamesWon());
        assertEquals(6, playerRateDTO2.totalGames());
        assertEquals(50.0, playerRateDTO2.successRate());

        verify(playerMongoDbRepository, times(1)).findAll();
    }

    @Test
    void deletePlayerGames() {
        // Arrange
        Player player = new Player();
        player.setId("Test000001");
        player.setUserId(1L);
        player.setName("playerTest01");
        player.setGamesWon(2);
        player.setTotalGames(5);
        player.setSuccessRate();

        Game game1 = new Game();
        Game game2 = new Game();
        player.getGames().add(game1);
        player.getGames().add(game2);

        when(playerMongoDbRepository.findByUserId(player.getUserId())).thenReturn(Optional.of(player));

        // Act
        PlayerDTO result = playerService.deletePlayerGames(player.getUserId());

        // Assert
        assertNotNull(result);
        assertEquals(0, player.getGames().size());
        assertEquals(0, player.getTotalGames());
        assertEquals(0, player.getGamesWon());
        verify(playerMongoDbRepository, times(1)).save(player);
    }

    @Test
    void getRanking() {
        // Arrange
        List<Player> players = new ArrayList<>();

        Player player1 = new Player();
        player1.setId("test0001");
        player1.setUserId(1L);
        player1.setName("playerTest01");
        player1.getGames().add(new Game());
        player1.setGamesWon(2);
        player1.setTotalGames(5);
        player1.setSuccessRate();
        players.add(player1);

        Player player2 = new Player();
        player2.setId("test0002");
        player2.setUserId(2L);
        player2.setName("playerTest02");
        player1.getGames().add(new Game());
        player2.setGamesWon(4);
        player2.setTotalGames(8);
        player2.setSuccessRate();
        players.add(player2);

        when(playerMongoDbRepository.findAll()).thenReturn(players);

        // Act
        RankingDTO result = playerService.getRanking();

        // Assert
        assertNotNull(result);
        assertEquals(40.0, result.mean());
    }

    @Test
    void getRankingLoserPlayer() {
        // Arrange
        Player player1 = new Player();
        player1.getGames().add(new Game());  // game list should not be empty
        player1.setSuccessRate(60.0);  // Higher success rate

        Player player2 = new Player();
        player2.getGames().add(new Game());  // game list should not be empty
        player2.setSuccessRate(30.0);  // Lower success rate

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        // Mock the repository's findAll() method
        when(playerMongoDbRepository.findAll()).thenReturn(players);

        // Act
        PlayerRateDTO result = playerService.getRankingLoserPlayer();

        // Assert
        assertEquals(player2.getSuccessRate(), result.successRate());
    }

    @Test
    void getRankingWinnerPlayer() {
        // Arrange
        Player player1 = new Player();
        player1.getGames().add(new Game());  // game list should not be empty
        player1.setSuccessRate(60.0);  // Higher success rate

        Player player2 = new Player();
        player2.getGames().add(new Game());  // game list should not be empty
        player2.setSuccessRate(30.0);  // Lower success rate

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        // Mock the repository's findAll() method
        when(playerMongoDbRepository.findAll()).thenReturn(players);

        // Act
        PlayerRateDTO result = playerService.getRankingWinnerPlayer();

        // Assert
        assertEquals(player1.getSuccessRate(), result.successRate());
    }
}