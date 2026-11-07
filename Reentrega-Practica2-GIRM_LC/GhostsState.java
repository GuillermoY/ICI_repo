package es.ucm.fdi.ici.c2627.practica2.grupoIndividual;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.fsm.State;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class GhostsState implements State {

    public enum FaseG { PERSEGUIR, EVADIR, HUIDA }

    private final FaseG fase;
    private final GhostsV1 controller;
    private final GHOST fantasma;

    public GhostsState(FaseG fase, GhostsV1 controller, GHOST fantasma) {
        this.fase = fase;
        this.controller = controller;
        this.fantasma = fantasma;
    }

    public FaseG getFase() { return fase; }

    @Override
    public MOVE execute(Input in) {
        Game game = in.getGame();
        return controller.actualizarEstado(game, fase, fantasma);
    }

    @Override
    public void stop() { }

    @Override
    public String toString() { return fase.name(); }
}