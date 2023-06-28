package cat.itacademy.barcelonactiva.mznmon.s05.t02.controller;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.JwtService;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos.*;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.services.IPlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlayerControllerTest {
    @Mock
    private IPlayerService playerService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private PlayerController playerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void savePlayer() {
        // Arrange
        String token = "valid_token";
        Long userId = 123L;
        when(jwtService.extractUserId(token)).thenReturn(userId);

        // Define the expected behavior of the IPlayerService mock
        RegisterPlayerDTO registerPlayerDTO = new RegisterPlayerDTO("playername");
        PlayerDTO savedPlayerDTO = new PlayerDTO("uuid344", userId, "playername", 0, 0, 0.0, null);;
        when(playerService.save(registerPlayerDTO, userId)).thenReturn(savedPlayerDTO);

        ResponseEntity<PlayerDTO> response = playerController.savePlayer(registerPlayerDTO, "Bearer " + token);

        // Verify that the mock methods were called
        verify(jwtService).extractUserId(token);
        verify(playerService).save(registerPlayerDTO, userId);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedPlayerDTO, response.getBody());
    }

    @Test
    void updatePlayer() {
        // Given
        String token = "valid_token";
        Long userId = 123L;
        when(jwtService.extractUserId(token)).thenReturn(userId);

        // Define the expected behavior of the IPlayerService mock
        PlayerNameDTO playerNameDTO = new PlayerNameDTO("othername");
        PlayerNameDTO updatedPlayerDTO = new PlayerNameDTO("playername");
        when(playerService.update(playerNameDTO, userId)).thenReturn(updatedPlayerDTO);

        // Call controller
        ResponseEntity<PlayerNameDTO> response = playerController.updatePlayer(playerNameDTO, "Bearer " + token);

        // Verify that the mock methods were called
        verify(jwtService).extractUserId(token);
        verify(playerService).update(playerNameDTO, userId);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(updatedPlayerDTO, response.getBody());
    }

    @Test
    void rollDice() {
        // Given
        String token = "valid_token";
        Long userId = 123L;
        LocalDateTime time = LocalDateTime.now();
        when(jwtService.extractUserId(token)).thenReturn(userId);

        // player service mock
        PlayerDTO playerDTO = new PlayerDTO("uuid344", userId, "playername", 0, 0, 0.0, null);
        Optional<PlayerDTO> optionalPlayerDTO = Optional.of(playerDTO);
        when(playerService.findPlayerById(userId)).thenReturn(optionalPlayerDTO);

        // Game mock
        GameDTO gameDTO = new GameDTO((byte) 1, (byte) 6, (byte)7, true, time);
        when(playerService.saveGame(playerDTO)).thenReturn(gameDTO);

        // Call the rollDice method to verify
        ResponseEntity<GameDTO> response = playerController.rollDice("Bearer " + token);

        // Verify
        verify(jwtService).extractUserId(token);
        verify(playerService).findPlayerById(userId);
        verify(playerService).saveGame(playerDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(gameDTO, response.getBody());
    }

    @Test
    void deletePlayerGames() {
        // Given
        String token = "valid_token";
        Long userId = 123L;
        when(jwtService.extractUserId(token)).thenReturn(userId);

        // IPlayerService mock
        PlayerDTO playerDTO = new PlayerDTO("uuid344", userId, "playername", 0, 0, 0.0, null);
        when(playerService.deletePlayerGames(userId)).thenReturn(playerDTO);

        // Call controller
        ResponseEntity<PlayerDTO> response = playerController.deletePlayerGames("Bearer " + token);

        // Verify
        verify(jwtService).extractUserId(token);
        verify(playerService).deletePlayerGames(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(playerDTO, response.getBody());
    }

    @Test
    void getPlayers() {
        // Given
        List<PlayerRateDTO> players = new ArrayList<>();
        players.add(new PlayerRateDTO("uuid001", "player1", 6, 8, 75.0));
        players.add(new PlayerRateDTO("uuid002", "player2", 3, 6, 50.0));
        when(playerService.findAll()).thenReturn(players);

        // Call controller
        ResponseEntity<List<PlayerRateDTO>> response = playerController.getPlayers();

        // Verify
        verify(playerService).findAll();

        // Asserts
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(players, response.getBody());
    }

    @Test
    void getPlayer() {
        // Given
        Long userId = 1L;
        String id = "uuid344";
        PlayerDTO playerDTO = new PlayerDTO(id, userId, "playername", 5, 10, 50.0, null);
        // An example token to test
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInN1YiI6InBsYXllclRlc3RAbWFpbC5tYWlsIiwiaWF0IjoxNjg3MjgxMjYwLCJleHAiOjE2ODczNjc2NjB9.ESm0JbtYvpG-XIvtt6eOZSE7NUljTB86ceqEOpuJYk8";
        when(jwtService.extractUserId(anyString())).thenReturn(userId);
        when(playerService.findPlayerById(userId)).thenReturn(Optional.of(playerDTO));

        // Call controller
        ResponseEntity<PlayerDTO> response = playerController.getPlayer("Bearer token");

        // Verify
        verify(playerService).findPlayerById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(playerDTO, response.getBody());
    }

    @Test
    void getRanking() {
        // Given
        RankingDTO rankingDTO = new RankingDTO(5.0);
        when(playerService.getRanking()).thenReturn(rankingDTO);

        // Call controller
        ResponseEntity<RankingDTO> response = playerController.getRanking();

        // Verify
        verify(playerService).getRanking();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rankingDTO, response.getBody());
    }

    @Test
    void getRankingWorstPlayer() {
        // Given
        PlayerRateDTO playerRateDTO = new PlayerRateDTO("uuuid12","WorstPlayer", 2, 5,40.0);
        when(playerService.getRankingLoserPlayer()).thenReturn(playerRateDTO);

        // Call Controller
        ResponseEntity<PlayerRateDTO> response = playerController.getRankingWorstPlayer();

        // Verify
        verify(playerService).getRankingLoserPlayer();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(playerRateDTO, response.getBody());
    }

    @Test
    void getRankingBestPlayer() {
        // Given
        PlayerRateDTO playerRateDTO = new PlayerRateDTO("uuuid12","WorstPlayer", 2, 5,40.0);
        when(playerService.getRankingWinnerPlayer()).thenReturn(playerRateDTO);

        // Call the method in the controller
        ResponseEntity<PlayerRateDTO> response = playerController.getRankingBestPlayer();

        // Verify that the method of the IPlayerService mock was called
        verify(playerService).getRankingWinnerPlayer();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(playerRateDTO, response.getBody());
    }
}