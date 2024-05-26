package ihm;

import cursors.Cursor;
import cursors.CursorMimic;
import cursors.CursorMirrorAxial;
import cursors.CursorMirrorCenter;
import cursors.CursorNormal;

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

    public void addCursor(Cursor cursor) {
        ObservableList<Cursor> cursorList = cursors.computeIfAbsent(cursor.getId(), k -> FXCollections.observableArrayList());
        cursorList.add(cursor);
        cursors.put(cursor.getId(), cursorList);
    }

    public Cursor createCursorNormal(int id) {
        Cursor cursor = new CursorNormal(canvas, this);
        cursor.setId(id);
        return cursor;
    }

    public Cursor createCursorMimic(int idToMimic, Cursor fathercursor) {
        Cursor cursor = new CursorMimic(canvas, this, fathercursor);
        cursor.setId(idToMimic);
        return cursor;
    }

    public Cursor createCursorMirrorCenter(Cursor fatherCursor, double xCenter, double yCenter) {
        Cursor cursor = new CursorMirrorCenter(canvas, this, fatherCursor, xCenter, yCenter);
        int id = fatherCursor.getId();
        cursor.setId(id);
        return cursor;
    }

    public Cursor createCursorMirrorAxial(Cursor fatherCursor, double x1, double y1, double x2, double y2) {
        Cursor cursor = new CursorMirrorAxial(canvas, this, fatherCursor, x1, y1, x2, y2);
        int id = fatherCursor.getId();
        cursor.setId(id);
        return cursor;
    }

    /*
    public void addCursorMimic(int id, CursorType type, Cursor fatherCursor) {
        Cursor cursor = new Cursor(canvas, this, type, fatherCursor);
        ObservableList<Cursor> cursorList = cursors.computeIfAbsent(id, k -> FXCollections.observableArrayList());
        cursorList.add(cursor);
        cursors.put(id, cursorList);
    }
    */

    public void removeCursors(int id) {
        cursors.remove(id);
    }

    public void removeCursor(Cursor cursor) {
        cursors.get(cursor.getId()).remove(cursor);
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
