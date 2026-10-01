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


public class MsPacMan extends PacmanController {

    // =========================================================
    //  PARÁMETROS
    // =========================================================
    public static final int CERCA                       = 30;
    public static final int HOLGURA                     = 6;
    public static final int FANTASMAS_PARA_PILDORA      = 3;
    public static final int PROFUNDIDAD_SEGURA          = 3;

    public static final boolean DEBUG                   = true;

    // Pesos base
    public static final double PELIGRO_BASE             = 500.0;
    public static final double APETITO_BASE             = 300.0;
    public static final double COSTE_INALCANZABLE       = 100_000.0;
    public static final double PESO_DISTANCIA           = 1.0;
    public static final double PESO_PELIGRO             = 1.0;
    public static final double PESO_CALLEJON            = 80.0;
    public static final double PESO_POWER_PILL          = 1.5;

    // Umbrales de color para debug
    private static final double UMBRAL_VERDE            = 15.0;
    private static final double UMBRAL_AMARILLO         = 40.0;
    private static final double UMBRAL_NARANJA          = 120.0;

    
 // Elementos de debug que ya se han dibujado
    private Set<Integer> nodosPintados = new HashSet<>();
    private Set<String> lineasPintadas = new HashSet<>();

    // =========================================================
    //  FASES DE ESTRATEGIA
    // =========================================================
    private enum Fase {
        CAZA,       // hay fantasmas comestibles -> perseguirlos
        POWER,      // power pill disponible y fantasmas no comestibles cerca -> ir a por ella
        HUIDA,      // fantasmas no comestibles muy cerca -> sobrevivir
        NORMAL      // comer píldoras normales
    }

    // =========================================================
    //  ESTADO
    // =========================================================
    private int      nNodos;
    private double[] dist;          // distancia a la píldora más cercana
    private double[] peligro;       // peligro acumulado de fantasmas no comestibles
    private double[] apetito;       // atractivo de fantasmas comestibles
    private double[] coste;         // coste combinado final
    private double[] callejon;      // penalización por callejones sin salida
    private int[]    grado;         // grado de cada nodo (nº vecinos)

    private Fase faseActual = Fase.NORMAL;

    // =========================================================
    //  API
    // =========================================================
    @Override
    public MOVE getMove(Game game, long timeDue) {

        int nodo  = game.getPacmanCurrentNodeIndex();
        MOVE last = game.getPacmanLastMoveMade();
        MOVE[] opciones = game.getPossibleMoves(nodo, last);

        if (opciones.length == 1) return opciones[0];

        asegurarBuffers(game);

        // 1) Detectar fase
        faseActual = detectarFase(game);

        // 2) Distancias a píldoras
        calcularDistanciasPildoras(game, faseActual);

        // 3) Peligro y apetito de fantasmas
        calcularPeligroYApetito(game, faseActual);

        // 4) Penalización por callejones
        calcularCallejones(game);

        // 5) Coste combinado según fase
        combinarCoste(faseActual);

        // 6) Decisión con lookahead
        MOVE decision = decidirConLookahead(game, nodo, last, opciones);

        // 7) Debug
        if (DEBUG) dibujarDecisiones(game, nodo, last, decision);

        	
        return decision;
    }

    // =========================================================
    //  BUFFERS
    // =========================================================
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
    //  DETECCIÓN DE FASE
    // =========================================================
    private Fase detectarFase(Game game) {

        int nodoPacman = game.getPacmanCurrentNodeIndex();

        boolean hayComestible = false;
        int     fantasmasCercaNoComestibles = 0;
        int     comestiblesCerca = 0;

        for (GHOST g : GHOST.values()) {
            if (game.getGhostLairTime(g) > 0) continue;
            int ng = game.getGhostCurrentNodeIndex(g);
            if (ng == -1) continue;

            int d = game.getShortestPathDistance(nodoPacman, ng);
            boolean comestible = game.isGhostEdible(g);

            if (comestible) {
                hayComestible = true;
                if (d >= 0 && d <= CERCA) comestiblesCerca++;
            } else {
                if (d >= 0 && d <= HOLGURA) fantasmasCercaNoComestibles++;
            }
        }

        // 1) Si hay fantasmas comestibles y están razonablemente cerca -> CAZA
        if (hayComestible && comestiblesCerca > 0) return Fase.CAZA;

        // 2) Si hay fantasmas no comestibles pegados -> HUIDA
        if (fantasmasCercaNoComestibles > 0) return Fase.HUIDA;

        // 3) Si quedan power pills y hay varios fantasmas rondando -> POWER
        int powerPillsRestantes = game.getActivePowerPillsIndices().length;
        int fantasmasCerca = contarFantasmasCerca(game, nodoPacman, CERCA);
        if (powerPillsRestantes > 0 && fantasmasCerca >= FANTASMAS_PARA_PILDORA)
            return Fase.POWER;

        return Fase.NORMAL;
    }

    private int contarFantasmasCerca(Game game, int nodo, int radio) {
        int c = 0;
        for (GHOST g : GHOST.values()) {
            if (game.getGhostLairTime(g) > 0) continue;
            int ng = game.getGhostCurrentNodeIndex(g);
            if (ng == -1) continue;
            int d = game.getShortestPathDistance(nodo, ng);
            if (d >= 0 && d <= radio) c++;
        }
        return c;
    }

    // =========================================================
    //  DISTANCIAS A PÍLDORAS (BFS multi-fuente)
    // =========================================================
    private void calcularDistanciasPildoras(Game game, Fase fase) {
        Arrays.fill(dist, COSTE_INALCANZABLE);

        // En fase CAZA, el "objetivo" son los fantasmas comestibles (BFS desde ellos)
        if (fase == Fase.CAZA) {
            Deque<Integer> cola = new ArrayDeque<>();
            for (GHOST g : GHOST.values()) {
                if (!game.isGhostEdible(g)) continue;
                if (game.getGhostLairTime(g) > 0) continue;
                int ng = game.getGhostCurrentNodeIndex(g);
                if (ng == -1) continue;
                dist[ng] = 0;
                cola.add(ng);
            }
            bfs(game, cola, dist);
            return;
        }

        // POWER -> priorizar power pills sobre píldoras normales
        Deque<Integer> cola = new ArrayDeque<>();

        if (fase == Fase.POWER) {
            for (int p : game.getActivePowerPillsIndices()) {
                dist[p] = 0;
                cola.add(p);
            }
            bfs(game, cola, dist);
            // Añadimos también píldoras normales, pero con menor prioridad
            // (no hacemos nada: la BFS desde power pills ya cubre el mapa)
            return;
        }

        // NORMAL / HUIDA -> cualquier píldora
        for (int p : game.getActivePillsIndices()) {
            dist[p] = 0;
            cola.add(p);
        }
        for (int p : game.getActivePowerPillsIndices()) {
            dist[p] = 0;
            cola.add(p);
        }

        boolean hayPildoras =
                game.getActivePillsIndices().length > 0;
                if (!hayPildoras) { Arrays.fill(dist, 0); return; }

        bfs(game, cola, dist);
    }

    private void bfs(Game game, Deque<Integer> cola, double[] d) {
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
    
 // =========================================================
//  SEGURIDAD DE INTERSECCIONES
// =========================================================

/**
 * Comprueba si Pac-Man puede llegar a un nodo antes que
 * un fantasma concreto.
 *
 * @param game estado actual
 * @param interseccion nodo que queremos comprobar
 * @param fantasma fantasma que queremos evitar
 * @return true si Pac-Man llega con suficiente margen
 */
private boolean pacmanLlegaAntes(
        Game game,
        int interseccion,
        GHOST fantasma) {

    int nodoPacman = game.getPacmanCurrentNodeIndex();
    int nodoFantasma = game.getGhostCurrentNodeIndex(fantasma);

    if (nodoFantasma == -1)
        return true;

    // Distancia de Pac-Man a la intersección
    int distanciaPacman =
            game.getShortestPathDistance(
                    nodoPacman,
                    interseccion, game.getPacmanLastMoveMade());

    // Distancia del fantasma a la intersección
    int distanciaFantasma =
            game.getShortestPathDistance(
                    nodoFantasma,
                    interseccion, game.getGhostLastMoveMade(fantasma));

    if (distanciaPacman < 0 || distanciaFantasma < 0)
        return false;

    return distanciaPacman + HOLGURA < distanciaFantasma;
}

private boolean esInterseccion(int nodo) {
    return grado[nodo] >= 2;
}

private boolean interseccionSegura(Game game, int interseccion) {
	
    if (!esInterseccion(interseccion))
        return true;

    for (GHOST g : GHOST.values()) {

        // Fantasma en la guarida
        if (game.getGhostLairTime(g) > 0)
            continue;

        int nodoFantasma = game.getGhostCurrentNodeIndex(g);

        if (nodoFantasma == -1)
            continue;

        // Si es comestible no supone peligro
        if (game.isGhostEdible(g))
            continue;

        if (!pacmanLlegaAntes(game, interseccion, g)) {

            return false;
        }
    }

    return true;
}
/**
 * Devuelve un coste de seguridad para una intersección.
 *
 * > 0  = peligro
 * <= 0 = suficientemente segura
 */
private double costeInterseccion(
        Game game,
        int interseccion) {

    if (!esInterseccion(interseccion))
        return 0.0;

    int nodoPacman =
            game.getPacmanCurrentNodeIndex();

    double peligroInterseccion = 0.0;

    for (GHOST g : GHOST.values()) {
    	
        if (game.getGhostLairTime(g) > 0)
            continue;

        if (game.isGhostEdible(g))
            continue;

        int nodoFantasma =
                game.getGhostCurrentNodeIndex(g);

        if (nodoFantasma == -1)
            continue;

        int tiempoPacman =
                game.getShortestPathDistance(
                        nodoPacman,
                        interseccion);

        int tiempoFantasma =
                game.getShortestPathDistance(
                        nodoFantasma,
                        interseccion);

        if (tiempoPacman < 0 ||
            tiempoFantasma < 0)
            continue;

        int margen =
                tiempoFantasma - tiempoPacman;

        /*
         * Si el fantasma llega antes o prácticamente
         * al mismo tiempo, la intersección es peligrosa.
         */
        if (margen <= HOLGURA) {

            double peligro;

            if (margen <= 0) {
                // El fantasma llega antes
                peligro = PELIGRO_BASE * 5.0;
            }
            else {
                // Llega después, pero demasiado justo
                peligro =
                        PELIGRO_BASE *
                        (HOLGURA + 1 - margen);
            }

            peligroInterseccion += peligro;
        }
    }

    return peligroInterseccion;
}


    // =========================================================
    //  PELIGRO Y APETITO
    // =========================================================
    private void calcularPeligroYApetito(Game game, Fase fase) {
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
                    double peso = (fase == Fase.CAZA) ? 2.0 : 1.0;
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
     // ---------------------------------------------------------
     // Seguridad de intersecciones
     // ---------------------------------------------------------

     for (int nodo = 0; nodo < nNodos; nodo++) {

         if (!esInterseccion(nodo))
             continue;

         peligro[nodo] += costeInterseccion(
                 game,
                 nodo);
     }
    }

    // =========================================================
    //  CALLEJONES SIN SALIDA
    // =========================================================
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

    // =========================================================
    //  COSTE COMBINADO SEGÚN FASE
    // =========================================================
    private void combinarCoste(Fase fase) {
        switch (fase) {
            case CAZA:
                // El objetivo es acercarse al fantasma comestible, evitar los no comestibles
                for (int i = 0; i < nNodos; i++) {
                    coste[i] = PESO_DISTANCIA * dist[i]
                             + PESO_PELIGRO * peligro[i]
                             - apetito[i] * 2.0
                             + callejon[i];
                }
                break;

            case POWER:
                // Priorizar power pills (ya están en dist), pero sin descuidar peligro
                for (int i = 0; i < nNodos; i++) {
                    coste[i] = PESO_DISTANCIA * dist[i] / PESO_POWER_PILL
                             + PESO_PELIGRO * peligro[i] * 1.2
                             - apetito[i]
                             + callejon[i];
                }
                break;

            case HUIDA:
                // Huir: el peligro domina, la distancia a píldoras pasa a segundo plano
                for (int i = 0; i < nNodos; i++) {
                    coste[i] = 0.3 * dist[i]
                             + 3.0 * peligro[i]
                             - apetito[i]
                             + 2.0 * callejon[i];
                }
                break;

            case NORMAL:
            default:
                for (int i = 0; i < nNodos; i++) {
                    coste[i] = PESO_DISTANCIA * dist[i]
                             + PESO_PELIGRO * peligro[i]
                             - apetito[i]
                             + callejon[i];
                }
                break;
        }
    }

    // =========================================================
    //  DECISIÓN CON LOOKAHEAD
    // =========================================================
    private MOVE decidirConLookahead(Game game, int nodo, MOVE ultimo, MOVE[] opciones) {
        MOVE mejor = opciones[0];
        double mejorCoste = Double.MAX_VALUE;

        for (MOVE m : opciones) {
            int v = game.getNeighbour(nodo, m);
            if (v == -1) continue;
            double c = costeDe(game, v, m, PROFUNDIDAD_SEGURA);
            if (c < mejorCoste && interseccionSegura(game, v)) {
                mejorCoste = c;
                mejor = m;
            }
        }
        return mejor;
    }

    private double costeDe(Game game, int nodo, MOVE ultimo, int prof) {
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
        double pesoLocal = (faseActual == Fase.HUIDA) ? 0.7 : 0.5;
        return pesoLocal * local + (1.0 - pesoLocal) * mejorFuturo;
    }

    
    private void dibujarDecisiones(
            Game game,
            int nodoInicial,
            MOVE ultimo,
            MOVE decision) {

        // =========================================================
        // 1. DIBUJAR TODOS LOS NODOS SEGÚN SU COSTE
        // =========================================================

        for (int nodo = 0; nodo < nNodos; nodo++) {

            double valorCoste = coste[nodo];

            Color color;

            if (valorCoste <= UMBRAL_VERDE) {
                color = Color.GREEN;
            }
            else if (valorCoste <= UMBRAL_AMARILLO) {
                color = Color.YELLOW;
            }
            else if (valorCoste <= UMBRAL_NARANJA) {
                color = Color.ORANGE;
            }
            else {
                color = Color.RED;
            }

            // No volver a pintar el mismo nodo
            if (!nodosPintados.contains(nodo)) {

                GameView.addPoints(
                    game,
                    color,
                    nodo
                );

                nodosPintados.add(nodo);
            }
        }


        // =========================================================
        // 2. MARCAR LAS OPCIONES INMEDIATAS
        // =========================================================

        MOVE[] opciones =
            game.getPossibleMoves(nodoInicial, ultimo);

        if (opciones == null || opciones.length == 0) {
            return;
        }

        int mejorVecino =
            game.getNeighbour(nodoInicial, decision);


        for (MOVE movimiento : opciones) {

            int vecino =
                game.getNeighbour(nodoInicial, movimiento);

            if (vecino == -1)
                continue;


            // -----------------------------------------------------
            // Mejor opción
            // -----------------------------------------------------

            if (vecino == mejorVecino) {

                String claveLinea =
                    nodoInicial + "-" + vecino + "-GREEN";

                if (!lineasPintadas.contains(claveLinea)) {

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

                    lineasPintadas.add(claveLinea);
                }
            }

            // -----------------------------------------------------
            // Alternativas
            // -----------------------------------------------------

            else {

                String claveNodo =
                    "BLUE-" + vecino;

                if (!lineasPintadas.contains(claveNodo)) {

                    GameView.addPoints(
                        game,
                        Color.BLUE,
                        vecino
                    );

                    lineasPintadas.add(claveNodo);
                }
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
                game.getPossibleMoves(
                    actual,
                    movimientoAnterior
                );

            if (posibles == null || posibles.length == 0)
                break;


            int siguiente =
                obtenerMejorVecino(
                    game,
                    actual,
                    posibles
                );

            if (siguiente == -1)
                break;


            // -----------------------------------------------------
            // Línea magenta
            // -----------------------------------------------------

            String claveLinea =
                actual + "-" + siguiente + "-MAGENTA";

            if (!lineasPintadas.contains(claveLinea)) {

                GameView.addLines(
                    game,
                    Color.MAGENTA,
                    actual,
                    siguiente
                );

                GameView.addPoints(
                    game,
                    Color.MAGENTA,
                    siguiente
                );

                lineasPintadas.add(claveLinea);
            }


            // -----------------------------------------------------
            // Actualizar dirección
            // -----------------------------------------------------

            MOVE siguienteMovimiento =
                game.getMoveToMakeToReachDirectNeighbour(
                    actual,
                    siguiente
                );

            movimientoAnterior = siguienteMovimiento;
            actual = siguiente;
        }
    }
    
    
    private int obtenerMejorVecino(
            Game game,
            int nodo,
            MOVE[] opciones) {

        int mejorNodo = -1;
        double mejorCoste = Double.MAX_VALUE;

        for (MOVE movimiento : opciones) {

            int vecino =
                game.getNeighbour(nodo, movimiento);

            if (vecino == -1)
                continue;

            double valorCoste = coste[vecino];

            if (valorCoste < mejorCoste) {

                mejorCoste = valorCoste;
                mejorNodo = vecino;
            }
        }

        return mejorNodo;
    }
    
    @Override
    public String getName() { return "MsPacMan"; }
}