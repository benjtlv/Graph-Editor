package view.graphical;

import controller.Controller;

import java.util.List;
import java.util.ArrayList;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.geometry.VPos;
import javafx.geometry.Side;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.Node;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

// CLASSE POUR LE SOMMET CERCLE

// HERITE DE CIRCLE QUI EST UNE SHAPE

public class SommetCercle extends SommetUI {

    public SommetCercle() {
	super(new Circle(IG.RADIUS));

	shapeWidth = 2*((Circle)shape).getRadius();

	SommetCercle s = this;
	clickToSetLine = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent e) {
		    setLink("arete");
		}
	    };

	clickToSetCurve = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
		    setLink("arc");
		}
	    };

	
	// Ce Handler est pour quand la souris bouge  
			
	createShape = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
		    shape.setVisible(true);
		    name.setVisible(true);
		    if (e.getX() > ((Circle)shape).getRadius() && e.getX() < IG.pane.getWidth() - ((Circle)shape).getRadius() && e.getY() >= ((Circle)shape).getRadius() && e.getY() < IG.pane.getHeight() - ((Circle)shape).getRadius()) { 
			((Circle)shape).setCenterX(e.getX());
			((Circle)shape).setCenterY(e.getY());
		    }
		    setTextProperties();
		}
	    };

	// Ce handler est pour régler le rayon grâce à la mollette de la souris
	    
	setShapeSize = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent e) {
		    double r;
		    setTextProperties();
		    shapeWidth = 2*((Circle)shape).getRadius();
		    if ((r = ((Circle)shape).getRadius() + e.getDeltaY()/10.0) >= w && r <= 75 && ((Circle)shape).getCenterX() - r > 0 && ((Circle)shape).getCenterX() + r < IG.pane.getWidth() && ((Circle)shape).getCenterY() - r > 0 && ((Circle)shape).getCenterY() + r < IG.pane.getHeight()) {
			((Circle)shape).setRadius(r);
		    }
		}
	    };
	
	// Ce handler est pour placer définitivement le sommet (et donc désactiver le bouton de création du cercle 

	SommetCercle so = this;
	setShape = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
		    ((Circle)shape).setCenterX(e.getX());
		    ((Circle)shape).setCenterY(e.getY());
		    IG.controller.createDataAndAssociate(so);
		    //IG.controller.displaySommetsGraphe();
		    IG.cercButton.setSelected(false);
		    setMouseActions();
		}
	    };
    }

    public void setLink(String type) {
	LinkUI li = null;
	if (!startLink) {
	    if (type.equals("arete")) {
		li = new Arete();
	    }
	    if (type.equals("arc")) {
		li = new Arc();
	    }
	    IG.link = li;
	    IG.link.sg = this;
	    IG.link.S1 = shape;
	    listLink.add(IG.link);
	    if (li instanceof Arete) {
		((Line)IG.link.link).setStartX(((Circle)shape).getCenterX());
		((Line)IG.link.link).setStartY(((Circle)shape).getCenterY());
	    }	
	    if (li instanceof Arc) {
		((QuadCurve)IG.link.link).setStartX(((Circle)shape).getCenterX());
		((QuadCurve)IG.link.link).setStartY(((Circle)shape).getCenterY());
	    }
	    startLink = true;
	}
	else if (startLink) {
	    li = IG.link;
	    if (!shape.equals(IG.link.S1)) {
		System.out.println("click end cercle");
		IG.link.sd = this;
		IG.link.S2 = shape;
		if (li instanceof Arete) {
		    ((Line)IG.link.link).setEndX(((Circle)shape).getCenterX());
		    ((Line)IG.link.link).setEndY(((Circle)shape).getCenterY());
		}
		if (li instanceof Arc) {
		    ((QuadCurve)IG.link.link).setEndX(((Circle)shape).getCenterX());
		    ((QuadCurve)IG.link.link).setEndY(((Circle)shape).getCenterY());
		}
		IG.link.setProperties();
		IG.link.setPoidsProperties();
		IG.pane.getChildren().add(IG.link.link);
		IG.pane.getChildren().add(IG.link.poids);
		IG.link.link.toBack();
		listLink.add(IG.link);
		IG.listLinkGlobal.add(IG.link);
		IG.link.setMouseActions();
	        IG.controller.createLinkAndAssociate(IG.link,Integer.parseInt(IG.link.poids.getText()));
		//IG.controller.displaySommetsGraphe();
		//IG.controller.displayAretesGraphe();
	    } else {
		listLink.remove(IG.link);
		IG.link.sg = null;
		IG.link.sd = null;
	    }
	    startLink = false;
	    if (li instanceof Arete) {
		IG.lineButton.setSelected(false);
	    }
	    if (li instanceof Arc) {
		IG.curveButton.setSelected(false);
	    }
	}
    }

    public void setW(double ww){
	w = ww;
    }

    public void setTextProperties() {
	name.boundsInLocalProperty().addListener((observer,oldValue,newValue) -> {
		setW(newValue.getWidth());
	    });
	name.xProperty().bind(((Circle)shape).centerXProperty().subtract(w/2));
	name.yProperty().bind(((Circle)shape).centerYProperty());
    }

    public void makeDraggable() {
        class T {

            double x, y, mouseX, mouseY;
        }

        final T t = new T();

	pressed = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent e) {
		    t.mouseX = e.getSceneX();
		    t.mouseY = e.getSceneY();
		    t.x = ((Circle)shape).getCenterX();
		    t.y = ((Circle)shape).getCenterY();
		}
	    };

	dragged = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent e) {
		    t.x += e.getSceneX()-t.mouseX;
		    t.y += e.getSceneY()-t.mouseY;
		    if (t.x - ((Circle)shape).getRadius() > 0 && t.x + ((Circle)shape).getRadius() < IG.pane.getWidth() && t.y - ((Circle)shape).getRadius() > 0 && t.y + ((Circle)shape).getRadius() < IG.pane.getHeight()) {
			((Circle)shape).setCenterX(t.x);
			((Circle)shape).setCenterY(t.y);
		    }
		    t.mouseX = e.getSceneX();
		    t.mouseY = e.getSceneY();
		}
	    };
	
	addDraggableHandlers();
    }
}
