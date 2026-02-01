/*
* CommandHandler.java
*
* Handles commands sent by clients
*/

import java.net.Socket;

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
            case POST:
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

                if (x < 0 || x > serverCtx.config.getBoardWidth() ||
                    y < 0 || y > serverCtx.config.getBoardHeight()) {
                    System.out.println("POST command with out-of-bounds coordinates: (" + x + ", " + y + ")");
                    return Response.error(ErrorCode.OUT_OF_BOUNDS);
                }

                if (color == null || content == null) {
                    System.out.println("Missing arguments in POST command");
                    return Response.error(ErrorCode.INVALID_FORMAT);
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
            case GET:
                System.out.println("GET command received");
                // TODO: Implement GET command and return note or pins
                return Response.success(SuccessCode.NOTE);
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
