public class Note {
    private int x;
    private int y;
    private int width;
    private int height;

    private String color;
    private String content;
    private boolean pinned;

    public Note(int x, int y, int width, int height, String color, String content) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.content = content;
        this.pinned = false;
    }

    public boolean containsPoint(int px, int py) {
        return (px >= this.x && px <= this.x + this.width && py >= this.y && py <= this.y + this.height);
    }

    public void pin() {
        this.pinned = true;
    }

    public void unpin() {
        this.pinned = false;
    }

    public boolean isPinned() {
        return this.pinned;
    }

    @Override
    public String toString() {
        return x + " " + y + " " + color + " " + content + " PINNED=" + pinned;
    }
}
