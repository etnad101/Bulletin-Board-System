/*
* ServerState.java
*
* Holds mutable information about the server
*/

import java.util.ArrayList;

public class ServerState {
    private ArrayList<Note> notes;
    private ArrayList<Pin> pins;

    public ServerState() {
        this.notes = new ArrayList<Note>();
    }

    public synchronized boolean addPin(Pin pin) {
        for (Note note : this.notes) {
            if (note.containsPoint(pin.getX(), pin.getY())) {
                note.pin();
            }
        }
        this.pins.add(pin);
        return true;
    }

    public synchronized boolean addNote(Note note) {
        this.notes.add(note);
        return true;
    }

    public synchronized ArrayList<Note> getNotes() {
        return new ArrayList<Note>(this.notes);
    }
}
