package es.ucm.fdi.ici.c2627.practica0.grupoIndividual;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.DM;
import pacman.game.Game;
import pacman.game.GameView;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class MsPacMan extends PacmanController {

    public static final int CERCA                       = 30;
    public static final int HOLGURA                     = 4;
    public static final int FANTASMAS_PARA_PILDORA      = 3;
    public static final int PROFUNDIDAD_SEGURA          = 2;
    public static final boolean DEBUG_PACMAN            = true;
    public static final boolean DEBUG_GHOSTS            = false;

    public Map<Integer, Double> costes = new HashMap<>();

//    costes.put(25, 10.0);
//    costes.put(26, 15.5);
//    costes.put(27, 30.0);


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
            if (d > CERCA) continue;
            if (d + FANTASMAS_PARA_PILDORA >= t) continue;
            
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
    	        if (game.getShortestPathDistance(nodo, nf, ultimo) < CERCA) cerca++;
    	    }
    	    
    	    //si no hay fantasmas cercanos se evita comer
    	    if (cerca < FANTASMAS_PARA_PILDORA) return null;

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
                    distancia <= CERCA) {

                    return false;
                }
            }
        }

        return true;
    }
    
    private void repintarCostes(Game game)
    {
    	ponerTodosLosNodosA(0);
    	
    	for (GHOST f : GHOST.values())
    	{
    		if (game.isGhostEdible(f))
    		{
//    			game.getApproximateNextMoveTowardsTarget(, CERCA, lastMove, null)
    			//ALBARATAR AL REDEDOR DE F (NODO(F)), radio, caida)
    			// Caida = factor, que es el multiplicador 0.75
    			abaratarAlrededorDe(game, costes, game.getGhostCurrentNodeIndex(f), 2, 0.75);
    		}
    		else
    		{
    			abaratarAlrededorDe(game, costes, game.getGhostCurrentNodeIndex(f), 2, -0.75);
    			//ENCARECER AL REDEDOR DE F (NODO(F)), radio, caida)
    		}
    		// ENCARECER EL CAMINO
    	}
    	if (hayFantasmaEncima(game) /*HAY FANTASMA ENCIMA*/ )
    	{
        	for (int e : game.getActivePowerPillsIndices())
        	{        		
        		abaratarAlrededorDe(game, costes, e, 1.5, 0.75);
        	}
			//ALBARATAR AL REDEDOR DE PILDORAS DE PODER (pildora más cercana, radio, caida)
    	}
    	for (int e : game.getActivePillsIndices())
    	{        		
    		abaratarAlrededorDe(game, costes, e, 1, 0.75);
    	}
	}
    
    private void ponerTodosLosNodosA(double coste)
    {
    	for (Map.Entry<Integer, Double> entrada : costes.entrySet())
    	{
        	entrada.setValue(coste);
        }
    }
    
    private void abaratarAlrededorDe(Game game, Map<Integer, Double> costes, int nodoFantasma, double radio, double caida) {
    	
        for (Map.Entry<Integer, Double> entrada : costes.entrySet()) {

            int nodo = entrada.getKey();

            double distancia =
                    game.getShortestPathDistance(nodoFantasma, nodo);

            if (distancia <= radio) {

                double influencia = 1.0 - distancia / radio;

                double nuevoCoste =
                        entrada.getValue() * (1.0 - caida * influencia);

                entrada.setValue(entrada.getValue() + nuevoCoste);
            }
        }
    }
    
    
    private boolean hayFantasmaEncima(Game game) {

        int pacman = game.getPacmanCurrentNodeIndex();

        for (GHOST ghost : GHOST.values()) {

        	if (!game.isGhostEdible(ghost))
        	{
	            int nodoFantasma =
	                game.getGhostCurrentNodeIndex(ghost);
	
	            double distancia =
	                game.getShortestPathDistance(pacman, nodoFantasma);
	
	            if (distancia <= 2) {
	                return true;
	            }
        	}
        }

        return false;
    }
    
    public String getName() {
    	return "MsPacMan";
    	
    }

}
