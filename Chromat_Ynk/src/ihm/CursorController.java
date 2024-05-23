package ihm;

//import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.canvas.Canvas;

public class CursorController {
    private ObservableMap<Integer, ObservableList<Cursor>> cursors = FXCollections.observableHashMap();
    private Canvas canvas;

    public CursorController(Canvas canvas) {
        this.canvas = canvas;
    }

    public void addCursor(int id) {
        Cursor cursor = new Cursor(canvas.getWidth()/2, canvas.getHeight()/2, canvas);
        ObservableList<Cursor> cursorList = cursors.computeIfAbsent(id, k -> FXCollections.observableArrayList());
        cursorList.add(cursor);
        cursors.put(id, cursorList);
    }

    public void removeCursor(int id) {
        cursors.remove(id);
    }


    public ObservableMap<Integer, ObservableList<Cursor>> getCursors() {
        return cursors;
    }


    public ObservableList<Cursor> getCursorList(Integer id) {
        if (cursors.containsKey(id)) {
            return cursors.get(id);
        }
        return null;
    }
}
