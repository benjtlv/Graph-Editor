package view.graphical;

import controller.Controller;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.geometry.Side;
import javafx.scene.effect.DropShadow;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;

import javafx.animation.ParallelTransition;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;

public abstract class LinkUI {
    public Shape link;
    
    public SommetUI sg, sd;
    public Shape S1, S2;
    DropShadow dropShadow = new DropShadow();//pour l'effet d'ombrage
    public Text poids;
    EventHandler<MouseEvent> showMenu, confirm, moved, exited; //confirm : confirmer la modification du poids par un clic sur la forme
    EventHandler<KeyEvent> supprHandler, numberHandler;//supprHandler : pour supprimer le txt de l'arrete , numberHandler : pour entrer les chiffres
    ContextMenu contextMenu = new ContextMenu();
    MenuItem edit = new MenuItem("edit");
    MenuItem delete = new MenuItem("delete");

    public LinkUI(Shape link){
	this.link = link;
	this.link.setStrokeWidth(3.0);
	this.link.setFill(Color.BLACK);
	dropShadow.setOffsetY(1.0);
	dropShadow.setOffsetX(1.0);
	dropShadow.setColor(Color.BLACK);
	poids = new Text(""+((int)(Math.random()*15)));
	poids.setFont(new Font(22));

	contextMenu.getItems().add(edit);
	contextMenu.getItems().add(delete);

	final SimpleBooleanProperty edit_enabled = new SimpleBooleanProperty();
	edit.setOnAction(e -> {//qd je clique sur le bouton edit du menu(clik droit)
		edit_enabled.set(true);
	    });

	LinkUI lin = this;
	edit_enabled.addListener((observer,oldValue,newValue) -> {
		if(newValue) {
		    IG.pane.requestFocus();//obligé pour pouvoir écrire sur le pane
		    IG.pane.addEventHandler(KeyEvent.KEY_PRESSED, supprHandler);//key_pressed pour pouvoir gérer le bouton clavier de suppression
		    IG.pane.addEventHandler(KeyEvent.KEY_TYPED, numberHandler);
		    link.addEventHandler(MouseEvent.MOUSE_CLICKED, confirm);
		    poids.addEventHandler(MouseEvent.MOUSE_CLICKED, confirm);
		    for (SommetUI sui : IG.listSommet) {
			sui.removeMouseActionsHandlers();
			sui.removeDraggableHandlers();
		    }
		    for (LinkUI lui : IG.listLinkGlobal) {
			if (lui != lin) {
			    lui.removeMouseActionsHandlers();
			} else {
			    link.removeEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
			    poids.removeEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
			}
			if (lui instanceof Arc) {
			    ((Arc)lui).removeDraggableHandlers();
			}
		    }
		} else {
		    IG.pane.removeEventHandler(KeyEvent.KEY_PRESSED, supprHandler);
		    IG.pane.removeEventHandler(KeyEvent.KEY_TYPED, numberHandler);
		    link.removeEventHandler(MouseEvent.MOUSE_CLICKED, confirm);
		    poids.removeEventHandler(MouseEvent.MOUSE_CLICKED, confirm);
		    for (SommetUI sui : IG.listSommet) {
			sui.addMouseActionsHandlers();
			sui.addDraggableHandlers();
		    }
		    for (LinkUI lui : IG.listLinkGlobal) {
			if (lui != lin) {
			    lui.addMouseActionsHandlers();
			} else {
			    link.addEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
			    poids.addEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
			}
			if (lui instanceof Arc) {
			    ((Arc)lui).addDraggableHandlers();
			}
		    }
		}
	    });

	showMenu = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent e) {
		    if (e.getButton() == MouseButton.SECONDARY) {
			contextMenu.show((Node)poids,Side.RIGHT,0,0);
		    }
		}
	    };

	confirm = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
		    if (e.getButton() == MouseButton.PRIMARY) {
			String s = poids.getText();
			if (s.length() > 0) {
			    edit_enabled.set(false);
			    IG.controller.setCoutArete(sg,sd,Integer.parseInt(s));
			    IG.controller.setCoutArete(sd,sg,Integer.parseInt(s));
			    //IG.controller.displayAretesGraphe();
			}
		    }
		}
	    };

	LinkUI l = this;
	delete.setOnAction(e -> {
		l.sg.listLink.remove(l);
		l.sd.listLink.remove(l);
		IG.pane.getChildren().remove(link);
		IG.pane.getChildren().remove(poids);
		IG.controller.deleteLinkAndNeighbor(sg,sd);
		//IG.controller.displaySommetsGraphe();
		//IG.controller.displayAretesGraphe();
	    });

	supprHandler = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent e) {
		    String s = poids.getText();
		    setPoidsProperties();
		    if (e.getCode() == KeyCode.BACK_SPACE && s.length() > 0) {
			s = s.substring(0,s.length()-1);
			poids.setText(s);
		    }
		}
	    };

	numberHandler = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent e) {
		    String s = poids.getText();
		    setPoidsProperties();
		    if (e.getCharacter().matches("[0-9]") && s.length() < 7) {
			s += e.getCharacter();
			poids.setText(s);
		    }
		}
	    };
    }
				       
    public void setMouseActions(){
	moved = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent e) {
		    link.setStroke(Color.LIME);
		    poids.setFill(Color.LIME);
		    link.setEffect(dropShadow);
		    poids.setEffect(dropShadow);
		}
	    };

	exited = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent e) {
		    link.setStroke(Color.BLACK);
		    poids.setFill(Color.BLACK);
		    link.setEffect(null);
		    poids.setEffect(null);
		}
	    };

        addMouseActionsHandlers();
    }

    public void update(boolean validate) {
	/*ParallelTransition p = new ParallelTransition();
	KeyFrame keyFrameLinkInit;
	KeyFrame keyFramePoidsInit;
	KeyFrame keyFrameLinkFin;
	KeyFrame keyFramePoidsFin;
	Timeline timeline1 = new Timeline();
	Timeline timeline2 = new Timeline();*/
	if (validate) {
	    /*keyFrameLinkInit = new KeyFrame(Duration.ZERO,
					     new KeyValue(link.strokeProperty(), Color.BLACK));
	    keyFramePoidsInit = new KeyFrame(Duration.ZERO,
						 new KeyValue(poids.fillProperty(), Color.BLACK));
	    keyFrameLinkFin = new KeyFrame(Duration.millis(4000),
						 new KeyValue(link.strokeProperty(), Color.RED));
	    keyFramePoidsFin = new KeyFrame(Duration.millis(4000),
					    new KeyValue(poids.fillProperty(), Color.RED));
	    timeline1.getKeyFrames().addAll(keyFrameLinkInit,keyFrameLinkFin);
	    timeline2.getKeyFrames().addAll(keyFramePoidsInit,keyFramePoidsFin);
	    p.getChildren().addAll(timeline1,timeline2);
	    System.out.println("h1");
	    p.play();*/
	    link.setStroke(Color.RED);
	    poids.setFill(Color.RED);
	} else {
	    /*keyFrameLinkInit = new KeyFrame(Duration.ZERO,
					     new KeyValue(link.strokeProperty(), Color.RED));
	    keyFramePoidsInit = new KeyFrame(Duration.ZERO,
						 new KeyValue(poids.fillProperty(), Color.RED));
	    keyFrameLinkFin = new KeyFrame(Duration.millis(4000),
						 new KeyValue(link.strokeProperty(), Color.BLACK));
	    keyFramePoidsFin = new KeyFrame(Duration.millis(4000),
						new KeyValue(poids.fillProperty(), Color.BLACK));
	    timeline1.getKeyFrames().addAll(keyFrameLinkInit,keyFrameLinkFin);
	    timeline2.getKeyFrames().addAll(keyFramePoidsInit,keyFramePoidsFin);
	    p.getChildren().addAll(timeline1,timeline2);
	    System.out.println("h2");
	    p.play();*/
	    link.setStroke(Color.BLACK);
	    poids.setFill(Color.BLACK);
	}
    }

    public void addMouseActionsHandlers() {
	link.addEventHandler(MouseEvent.MOUSE_MOVED, moved);
	link.addEventHandler(MouseEvent.MOUSE_EXITED, exited);
	link.addEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
	poids.addEventHandler(MouseEvent.MOUSE_MOVED, moved);
	poids.addEventHandler(MouseEvent.MOUSE_EXITED, exited);
	poids.addEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
    }

    public void removeMouseActionsHandlers() {
	link.removeEventHandler(MouseEvent.MOUSE_MOVED, moved);
	link.removeEventHandler(MouseEvent.MOUSE_EXITED, exited);
	link.removeEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
	poids.removeEventHandler(MouseEvent.MOUSE_MOVED, moved);
	poids.removeEventHandler(MouseEvent.MOUSE_EXITED, exited);
	poids.removeEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
    }
    
    public abstract void setProperties();
    public abstract void setPoidsProperties();
}
