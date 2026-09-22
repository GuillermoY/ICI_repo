package es.ucm.fdi.ici.c2627.practica0.grupoIndividual;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.DM;
import pacman.game.Game;
import pacman.game.GameView;
import java.awt.Color;
public class MsPacman extends PacmanController {

    @Override
    public MOVE getMove(Game game, long timeDue) {
    	int nodo = game.getPacmanCurrentNodeIndex();
    	MOVE last = game.getPacmanLastMoveMade();
    	MOVE[] options = game.getPossibleMoves(nodo, last);
    	
    	dibujar(game);
    	
    	if(options.length  == 1) return options[0];
        return decide(game, nodo,last, options);
    }

    private void dibujar(Game game) {
        int nodo = game.getPacmanCurrentNodeIndex();

        // Power pills
        int[] pp = game.getActivePowerPillsIndices();
        for (int p : pp) {
            GameView.addLines(game, Color.CYAN, nodo, p);
            GameView.addPoints(game, Color.BLUE,
                    game.getShortestPath(nodo, p));
        }
    }
    
    private MOVE decide(Game game, int nodo, MOVE last, MOVE [] options) {	
    	MOVE toGhost = weakGhost(game, nodo, last);
        if (toGhost != null) return toGhost;
        
        MOVE toPowerPill = toNearPowerPill(game, nodo, last);
        if (toPowerPill != null) return toPowerPill;
        
        MOVE toPill = toSafePill(game, nodo, last);
        if (toPill != null) return toPill;
        
        return huir (game, nodo, last, options);
    }
    
    private MOVE weakGhost(Game game, int nodo, MOVE ultimo) {
    	GHOST objetivo = null;
        int mejorDist = Integer.MAX_VALUE;

        for (GHOST f : GHOST.values()) {
            int t = game.getGhostEdibleTime(f);
            if (t <= 0) continue;

            int nf = game.getGhostCurrentNodeIndex(f);
            int d  = game.getShortestPathDistance(nodo, nf, ultimo);
            if (d < 0) continue;
            if (d > Constantes.CERCA) continue;
            if (d + Constantes.FANTASMAS_PARA_PILDORA >= t) continue;
            
            int[] camino = game.getShortestPath(nodo, nf, ultimo);

            if (!caminoSeguro(game, camino, f)) {
                continue;
            }
            
            if (d < mejorDist) {
                mejorDist = d;
                objetivo = f;
            }
           
        }
        if (objetivo == null) return null;

        int nf = game.getGhostCurrentNodeIndex(objetivo);
        int[] camino = game.getShortestPath(nodo, nf, ultimo);
        if (camino == null || camino.length < 2) return null;
        return game.getMoveToMakeToReachDirectNeighbour(camino[0], camino[1]);
    }
    
    private MOVE toNearPowerPill(Game game, int nodo, MOVE ultimo) {
    	
    	 int[] pp = game.getActivePowerPillsIndices();
    	    if (pp.length == 0) return null;
    	    
    	    //fantasmas cercanos
    	    int cerca = 0;
    	    for (GHOST f : GHOST.values()) {
    	        if (game.getGhostLairTime(f) > 0) continue;
    	        if (game.getGhostEdibleTime(f) > 0) continue;
    	        int nf = game.getGhostCurrentNodeIndex(f);
    	        if (game.getShortestPathDistance(nodo, nf, ultimo) < Constantes.CERCA) cerca++;
    	    }
    	    
    	    //si no hay fantasmas cercanos se evita comer
    	    if (cerca < Constantes.FANTASMAS_PARA_PILDORA) return null;

    	    MOVE mejorMovimiento = null;
    	    int mejorDistancia = Integer.MAX_VALUE;
    	    
    	    for (int p : pp) {
    	        int[] camino = game.getShortestPath(nodo, p, ultimo);

    	        if (camino == null || camino.length < 2) continue;

    	        // La power pill también tiene que ser alcanzable de forma segura.
    	        if (!caminoSeguro(game, camino, null)) continue;

    	        if (camino.length < mejorDistancia) {
    	            mejorDistancia = camino.length;
    	            mejorMovimiento =
    	                game.getMoveToMakeToReachDirectNeighbour(camino[0], camino[1]);
    	        }
    	    }

    	    return mejorMovimiento;
    }
    
    private MOVE toSafePill(Game game, int nodo, MOVE ultimo) {
    	
    	int[] pildoras = game.getActivePillsIndices();
        if (pildoras.length == 0) return null;

        MOVE mejor = null;
        int mejorDist = Integer.MAX_VALUE;
        
        for (int p : pildoras) {
            int[] camino = game.getShortestPath(nodo, p, ultimo);
            if (camino == null || camino.length < 2) continue;

            if (!caminoSeguro(game, camino, null)) {
                continue;
            }

            if (camino.length < mejorDist) {
                mejorDist = camino.length;
                mejor = game.getMoveToMakeToReachDirectNeighbour(camino[0], camino[1]);
            
            }
        }
       
        return mejor;
    }
    
    private MOVE huir(Game game, int nodo, MOVE ultimo, MOVE[] opciones) {
    	MOVE mejor = opciones[0];
        double mejorDist = -1;

        for (MOVE m : opciones) {
            int vecino = game.getNeighbour(nodo, m);
            if (vecino == -1) continue;

            double suma = 0;
            int cuenta = 0;
            for (GHOST f : GHOST.values()) {
                if (game.getGhostLairTime(f) > 0) continue;    
                if (game.getGhostEdibleTime(f) > 0) continue; 
                int nf = game.getGhostCurrentNodeIndex(f);
                suma += game.getShortestPathDistance(vecino, nf, m);
                cuenta++;
            }
            double media = (cuenta == 0) ? Double.MAX_VALUE : suma / cuenta;

            if (media > mejorDist) {
                mejorDist = media;
                mejor = m;
            }
        }
        return mejor;
    }
    
    private boolean caminoSeguro(Game game, int[] camino, GHOST objetivoComestible) {

        if (camino == null || camino.length == 0)return false;

        for (GHOST f : GHOST.values()) {
            if (f == objetivoComestible) continue;

            if (game.getGhostLairTime(f) > 0) continue;

            if (game.getGhostEdibleTime(f) > 0) continue;

            int nf = game.getGhostCurrentNodeIndex(f);

            if (nf == -1) continue;

            for (int nodo : camino) {

                int distancia = game.getShortestPathDistance(nodo, nf);

                if (distancia >= 0 &&
                    distancia <= Constantes.CERCA) {

                    return false;
                }
            }
        }

        return true;
    }
    
    public String getName() {
    	return "MsPacMan";
    	
    }

}
