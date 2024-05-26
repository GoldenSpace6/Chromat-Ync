package ihm;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.HashMap;
import java.util.Map;

import cursors.Cursor;
import cursors.CursorNormal;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;

public class CursorPresenter {
    private Pane root;
    private CursorController controller;
    private Map<Cursor, Polygon> cursorMap;

    public CursorPresenter(Pane root, CursorController controller) {
        this.root = root;
        this.controller = controller;
        this.cursorMap = new HashMap<>();
        initView();
    }

    private void initView() {
        controller.getCursors().addListener((MapChangeListener.Change<? extends Integer, ? extends ObservableList<Cursor>> change) -> {
            if (change.wasAdded()) {
                ObservableList<Cursor> addedList = change.getValueAdded();
                listenToListChanges(addedList);     
            } else if (change.wasRemoved()) {
                ObservableList<Cursor> removedList = change.getValueRemoved();
                Platform.runLater(() -> {
                    for (Cursor cursor : removedList) { 
                        Polygon cursorTriangle = cursorMap.remove(cursor);
                        if (cursorTriangle != null) {
                            root.getChildren().remove(cursorTriangle);
                        }                   
                    }
                });
            }
        });

        controller.getCursors().values().forEach(this::listenToListChanges);
    }

    private void listenToListChanges(ObservableList<Cursor> list) {
        list.addListener((ListChangeListener.Change<? extends Cursor> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    Platform.runLater(() -> {
                        for (Cursor cursor : change.getAddedSubList()) {
                            Polygon cursorTriangle = createTriangleCursor();
                            if (!(cursor instanceof CursorNormal)) {
                                cursorTriangle.setFill(Color.GREY);
                            }
                            cursorTriangle.layoutXProperty().bind(cursor.xProperty());
                            cursorTriangle.layoutYProperty().bind(cursor.yProperty());
                            cursorTriangle.visibleProperty().bind(cursor.isVisibleProperty());
                            cursorTriangle.rotateProperty().bind(cursor.rotationProperty());
                            root.getChildren().add(cursorTriangle);
                            cursorMap.put(cursor, cursorTriangle);
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        for (Cursor cursor : change.getRemoved()) {           
                            Polygon cursorTriangle = cursorMap.remove(cursor);
                            if (cursorTriangle != null) {
                                root.getChildren().remove(cursorTriangle);
                            }   
                        }
                    });
                }
            }
        });
    }


    private Polygon createTriangleCursor() {
        Polygon cursorTriangle = new Polygon();
        cursorTriangle.getPoints().addAll(new Double[]{
            0.0, -10.0,
            5.0, 10.0,
            -5.0, 10.0 });
        cursorTriangle.setFill(Color.RED);
        return cursorTriangle;
    }
}
