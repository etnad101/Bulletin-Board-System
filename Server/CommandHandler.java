/*
* CommandHandler.java
*
* Handles commands sent by clients
*/

import java.net.Socket;
import java.util.ArrayList;

public class CommandHandler {
    private ServerCtx serverCtx;

    public CommandHandler(ServerCtx serverCtx) {
        this.serverCtx = serverCtx;
    }

    private boolean isValidColor(String color) {
        for (String allowedColor : serverCtx.config.getAllowedColors()) {
            if (allowedColor.equalsIgnoreCase(color)) {
                return true;
            }
        }
        return false;
    }

    private Response handle_post(int x, int y, String color, String content) {
        if (x < 0 || x > serverCtx.config.getBoardWidth() ||
            y < 0 || y > serverCtx.config.getBoardHeight()) {
            System.out.println("POST command with out-of-bounds coordinates: (" + x + ", " + y + ")");
            return Response.error(ErrorCode.OUT_OF_BOUNDS);
        }

        if (!isValidColor(color)) {
            System.out.println("Invalid color in POST command: " + color);
            return Response.error(ErrorCode.COLOR_NOT_SUPPORTED);
        }

        Note note = new Note(
            x,
            y,
            color,
            content
        );

        this.serverCtx.state.addNote(note);
        return Response.success(SuccessCode.NOTE_POSTED);
    }

    private Response handleGet() {
        Response response = Response.success(SuccessCode.NOTE);
        ArrayList<Note> notes = this.serverCtx.state.getNotes(); 
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Note note : notes) {
            sb.append(note.serialize());
            sb.append('\n');
            count++;
        }
        response.setData(sb.toString(), count);
        return response; 
    }

    private Response handleGetPins() {
        return Response.success(SuccessCode.PINS);
    }

    public Response handleCommand(String commandString) {
        // Parse command string into more accessible format
        Command command = new Command(commandString);

        // Check for invalid command
        if (command.getType() == null) {
            System.out.println("Invalid command received: " + commandString);
            return Response.error(ErrorCode.INVALID_FORMAT);
        }

        // Handle command based on type
        System.out.println("Handling command: " + command.getType());
        for (int i = 0; i < command.getArgc(); i++) {
            System.out.println("Arg " + i + ": " + command.getArgv()[i]);
        }
        switch (command.getType()) {
            case POST: {

                System.out.println("POST command received");
                int x;
                int y;
                String color; 
                String content; 

                try {
                    x = Integer.parseInt(command.popArgv());
                    y = Integer.parseInt(command.popArgv());
                    color = command.popArgv();
                    content = command.consumeArgv();
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing POST command arguments");
                    return Response.error(ErrorCode.INVALID_FORMAT);
                }

                if (color == null || content == null) {
                    System.out.println("Missing arguments in POST command");
                    return Response.error(ErrorCode.INVALID_FORMAT);
                }

                return handle_post(x, y, color, content);
            }
            case GET: {

                String arg1 = command.popArgv();

                if (arg1 == null) {
                    System.out.println("Blank GET command received");
                    return handleGet();
                } 

                if (arg1.equals(new String("PINS"))) {
                    System.out.println("GET PINS command received");
                    return handleGetPins();
                } else {
                    System.out.println("General GET command received");
                }

                return handleGet();
            }
            case PIN:
                System.out.println("PIN command received");
                return Response.success(SuccessCode.PIN_ADDED);
            case UNPIN:
                System.out.println("UNPIN command received");
                return Response.success(SuccessCode.PIN_REMOVED);
            case SHAKE:
                System.out.println("SHAKE command received");
                return Response.success(SuccessCode.SHAKE_COMPLETE);
            case CLEAR:
                System.out.println("CLEAR command received");
                return Response.success(SuccessCode.CLEARED);
            case DISCONNECT:
                System.out.println("DISCONNECT command received");
                return Response.success(SuccessCode.DISCONNECTED);
            default:
                System.out.println("Invalid command received: " + commandString);
                return Response.error(ErrorCode.INVALID_FORMAT);
        }
    }
}
