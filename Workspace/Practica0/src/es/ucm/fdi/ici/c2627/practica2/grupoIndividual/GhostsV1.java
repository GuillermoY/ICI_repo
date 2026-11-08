package es.ucm.fdi.ici.c2627.practica2.grupoIndividual;

import java.util.EnumMap;
import pacman.controllers.GhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.DM;
import pacman.game.Game;
import es.ucm.fdi.ici.fsm.*;
import es.ucm.fdi.ici.c2627.practica2.grupoIndividual.GhostsState.FaseG;
import java.awt.Dimension;
import es.ucm.fdi.ici.fsm.observers.*;

public class GhostsV1 extends GhostController {

    private EnumMap<GHOST, MOVE> moves = new EnumMap<GHOST, MOVE>(GHOST.class);
    public static final double DISTANCIA_MINIMA_FANTASMAS = 5.0;
    public static final double DISTANCIA_PACMAN_CERCA = 10.0;

    private EnumMap<GHOST, FSM> fsms = new EnumMap<>(GHOST.class);
    private EnumMap<GHOST, GraphFSMObserver> grafos =
            new EnumMap<>(GHOST.class);
    
    private FSM fsm;
//    private FSMObserver grafo;
    
    public GhostsV1() {
        for (GHOST f : GHOST.values()) {
            fsms.put(f, construirFSM(f));
        }
    }

    private FSM construirFSM(GHOST f) {

        FSM fsm = new FSM("FSM_" + f.name());

        GraphFSMObserver grafo =
                new GraphFSMObserver("FSM_" + f.name());

        fsm.addObserver(grafo);
        grafos.put(f, grafo);

        GhostsState sPerseguir =
                new GhostsState(FaseG.PERSEGUIR, this, f);

        GhostsState sEvadir =
                new GhostsState(FaseG.EVADIR, this, f);

        GhostsState sHuida =
                new GhostsState(FaseG.HUIDA, this, f);

        fsm.add(sPerseguir,
                (Transition) in -> ((GhostsInput) in).esComestible(),
                sHuida);

        fsm.add(sEvadir,
                (Transition) in -> ((GhostsInput) in).esComestible(),
                sHuida);

        fsm.add(sHuida,
                (Transition) in -> !((GhostsInput) in).esComestible(),
                sPerseguir);

        fsm.add(sEvadir,
                (Transition) in -> ((GhostsInput) in).pacmanCerca(),
                sPerseguir);

        fsm.add(sPerseguir,
                (Transition) in ->
                        !((GhostsInput) in).pacmanCerca()
                        && ((GhostsInput) in).necesitanDispersarse(),
                sEvadir);

        fsm.ready(sPerseguir);

        // Mostrar el grafo de este fantasma
//        grafo.showInFrame(new Dimension(500, 350));

        return fsm;
    }
    
    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        moves.clear();

        for (GHOST f : GHOST.values()) {

            try {
                // Intentamos leer del motor. Si el motor no está listo, saltará al catch.
                if (!game.doesGhostRequireAction(f)) continue;
                if (game.getGhostLairTime(f) > 0) {
                    moves.put(f, MOVE.NEUTRAL);
                    continue;
                }

                int nodo = game.getGhostCurrentNodeIndex(f);
                if (nodo == -1) {
                    moves.put(f, MOVE.NEUTRAL);
                    continue;
                }

                GhostsInput in = new GhostsInput(game, f);
                MOVE mov = fsms.get(f).run(in);
                moves.put(f, mov);

            } catch (NullPointerException e) {
                // Si el motor no está inicializado (al principio de la partida),
                // ignoramos este turno y nos movemos neutral.
                moves.put(f, MOVE.NEUTRAL);
            }
        }

        return moves;
    }

    public MOVE actualizarEstado(Game game, FaseG estado, GHOST f) {
        switch (estado) {
            case HUIDA:
                return huirConCriterio(game, game.getGhostCurrentNodeIndex(f), game.getGhostLastMoveMade(f), game.getPacmanCurrentNodeIndex(), f);
            case EVADIR:
                return movimientoDispersar(game, f);
            case PERSEGUIR:
            default:
                return segunSuPapel(game, f);
        }
    }

    private MOVE segunSuPapel(Game game, GHOST f) {
        int  nodo    = game.getGhostCurrentNodeIndex(f);
        MOVE ultimo  = game.getGhostLastMoveMade(f);
        int  pacman  = game.getPacmanCurrentNodeIndex();

        if (game.getGhostEdibleTime(f) > 0) {
            return huirConCriterio(game, nodo, ultimo, pacman, f);
        }

        switch (f) {
            case BLINKY: 
                return primerMovimientoDe(game, game.getShortestPath(nodo, pacman, ultimo));
            case PINKY: {
                int cruce = proximoCruceDe(game);
                if (cruce != -1) return primerMovimientoDe(game, game.getShortestPath(nodo, cruce, ultimo));
                return primerMovimientoDe(game, game.getShortestPath(nodo, pacman, ultimo));
            }
            case INKY: {
                int[] pp = game.getActivePowerPillsIndices();
                if (pp.length > 0) {
                    int guardar = game.getClosestNodeIndexFromNodeIndex(pacman, pp, DM.PATH);
                    return primerMovimientoDe(game, game.getShortestPath(nodo, guardar, ultimo));
                }
                return primerMovimientoDe(game, game.getShortestPath(nodo, pacman, ultimo));
            }
            default: 
                if (game.getCurrentLevelTime() % 20 == 0) {
                    return movimientoAleatorioLegal(game, nodo, ultimo);
                }
                return primerMovimientoDe(game, game.getShortestPath(nodo, pacman, ultimo));
        }
    }

    private MOVE primerMovimientoDe(Game game, int[] camino) {
        if (camino == null || camino.length < 2) return MOVE.NEUTRAL;
        return game.getMoveToMakeToReachDirectNeighbour(camino[0], camino[1]);
    }
    
    private int proximoCruceDe(Game game) {
        int pacman = game.getPacmanCurrentNodeIndex();
        MOVE direccion = game.getPacmanLastMoveMade();
        if (direccion == null || direccion == MOVE.NEUTRAL) return -1;

        int actual = game.getNeighbour(pacman, direccion);
        if (actual == -1) return -1;

        MOVE movimientoAnterior = direccion;
        int limite = 0;

        while (actual != -1 && !game.isJunction(actual) && limite < 1000) {
            MOVE[] opciones = game.getPossibleMoves(actual, movimientoAnterior);
            if (opciones == null || opciones.length == 0) return -1;
            MOVE siguiente = opciones[0];
            int vecino = game.getNeighbour(actual, siguiente);
            if (vecino == -1) return -1;
            actual = vecino;
            movimientoAnterior = siguiente;
            limite++;
        }
        if (limite >= 1000 || actual == -1) return -1;
        return actual;
    }

    private MOVE huirConCriterio(Game game, int nodo, MOVE ultimo, int pacman, GHOST ghostType) {
        GHOST mejorCompanero = null;
        int menorDistancia = Integer.MAX_VALUE;

        for (GHOST otro : GHOST.values()) {
            if (otro == ghostType) continue;
            if (game.getGhostLairTime(otro) > 0) continue;
            if (game.isGhostEdible(otro)) continue;

            int nodoOtro = game.getGhostCurrentNodeIndex(otro);
            if (nodoOtro == -1) continue;

            int distancia = game.getShortestPathDistance(nodo, nodoOtro);
            if (distancia >= 0 && distancia < menorDistancia) {
                menorDistancia = distancia;
                mejorCompanero = otro;
            }
        }

        if (mejorCompanero != null) {
            int objetivo = game.getGhostCurrentNodeIndex(mejorCompanero);
            int[] camino = game.getShortestPath(nodo, objetivo, ultimo);
            return primerMovimientoDe(game, camino);
        }

        return game.getApproximateNextMoveAwayFromTarget(nodo, pacman, ultimo, DM.PATH);
    }

    private MOVE movimientoAleatorioLegal(Game game, int nodo, MOVE ultimo) {
        MOVE[] opciones = game.getPossibleMoves(nodo, ultimo);
        if (opciones == null || opciones.length == 0) return MOVE.NEUTRAL;
        return opciones[(int) (Math.random() * opciones.length)];
    }

    private MOVE movimientoDispersar(Game game, GHOST fantasma) {
        int nodo = game.getGhostCurrentNodeIndex(fantasma);
        MOVE ultimo = game.getGhostLastMoveMade(fantasma);
        MOVE[] opciones = game.getPossibleMoves(nodo, ultimo);
        if (opciones == null || opciones.length == 0) return MOVE.NEUTRAL;

        MOVE mejor = opciones[0];
        double mejorPuntuacion = Double.NEGATIVE_INFINITY;

        for (MOVE m : opciones) {
            int vecino = game.getNeighbour(nodo, m);
            if (vecino == -1) continue;
            double puntuacion = puntuacionDispersion(game, fantasma, vecino);
             
            switch (fantasma) {
                case BLINKY: if (m == MOVE.LEFT) puntuacion += 40; break;
                case PINKY:  if (m == MOVE.UP) puntuacion += 40; break;
                case INKY:   if (m == MOVE.RIGHT) puntuacion += 40; break;
                case SUE:    if (m == MOVE.DOWN) puntuacion += 40; break;
            }

            if (puntuacion > mejorPuntuacion) {
                mejorPuntuacion = puntuacion;
                mejor = m;
            }
        }
        return mejor;
    }

    private double puntuacionDispersion(Game game, GHOST fantasma, int candidato) {
        double puntuacion = 0.0;

        for (GHOST otro : GHOST.values()) {
            if (otro == fantasma || game.getGhostLairTime(otro) > 0) continue;
            int nodoOtro = game.getGhostCurrentNodeIndex(otro);
            if (nodoOtro == -1) continue;

            int distancia = game.getShortestPathDistance(candidato, nodoOtro);
            if (distancia < 0) continue;
            
            puntuacion += distancia * 10.0;
            if (distancia <= 2) puntuacion -= 500;
            else if (distancia <= 4) puntuacion -= 200;
            else if (distancia <= 6) puntuacion -= 50;
        }

        if (game.isJunction(candidato)) puntuacion += 30;

        int pacman = game.getPacmanCurrentNodeIndex();
        int distanciaPacman = game.getShortestPathDistance(candidato, pacman);
        if (distanciaPacman >= 0) puntuacion += distanciaPacman * 2.0;

        for (int powerPill : game.getActivePowerPillsIndices()) {
            int distanciaPildora = game.getShortestPathDistance(candidato, powerPill);
            if (distanciaPildora < 0) continue;
            if (distanciaPildora == 0) puntuacion -= 1000;
            else if (distanciaPildora <= 2) puntuacion -= 300;
            else if (distanciaPildora <= 4) puntuacion -= 100;
        }
        return puntuacion;
    }

    public String getName() { return "GhostsV1"; }
}