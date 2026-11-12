package es.ucm.fdi.ici.c2627.practica2.GIRM_LC;

import es.ucm.fdi.ici.Input;
import es.ucm.fdi.ici.c2627.practica2.GIRM_LC.*;
import es.ucm.fdi.ici.fsm.State;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManState implements State {

    public enum Fase { NORMAL, CAZA, POWER, HUIDA }
//    public boolean esHuida() { return this.fase == Fase.HUIDA; }
    private final Fase fase;
    private final MsPacManBase controller;

    public MsPacManState(Fase fase, MsPacManBase controller) {
        this.fase = fase;
        this.controller = controller;
    }

    public Fase getFase() { return fase; }

    @Override
    public MOVE execute(Input in) {
        Game game = ((MsPacManInput) in).getGame();
        return controller.decidir(game, fase);
    }

    @Override
    public void stop() {
        // Nada que hacer: no guardamos estado entre turnos.
    }

    @Override
    public String toString() { return fase.name(); }
}