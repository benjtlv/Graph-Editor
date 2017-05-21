package view.graphical;

import controller.Controller;

import java.util.ArrayList;

import javafx.scene.shape.Shape;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.paint.Color;
import javafx.geometry.VPos;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;

public class TextSommet extends Text {
    Controller controller;
    
    SommetUI sommet;
    EventHandler<MouseEvent> textClickToSetLine, textClickToSetCurve;

    public TextSommet(SommetUI sommet) {
	super();
	this.sommet = sommet;
	setFont(Font.font(20));
	setTextOrigin(VPos.CENTER);
	setTextAlignment(TextAlignment.CENTER);
	
	
	textClickToSetLine = new EventHandler<MouseEvent>(){ //Quand on clique sur le nom du sommet
		@Override
		public void handle(MouseEvent e) {
		    setLink("arete");
		}
	    };

	textClickToSetCurve = new EventHandler<MouseEvent>(){ //pareil pour l'arc
		@Override
		public void handle(MouseEvent e) {
		    setLink("arc");
		}
	    };
    }

    public void setLink(String type) {
	LinkUI li = null;
	Shape sh = sommet.shape;
	if (!SommetUI.startLink) { //si on a pas séléctionner le premier sommet à  lier
	    if (type.equals("arete")) {
		li = new Arete();
	    }
	    if (type.equals("arc")) {
		li = new Arc();
	    }
	    System.out.println("click start text");
	    IG.link = li;
	    IG.link.sg = sommet;
	    IG.link.S1 = sh;
	    sommet.listLink.add(IG.link);
	    if (sh instanceof Circle) {
		if (li instanceof Arete) {
		    ((Line)IG.link.link).setStartX(((Circle)sh).getCenterX());
		    ((Line)IG.link.link).setStartY(((Circle)sh).getCenterY());
		}
		if (li instanceof Arc) {
		    ((QuadCurve)IG.link.link).setStartX(((Circle)sh).getCenterX());
		    ((QuadCurve)IG.link.link).setStartY(((Circle)sh).getCenterY());
		}
	    }
	    if (sh instanceof Rectangle) {
		if (li instanceof Arete) {
		    ((Line)IG.link.link).setStartX(((Rectangle)sh).getX()+((Rectangle)sh).getWidth()/2);
		    ((Line)IG.link.link).setStartY(((Rectangle)sh).getY() + ((Rectangle)sh).getHeight()/2);
		}
		if (li instanceof Arc) {
		    ((QuadCurve)IG.link.link).setStartX(((Rectangle)sh).getX()+((Rectangle)sh).getWidth()/2);
		    ((QuadCurve)IG.link.link).setStartY(((Rectangle)sh).getY() + ((Rectangle)sh).getHeight()/2);
		}
	    }
	    SommetUI.startLink = true;
	}
	else if (SommetUI.startLink) {
	    li = IG.link;
	    if (!sh.equals(IG.link.S1)) {
		System.out.println("click end text");
		IG.link.sd = sommet;
		IG.link.S2 = sh;
		if (sh instanceof Circle) {
		    if (li instanceof Arete) {
			((Line)IG.link.link).setEndX(((Circle)sh).getCenterX());
			((Line)IG.link.link).setEndY(((Circle)sh).getCenterY());
		    }
		    if (li instanceof Arc) {
			((QuadCurve)IG.link.link).setEndX(((Circle)sh).getCenterX());
			((QuadCurve)IG.link.link).setEndY(((Circle)sh).getCenterY());
		    }
		}
		if (sh instanceof Rectangle) {
		    if (li instanceof Arete) {
			((Line)IG.link.link).setEndX(((Rectangle)sh).getX()+((Rectangle)sh).getWidth()/2);
			((Line)IG.link.link).setEndY(((Rectangle)sh).getY() + ((Rectangle)sh).getHeight()/2);
		    }
		    if (li instanceof Arc) {
			((QuadCurve)IG.link.link).setEndX(((Rectangle)sh).getX()+((Rectangle)sh).getWidth()/2);
			((QuadCurve)IG.link.link).setEndY(((Rectangle)sh).getY() + ((Rectangle)sh).getHeight()/2);
		    }
		}
		IG.link.setProperties();
		IG.link.setPoidsProperties();
		IG.pane.getChildren().add(IG.link.link);
		IG.pane.getChildren().add(IG.link.poids);
		IG.link.link.toBack();
		sommet.listLink.add(IG.link);
		IG.listLinkGlobal.add(IG.link);
		IG.link.setMouseActions();
		IG.controller.createLinkAndAssociate(IG.link,Integer.parseInt(IG.link.poids.getText()));
		//IG.controller.displaySommetsGraphe();
		//IG.controller.displayAretesGraphe();
	    } else {
		sommet.listLink.remove(IG.link);
		IG.link.sg = null;
		IG.link.sd = null;
	    }
	    SommetUI.startLink = false;
	    if (li instanceof Arete) {
		IG.lineButton.setSelected(false);
	    }
	    if (li instanceof Arc) {
		IG.curveButton.setSelected(false);
	    }
	}
    }
}
