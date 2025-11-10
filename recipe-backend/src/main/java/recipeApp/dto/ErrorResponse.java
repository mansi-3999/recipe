package recipeApp.dto;

public class ErrorResponse {
    public int status;
    public String code;
    public String message;

    public ErrorResponse(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
