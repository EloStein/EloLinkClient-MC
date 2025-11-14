package elolink.de.client.errors;

public class Error {

    public static void withCode(Errors code){
        ErrorWindow existingErrorWindow = ErrorWindow.getInstanceWithCode(code);
        if (existingErrorWindow == null){
            ErrorWindow errorWindow = new ErrorWindow();
            errorWindow.runWindow(code, null);
        }
    }

    public static void withCode(Errors code, String note){
        ErrorWindow existingErrorWindow = ErrorWindow.getInstanceWithCode(code);
        if (existingErrorWindow == null){
            ErrorWindow errorWindow = new ErrorWindow();
            errorWindow.runWindow(code, note);
        }
    }
}
