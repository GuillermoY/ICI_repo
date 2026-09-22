package es.ucm.fdi.ici.c2627.practica0.grupoIndividual;

import java.util.EnumMap;

import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.DM;
import pacman.game.Game;

public class Ghosts extends GhostController {
	private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);

    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
    	 moves.clear();
         for (GHOST f : GHOST.values()) {
             if (game.doesGhostRequireAction(f)) {
                 moves.put(f, segunSuPapel(game, f));
             }
         }
         return moves;
    }
    
    private MOVE segunSuPapel(Game game, GHOST f) {
        int  nodo    = game.getGhostCurrentNodeIndex(f);
        MOVE ultimo  = game.getGhostLastMoveMade(f);
        int  pacman  = game.getPacmanCurrentNodeIndex();
        MOVE ultPac  = game.getPacmanLastMoveMade();

        if (game.getGhostEdibleTime(f) > 0) {
            return huirConCriterio(game, nodo, ultimo, pacman);
        }

        switch (f) {
            case BLINKY:
                return primerMovimientoDe(game,
                        game.getShortestPath(nodo, pacman, ultimo));

            case PINKY: {
                int destino = proximoCruceDe(game, pacman, ultPac);
                return primerMovimientoDe(game,
                        game.getShortestPath(nodo, destino, ultimo));
            }

            case INKY: { 
                int[] pp = game.getActivePowerPillsIndices();
                if (pp.length > 0) {
                    int guardar = game.getClosestNodeIndexFromNodeIndex(
                            pacman, pp, DM.PATH);
                    return primerMovimientoDe(game,
                            game.getShortestPath(nodo, guardar, ultimo));
                }
                return primerMovimientoDe(game,
                        game.getShortestPath(nodo, pacman, ultimo));
            }

            default:
                if (game.getCurrentLevelTime() % 20 == 0) {
                    return movimientoAleatorioLegal(game, nodo, ultimo);
                }
                return primerMovimientoDe(game,
                        game.getShortestPath(nodo, pacman, ultimo));
        }
    }
    
    private MOVE primerMovimientoDe(Game game, int[] camino) {
        if (camino == null || camino.length < 2) return MOVE.NEUTRAL;
        return game.getMoveToMakeToReachDirectNeighbour(camino[0], camino[1]);
    }

    private int proximoCruceDe(Game game, int nodo, MOVE ultimo) {
        return nodo;
    }

    private MOVE huirConCriterio(Game game, int nodo, MOVE ultimo, int pacman) {
        return MOVE.NEUTRAL;
    }

    private MOVE movimientoAleatorioLegal(Game game, int nodo, MOVE ultimo) {
        MOVE[] ops = game.getPossibleMoves(nodo, ultimo);
        return ops[(int)(Math.random() * ops.length)];
    }
    
    public String getName() {
    	return "Ghosts";
    }

}
