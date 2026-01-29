/*
* CommandHandler.java
*
* Handles commands sent by clients
*/

public class CommandHandler {
    private ServerCtx serverCtx;

    public CommandHandler(ServerCtx serverCtx) {
        this.serverCtx = serverCtx;
    }

    public void handleCommand(String commandString) {
        // Parse command string into more accessible format
        Command command = new Command(commandString);

        // Check for invalid command
        if (command.getType() == null) {
            System.out.println("Invalid command received: " + commandString);
            return;
        }

        // Handle command based on type
        System.out.println("Handling command: " + command.getType());
        for (int i = 0; i < command.getArgc(); i++) {
            System.out.println("Arg " + i + ": " + command.getArgv()[i]);
        }
        switch (command.getType()) {
            case POST:
                System.out.println("POST command received");
                Note note = new Note(
                    Integer.parseInt(command.getArgv()[0]),
                    Integer.parseInt(command.getArgv()[1]),
                    command.getArgv()[2],
                    command.getArgv()[3]
                );
                this.serverCtx.state.addNote(note);
                break;
            case GET:
                System.out.println("GET command received");
                break;
            case PIN:
                System.out.println("PIN command received");
                break;
            case UNPIN:
                System.out.println("UNPIN command received");
                break;
            case SHAKE:
                System.out.println("SHAKE command received");
                break;
            case CLEAR:
                System.out.println("CLEAR command received");
                break;
            case DISCONNECT:
                System.out.println("DISCONNECT command received");
                break;
        }
    }
}
