package cat.itacademy.barcelonactiva.mznmon.s05.t02.controller;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos.*;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.services.IPlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1")
public class PlayerController {
    private IPlayerService playerService;


    public PlayerController(IPlayerService playerService) {
        this.playerService = playerService;
    }


    @Operation(summary = "Add a new player")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added player",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterPlayerDTO.class)) }),
            @ApiResponse(responseCode = "500", description = "Server internal error",
                    content = @Content)
    })
    @PostMapping("/players")
    public ResponseEntity<PlayerDTO> savePlayer(@RequestBody @Valid RegisterPlayerDTO registerPlayerDTO) {
        // Get userId
        Long userId = 1L;

        PlayerDTO savedPlayer = playerService.save(registerPlayerDTO, userId);
        return (savedPlayer != null)
                ? ResponseEntity.status(201).body(savedPlayer)
                : ResponseEntity.status(500).body(null);
    }

    @Operation(summary = "Modify the player's name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated player",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerNameDTO.class)) }),
            @ApiResponse(responseCode = "500", description = "Server internal error",
                    content = @Content)
    })
    @PutMapping("/players")
    public ResponseEntity<PlayerNameDTO> updatePlayer(@RequestBody @Valid PlayerNameDTO playerNameDTO) {
        // Get userId
        Long userId = 1L;
        PlayerNameDTO updatedPlayer = playerService.update(playerNameDTO, userId);
        return (updatedPlayer != null)
                ? ResponseEntity.status(201).body(updatedPlayer)
                : ResponseEntity.status(500).body(null);
    }

    @Operation(summary = "The player plays a new game and rolls the dice.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Saved Game",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDTO.class)) }),
            @ApiResponse(responseCode = "500", description = "Server internal error",
                    content = @Content)
    })
    @PostMapping("/players/games/roll")
    public ResponseEntity<GameDTO> rollDice() {
        // Get userId
        Long userId = 1L;
        // TODO: if player not exists, throw an NoValidPlayerException()
        Optional<PlayerDTO> playerDTO = playerService.findPlayerById(userId);
        // TODO: if player exists, player roll a dice and save the game.
        if (playerDTO.isPresent()) {
            GameDTO gameDTO = playerService.saveGame(playerDTO.get());
            return ResponseEntity.status(201).body(gameDTO);
        }
        return ResponseEntity.status(500).body(null);
    }

    @Operation(summary = "Delete player's game list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "game list deleted",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @DeleteMapping("/players/games/delete")
    public ResponseEntity<PlayerDTO> deletePlayerGames() {
        // Get userId
        Long userId = 1L;
        PlayerDTO playerDTO = playerService.deletePlayerGames(userId);
        return (playerDTO != null)
                ? ResponseEntity.status(200).body(playerDTO)
                : ResponseEntity.status(404).body(null);
    }

    @Operation(summary = "Get the list of players")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get the players list",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerRateDTO.class)) }),
            @ApiResponse(responseCode = "500", description = "Server internal error")
    })
    @GetMapping("/players/")
    public ResponseEntity<List<PlayerRateDTO>> getPlayers() {
        List<PlayerRateDTO> players = playerService.findAll();
        return ResponseEntity.ok().body(players);
    }

    @Operation(summary = "Get player's game list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of games has been obtained",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "player not found")
    })
    @GetMapping("/players/games")
    public ResponseEntity<PlayerDTO> getPlayer() {
        // Get userId
        Long userId = 1L;
        Optional<PlayerDTO> player = playerService.findPlayerById(userId);

        return player.map(playerDTO -> ResponseEntity.ok().body(playerDTO))
                .orElseGet(() -> ResponseEntity.status(404).body(null));
    }

    @Operation(summary = "Get the rate of all players")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The average hit percentage of all players has been obtained",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RankingDTO.class)) }),
            @ApiResponse(responseCode = "500", description = "Server internal error")
    })
    @GetMapping("/players/ranking")
    public ResponseEntity<RankingDTO> getRanking() {
        return ResponseEntity.ok().body(playerService.getRanking());
    }

    @Operation(summary = "Get the rate of the worst player")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The average hit percentage of worst player has been obtained",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerRateDTO.class)) }),
            @ApiResponse(responseCode = "500", description = "Server internal error")
    })
    @GetMapping("/players/ranking/loser")
    public ResponseEntity<PlayerRateDTO> getRankingWorstPlayer() {
        return ResponseEntity.ok().body(playerService.getRankingLoserPlayer());
    }

    @Operation(summary = "Get the rate of the best player")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The average hit percentage of best player has been obtained",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerRateDTO.class)) }),
            @ApiResponse(responseCode = "500", description = "Server internal error")
    })
    @GetMapping("/players/ranking/winner")
    public ResponseEntity<PlayerRateDTO> getRankingBestPlayer() {
        return ResponseEntity.ok().body(playerService.getRankingWinnerPlayer());
    }

}
