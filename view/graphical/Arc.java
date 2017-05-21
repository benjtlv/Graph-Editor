package view.graphical;

import controller.Controller;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.beans.binding.Bindings;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;

public class Arc extends LinkUI {
    double angle, r;
    EventHandler<MouseEvent> pressed, dragged;
    
    public Arc() {
	super(new QuadCurve());
	this.link.setFill(null);
	this.link.setStroke(Color.BLACK);
	makeDraggable();
    }

    public void setStartEnd() {
	QuadCurve curve = (QuadCurve)link;
	if (S1 instanceof Circle) {
	    curve.startXProperty().bind(((Circle)S1).centerXProperty());
	    curve.startYProperty().bind(((Circle)S1).centerYProperty());
	}
	if (S2 instanceof Circle) {
	    curve.endXProperty().bind(((Circle)S2).centerXProperty());
	    curve.endYProperty().bind(((Circle)S2).centerYProperty());
	}
	if (S1 instanceof Rectangle) {
	    curve.startXProperty().bind(Bindings.add(((Rectangle)S1).xProperty(),((Rectangle)S1).widthProperty().divide(2)));
	    curve.startYProperty().bind(Bindings.add(((Rectangle)S1).yProperty(),((Rectangle)S1).heightProperty().divide(2)));
	}
	if (S2 instanceof Rectangle) {
	    curve.endXProperty().bind(Bindings.add(((Rectangle)S2).xProperty(),((Rectangle)S2).widthProperty().divide(2)));
	    curve.endYProperty().bind(Bindings.add(((Rectangle)S2).yProperty(),((Rectangle)S2).heightProperty().divide(2)));
	}
    }

    public void setProperties() {
	QuadCurve curve = (QuadCurve)link;
	double angle;
	setStartEnd();
	angle = (-Math.PI/2) + Math.atan((curve.getEndY()-curve.getStartY())/(curve.getEndX()-curve.getStartX()));
	r = (20*Math.random())+5;
	curve.setControlX(((curve.getStartX()+curve.getEndX())/2)+(r*Math.cos(angle)));
        curve.setControlY(((curve.getStartY()+curve.getEndY())/2)+(r*Math.sin(angle)));
    }

    public void setPoidsProperties() {
	QuadCurve curve = (QuadCurve)link;
	poids.xProperty().bind(Bindings.add(curve.startXProperty(),Bindings.add(curve.endXProperty(),curve.controlXProperty())).divide(3));
        poids.yProperty().bind(Bindings.add(curve.startYProperty(),Bindings.add(curve.endYProperty(),curve.controlYProperty())).divide(3));
    }

    public void makeDraggable() {
	QuadCurve curve = (QuadCurve)link;
	class T {
	    double mouseX, mouseY, x, y;
	}
	final T t = new T();
        pressed = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
		    t.mouseX = e.getSceneX();
		    t.mouseY = e.getSceneY();
		    t.x = curve.getControlX();
		    t.y = curve.getControlY();
		}
	    };
        dragged = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
		    t.x += e.getSceneX()-t.mouseX;
		    t.y += e.getSceneY()-t.mouseY;
		    curve.setControlX(t.x);
		    curve.setControlY(t.y);
		    t.mouseX = e.getSceneX();
		    t.mouseY = e.getSceneY();
		}
	    };
	addDraggableHandlers();
    }

    public void addDraggableHandlers() {
	QuadCurve curve = (QuadCurve)link;
	curve.addEventHandler(MouseEvent.MOUSE_PRESSED, pressed);
	curve.addEventHandler(MouseEvent.MOUSE_DRAGGED, dragged);
	poids.addEventHandler(MouseEvent.MOUSE_PRESSED, pressed);
	poids.addEventHandler(MouseEvent.MOUSE_DRAGGED, dragged);
    }

    public void removeDraggableHandlers() {
	QuadCurve curve = (QuadCurve)link;
	curve.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressed);
	curve.removeEventHandler(MouseEvent.MOUSE_DRAGGED, dragged);
	poids.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressed);
	poids.removeEventHandler(MouseEvent.MOUSE_DRAGGED, dragged);
    }
}
