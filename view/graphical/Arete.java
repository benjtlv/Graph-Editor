package view.graphical;

import controller.Controller;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.beans.binding.Bindings;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;

public class Arete extends LinkUI {

    public Arete() {
	super(new Line());
	this.link.setStroke(Color.BLACK);
    }

    public void setProperties() {
	if (S1 instanceof Circle) {
	    ((Line)link).startXProperty().bind(((Circle)S1).centerXProperty());
	    ((Line)link).startYProperty().bind(((Circle)S1).centerYProperty());
	}
	if (S2 instanceof Circle) {
	    ((Line)link).endXProperty().bind(((Circle)S2).centerXProperty());
	    ((Line)link).endYProperty().bind(((Circle)S2).centerYProperty());
	}
	if (S1 instanceof Rectangle) {
	    ((Line)link).startXProperty().bind(Bindings.add(((Rectangle)S1).xProperty(),((Rectangle)S1).widthProperty().divide(2)));
	    ((Line)link).startYProperty().bind(Bindings.add(((Rectangle)S1).yProperty(),((Rectangle)S1).heightProperty().divide(2)));
	}
	if (S2 instanceof Rectangle) {
	    ((Line)link).endXProperty().bind(Bindings.add(((Rectangle)S2).xProperty(),((Rectangle)S2).widthProperty().divide(2)));
	    ((Line)link).endYProperty().bind(Bindings.add(((Rectangle)S2).yProperty(),((Rectangle)S2).heightProperty().divide(2)));
	}
    }
    
    public void setPoidsProperties() {
	if (S1 instanceof Circle) {
	    if (S2 instanceof Circle) {
		poids.xProperty().bind(Bindings.add(((Circle)S1).centerXProperty(),((Circle)S2).centerXProperty()).divide(2));
		poids.yProperty().bind(Bindings.add(((Circle)S1).centerYProperty(),((Circle)S2).centerYProperty()).divide(2));
	    }
	    if (S2 instanceof Rectangle) {
		poids.xProperty().bind(Bindings.add(((Circle)S1).centerXProperty(),Bindings.add(((Rectangle)S2).xProperty(),((Rectangle)S2).widthProperty().divide(2))).divide(2));
		poids.yProperty().bind(Bindings.add(((Circle)S1).centerYProperty(),Bindings.add(((Rectangle)S2).yProperty(),((Rectangle)S2).heightProperty().divide(2))).divide(2));
	    }
	}
	if (S1 instanceof Rectangle) {
	    if (S2 instanceof Circle) {
		poids.xProperty().bind(Bindings.add(Bindings.add(((Rectangle)S1).xProperty(),((Rectangle)S1).widthProperty().divide(2)),((Circle)S2).centerXProperty()).divide(2));
		poids.yProperty().bind(Bindings.add(Bindings.add(((Rectangle)S1).yProperty(),((Rectangle)S1).heightProperty().divide(2)),((Circle)S2).centerYProperty()).divide(2));
	    }
	    if (S2 instanceof Rectangle) {
		poids.xProperty().bind(Bindings.add(Bindings.add(((Rectangle)S1).xProperty(),((Rectangle)S1).widthProperty().divide(2)),Bindings.add(((Rectangle)S2).xProperty(),((Rectangle)S2).widthProperty().divide(2))).divide(2));
		poids.yProperty().bind(Bindings.add(Bindings.add(((Rectangle)S1).yProperty(),((Rectangle)S1).heightProperty().divide(2)),Bindings.add(((Rectangle)S2).yProperty(),((Rectangle)S2).heightProperty().divide(2))).divide(2));
	    }
	}	
    }
}
