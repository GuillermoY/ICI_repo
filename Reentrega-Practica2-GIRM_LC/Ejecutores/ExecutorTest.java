import es.ucm.fdi.ici.c2627.practica2.GIRM_LC.*;
import pacman.controllers.*;
import pacman.Executor;
import pacman.controllers.PacmanController;
import pacman.controllers.GhostController;

/** Una partida en pantalla, con la traza de las reglas de Ms. Pac-Man en la consola. */
public class ExecutorTest {

    public static void main(String[] args) {
        Executor executor = new Executor.Builder()
                .setTickLimit(4000)
                .setVisual(true)
                .setScaleFactor(3.0)
                .build();

        PacmanController pacman = new MsPacManV3Me();
        GhostController ghosts = new GhostsV1();

        executor.runGame(pacman, ghosts, 40);   // 40 ms por turno: más, para verlo despacio
    }
}
