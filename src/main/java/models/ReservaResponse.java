package models;

public class ReservaResponse {

    private Reserva reserva;
    private boolean error;
    private String mensajeError;

    public ReservaResponse() {
    }

    public ReservaResponse(Reserva reserva, boolean error, String mensajeError) {
        this.reserva = reserva;
        this.error = error;
        this.mensajeError = mensajeError;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }
}
