import java.util.ArrayList;

public class Response {
    public enum Type {
        SUCCESS,
        ERROR
    }

    private static final int NO_COUNT = -1;
    private Type type;
    private SuccessCode successCode;
    private ErrorCode errorCode;
    private int count;
    private String data = null;

    private Response(Type type, SuccessCode successCode, ErrorCode errorCode) {
        this.type = type;
        this.successCode = successCode;
        this.errorCode = errorCode;
        this.count = NO_COUNT;
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

    public void setData(String data, int count) {
        assert(count >= 0);
        this.data = data;
        this.count = count;
    }

    public boolean hasData() {
        return this.count != NO_COUNT;
    }


    public String serialize() {
        switch (this.type) {
            case SUCCESS:
                StringBuilder res = new StringBuilder();
                res.append("OK ").append(this.successCode.name());
                if (this.count != NO_COUNT) {
                    res.append(" ").append(this.count);
                    if (this.data != null) {
                        res.append("\n").append(this.data);
                    }
                }

                return res.toString();
            case ERROR:
                return "ERROR " + this.errorCode.name();
            default:
                return "ERROR UNKNOWN_ERROR";
        }
    }
}
