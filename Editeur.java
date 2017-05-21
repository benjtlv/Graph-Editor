import view.graphical.IG;
import controller.Controller;
import model.Graphe;
import parsexml.ParserXML;

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType; 

import java.util.Optional;


public class Editeur extends Application {
    Scene scene;
    BorderPane bp;
    IG ig;
    Controller controller;
    Graphe graphe;
    ParserXML parser = new ParserXML();
    MenuBar menuBar;
    Menu file;
    MenuItem open;
    MenuItem neww;
    MenuItem save;
    
    @Override
    public void start(Stage stage) {
	graphe = new Graphe();
	controller = new Controller(graphe);
	ig = new IG(controller);

	graphe.setObserver(ig);

	bp = new BorderPane();
	menuBar = new MenuBar();
	file = new Menu("File");
	open = new MenuItem("open");
	neww = new MenuItem("new graph");
	save = new MenuItem("save");
	menuBar.getMenus().add(file);
	file.getItems().addAll(open,neww,save);
	bp.setTop(menuBar);
	bp.setCenter(ig);

	open.setOnAction(e -> {
		FileChooser fc = new FileChooser();
		fc.setTitle("Open a graph");
		fc.getExtensionFilters().add(new ExtensionFilter("xml Files","*.xml"));
		File file = fc.showOpenDialog(stage);
		if (file != null) {
		    graphe.vider();
		    ig.vider();
		    parser.open(ig,file.getName());
		    if (ig.listSommet.size() > 1) {
			ig.lineButton.setDisable(false);
			ig.curveButton.setDisable(false);
		    }
		}
	    });
	
	neww.setOnAction(e -> {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Create a new graph?");
		alert.setContentText("Are you sure ? All the modifications will be erased");
		Optional<ButtonType> result = alert.showAndWait();
		if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
		    graphe.vider();
		    ig.vider();
		}
	    });
	
	save.setOnAction(e -> {
		FileChooser fc = new FileChooser();
		fc.setTitle("Save a graph");
		fc.getExtensionFilters().add(new ExtensionFilter("xml Files","*.xml"));
		File file = fc.showSaveDialog(stage);
		if (file != null) {
		    String name = file.getName();
		    if (name.length() == 4 && name.substring(name.length()-4,name.length()).equals(".xml")) {
			name = name.substring(0,name.length()-4);
		    }
		    parser.save(ig,name);
		}
	    });

	stage.setTitle("Editeur de graphes");
	scene = new Scene(bp);
	stage.setScene(scene);
	stage.show();
    }
	

    public static void main(String[] args){
	launch(args);
    }
}
