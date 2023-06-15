package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.services;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.dtos.*;

import java.util.List;
import java.util.Optional;

public interface IPlayerService {
    PlayerDTO save(RegisterPlayerDTO registerPlayerDTO, Long id);
    PlayerNameDTO update(PlayerNameDTO PlayerNameDTO, Long id);
    void delete(String id);
    List<PlayerRateDTO> findAll();
    GameDTO saveGame(PlayerDTO playerDTO);
    Optional<PlayerDTO> findPlayerById(Long id);
    PlayerDTO deletePlayerGames(Long id);
    RankingDTO getRanking();
    PlayerRateDTO getRankingLoserPlayer();
    PlayerRateDTO getRankingWinnerPlayer();
}
