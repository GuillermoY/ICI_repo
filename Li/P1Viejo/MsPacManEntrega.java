package es.ucm.fdi.ici.c2627.practica0.grupoIndividual;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;
import pacman.game.Game;
import pacman.game.GameView;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

public class MsPacMan extends PacmanController {

  
    //  PARÁMETROS
    public static final int CERCA                       = 30;
    public static final int HOLGURA                     = 6;
    public static final int FANTASMAS_PARA_PILDORA      = 2;
    public static final int PROFUNDIDAD_SEGURA          = 3;

    public static final boolean DEBUG                   = true;

    // Pesos base
    public static final double PELIGRO_BASE             = 500.0;
    public static final double APETITO_BASE             = 100.0;
    public static final double COSTE_INALCANZABLE       = 100_000.0;
    public static final double PESO_DISTANCIA           = 1.0;
    public static final double PESO_PELIGRO             = 1.0;
    public static final double PESO_CALLEJON            = 80.0;
    public static final double PESO_POWER_PILL          = 1.5;

  
    //  ESTADO
    private int      nNodos;
    private double[] dist;          // distancia a la píldora más cercana
    private double[] peligro;       // peligro acumulado de fantasmas no comestibles
    private double[] apetito;       // atractivo de fantasmas comestibles
    private double[] coste;         // coste combinado final
    private double[] callejon;      // penalización por callejones sin salida
    private int[]    grado;         // grado de cada nodo (nº vecinos)


    @Override
    public MOVE getMove(Game game, long timeDue) {

        int nodo  = game.getPacmanCurrentNodeIndex();
        MOVE last = game.getPacmanLastMoveMade();
        MOVE[] opciones = game.getPossibleMoves(nodo, last);
        
        if (opciones.length == 1) return opciones[0];

        asegurarBuffers(game);

//        faseActual = detectarFase(game); //Detectar fase
        
        calcularDistanciasPildoras(game); //Distancias a píldoras

        calcularPeligroYApetito(game); //Peligro y apetito de fantasmas

        calcularCallejones(game); //Penalización por callejones

        combinarCoste(); //Coste combinado

        // 6) DEBUG SIEMPRE
//        if (DEBUG)
//            debugCostes(game);

        return decidirConLookahead(game, nodo, last, opciones); //Decisión
    }


//  DEBUG DE COSTES
    private void asegurarBuffers(Game game) {
        int n = game.getNumberOfNodes();
        if (dist == null || nNodos != n) {
            nNodos    = n;
            dist      = new double[n];
            peligro   = new double[n];
            apetito   = new double[n];
            coste     = new double[n];
            callejon  = new double[n];
            grado     = new int[n];
            precomputarGrado(game);
        }
    }

    private void precomputarGrado(Game game) {
        for (int i = 0; i < nNodos; i++) {
            int g = 0;
            for (MOVE m : MOVE.values()) {
                if (game.getNeighbour(i, m) != -1) g++;
            }
            grado[i] = g;
        }
    }

    // =========================================================
    //  DISTANCIAS A PÍLDORAS
    // =========================================================
    private void calcularDistanciasPildoras(Game game) {
        Arrays.fill(dist, COSTE_INALCANZABLE);

        Deque<Integer> cola = new ArrayDeque<>();
        
        for (int p : game.getActivePillsIndices()) {
            dist[p] = 0;
            cola.add(p);
        }
        for (int p : game.getActivePowerPillsIndices()) {
            dist[p] = 0;
            cola.add(p);
        }

        boolean hayPildoras = game.getActivePillsIndices().length > 0;
        if (!hayPildoras) { Arrays.fill(dist, 0); return; }

        recorrerAnchura(game, cola, dist);
    }

    private void recorrerAnchura(Game game, Deque<Integer> cola, double[] d) {
        while (!cola.isEmpty()) {
            int u = cola.poll();
            double du = d[u];
            for (MOVE m : MOVE.values()) {
                int v = game.getNeighbour(u, m);
                if (v == -1) continue;
                if (du + 1 < d[v]) {
                    d[v] = du + 1;
                    cola.add(v);
                }
            }
        }
    }
    
private boolean esInterseccion(int nodo) {
    return grado[nodo] >= 2;
}

private double costeInterseccion(Game game, int interseccion) {

    if (!esInterseccion(interseccion)) return 0.0;

    int nodoPacman = game.getPacmanCurrentNodeIndex();

    double peligroInterseccion = 0.0;

    for (GHOST g : GHOST.values()) {

        if (game.getGhostLairTime(g) > 0) continue;

        if (game.isGhostEdible(g)) continue;

        int nodoFantasma = game.getGhostCurrentNodeIndex(g);

        if (nodoFantasma == -1) continue;

        int tiempoPacman =
                game.getShortestPathDistance( nodoPacman, interseccion, game.getPacmanLastMoveMade());

        int tiempoFantasma =
                game.getShortestPathDistance(nodoFantasma, interseccion, game.getGhostLastMoveMade(g));

        if (tiempoPacman < 0 || tiempoFantasma < 0) continue;

        int margen = tiempoFantasma - tiempoPacman;

        if (margen <= HOLGURA) {
            if (margen <= 0) {
            	 //Si se llega antes o no
            	peligroInterseccion += PELIGRO_BASE * 5.0;
            } else {
            	// Llega después, pero con poco margen
                peligroInterseccion += PELIGRO_BASE * (HOLGURA + 1 - margen);
            }
        }
    }

    return peligroInterseccion;
}

    // =========================================================
    //  PELIGRO Y APETITO
    // =========================================================
    private void calcularPeligroYApetito(Game game) {
        Arrays.fill(peligro, 0.0);
        Arrays.fill(apetito, 0.0);

        for (GHOST g : GHOST.values()) {
            if (game.getGhostLairTime(g) > 0) continue;
            int ng = game.getGhostCurrentNodeIndex(g);
            if (ng == -1) continue;

            boolean comestible = game.isGhostEdible(g);

            for (int nodo = 0; nodo < nNodos; nodo++) {
                int d = game.getShortestPathDistance(nodo, ng);
                if (d < 0 || d > CERCA) continue;

                if (comestible) {
                    double t = game.getGhostEdibleTime(g);
                    double factorTiempo = Math.min(1.0, t / 40.0);
                    // Atrae hacia el fantasma, especialmente en fase CAZA
                    double peso = 2.0;
                    apetito[nodo] += peso * APETITO_BASE * factorTiempo / (d + 1);
                } else {
                    double multiplicador = 1.0;
                    MOVE dirG = game.getGhostLastMoveMade(g);
                    if (dirG != null) {
                        int sig = game.getNeighbour(ng, dirG);
                        if (sig == nodo) multiplicador = 1.5;
                    }
                    peligro[nodo] += PELIGRO_BASE * multiplicador / ((d + 1) * (d + 1));
                }
            }
        }
     // Seguridad de intersecciones
     for (int nodo = 0; nodo < nNodos; nodo++) {

         if (!esInterseccion(nodo))
             continue;

         peligro[nodo] += costeInterseccion(game, nodo);
         }
    }

    //  CALLEJONES SIN SALIDA
    private void calcularCallejones(Game game) {
        Arrays.fill(callejon, 0.0);
        // Penaliza nodos de grado 1 ó 2 que además estén cerca de un fantasma no comestible.
        for (int i = 0; i < nNodos; i++) {
            if (grado[i] > 2) continue;
            double p = peligro[i];
            if (p <= 0) continue;
            // Cuanto más "encerrado" y más peligroso, más penalización
            callejon[i] = PESO_CALLEJON * p * (3 - grado[i]) / PELIGRO_BASE;
        }
    }
    
//  COSTE COMBINADO SEGÚN FASE (Maquina de estados)
    private void combinarCoste() {

        for (int i = 0; i < nNodos; i++) {
            coste[i] = PESO_DISTANCIA * dist[i]
                     + PESO_PELIGRO * peligro[i]
                     - apetito[i]
                     + callejon[i];
        }
    }

    //  DECISIÓN CON LOOKAHEAD
    private MOVE decidirConLookahead(Game game, int nodo, MOVE ultimo, MOVE[] opciones) {
        MOVE mejor = opciones[0];
        double mejorCoste = Double.MAX_VALUE;

        for (MOVE m : opciones) {
            int v = game.getNeighbour(nodo, m);
            if (v == -1) continue;
            double c = costeDe(game, v, m, PROFUNDIDAD_SEGURA);
            if (c < mejorCoste) {
                mejorCoste = c;
                mejor = m;
            }
        }
        return mejor;
    }

    private double costeDe(Game game, int nodo, MOVE ultimo, int prof) { // Devulve el coste del nodo especificado
        double local = coste[nodo];
        if (prof <= 0) return local;

        MOVE[] ops = game.getPossibleMoves(nodo, ultimo);
        if (ops == null || ops.length == 0) return local;

        double mejorFuturo = Double.MAX_VALUE;
        for (MOVE m : ops) {
            int v = game.getNeighbour(nodo, m);
            if (v == -1) continue;
            double c = costeDe(game, v, m, prof - 1);
            if (c < mejorFuturo) mejorFuturo = c;
        }
        if (mejorFuturo == Double.MAX_VALUE) mejorFuturo = local;

        // En HUIDA, priorizar el corto plazo (huir ya)
        double pesoLocal = 0.5;
        return pesoLocal * local + (1.0 - pesoLocal) * mejorFuturo;
    }

    @Override
    public String getName() { return "MsPacMan"; }
}