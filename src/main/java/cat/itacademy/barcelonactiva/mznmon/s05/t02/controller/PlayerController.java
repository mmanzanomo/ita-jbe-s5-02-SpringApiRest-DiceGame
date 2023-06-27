package cat.itacademy.barcelonactiva.mznmon.s05.t02.controller;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.auth.JwtService;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos.*;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.services.IPlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Game", description = "Game logic management.")
@RequestMapping("api/v1")
public class PlayerController {
    private final IPlayerService playerService;
    private final JwtService jwtService;


    @Operation(summary = "Add a new player")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added player",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.")
    })
    @PostMapping("/players")
    public ResponseEntity<PlayerDTO> savePlayer(@RequestBody RegisterPlayerDTO registerPlayerDTO,
                                                @RequestHeader("Authorization") String tokenHeader) {
        // Get userId
        String token = tokenHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        PlayerDTO savedPlayer = playerService.save(registerPlayerDTO, userId);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPlayer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Modify the player's name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated player",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerNameDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.")
    })
    @PutMapping("/players")
    public ResponseEntity<PlayerNameDTO> updatePlayer(@RequestBody @Valid PlayerNameDTO playerNameDTO,
                                                      @RequestHeader("Authorization") String tokenHeader) {
        // Get userId
        String token = tokenHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        PlayerNameDTO updatedPlayer = playerService.update(playerNameDTO, userId);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedPlayer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "The player plays a new game and rolls the dice.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Saved Game",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.")
    })
    @PostMapping("/players/games/roll")
    public ResponseEntity<GameDTO> rollDice(@RequestHeader("Authorization") String tokenHeader) {
        // Get userId
        String token = tokenHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        try {
            Optional<PlayerDTO> playerDTO = playerService.findPlayerById(userId);

            // if player exists, player roll a dice and save the game.
            if (playerDTO.isPresent()) {
                GameDTO gameDTO = playerService.saveGame(playerDTO.get());
                return ResponseEntity.status(HttpStatus.CREATED).body(gameDTO);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Delete player's game list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "game list deleted",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Invalid credentials."),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @DeleteMapping("/players/games/delete")
    public ResponseEntity<PlayerDTO> deletePlayerGames(@RequestHeader("Authorization") String tokenHeader) {
        // Get userId
        String token = tokenHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        try {
            PlayerDTO playerDTO = playerService.deletePlayerGames(userId);
            return (playerDTO != null)
                    ? ResponseEntity.status(HttpStatus.OK).body(playerDTO)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Get the list of players")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get the players list",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerRateDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.")
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
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.")
    })
    @GetMapping("/players/games")
    public ResponseEntity<PlayerDTO> getPlayer(@RequestHeader("Authorization") String tokenHeader) {
        // Get userId
        String token = tokenHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        Optional<PlayerDTO> player = playerService.findPlayerById(userId);

        return player.map(playerDTO -> ResponseEntity.ok().body(playerDTO))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
    }

    @Operation(summary = "Get the rate of all players")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The average hit percentage of all players has been obtained",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RankingDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.")
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
            @ApiResponse(responseCode = "401", description = "Invalid credentials.")
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
            @ApiResponse(responseCode = "401", description = "Invalid credentials.")
    })
    @GetMapping("/players/ranking/winner")
    public ResponseEntity<PlayerRateDTO> getRankingBestPlayer() {
        return ResponseEntity.ok().body(playerService.getRankingWinnerPlayer());
    }

}
