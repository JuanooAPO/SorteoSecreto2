package model;

/**
 * Restricción: un usuario no puede sacar a otro.
 */
public class RestriccionAsignacion {

    private Usuario origen;
    private Usuario destino;

    public RestriccionAsignacion(Usuario origen, Usuario destino) {
        this.origen = origen;
        this.destino = destino;
    }

    public boolean aplica(Usuario donante, Usuario receptor) {
        return origen.getCorreo().equals(donante.getCorreo())
                && destino.getCorreo().equals(receptor.getCorreo());
    }
}