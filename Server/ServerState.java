/*
* ServerState.java
*
* Holds mutable information about the server
*/

import java.util.ArrayList;

public class ServerState {
    private ArrayList<Note> notes;

    public ServerState() {
        this.notes = new ArrayList<Note>();
    }

    public synchronized void addNote(Note note) {
        this.notes.add(note);
    }

    public synchronized ArrayList<Note> getNotes() {
        return new ArrayList<Note>(this.notes);
    }
}
