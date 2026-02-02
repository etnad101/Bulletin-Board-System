/*
* CommandHandler.java
*
* Handles commands sent by clients
*/

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

    private boolean isWithinBounds(int x, int y) {
        return (x >= 0 && x <= serverCtx.config.getBoardWidth() &&
                y >= 0 && y <= serverCtx.config.getBoardHeight());
    }

    private int[] parseCoordinates(Command command) {
        int x;
        int y;

        try {
            x = Integer.parseInt(command.popArgv());
            y = Integer.parseInt(command.popArgv());
        } catch (NumberFormatException e) {
            return null;
        }

        return new int[] { x, y };
    }

    private Response handle_post(int x, int y, String color, String content) {

        Note note = new Note(
            x,
            y,
            serverCtx.config.getNoteWidth(),
            serverCtx.config.getNoteHeight(),
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
            sb.append(note);
            sb.append('\n');
            count++;
        }
        response.setData(sb.toString(), count);
        return response; 
    }

    private Response handleGetPins() {
        ArrayList<Pin> pins = this.serverCtx.state.getPins();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Pin pin : pins) {  
            sb.append("PIN ");
            sb.append(pin.getX());
            sb.append(" ");
            sb.append(pin.getY());
            sb.append('\n');
            count++;
        }
        Response response = Response.success(SuccessCode.PINS);
        response.setData(sb.toString(), count);
        return response;
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
        System.out.println("Incoming command: " + command.getType());
        for (int i = 0; i < command.getArgc(); i++) {
            System.out.println("Arg " + i + ": " + command.getArgv()[i]);
        }

        switch (command.getType()) {
            case POST: {
                System.out.println("POST command received");
                String color; 
                String content; 

                int[] coords = parseCoordinates(command);
                if (coords == null) {
                    return Response.error(ErrorCode.INVALID_FORMAT);
                }

                color = command.popArgv();
                content = command.consumeArgv();

                if (color == null || content == null) {
                    return Response.error(ErrorCode.INVALID_FORMAT);
                }

                if (!isWithinBounds(coords[0], coords[1])) {
                    return Response.error(ErrorCode.OUT_OF_BOUNDS);
                }

                if (!isValidColor(color)) {
                    return Response.error(ErrorCode.COLOR_NOT_SUPPORTED);
                }

                return handle_post(coords[0], coords[1], color, content);
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
                }

                System.out.println("General GET command received");
                return handleGet();
            }

            case PIN: {
                System.out.println("PIN command received");

                int[] coords = parseCoordinates(command);
                if (coords == null) {
                    return Response.error(ErrorCode.INVALID_FORMAT);
                }

                if (!isWithinBounds(coords[0], coords[1])) {
                    return Response.error(ErrorCode.OUT_OF_BOUNDS);
                }

                serverCtx.state.addPin(coords[0], coords[1]); 
                return Response.success(SuccessCode.PIN_ADDED);
            }

            case UNPIN: {
                System.out.println("UNPIN command received");

                int[] coords = parseCoordinates(command);
                if (coords == null) {
                    return Response.error(ErrorCode.INVALID_FORMAT);
                }

                if (!isWithinBounds(coords[0], coords[1])) {
                    return Response.error(ErrorCode.OUT_OF_BOUNDS);
                }

                serverCtx.state.removePin(coords[0], coords[1]); 
                return Response.success(SuccessCode.PIN_REMOVED);
            }

            case SHAKE:
                System.out.println("SHAKE command received");
                if (command.getArgc() != 0) {
                    return Response.error(ErrorCode.INVALID_FORMAT);
                }
                serverCtx.state.shake();
                return Response.success(SuccessCode.SHAKE_COMPLETE);

            case CLEAR:
                System.out.println("CLEAR command received");
                if (command.getArgc() != 0) {
                    return Response.error(ErrorCode.INVALID_FORMAT);
                }
                serverCtx.state.clear();
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
