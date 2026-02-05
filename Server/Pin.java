/*
* Pin.java
*
* Contains the position of a pin 
*/

public class Pin {
    private int x;
    private int y;

    public Pin(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}