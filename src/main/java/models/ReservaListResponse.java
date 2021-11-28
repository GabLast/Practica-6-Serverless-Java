package models;

import java.util.List;

public class ReservaListResponse {

    private List<Reserva> reserva;
    private boolean error;
    private String mensajeError;

    public ReservaListResponse() {
    }

    public ReservaListResponse(List<Reserva> reserva, boolean error, String mensajeError) {
        this.reserva = reserva;
        this.error = error;
        this.mensajeError = mensajeError;
    }

    public List<Reserva> getReserva() {
        return reserva;
    }

    public void setReserva(List<Reserva> reserva) {
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
