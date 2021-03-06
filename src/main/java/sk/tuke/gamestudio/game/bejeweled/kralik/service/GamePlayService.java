package sk.tuke.gamestudio.game.bejeweled.kralik.service;

import sk.tuke.gamestudio.game.bejeweled.kralik.entity.GamePlay;

public interface GamePlayService {
    void store(GamePlay gamePlay);

    GamePlay load(int ident);

    GamePlay loadLast();
}
