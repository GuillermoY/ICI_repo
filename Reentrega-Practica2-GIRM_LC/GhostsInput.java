package es.ucm.fdi.ici.c2627.practica2.grupoIndividual;

import es.ucm.fdi.ici.Input;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

public class GhostsInput extends Input {

    public static final double DISTANCIA_PACMAN_CERCA = 10.0;

    private final GHOST fantasma;
    private boolean esComestible;
    private boolean pacmanCerca;
    private boolean necesitanDispersarse;

    public GhostsInput(Game game, GHOST fantasma) {
        super(game);
        this.fantasma = fantasma;
    }

    public GHOST getFantasma() { return fantasma; }

    @Override
 public void parseInput() {
        try {
            // Comprobación de seguridad inicial
            if (game.getGhostLairTime(fantasma) > 0) {
                this.esComestible = false;
                this.pacmanCerca = false;
                this.necesitanDispersarse = false;
                return;
            }

            int nodo = game.getGhostCurrentNodeIndex(fantasma);
            int pacman = game.getPacmanCurrentNodeIndex();

            // Seguridad por si acaso el nodo sigue siendo -1
            if (nodo == -1) {
                this.esComestible = false;
                this.pacmanCerca = false;
                this.necesitanDispersarse = false;
                return;
            }

            this.esComestible = game.isGhostEdible(fantasma);

            int distancia = game.getShortestPathDistance(nodo, pacman);
            this.pacmanCerca = (distancia >= 0 && distancia <= DISTANCIA_PACMAN_CERCA);

            this.necesitanDispersarse = evaluarDispersion(game);

        } catch (NullPointerException e) {
            // Si el motor falla (no inicializado), valores seguros por defecto
            this.esComestible = false;
            this.pacmanCerca = false;
            this.necesitanDispersarse = false;
        }
    }

    private boolean evaluarDispersion(Game game) {
        int activos = 0;
        for (GHOST f : GHOST.values()) {
            if (game.getGhostLairTime(f) > 0) continue;
            if (game.getGhostCurrentNodeIndex(f) == -1) continue;
            activos++;
        }
        if (activos < 2) return false;

        for (GHOST f1 : GHOST.values()) {
            if (game.getGhostLairTime(f1) > 0) continue;
            int n1 = game.getGhostCurrentNodeIndex(f1);
            if (n1 == -1) continue;

            for (GHOST f2 : GHOST.values()) {
                if (f1 == f2 || game.getGhostLairTime(f2) > 0) continue;
                int n2 = game.getGhostCurrentNodeIndex(f2);
                if (n2 == -1) continue;

                int distancia = game.getShortestPathDistance(n1, n2);
                if (distancia >= 0 && distancia < 5.0) return true;
            }
        }
        return false;
    }

    public boolean esComestible() { return esComestible; }
    public boolean pacmanCerca() { return pacmanCerca; }
    public boolean necesitanDispersarse() { return necesitanDispersarse; }
}