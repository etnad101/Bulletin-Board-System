/*
* ServerState.java
*
* Holds mutable information about the server
*/

import java.util.ArrayList;
import java.util.HashMap;

public class ServerState {
    private ArrayList<Note> notes;
    private ArrayList<Pin> pins;
    private HashMap<Note, ArrayList<Pin>> notePinMap;
    
    public ServerState() {
        this.notes = new ArrayList<Note>();
        this.pins = new ArrayList<Pin>();
        this.notePinMap = new HashMap<Note, ArrayList<Pin>>();
    }

    public synchronized void shake() {
        ArrayList<Note> unpinnedNotes = new ArrayList<Note>();
        for (Note note : this.notes) {
            if (!note.isPinned()) {
                unpinnedNotes.add(note);
            }
        }
        this.notes.removeAll(unpinnedNotes);
        for (Note note : unpinnedNotes) {
            this.notePinMap.remove(note);
        }
    }

    public synchronized void clear() {
        this.notes.clear();
        this.pins.clear();
        this.notePinMap.clear();
    }

    private Pin findPinAt(int x, int y) {
        for (Pin pin : this.pins) {
            if (pin.getX() == x && pin.getY() == y) {
                return pin;
            }
        }
        return null;
    }

    public synchronized boolean addPin(int x, int y) {
        Pin pin = findPinAt(x, y);
        if (pin != null) {
            return false;
        }

        pin = new Pin(x, y);
        for (Note note : this.notes) {
            if (note.containsPoint(pin.getX(), pin.getY())) {
                note.pin();
                ArrayList<Pin> notePins = this.notePinMap.get(note);
                if (notePins == null) {
                    notePins = new ArrayList<Pin>();
                    this.notePinMap.put(note, notePins);
                }
                notePins.add(pin);
            }
        }
        this.pins.add(pin);
        return true;
    }

    public synchronized boolean removePin(int x, int y) {
        Pin pin = findPinAt(x, y);
        if (pin == null) {
            return false;
        }
        for (Note note : this.notes) {
            if (note.containsPoint(pin.getX(), pin.getY())) {
                ArrayList<Pin> notePins = this.notePinMap.get(note);
                if (notePins != null) {
                    notePins.remove(pin);
                    if (notePins.isEmpty()) {
                        note.unpin();
                    }
                }
            }
        }
        return this.pins.remove(pin);
    }

    public synchronized ArrayList<Pin> getPins() {
        return new ArrayList<Pin>(this.pins);
    }

    public synchronized boolean addNote(Note note) {
        this.notes.add(note);
        return true;
    }

    public synchronized ArrayList<Note> getNotes() {
        return new ArrayList<Note>(this.notes);
    }
}
