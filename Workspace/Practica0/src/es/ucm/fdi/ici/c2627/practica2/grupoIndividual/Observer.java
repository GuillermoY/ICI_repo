package es.ucm.fdi.ici.c2627.practica2.grupoIndividual;
import es.ucm.fdi.ici.fsm.FSMObserver;

public class Observer implements FSMObserver {

    private String nombre;

    public Observer(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void fsmAdd(String sourceState, String transition, String targetState) {
        // Opcional: System.out.println("Añadida transicion: " + sourceState + " -> " + targetState);
    }

    @Override
    public void fsmReady(String initialState) {
        System.out.println("[" + nombre + "] FSM LISTA en estado: " + initialState);
    }

    @Override
    public void fsmTransition(String sourceState, String transition, String targetState) {
        System.out.println("[" + nombre + "] CAMBIO DE ESTADO: " + sourceState + " -> " + targetState);
    }

    @Override
    public void fsmReset(String initialState) {
        System.out.println("[" + nombre + "] FSM RESET a: " + initialState);
    }
}