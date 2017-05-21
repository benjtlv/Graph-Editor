package view.graphical;

import controller.Controller;

import java.util.List;
import java.util.ArrayList;

import javafx.scene.shape.Rectangle;
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
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.beans.property.SimpleBooleanProperty;

public class SommetRectangle extends SommetUI {

    public SommetRectangle() {
	super(new Rectangle(IG.WIDTH,IG.HEIGHT));
	
	shapeWidth = ((Rectangle)shape).getWidth();

	clickToSetLine = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent e) {
		    setLink("arete");
		}
	    };

	clickToSetCurve = new EventHandler<MouseEvent>(){
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
		    if (e.getX() > ((Rectangle)shape).getWidth()/2 && e.getX() < IG.pane.getWidth() - ((Rectangle)shape).getWidth()/2 && e.getY() >= ((Rectangle)shape).getHeight()/2 && e.getY() < IG.pane.getHeight() - ((Rectangle)shape).getHeight()/2) { 
			((Rectangle)shape).setX(e.getX()-((Rectangle)shape).getWidth()/2);
			((Rectangle)shape).setY(e.getY()-((Rectangle)shape).getHeight()/2);
		    }
		    setTextProperties();
		}
	    };

	// Ce handler est pour régler le rayon grâce à la mollette de la souris
	    
	setShapeSize = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent e) {
		    setTextProperties();
		    shapeWidth = ((Rectangle)shape).getWidth();
		    double oldWidth = ((Rectangle)shape).getWidth();
		    double oldHeight = ((Rectangle)shape).getHeight();
		    double rw = e.getDeltaY()/20.0;
		    double rh = e.getDeltaY()/20.0;
		    double newX = ((Rectangle)shape).getX()-rw;
		    double newY = ((Rectangle)shape).getY()-rh;
		    double newWidth = oldWidth+2*rw;
		    double newHeight = oldHeight+2*rh;
		    if (newWidth >= w && newWidth <=165 && newHeight >= 25 && newHeight <= 145 && newX > 0 && newX + newWidth < IG.pane.getWidth() && newY > 0 && newY + newHeight < IG.pane.getHeight()) {
			((Rectangle)shape).setX(newX);
			((Rectangle)shape).setY(newY);
			((Rectangle)shape).setWidth(newWidth);
			((Rectangle)shape).setHeight(newHeight);
		    }
		}
	    };

	// Ce handler est pour placer définitivement le sommet (et donc désactiver le bouton de création du cercle 

	SommetRectangle so = this;
	setShape = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
		    ((Rectangle)shape).setX(e.getX()-((Rectangle)shape).getWidth()/2);
		    ((Rectangle)shape).setY(e.getY()-((Rectangle)shape).getHeight()/2);
		    IG.controller.createDataAndAssociate(so);
		    //IG.controller.displaySommetsGraphe();
		    IG.rectButton.setSelected(false);
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
	    System.out.println("click start rect");
	    IG.link = li;
	    IG.link.sg = this;
	    IG.link.S1 = shape;
	    listLink.add(IG.link);
	    if (li instanceof Arete) {
		((Line)IG.link.link).setStartX(((Rectangle)shape).getX() + ((Rectangle)shape).getWidth()/2);
		((Line)IG.link.link).setStartY(((Rectangle)shape).getY() + ((Rectangle)shape).getHeight()/2);
	    }
	    if (li instanceof Arc) {
		((QuadCurve)IG.link.link).setStartX(((Rectangle)shape).getX() + ((Rectangle)shape).getWidth()/2);
		((QuadCurve)IG.link.link).setStartY(((Rectangle)shape).getY() + ((Rectangle)shape).getHeight()/2);
	    }
	    startLink = true;
	}
	else if (startLink) {
	    li = IG.link;
	    if (!shape.equals(IG.link.S1)) {
		System.out.println("click end rect");
		IG.link.sd = this;
		IG.link.S2 = shape;
		if (li instanceof Arete) {
		    ((Line)IG.link.link).setEndX(((Rectangle)shape).getX() + ((Rectangle)shape).getWidth()/2);
		    ((Line)IG.link.link).setEndY(((Rectangle)shape).getY() + ((Rectangle)shape).getHeight()/2);
		}
		if (li instanceof Arc) {
		    ((QuadCurve)IG.link.link).setEndX(((Rectangle)shape).getX() + ((Rectangle)shape).getWidth()/2);
		    ((QuadCurve)IG.link.link).setEndY(((Rectangle)shape).getY() + ((Rectangle)shape).getHeight()/2);
       
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
	name.xProperty().bind(Bindings.add(((Rectangle)shape).xProperty(),((Rectangle)shape).widthProperty().divide(2)).subtract(w/2));
	name.yProperty().bind(Bindings.add(((Rectangle)shape).yProperty(),((Rectangle)shape).heightProperty().divide(2)));
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
		    t.x = ((Rectangle)shape).getX();
		    t.y = ((Rectangle)shape).getY();
		}
	    };

	dragged = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent e) {
		    t.x += e.getSceneX()-t.mouseX;
		    t.y += e.getSceneY()-t.mouseY;
		    if (t.x > 0 && t.x + ((Rectangle)shape).getWidth() < IG.pane.getWidth() && t.y > 0 && t.y + ((Rectangle)shape).getHeight() < IG.pane.getHeight()) {
			((Rectangle)shape).setX(t.x);
			((Rectangle)shape).setY(t.y);
		    }
		    t.mouseX = e.getSceneX();
		    t.mouseY = e.getSceneY();
		}
	    };
	
	addDraggableHandlers();
    }
}
