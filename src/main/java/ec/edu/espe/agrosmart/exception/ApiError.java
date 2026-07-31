package ec.edu.espe.agrosmart.exception;

public class ApiError {

    private final ErrorCode codigo;
    private final String mensaje;

    public ApiError(ErrorCode codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public ErrorCode getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }
}
