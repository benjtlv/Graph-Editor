package view.graphical;


import model.Sommet;
import controller.Controller;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

import javafx.scene.shape.Shape;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
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
import javafx.scene.control.Menu;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import javafx.animation.ParallelTransition;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;

public abstract class SommetUI {
    Sommet data;

    public Shape shape;
    public TextSommet name;
    static int i = 0;
    public int nb;
    DropShadow dropShadow = new DropShadow(); // Pour l'effet d'ombre
    double w; // largeur du texte
    double shapeWidth;
    EventHandler<MouseEvent> pressed, dragged, showMenu, confirm, createShape, setShape, clickToSetLine, clickToSetCurve, moveToMakeLine, moved, exited;
    static boolean startLink = false;
    EventHandler<KeyEvent> charHandler, numberHandler;
    EventHandler<ScrollEvent> setShapeSize; 
    ContextMenu contextMenu = new ContextMenu(); //Menu lors du clic droit
    MenuItem edit_size = new MenuItem("edit size");
    MenuItem edit_name = new MenuItem("edit name");
    MenuItem delete = new MenuItem("delete");
    Menu dijkstra = new Menu("dijkstra");
    Menu kruskal = new Menu("kruskal");
    Menu prim = new Menu("prim");
    MenuItem continuous1 = new MenuItem("continuous");
    MenuItem stepByStep1 = new MenuItem("step by step");
    MenuItem continuous2 = new MenuItem("continuous");
    MenuItem stepByStep2 = new MenuItem("step by step");
    MenuItem continuous3 = new MenuItem("continuous");
    MenuItem stepByStep3 = new MenuItem("step by step");
    
    public LinkedList<LinkUI> listLink = new LinkedList<LinkUI>();
    final static SimpleBooleanProperty dijkstra_enabled = new SimpleBooleanProperty();
    final static SimpleBooleanProperty kruskal_enabled = new SimpleBooleanProperty();
    final static SimpleBooleanProperty prim_enabled = new SimpleBooleanProperty();

    
    public SommetUI(Shape sh){
	this.shape = sh;
	this.name = new TextSommet(this); // Pour le nom
	shape.setStroke(Color.BLACK);
	name.setFill(Color.BLACK);
	shape.setStrokeWidth(3.0);
	shape.setFill(Color.WHITESMOKE);
	nb = i;
	i++;
        name.setText(""+nb);
	dropShadow.setOffsetY(1.0);
	dropShadow.setOffsetX(1.0);
	dropShadow.setColor(Color.BLACK);
	makeDraggable();

	contextMenu.getItems().addAll(edit_size,edit_name,delete,dijkstra,kruskal,prim);
	dijkstra.getItems().addAll(continuous1,stepByStep1);
	kruskal.getItems().addAll(continuous2,stepByStep2);
	prim.getItems().addAll(continuous3,stepByStep3);
	
	final SimpleBooleanProperty edit_size_enabled = new SimpleBooleanProperty();
	edit_size.setOnAction(e -> {
		edit_size_enabled.set(true);
	    });

	SommetUI som = this;
	edit_size_enabled.addListener((observer,oldValue,newValue) -> {
		if (newValue) {
		    IG.pane.addEventHandler(ScrollEvent.SCROLL, setShapeSize);
		    shape.addEventHandler(MouseEvent.MOUSE_CLICKED, confirm);
		    name.addEventHandler(MouseEvent.MOUSE_CLICKED, confirm);
		    for (SommetUI sui : IG.listSommet) {
			if (sui != som) {
			    sui.removeMouseActionsHandlers();
			} else {
			    shape.removeEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
			    name.removeEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
			}
			sui.removeDraggableHandlers();
		    }
		    for (LinkUI lui : IG.listLinkGlobal) {
			lui.removeMouseActionsHandlers();
			if (lui instanceof Arc) {
			    ((Arc)lui).removeDraggableHandlers();
			}
		    }
		} else {
		    IG.pane.removeEventHandler(ScrollEvent.SCROLL, setShapeSize);
		    shape.removeEventHandler(MouseEvent.MOUSE_CLICKED, confirm);
		    name.removeEventHandler(MouseEvent.MOUSE_CLICKED, confirm);
		    for (SommetUI sui : IG.listSommet) {
			if (sui != som) {
			    sui.addMouseActionsHandlers();
			} else {
			    shape.addEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
			    name.addEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
			}
			sui.addDraggableHandlers();
		    }
		    for (LinkUI lui : IG.listLinkGlobal) {
			lui.addMouseActionsHandlers();
			if (lui instanceof Arc) {
			    ((Arc)lui).addDraggableHandlers();
			}
		    }
		}
		    
	    });

	charHandler = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent e) {
		    String s = name.getText();
		    setTextProperties();
		    if (e.getCode() == KeyCode.BACK_SPACE && s.length() > 0) {
			s = s.substring(0,s.length()-1);
			name.setText(s);
		    } else {
			if (e.getCode().isLetterKey() || e.getCode() == KeyCode.COMMA) {
			    s += e.getCode().getName().toLowerCase();
			    name.setText(s);
			    if (w > shapeWidth/1.2) {
				s = s.substring(0,s.length()-1);
				name.setText(s);
			    }
			}
		    }
		}
	    };

	numberHandler = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent e) {
		    String s = name.getText();
		    setTextProperties();
		    if (e.getCharacter().matches("[0-9]")) {
			s += e.getCharacter();
			name.setText(s);
			if (w > shapeWidth/1.2) {
			    s = s.substring(0,s.length()-1);
			    name.setText(s);
			}
		    }
		}
	    };
	
	final SimpleBooleanProperty edit_name_enabled = new SimpleBooleanProperty();

	edit_name.setOnAction(e -> {
		edit_name_enabled.set(true);
	    });

	edit_name_enabled.addListener((observer,oldValue,newValue) -> {
		if(newValue) {
		    IG.pane.requestFocus();
		    IG.pane.addEventHandler(KeyEvent.KEY_PRESSED, charHandler);
		    IG.pane.addEventHandler(KeyEvent.KEY_TYPED, numberHandler);
		    shape.addEventHandler(MouseEvent.MOUSE_CLICKED, confirm);
		    name.addEventHandler(MouseEvent.MOUSE_CLICKED, confirm);
		    for (SommetUI sui : IG.listSommet) {
			if (sui != som) {
			    sui.removeMouseActionsHandlers();
			} else {
			    shape.removeEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
			    name.removeEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
			}
			sui.removeDraggableHandlers();
		    }
		    for (LinkUI lui : IG.listLinkGlobal) {
			lui.removeMouseActionsHandlers();
			if (lui instanceof Arc) {
			    ((Arc)lui).removeDraggableHandlers();
			}
		    }
		} else {
		    IG.pane.removeEventHandler(KeyEvent.KEY_PRESSED, charHandler);
		    IG.pane.removeEventHandler(KeyEvent.KEY_TYPED, numberHandler);
		    shape.removeEventHandler(MouseEvent.MOUSE_CLICKED, confirm);
		    name.removeEventHandler(MouseEvent.MOUSE_CLICKED, confirm);
		    for (SommetUI sui : IG.listSommet) {
			if (sui != som) {
			    sui.addMouseActionsHandlers();
			} else {
			    shape.addEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
			    name.addEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
			}
			sui.addDraggableHandlers();
		    }
		    for (LinkUI lui : IG.listLinkGlobal) {
			lui.addMouseActionsHandlers();
			if (lui instanceof Arc) {
			    ((Arc)lui).addDraggableHandlers();
			}
		    }
		}
	    });

	SommetUI soo = this;
	continuous1.setOnAction(e-> {
		dijkstra_enabled.set(true);
		IG.stopAlgo.setDisable(false);
		IG.controller.execDijkstra(soo,false);
	    });

	stepByStep1.setOnAction(e -> {
		dijkstra_enabled.set(true);
		IG.next.setDisable(false);
		IG.stopAlgo.setDisable(false);
		IG.controller.execDijkstra(soo,true);
	    });

	dijkstra_enabled.addListener((observer,oldValue,newValue) -> {
		if (newValue) {
		    IG.algo = "dijkstra";
		    IG.cercButton.setDisable(true);
		    IG.rectButton.setDisable(true);
		    IG.lineButton.setDisable(true);
		    IG.curveButton.setDisable(true);
		    for (SommetUI sui : IG.listSommet) {
			sui.removeMouseActionsHandlers();
			sui.removeDraggableHandlers();
		    }
		    for (LinkUI lui : IG.listLinkGlobal) {
			lui.removeMouseActionsHandlers();
			if (lui instanceof Arc) {
			    ((Arc)lui).removeDraggableHandlers();
			}
		    }
		} else {
		    IG.cercButton.setDisable(false);
		    IG.rectButton.setDisable(false);
		    IG.lineButton.setDisable(false);
		    IG.curveButton.setDisable(false);
		    for (SommetUI sui : IG.listSommet) {
			sui.addMouseActionsHandlers();
			sui.addDraggableHandlers();
		    }
		    for (LinkUI lui : IG.listLinkGlobal) {
			lui.addMouseActionsHandlers();
			if (lui instanceof Arc) {
			    ((Arc)lui).addDraggableHandlers();
			}
		    }
		}
	    });

	continuous2.setOnAction(e-> {
		kruskal_enabled.set(true);
		IG.stopAlgo.setDisable(false);
		IG.controller.execKruskal(false);
	    });

	stepByStep2.setOnAction(e -> {
		kruskal_enabled.set(true);
		IG.next.setDisable(false);
		IG.stopAlgo.setDisable(false);
		IG.controller.execKruskal(true);
	    });

	kruskal_enabled.addListener((observer,oldValue,newValue) -> {
		if (newValue) {
		    IG.algo = "kruskal";
		    IG.cercButton.setDisable(true);
		    IG.rectButton.setDisable(true);
		    IG.lineButton.setDisable(true);
		    IG.curveButton.setDisable(true);
		    for (SommetUI sui : IG.listSommet) {
			sui.removeMouseActionsHandlers();
			sui.removeDraggableHandlers();
		    }
		    for (LinkUI lui : IG.listLinkGlobal) {
			lui.removeMouseActionsHandlers();
			if (lui instanceof Arc) {
			    ((Arc)lui).removeDraggableHandlers();
			}
		    }
		} else {
		    IG.cercButton.setDisable(false);
		    IG.rectButton.setDisable(false);
		    IG.lineButton.setDisable(false);
		    IG.curveButton.setDisable(false);
		    for (SommetUI sui : IG.listSommet) {
			sui.addMouseActionsHandlers();
			sui.addDraggableHandlers();
		    }
		    for (LinkUI lui : IG.listLinkGlobal) {
			lui.addMouseActionsHandlers();
			if (lui instanceof Arc) {
			    ((Arc)lui).addDraggableHandlers();
			}
		    }
		}
	    });

	continuous3.setOnAction(e-> {
		prim_enabled.set(true);
		IG.stopAlgo.setDisable(false);
		IG.controller.execPrim(soo,false);
	    });

	stepByStep3.setOnAction(e -> {
		prim_enabled.set(true);
		IG.next.setDisable(false);
		IG.stopAlgo.setDisable(false);
		IG.controller.execPrim(soo,true);
	    });

	prim_enabled.addListener((observer,oldValue,newValue) -> {
		if (newValue) {
		    IG.algo = "prim";
		    IG.cercButton.setDisable(true);
		    IG.rectButton.setDisable(true);
		    IG.lineButton.setDisable(true);
		    IG.curveButton.setDisable(true);
		    for (SommetUI sui : IG.listSommet) {
			sui.removeMouseActionsHandlers();
			sui.removeDraggableHandlers();
		    }
		    for (LinkUI lui : IG.listLinkGlobal) {
			lui.removeMouseActionsHandlers();
			if (lui instanceof Arc) {
			    ((Arc)lui).removeDraggableHandlers();
			}
		    }
		} else {
		    IG.cercButton.setDisable(false);
		    IG.rectButton.setDisable(false);
		    IG.lineButton.setDisable(false);
		    IG.curveButton.setDisable(false);
		    for (SommetUI sui : IG.listSommet) {
			sui.addMouseActionsHandlers();
			sui.addDraggableHandlers();
		    }
		    for (LinkUI lui : IG.listLinkGlobal) {
			lui.addMouseActionsHandlers();
			if (lui instanceof Arc) {
			    ((Arc)lui).addDraggableHandlers();
			}
		    }
		}
	    });
		    
		    
		    
		    
	// TOUT CES HANDLERS SONT POUR LA GESTION DE LA SOURIS LORSQUE L'ON
	// CLIQUE SUR LE BOUTON DE CREATION DU SOMMET

	
	showMenu = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent e) {
		    if (e.getButton() == MouseButton.SECONDARY) {
			contextMenu.show((Node)shape,Side.RIGHT,0,0);
		    }
		}
	    };

	confirm = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
		    if (e.getButton() == MouseButton.PRIMARY) {
			String s = name.getText();
			if (s.length() > 0) {
			    edit_size_enabled.set(false);
			    edit_name_enabled.set(false);
			}
		    }
		}
	    };

	SommetUI s = (SommetUI)this;
	delete.setOnAction(e -> {
		List l = IG.pane.getChildren();
		IG.listSommet.remove(s);
		if (IG.listSommet.size() == 1) {
		    IG.lineButton.setDisable(true);
		    IG.curveButton.setDisable(true);
		}
		l.remove(s.shape);
		l.remove(s.name);
		IG.controller.deleteData(s);
		for (LinkUI li : listLink) {
		    l.remove(li.poids);
		    l.remove(li.link);
		    for (SommetUI so : IG.listSommet) {
			if (so.listLink.contains(li)) {
			    so.listLink.remove(li);
			    IG.listLinkGlobal.remove(li);
			    IG.controller.deleteLinkAndNeighbor(s,so);
			    IG.controller.deleteLinkAndNeighbor(so,s);
			}
		    }
		}
		listLink.removeAll(listLink);
		//IG.controller.displaySommetsGraphe();
		//IG.controller.displayAretesGraphe();
	    });
    }

    public void setMouseActions(){
	moved = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent e) {
		    shape.setStroke(Color.LIME);
		    name.setFill(Color.LIME);
		    shape.setEffect(dropShadow);
		    name.setEffect(dropShadow);
		}
	    };

	exited = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent e) {
		    shape.setStroke(Color.BLACK);
		    name.setFill(Color.BLACK);
		    shape.setEffect(null);
		    name.setEffect(null);
		}
	    };
	
	addMouseActionsHandlers();
    }

    public Sommet getData() {
	return this.data;
    }
    
    public void setData(Sommet s) {
	this.data = s;
    }

    public void update(boolean validate) {
	if (validate) {
	    shape.setStroke(Color.RED);
	    name.setFill(Color.RED);
	} else {
	    shape.setStroke(Color.BLACK);
	    name.setFill(Color.BLACK);
	}
    }

    public void addMouseActionsHandlers() {
	shape.addEventHandler(MouseEvent.MOUSE_MOVED, moved);
	shape.addEventHandler(MouseEvent.MOUSE_EXITED, exited);
	shape.addEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
	name.addEventHandler(MouseEvent.MOUSE_MOVED, moved);
	name.addEventHandler(MouseEvent.MOUSE_EXITED, exited);
	name.addEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
    }

    public void removeMouseActionsHandlers() {
	shape.removeEventHandler(MouseEvent.MOUSE_MOVED, moved);
	shape.removeEventHandler(MouseEvent.MOUSE_EXITED, exited);
	shape.removeEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
	name.removeEventHandler(MouseEvent.MOUSE_MOVED, moved);
	name.removeEventHandler(MouseEvent.MOUSE_EXITED, exited);
	name.removeEventHandler(MouseEvent.MOUSE_CLICKED, showMenu);
    }
    
    public void addDraggableHandlers() {
	shape.addEventHandler(MouseEvent.MOUSE_PRESSED, pressed);
	shape.addEventHandler(MouseEvent.MOUSE_DRAGGED, dragged);
	name.addEventHandler(MouseEvent.MOUSE_PRESSED, pressed);
	name.addEventHandler(MouseEvent.MOUSE_DRAGGED, dragged);
    }

    public void removeDraggableHandlers() {
	shape.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressed);
	shape.removeEventHandler(MouseEvent.MOUSE_DRAGGED, dragged);
	name.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressed);
	name.removeEventHandler(MouseEvent.MOUSE_DRAGGED, dragged);
    }

    public abstract void setTextProperties();
    public abstract void makeDraggable();
    public abstract void setLink(String type);
}
