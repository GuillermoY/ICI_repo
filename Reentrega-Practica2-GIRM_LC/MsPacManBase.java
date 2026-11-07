package es.ucm.fdi.ici.c2627.practica2.grupoIndividual;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.GameView;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import es.ucm.fdi.ici.fsm.FSM;
import es.ucm.fdi.ici.fsm.Transition;
import es.ucm.fdi.ici.c2627.practica2.grupoIndividual.MsPacManState.Fase;

public class MsPacManBase extends PacmanController {
	
    @Override
    public MOVE getMove(Game game, long timeDue) {
        return null;
    }
    
    public MOVE decidir(Game game, Fase fase) {
    	return null;
    }
    
    @Override
    public String getName() { return "MsPacManBase"; }
}
