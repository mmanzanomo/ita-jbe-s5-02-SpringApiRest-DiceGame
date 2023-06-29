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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Game", description = "Game logic management.")
@Validated
@RequestMapping("api/v1")
public class PlayerController {
    private final IPlayerService playerService;
    private final JwtService jwtService;


    @Operation(summary = "Add a new player")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added player",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) })
    })
    @PostMapping("/players")
    public ResponseEntity<?> savePlayer(@RequestBody RegisterPlayerDTO registerPlayerDTO,
                                                @RequestHeader("Authorization") String tokenHeader) {
        // Get userId
        String token = tokenHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        PlayerDTO savedPlayer = playerService.save(registerPlayerDTO, userId);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPlayer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @Operation(summary = "Modify the player's name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated player",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerNameDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) })
    })
    @PutMapping("/players")
    public ResponseEntity<?> updatePlayer(@RequestBody @Valid PlayerNameDTO playerNameDTO,
                                                      @RequestHeader("Authorization") String tokenHeader) {
        // Get userId
        String token = tokenHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        PlayerNameDTO updatedPlayer = playerService.update(playerNameDTO, userId);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedPlayer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @Operation(summary = "The player plays a new game and rolls the dice.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Saved Game",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) })
    })
    @PostMapping("/players/games/roll")
    public ResponseEntity<?> rollDice(@RequestHeader("Authorization") String tokenHeader) {
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
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponseDTO(HttpStatus.UNAUTHORIZED.value(), "Permission to roll the dice denied."));

            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @Operation(summary = "Delete player's game list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "game list deleted",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Player not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) })
    })
    @DeleteMapping("/players/games/delete")
    public ResponseEntity<?> deletePlayerGames(@RequestHeader("Authorization") String tokenHeader) {
        // Get userId
        String token = tokenHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        try {
            PlayerDTO playerDTO = playerService.deletePlayerGames(userId);
            return (playerDTO != null)
                    ? ResponseEntity.status(HttpStatus.OK).body(playerDTO)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponseDTO(HttpStatus.NOT_FOUND.value(), "Player not found"));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponseDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @Operation(summary = "Get the list of players")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get the players list",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerRateDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) })
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
            @ApiResponse(responseCode = "400", description = "Bad request.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) })
    })
    @GetMapping("/players/games")
    public ResponseEntity<?> getPlayer(@RequestHeader("Authorization") String tokenHeader) {
        // Get userId
        String token = tokenHeader.substring(7);
        Long userId = jwtService.extractUserId(token);

        Optional<PlayerDTO> player = playerService.findPlayerById(userId);

        return player.map(playerDTO -> ResponseEntity.ok().body(playerDTO))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @Operation(summary = "Get the rate of all players")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The average hit percentage of all players has been obtained",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RankingDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "Invalid credentials.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) })
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
            @ApiResponse(responseCode = "401", description = "Invalid credentials.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) })
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
            @ApiResponse(responseCode = "401", description = "Invalid credentials.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponseDTO.class)) })
    })
    @GetMapping("/players/ranking/winner")
    public ResponseEntity<PlayerRateDTO> getRankingBestPlayer() {
        return ResponseEntity.ok().body(playerService.getRankingWinnerPlayer());
    }


    // Handle MethodArgumentNotValidException exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        // Build the custom MessageResponseDTO
        MessageResponseDTO responseDTO = new MessageResponseDTO(HttpStatus.BAD_REQUEST.value(), "Bad Request. The request data is not as expected.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MessageResponseDTO> handleAccessDeniedException(AccessDeniedException ex) {
        MessageResponseDTO responseDTO = new MessageResponseDTO(0, "Access Denied.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseDTO);
    }

}
