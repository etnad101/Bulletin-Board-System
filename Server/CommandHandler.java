/*
* CommandHandler.java
*
* Handles commands sent by clients
* - INVALID_FORMAT error is returned for any commands that do not conform to the expected format
* - Command args are passed to specific handlers after basic validation
* - Specific handlers perform further validation and execute the command
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

    private boolean isPointWithinBounds(int x, int y) {
        return (x >= 0 && x <= serverCtx.config.getBoardWidth() &&
                y >= 0 && y <= serverCtx.config.getBoardHeight());
    }

    private boolean isNoteWIthinBounds(int x, int y) {
        return (x >= 0 && x + serverCtx.config.getNoteWidth() <= serverCtx.config.getBoardWidth() &&
                y >= 0 && y + serverCtx.config.getNoteHeight() <= serverCtx.config.getBoardHeight());
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

    private Response handlePost(int x, int y, String color, String content) {
        if (!isPointWithinBounds(x, y)) {
            return Response.error(ErrorCode.OUT_OF_BOUNDS);
        }

        if (!isNoteWIthinBounds(x, y)) {
            return Response.error(ErrorCode.OUT_OF_BOUNDS);
        }

        if (!isValidColor(color)) {
            return Response.error(ErrorCode.COLOR_NOT_SUPPORTED);
        }

        Note note = new Note(
            x,
            y,
            serverCtx.config.getNoteWidth(),
            serverCtx.config.getNoteHeight(),
            color,
            content
        );

        if (!this.serverCtx.state.addNote(note)) {
            return Response.error(ErrorCode.COMPLETE_OVERLAP);
        }

        return Response.success(SuccessCode.NOTE_POSTED);
    }

    private Response handleGet(String color, int containsX, int containsY, String substring) {
        if (containsX != -1 && containsY != -1 && !isPointWithinBounds(containsX, containsY)) {
            return Response.error(ErrorCode.OUT_OF_BOUNDS);
        }

        if (color != null && !isValidColor(color)) {
            return Response.error(ErrorCode.COLOR_NOT_SUPPORTED);
        }

        Response response = Response.success(SuccessCode.NOTE);
        ArrayList<Note> notes = this.serverCtx.state.getNotes(); 
        StringBuilder sb = new StringBuilder();
        int count = 0;

        for (Note note : notes) {
            if (color != null && !note.getColor().equalsIgnoreCase(color)) {
                continue;
            }
            if (containsX != -1 && containsY != -1 && !note.containsPoint(containsX, containsY)) {
                continue;
            }
            if (substring != null && !note.getContent().contains(substring)) {
                continue;
            }
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

                return handlePost(coords[0], coords[1], color, content);
            }

            case GET: {
                String arg1 = command.popArgv();

                if (arg1 == null) {
                    System.out.println("Blank GET command received");
                    return handleGet(null, -1, -1, null);
                } 

                if (arg1.equals(new String("PINS"))) {
                    System.out.println("GET PINS command received");
                    if (command.getArgc() != 1) {
                        return Response.error(ErrorCode.INVALID_FORMAT);
                    }
                    return handleGetPins();
                }

                System.out.println("General GET command received");

                String color = null;
                int containsX = -1;
                int containsY = -1;
                String substring = null;

                if (command.containsArg("color=")) {
                    String target = command.popArgv();
                    color = target.split("=")[1];
                }
                if (command.containsArg("contains=")) {
                    String target = command.popArgv();
                    String target2 = command.popArgv();
                    try {
                        containsX = Integer.parseInt(target.split("=")[1]);
                        containsY = Integer.parseInt(target2);
                    } catch (NumberFormatException e) {
                        return Response.error(ErrorCode.INVALID_FORMAT);
                    }
                }
                if (command.containsArg("refersTo=")) {
                    String target = command.popArgv();
                    substring = target.split("=")[1];
                }
                System.out.println("GET filters - color: " + color + ", contains: (" + containsX + ", " + containsY + "), refersTo: " + substring);
                return handleGet(color, containsX, containsY, substring);
            }

            case PIN: {
                System.out.println("PIN command received");

                int[] coords = parseCoordinates(command);
                if (coords == null) {
                    return Response.error(ErrorCode.INVALID_FORMAT);
                }

                if (!isPointWithinBounds(coords[0], coords[1])) {
                    return Response.error(ErrorCode.OUT_OF_BOUNDS);
                }

                if (!serverCtx.state.addPin(coords[0], coords[1])) {
                    return Response.error(ErrorCode.PIN_ALREADY_EXISTS);
                } 

                return Response.success(SuccessCode.PIN_ADDED);
            }

            case UNPIN: {
                System.out.println("UNPIN command received");

                int[] coords = parseCoordinates(command);
                if (coords == null) {
                    return Response.error(ErrorCode.INVALID_FORMAT);
                }

                if (!isPointWithinBounds(coords[0], coords[1])) {
                    return Response.error(ErrorCode.OUT_OF_BOUNDS);
                }

                if (!serverCtx.state.removePin(coords[0], coords[1])) {
                    return Response.error(ErrorCode.PIN_NOT_FOUND);
                }

                return Response.success(SuccessCode.PIN_REMOVED);
            }

            case SHAKE: {
                System.out.println("SHAKE command received");

                if (command.getArgc() != 0) {
                    return Response.error(ErrorCode.INVALID_FORMAT);
                }

                if (serverCtx.state.getNotes().isEmpty()){
                    return Response.error(ErrorCode.EMPTY_BOARD);
                }

                serverCtx.state.shake();
                return Response.success(SuccessCode.SHAKE_COMPLETE);
            }

            case CLEAR:
                System.out.println("CLEAR command received");

                if (command.getArgc() != 0) {
                    return Response.error(ErrorCode.INVALID_FORMAT);
                }

                if (serverCtx.state.getNotes().isEmpty()){
                    return Response.error(ErrorCode.EMPTY_BOARD);
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
