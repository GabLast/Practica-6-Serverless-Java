package util;

public class Helper {

    public enum VariableAmbiente{

        TABLA("TABLA_RESERVAS");

        private String valor;

        VariableAmbiente(String valor) {
            this.valor = valor;
        }

        public String getValor() {
            return valor;
        }
    }

    public static String getNombreTabla(){
        return System.getenv(VariableAmbiente.TABLA.getValor());
    }
}