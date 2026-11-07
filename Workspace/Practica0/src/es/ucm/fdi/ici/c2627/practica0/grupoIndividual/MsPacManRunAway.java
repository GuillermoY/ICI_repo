package es.ucm.fdi.ici.c2627.practica0.grupoIndividual;

import java.util.Random;

import pacman.controllers.PacmanController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacManRunAway extends PacmanController{
	
    private Random rnd = new Random();
    private MOVE[] allMoves = MOVE.values();

    @Override
    public MOVE getMove(Game game, long timeDue) {

    	int[] posGhosts = new int[4];
    	int i = 0;
        for (GHOST ghostType : GHOST.values()) {
    	
    		posGhosts[i] = game.getGhostCurrentNodeIndex(ghostType);
    		++i;
    	}
    	
        return game.getApproximateNextMoveAwayFromTarget(
        		game.getPacmanCurrentNodeIndex(),
        		game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(), posGhosts, DM.MANHATTAN),
        		game.getPacmanLastMoveMade(),
        		DM.MANHATTAN);
        
    }

    public String getName() {
    	return "MsPacManRunAway";
    }
    
}
