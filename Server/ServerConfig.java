/*
* ServerConfig.java
*
* Holds immutable information about the server
*/

public class ServerConfig {
    private final int boardWidth;
    private final int boardHeight;
    private final int noteWidth;
    private final int noteHeight;
    private final String[] allowedColors;

    public ServerConfig(int boardWidth, int boardHeight, int noteWidth, int noteHeight, String[] allowedColors) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.noteWidth = noteWidth;
        this.noteHeight = noteHeight;
        this.allowedColors = allowedColors;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public int getBoardHeight() {
        return boardHeight;
    }

    public int getNoteWidth() {
        return noteWidth;
    }

    public int getNoteHeight() {
        return noteHeight;
    }

    public String[] getAllowedColors() {
        return allowedColors.clone();   
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(boardWidth).append(",")
          .append(boardHeight).append(",")
          .append(noteWidth).append(",")
          .append(noteHeight);
        for (String color : allowedColors) {
            sb.append(",").append(color);
        }
        sb.append('\n');
        return sb.toString();
    }
}
