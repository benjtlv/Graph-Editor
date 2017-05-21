package view.graphical;

import controller.Controller;
import model.Model;
import view.ModelObserver;

import java.io.FileInputStream;

import java.util.List;
import java.util.ArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import javafx.scene.shape.Sphere;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.shape.CullFace;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.PerspectiveCamera;

//Correspond à la fenêtre

public class IG extends BorderPane implements ModelObserver {
    //Proprietes par defaut pour les shapes
    public static final int RADIUS = 25;
    public static final int WIDTH = 55;
    public static final int HEIGHT = 35;

    Stage stage;
    public static Controller controller;
    ToolBar toolBar = new ToolBar();

    public static Pane pane = new Pane();//"canvas" pour afficher les nodes
    static ToggleButton cercButton = new ToggleButton(null);
    static ToggleButton rectButton = new ToggleButton(null);
    public static ToggleButton lineButton = new ToggleButton(null);
    public static ToggleButton curveButton = new ToggleButton(null);
    //static ToggleButton _3D = new ToggleButton(null);
    static Button prev = new Button("prev");
    static Button next = new Button("next");
    static Button stopAlgo = new Button("stop");
    GraphicsContext context;
    //EventHandler<KeyEvent> turnLeft, turnRigth, turnUp, turnDown;
    PerspectiveCamera camera;
    double angleRot;
    public static ArrayList<SommetUI> listSommet = new ArrayList<SommetUI>();
    public static ArrayList<LinkUI> listLinkGlobal = new ArrayList<LinkUI>();
    static SommetUI s;
    static LinkUI link;
    int advanceAlgo = 0;
    int nbEtapes;
    static String algo;
    
    public IG(Controller controller) {
	this.controller = controller;
	final int TAILLE = 600;
	setTop(toolBar);
	setCenter(pane);

	prev.setDisable(true);
	next.setDisable(true);
	stopAlgo.setDisable(true);
	
	lineButton.setDisable(true); //désactiver par défaut car 0 formes mises dans le pane
	curveButton.setDisable(true);//idem
	
	pane.setPrefSize(TAILLE, TAILLE-100);
	
	/////////////////////// Creation le bouton pour créer un cercle
	
	Canvas cercCanvas = new Canvas(25, 25);//partie graphique du tooglebutton du cercle
        context = cercCanvas.getGraphicsContext2D();
	context.setLineWidth(2.0);
        context.setStroke(Color.LIME);
        context.strokeOval(5, 5, 15, 15);
        cercButton.setGraphic(cercCanvas); //pour ajouter le canvas au bouton
        toolBar.getItems().add(cercButton);
        //cercButton.setToggleGroup(toggleGroup);

	//////////////////////
	
	{
	    cercButton.selectedProperty().addListener((observer, oldValue, newValue) -> {
		    if (newValue) { // Si on l'allume
			cercButton.setDisable(true);
			rectButton.setDisable(true);
			lineButton.setDisable(true);
			curveButton.setDisable(true);
			s = (SommetCercle)new SommetCercle();
			listSommet.add(s);
			if(listSommet.size()==2) {
			    lineButton.setDisable(false);//j'active les arretes
			    curveButton.setDisable(false);//j'active les arcs
			}
			
			s.shape.setVisible(false);
			s.name.setVisible(false);
			
			pane.getChildren().add(s.shape);
			pane.getChildren().add(s.name);

			addPaneHandlers(s);//ajouter des gestionnaires d'évènements(handlers) dans le canvas (pane)
		    } else {
		        if(listSommet.size()>=2) {
			    lineButton.setDisable(false);
			    curveButton.setDisable(false);
			}
		        cercButton.setDisable(false);
			rectButton.setDisable(false);
			removePaneHandlers(s);
		    }
		});
	}

	
	/////////////////////// Creation le bouton pour créer un rectangle
	
	Canvas rectCanvas = new Canvas(25, 25);
        context = rectCanvas.getGraphicsContext2D();
	context.setLineWidth(2.0);
        context.setStroke(Color.RED);
        context.strokeRect(3, 7, 17, 13);
        rectButton.setGraphic(rectCanvas);
        toolBar.getItems().add(rectButton);
        //rectButton.setToggleGroup(toggleGroup);

	//////////////////////
	
	{

	    // Gestion des actions lorsque l'on allume/eteint le toggleButton cercButton
	    
	    rectButton.selectedProperty().addListener((observer, oldValue, newValue) -> {
		    if (newValue) { // Si on l'allume
			cercButton.setDisable(true);
			rectButton.setDisable(true);
			lineButton.setDisable(true);
			curveButton.setDisable(true);
			s = (SommetRectangle)new SommetRectangle();
			listSommet.add(s);
			s.shape.setVisible(false);
			s.name.setVisible(false);
			
			pane.getChildren().add(s.shape);
			pane.getChildren().add(s.name);
			
			addPaneHandlers(s);
		    } else {
			if(listSommet.size()>=2) {
			    lineButton.setDisable(false);
			    curveButton.setDisable(false);
			}
		        cercButton.setDisable(false);
			rectButton.setDisable(false);
			removePaneHandlers(s);
		    }
		});
	}

	/////////////////////// Creation le bouton pour créer une arête
	
	Canvas lineCanvas = new Canvas(25, 25);
        context = lineCanvas.getGraphicsContext2D();
	context.setLineWidth(2.0);
        context.setStroke(Color.BLACK);
	context.strokeOval(1,14,3,3);
        context.strokeLine(1, 15, 22, 15);
	context.strokeOval(21,14,3,3);
        lineButton.setGraphic(lineCanvas);
        toolBar.getItems().add(lineButton);
        //rectButton.setToggleGroup(toggleGroup);

	//////////////////////

	{
	    lineButton.selectedProperty().addListener((observer,oldValue,newValue) -> {
		    if (newValue) {
			if (listSommet.size() > 1) {
			    lineButton.setDisable(true);
			    cercButton.setDisable(true);
			    rectButton.setDisable(true);
			    curveButton.setDisable(true);
			    for (SommetUI s : listSommet) {
				s.removeDraggableHandlers();
				s.shape.removeEventHandler(MouseEvent.MOUSE_CLICKED, s.showMenu);
				s.shape.addEventHandler(MouseEvent.MOUSE_CLICKED, s.clickToSetLine);
				s.name.addEventHandler(MouseEvent.MOUSE_CLICKED, s.name.textClickToSetLine);
			    }
			}
		    } else {
			if (listSommet.size() > 1) {
			    lineButton.setDisable(false);
			    cercButton.setDisable(false);
			    rectButton.setDisable(false);
			    curveButton.setDisable(false);
			    for (SommetUI s : listSommet) {
				s.addDraggableHandlers();
				s.shape.addEventHandler(MouseEvent.MOUSE_CLICKED, s.showMenu);
				s.shape.removeEventHandler(MouseEvent.MOUSE_CLICKED, s.clickToSetLine);
				s.name.removeEventHandler(MouseEvent.MOUSE_CLICKED, s.name.textClickToSetLine);
			    }
			}
		    }
		});
	}

	/////////////////////// Creation le bouton pour créer un arc
	
	Canvas curveCanvas = new Canvas(25, 25);
        context = curveCanvas.getGraphicsContext2D();
	context.setLineWidth(2.0);
        context.setStroke(Color.BLACK);
	context.strokeArc(-6,15,80,25,90,90,ArcType.OPEN);
        curveButton.setGraphic(curveCanvas);
        toolBar.getItems().add(curveButton);
        //curveButton.setToggleGroup(toggleGroup);

	//////////////////////

	{
	    curveButton.selectedProperty().addListener((observer,oldValue,newValue) -> {
		    if (newValue) {
			if (listSommet.size() > 1) {
			    curveButton.setDisable(true);
			    cercButton.setDisable(true);
			    rectButton.setDisable(true);
			    lineButton.setDisable(true);
			    for (SommetUI s : listSommet) {
				s.removeDraggableHandlers();
				s.shape.removeEventHandler(MouseEvent.MOUSE_CLICKED, s.showMenu);
				s.shape.addEventHandler(MouseEvent.MOUSE_CLICKED, s.clickToSetCurve);
				s.name.addEventHandler(MouseEvent.MOUSE_CLICKED, s.name.textClickToSetCurve);
			    }
			}
		    } else {
			if (listSommet.size() > 1) {
			    curveButton.setDisable(false);
			    lineButton.setDisable(false);
			    cercButton.setDisable(false);
			    rectButton.setDisable(false);
			    for (SommetUI s : listSommet) {
				s.addDraggableHandlers();
				s.shape.addEventHandler(MouseEvent.MOUSE_CLICKED, s.showMenu);
				s.shape.removeEventHandler(MouseEvent.MOUSE_CLICKED, s.clickToSetCurve);
				s.name.removeEventHandler(MouseEvent.MOUSE_CLICKED, s.name.textClickToSetCurve);
			    }
			}
		    }
		});
	    
	    toolBar.getItems().addAll(prev,stopAlgo,next);
	    
	    prev.setOnAction(e -> {
		    advanceAlgo--;
		    if (advanceAlgo==0) {
			prev.setDisable(true);
		    }
		    if (algo.equals("dijkstra"))
			controller.execStepDijkstra(advanceAlgo);
		    if (algo.equals("kruskal"))
			controller.execStepKruskal(advanceAlgo,false);
		    if (algo.equals("prim"))
			controller.execStepPrim(advanceAlgo,false);
		    next.setDisable(false);
		});

	    next.setOnAction(e -> {
		    advanceAlgo++;
		    if (algo.equals("dijkstra"))
			controller.execStepDijkstra(advanceAlgo);
		    if (algo.equals("kruskal"))
			controller.execStepKruskal(advanceAlgo,true);
		    if (algo.equals("prim"))
			controller.execStepPrim(advanceAlgo,true);
		    if (advanceAlgo==nbEtapes-1) {
			advanceAlgo++;
			next.setDisable(true);
		    }
		    prev.setDisable(false);
		});

	    stopAlgo.setOnAction(e -> {
		    advanceAlgo = 0;
		    if (algo.equals("dijkstra")) {
			controller.viderStepsDijkstra();
			SommetUI.dijkstra_enabled.set(false);
		    }
		    if (algo.equals("kruskal")) {
			controller.viderStepsKruskal();
			SommetUI.kruskal_enabled.set(false);
		    }
		    if (algo.equals("prim")) {
			controller.viderStepsPrim();
			SommetUI.prim_enabled.set(false);
		    }
		    for (SommetUI sui : listSommet) {
			sui.shape.setStroke(Color.BLACK);
			sui.name.setFill(Color.BLACK);
		    }
		    for (LinkUI lui : listLinkGlobal) {
			lui.link.setStroke(Color.BLACK);
			lui.poids.setFill(Color.BLACK);
		    }
		    prev.setDisable(true);
		    next.setDisable(true);
		    stopAlgo.setDisable(true);
		});
	    
	    /*
	    EventHandler<KeyEvent> turnLeft = new EventHandler<KeyEvent>(){
		    @Override
		    public void handle(KeyEvent e) {
			KeyCode code = e.getCode();
			RotateTransition rt = new RotateTransition(Duration.millis(200),pane);
			if (code == KeyCode.LEFT) {
			    rt.setAxis(new Point3D(1,0,0));
			    rt.setFromAngle(angleRot);
			    rt.setToAngle(angleRot-1);
			    rt.play();
			    angleRot -= 1;
			}
		    }
		};*/
	    /*
	    /////////////////////// Creation le bouton pour la 3D
	
	    Canvas _3DCanvas = new Canvas(25, 25);
	    context = _3DCanvas.getGraphicsContext2D();
	    context.setLineWidth(1.0);
	    context.setStroke(Color.BLUE);
	    context.strokeText("3D",4,18);
	    _3D.setGraphic(_3DCanvas);
	    toolBar.getItems().add(_3D);

	    //////////////////////

	    {
		_3D.selectedProperty().addListener((observer,oldValue,newValue) -> {
			Sphere[] sphere = null;
			Box[] box = null;
			if (newValue) {
			    camera = new PerspectiveCamera();
			    curveButton.setDisable(true);
			    cercButton.setDisable(true);
			    rectButton.setDisable(true);
			    lineButton.setDisable(true);
			    _3D.setDisable(true);
			    int nbCercle = 0, nbRect = 0;
			    for (SommetUI sui : listSommet) {
				if (sui instanceof SommetCercle) {
				    nbCercle++;
				}
				if (sui instanceof SommetRectangle) {
				    nbRect++;
				}
			    }
			    sphere = new Sphere[nbCercle];
			    box = new Box[nbRect];
			    int i1 = 0, i2 = 0;
			    for (SommetUI sui : listSommet) {
				if (sui instanceof SommetCercle) {
				    Circle c = ((Circle)sui.shape);
				    sphere[i1] = new Sphere(c.getRadius());
				    pane.getChildren().add(sphere[i1]);
				    TranslateTransition tt = new TranslateTransition(Duration.millis(1), sphere[i1++]);
				    tt.setToX(c.getCenterX());
				    tt.setToY(c.getCenterY());
				    tt.play();
				}
				if (sui instanceof SommetRectangle) {
				    Rectangle r = ((Rectangle)sui.shape);
				    box[i2] = new Box(r.getWidth(),r.getHeight(),30);
				    pane.getChildren().add(box[i2]);
				    TranslateTransition tt = new TranslateTransition(Duration.millis(1), box[i2++]);
				    tt.setToX(r.getX());
				    tt.setToY(r.getY());
				    tt.play();
				}
				sui.removeMouseActionsHandlers();
				sui.removeDraggableHandlers();
			    }
			    for (LinkUI lui : listLinkGlobal) {
				lui.removeMouseActionsHandlers();
				if (lui instanceof Arc) {
				    ((Arc)lui).removeDraggableHandlers();
				}
			    }
			    RotateTransition rt = new RotateTransition(Duration.millis(10000),pane);
			    rt.setAxis(new Point3D(0,1,0));
			    rt.setFromAngle(0);
			    rt.setToAngle(360);
			    rt.play();
			    _3D.setDisable(false);
			    //_3D.setSelected(false);
			} else {
			    camera = null;
			    curveButton.setDisable(false);
			    cercButton.setDisable(false);
			    rectButton.setDisable(false);
			    lineButton.setDisable(false);
			    for (int i = 0; i < sphere.length; i++) {
				pane.getChildren().remove(sphere[i]);
			    }
			    for (int i = 0; i < box.length; i++) {
				pane.getChildren().remove(box[i]);
			    }
			    for (SommetUI sui : listSommet) {
				sui.addMouseActionsHandlers();
				sui.addDraggableHandlers();
			    }
			    for (LinkUI lui : listLinkGlobal) {
				lui.addMouseActionsHandlers();
				if (lui instanceof Arc) {
				    ((Arc)lui).addDraggableHandlers();
				}
			    }
			}
		    });
	    }*/
	}
	    
    }
	
    public void addPaneHandlers(SommetUI s){ //faire que pane reagisse à certaines actions sur la souris quand on veut creer et placer une forme
	pane.addEventHandler(MouseEvent.MOUSE_MOVED, s.createShape);
	pane.addEventHandler(MouseEvent.MOUSE_DRAGGED, s.createShape);
	pane.addEventHandler(MouseEvent.MOUSE_CLICKED, s.setShape);
	pane.addEventHandler(ScrollEvent.SCROLL, s.setShapeSize);
    }

    public void removePaneHandlers(SommetUI s){
	pane.removeEventHandler(MouseEvent.MOUSE_MOVED, s.createShape);
	pane.removeEventHandler(MouseEvent.MOUSE_DRAGGED, s.createShape);
	pane.removeEventHandler(MouseEvent.MOUSE_CLICKED, s.setShape);
	pane.removeEventHandler(ScrollEvent.SCROLL, s.setShapeSize);
    }

    public void update(int nbEtapes){
	this.nbEtapes = nbEtapes;
    }
    
    public SommetUI getById(int id) {
	SommetUI som = null;
	for (SommetUI sui : listSommet) 
	    if (sui.nb == id) 
		som = sui;
	return som;
    }
    
    public void vider() {
	listSommet.removeAll(listSommet);
	listLinkGlobal.removeAll(listLinkGlobal);
	pane.getChildren().removeAll(pane.getChildren());
	SommetUI.i = 0;
    }
}
