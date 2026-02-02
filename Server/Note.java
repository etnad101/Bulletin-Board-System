public class Note {
    private int x;
    private int y;
    private String color;
    private String content;
    private boolean pinned;

    public Note(int x, int y, String color, String content) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.content = content;
        this.pinned = false;
    }


    public String serialize() {
        return x + " " + y + " " + color + " " + content + " PINNED=" + pinned;
    }
}
