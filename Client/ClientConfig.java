public class ClientConfig {
	
    private final int boardWidth;
    private final int boardHeight;
    private final int noteWidth;
    private final int noteHeight;
    private final String[] allowedColors;

    // Parses the String sent by Server
    public ClientConfig(String configStr) {
        String[] parts = configStr.split(",");
        this.boardWidth = Integer.parseInt(parts[0]);
        this.boardHeight = Integer.parseInt(parts[1]);
        this.noteWidth = Integer.parseInt(parts[2]);
        this.noteHeight = Integer.parseInt(parts[3]);
        
        // Extract colors (remaining parts)
        this.allowedColors = new String[parts.length - 4];
        System.arraycopy(parts, 4, this.allowedColors, 0, parts.length - 4);
    }

    
    // Getters

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
    	return allowedColors; 
    }
    
}