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
import java.util.Random;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MsPacMan extends PacmanController {

    public static final int CERCA                       = 30;
    public static final int HOLGURA                     = 4;
    public static final int FANTASMAS_PARA_PILDORA      = 3;
    public static final int PROFUNDIDAD_SEGURA          = 2;
    public static final boolean DEBUG_PACMAN            = true;
    public static final boolean DEBUG_GHOSTS            = false;
    
    
    public static final double PELIGRO_BASE            = 250.0;

    public Map<Integer, Double> costes = new HashMap<>();

//    costes.put(25, 10.0);
//    costes.put(26, 15.5);
//    costes.put(27, 30.0);

    private Random rnd = new Random();
    private MOVE[] allMoves = MOVE.values();

    @Override
    public MOVE getMove(Game game, long timeDue) {

        int nodo = game.getPacmanCurrentNodeIndex();
        MOVE last = game.getPacmanLastMoveMade();
        MOVE[] options = game.getPossibleMoves(nodo, last);

        for (int i = 0; i < game.getNumberOfNodes(); i++) {
            costes.putIfAbsent(i, 0.0);
        }

        // Recalcular costes
        repintarCostes(game);

        // Dibujar información de depuración
        dibujarDecisiones(game, nodo, last);

        if (options.length == 1) return options[0];

//        return decide(game, nodo, last, options);
        return decidirConCostes(game, nodo, last, options);
    }
    
    private void dibujarDecisiones(Game game, int nodoInicial, MOVE ultimo) { // PINTAR

        // =========================================================
        // 1. DIBUJAR TODOS LOS NODOS SEGÚN SU COSTE
        // =========================================================

        double maxCoste = 0;

        for (double coste : costes.values()) {
            if (coste > maxCoste) {
                maxCoste = coste;
            }
        }

        for (Map.Entry<Integer, Double> entrada : costes.entrySet()) {

            int nodo = entrada.getKey();
            double coste = entrada.getValue();

            Color color;

            if (coste <= 0.25) {
                color = Color.GREEN;
            }
            else if (coste <= 0.50) {
                color = Color.YELLOW;
            }
            else if (coste <= 1.0) {
                color = Color.ORANGE;
            }
            else {
                color = Color.RED;
            }

            GameView.addPoints(
                game,
                color,
                nodo
            );
        }


        // =========================================================
        // 2. MARCAR LAS OPCIONES INMEDIATAS
        // =========================================================

        MOVE[] opciones = game.getPossibleMoves(nodoInicial, ultimo);

        for (MOVE movimiento : opciones) {

            int vecino = game.getNeighbour(nodoInicial, movimiento);

            if (vecino == -1)
                continue;

            double coste = costes.getOrDefault(
                vecino,
                Double.MAX_VALUE
            );

            // Verde = elección actual
            if (vecino == obtenerMejorVecino(game, nodoInicial, opciones)) {

                GameView.addPoints(
                    game,
                    Color.GREEN,
                    vecino
                );

                GameView.addLines(
                    game,
                    Color.GREEN,
                    nodoInicial,
                    vecino
                );
            }

            // Azul = alternativas
            else {

                GameView.addPoints(
                    game,
                    Color.BLUE,
                    vecino
                );
            }
        }


        // =========================================================
        // 3. PREDECIR VARIAS DECISIONES HACIA DELANTE
        // =========================================================

        int actual = nodoInicial;
        MOVE movimientoAnterior = ultimo;

        int profundidad = 8;

        for (int i = 0; i < profundidad; i++) {

            MOVE[] posibles =
                game.getPossibleMoves(actual, movimientoAnterior);

            if (posibles == null || posibles.length == 0)
                break;

            int siguiente = obtenerMejorVecino(game, actual, posibles);

            if (siguiente == -1)
                break;


            // Línea que representa la futura trayectoria
            GameView.addLines(
                game,
                Color.MAGENTA,
                actual,
                siguiente
            );

            // Punto del siguiente nodo
            GameView.addPoints(
                game,
                Color.MAGENTA,
                siguiente
            );


            // Actualizar dirección
            MOVE siguienteMovimiento = game.getMoveToMakeToReachDirectNeighbour(actual, siguiente);

            movimientoAnterior = siguienteMovimiento;
            actual = siguiente;
        }
    }

    private int obtenerMejorVecino(Game game, int nodo, MOVE[] opciones) { // PINTAR

        int mejorNodo = -1;
        double mejorCoste = Double.MAX_VALUE;

        for (MOVE movimiento : opciones) {

            int vecino = game.getNeighbour(nodo, movimiento);

            if (vecino == -1) continue;

            double coste = costes.getOrDefault(vecino, Double.MAX_VALUE);

            if (coste < mejorCoste) {
                mejorCoste = coste;
                mejorNodo = vecino;
            }
        }

        return mejorNodo;
    }


    private MOVE elegirPorCoste(Game game, int nodo, MOVE[] opciones) {
        MOVE mejor = opciones[0];
        double mejorCoste = Double.MAX_VALUE;
        for (MOVE m : opciones) {
            int vecino = game.getNeighbour(nodo, m);
            if (vecino == -1) continue;
            double c = costes.getOrDefault(vecino, Double.MAX_VALUE);
            if (c < mejorCoste) {
                mejorCoste = c;
                mejor = m;
            }
        }
        return mejor;
    }
    
    private MOVE decidirConCostes(Game game, int nodo, MOVE ultimo, MOVE[] opciones) {
        MOVE mejor = opciones[0];
        double mejorCoste = Double.MAX_VALUE;
        for (MOVE m : opciones) {
            int vecino = game.getNeighbour(nodo, m);
            if (vecino == -1) continue;
            double c = costes.getOrDefault(vecino, 0.0);
            // opcional: ponderar por distancia al fantasma, etc.
            if (c < mejorCoste) {
                mejorCoste = c;
                mejor = m;
            }
        }
        return mejor;
    }

    private void repintarCostes(Game game) {

        ponerTodosLosNodosA(Double.MAX_VALUE);

        int[] pills = game.getActivePillsIndices();
        int[] powerPills = game.getActivePowerPillsIndices();

		//  Cada nodo recibe como coste base la distancia
		//  a la píldora más cercana.
         
        for (int nodo : costes.keySet()) {

            double mejorDistancia = Double.MAX_VALUE;

            for (int pill : pills) {

                int distancia = game.getShortestPathDistance(nodo, pill);

                if (distancia >= 0 && distancia < mejorDistancia) 
                	mejorDistancia = distancia;                
            }

            for (int pill : powerPills) {

                int distancia = game.getShortestPathDistance(nodo, pill);

                if (distancia >= 0 && distancia < mejorDistancia) 
                	mejorDistancia = distancia;                
            }

            if (mejorDistancia != Double.MAX_VALUE) {
                costes.put(nodo, mejorDistancia);
            }
            else {
                costes.put(nodo, 0.0);
            }
        }

        añadirPeligroFantasmas(game); // Ahora añadimos el peligro de los fantasmas.
    }
    private void añadirPeligroFantasmas(Game game) {

        for (GHOST fantasma : GHOST.values()) {

            // Si está en la guarida, no supone peligro
            if (game.getGhostLairTime(fantasma) > 0) continue;

            int nodoFantasma = game.getGhostCurrentNodeIndex(fantasma);

            if (nodoFantasma == -1) continue;

            if (game.isGhostEdible(fantasma)) añadirApetitoFantasmas(game, fantasma);

            for (Map.Entry<Integer, Double> entrada : costes.entrySet()) {

                int nodo = entrada.getKey();

                int distancia = game.getShortestPathDistance(nodo, nodoFantasma);

                if (distancia < 0) continue;
                
                double peligro = 0.0;
                
				//  Cuanto más cerca esté el fantasma,
				//  mayor será la penalización.

                if (distancia == 0) {//  distancia 0 -> peligro máximo
                    peligro = PELIGRO_BASE * 4;
                }
                else if (distancia == 1) {//  distancia 1 -> peligro alto
                    peligro = PELIGRO_BASE * 2;
                }
                else if (distancia == 2) {//  distancia 2 -> peligro medio
                    peligro = PELIGRO_BASE;
                }
                else if (distancia <= CERCA) { 
                    peligro = 100.0 / distancia; // Peligro decreciente con la distancia
                }

                entrada.setValue(entrada.getValue() + peligro);
            }
        }
    }
    private void añadirApetitoFantasmas(Game game, GHOST fantasma) {

            int nodoFantasma = game.getGhostCurrentNodeIndex(fantasma);


            for (Map.Entry<Integer, Double> entrada : costes.entrySet()) {

                int nodo = entrada.getKey();

                int distancia = game.getShortestPathDistance(nodo, nodoFantasma);

                if (distancia < 0) continue;
                
                double peligro = 0.0;
                
				//  Cuanto más cerca esté el fantasma,
				//  mayor será la penalización.

                if (distancia == 0) {//  distancia 0 -> peligro máximo
                    peligro = PELIGRO_BASE;
                }
                else if (distancia == 1) {//  distancia 1 -> peligro alto
                    peligro = PELIGRO_BASE * 2;
                }
                else if (distancia == 2) {//  distancia 2 -> peligro medio
                    peligro = PELIGRO_BASE * 4;
                }

                entrada.setValue(entrada.getValue() - peligro);
            }
    }
    
    private void ponerTodosLosNodosA(double coste)
    {
    	for (Map.Entry<Integer, Double> entrada : costes.entrySet())
    	{
        	entrada.setValue(coste);
        }
    }
      
    public String getName() {
    	return "MsPacMan";
    }
}
