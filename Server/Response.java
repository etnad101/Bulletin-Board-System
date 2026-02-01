public class Response {
    public enum Type {
        SUCCESS,
        ERROR
    }

    private Type type;
    private SuccessCode successCode;
    private ErrorCode errorCode;

    private Response(Type type, SuccessCode successCode, ErrorCode errorCode) {
        this.type = type;
        this.successCode = successCode;
        this.errorCode = errorCode;
    }

    public static Response success(SuccessCode code) {
        return new Response(Type.SUCCESS, code, null);
    }

    public static Response error(ErrorCode code) {
        return new Response(Type.ERROR, null, code);
    }

    public Type getType() {
        return type;
    }

    public SuccessCode getSuccessCode() {
        return successCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String serialize() {
        switch (this.type) {
            case SUCCESS:
                return "OK " + this.successCode.name();
            case ERROR:
                return "ERROR " + this.errorCode.name();
            default:
                return "ERROR UNKNOWN_ERROR";
        }
    }
}
