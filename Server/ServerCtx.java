/*
* ServerCtx.java
*
* Holds server context information for easy sharing between classes
*/

public class ServerCtx {
    private final int boardWidth;
    private final int boardHeight;
    private final int noteWidth;
    private final int noteHeight;
    private final String[] allowedColors;

    public ServerCtx(int boardWidth, int boardHeight, int noteWidth, int noteHeight, String[] allowedColors) {
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
}
