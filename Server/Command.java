/*
* Command.java
*
* Parses command from strings to be handled by CommandHandler
*/

// All valid commands
enum CommandType {
    POST, 
    GET, 
    PIN, 
    UNPIN, 
    SHAKE, 
    CLEAR, 
    DISCONNECT
}

public class Command {
    private CommandType type;
    private int argc;
    private String[] argv;
    private int argvIndex = 0;

    public Command(String commandStr) {
        String[] parts = commandStr.split(" ");

        // Get command type, set to null if invalid
        try {
            this.type = CommandType.valueOf(parts[0]);
        } catch (IllegalArgumentException e) {
            this.type = null;
        }

        // Add remaining tokens to argv
        this.argc = parts.length - 1;
        if (parts.length > 1) {
            this.argv = new String[this.argc];
            for (int i = 1; i < parts.length; i++) {
                this.argv[i - 1] = parts[i].trim();
            }
        }
    }

    public CommandType getType() {
        return type;
    }

    public int getArgc() {
        return argc;
    }

    public String[] getArgv() {
        return argv;
    }

    public String popArgv() {
        if (argvIndex < argc) {
            return argv[argvIndex++];
        } else {
            return null;
        }
    }

    public String consumeArgv() {
        StringBuilder sb = new StringBuilder();
        if (argvIndex >= argc) {
            return null;
        }
        while (argvIndex < argc) {
            sb.append(argv[argvIndex++]);
            if (argvIndex < argc) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
