package parsexml;

import view.graphical.IG;
import view.graphical.TextSommet;
import view.graphical.SommetUI;
import view.graphical.SommetCercle;
import view.graphical.SommetRectangle;
import view.graphical.LinkUI;
import view.graphical.Arete;
import view.graphical.Arc;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;


public class ParserXML {

    public int getAttrInteger(Element e, String attr) {
	return Integer.parseInt(e.getAttribute(attr));
    }
    
    public double getAttrDouble(Element e,String attr){
	return Double.parseDouble(e.getAttribute(attr));
    }

    //chargement
    public void open(IG ig, String filename) {
	final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	try {
	    final DocumentBuilder builder = factory.newDocumentBuilder();
	    final Document document = builder.parse(new File(filename));
	    final Element racine = document.getDocumentElement();
	    final NodeList noeuds = racine.getChildNodes();
	    for (int i=0; i<noeuds.getLength(); i++) {
		Node n = noeuds.item(i);
		if (n.getNodeType() == Node.ELEMENT_NODE){
		    final Element e = (Element)n;
		    if (n.getNodeName().equals("cercle")) {
			SommetCercle sc = new SommetCercle();
			ig.controller.createDataAndAssociate(sc);
			sc.nb = getAttrInteger(e,"id");
			Circle c = (Circle)sc.shape;
			c.setCenterX(getAttrDouble(e,"x"));
			c.setCenterY(getAttrDouble(e,"y"));
			sc.setTextProperties();// Pour bind le nom du sommet aux bonnes coordonnées(qd je bouge le sommet alors le nom du sommet bouge aussi
			c.setRadius(getAttrDouble(e,"radius"));
			ig.pane.getChildren().add(c);// Pour ajouter au pane graphique
			final Element name = (Element)e.getElementsByTagName("name").item(0);
			TextSommet tx = new TextSommet(sc);
			sc.name = tx;
			tx.setText(name.getTextContent());
			tx.setX(getAttrDouble(name,"x"));
			tx.setY(getAttrDouble(name,"y"));
			sc.setMouseActions();//Pour reagir au curseur de la souris, pour le clic droit du menu...
			sc.addDraggableHandlers();// Pour le deplacer
			sc.setTextProperties();
			ig.pane.getChildren().add(tx);

			ig.listSommet.add(sc);
		    }
		    if (n.getNodeName().equals("rectangle")) {
			SommetRectangle sr = new SommetRectangle();
			ig.controller.createDataAndAssociate(sr);
			sr.nb = getAttrInteger(e,"id");
			Rectangle r = (Rectangle)sr.shape;
			r.setX(getAttrDouble(e,"x"));
			r.setY(getAttrDouble(e,"y"));
			r.setWidth(getAttrDouble(e,"width"));
			r.setHeight(getAttrDouble(e,"height"));
			sr.setTextProperties();
			ig.pane.getChildren().add(r);
			final Element name = (Element)e.getElementsByTagName("name").item(0);
			TextSommet tx = new TextSommet(sr);
			sr.name = tx;
			tx.setText(name.getTextContent());
			tx.setX(getAttrDouble(name,"x"));
			tx.setY(getAttrDouble(name,"y"));
			sr.setMouseActions();
			sr.addDraggableHandlers();
			sr.setTextProperties();
			ig.pane.getChildren().add(tx);

			ig.listSommet.add(sr);
		    }
		    if (n.getNodeName().equals("arete")) {
			Arete arete = new Arete();
			Line line = (Line)arete.link;
			line.setStartX(getAttrDouble(e,"startX"));
			line.setStartY(getAttrDouble(e,"startY"));
			line.setEndX(getAttrDouble(e,"endX"));
			line.setEndY(getAttrDouble(e,"endY"));
			SommetUI sg = ig.getById(getAttrInteger(e,"sg"));
			SommetUI sd = ig.getById(getAttrInteger(e,"sd"));
			arete.sg = sg;
			arete.sd = sd;
			arete.sg.listLink.add(arete);
			arete.sd.listLink.add(arete);
			arete.S1 = sg.shape;
			arete.S2 = sd.shape;
			arete.setProperties();//Pour que l'arrete reste collée au sommets
			final Element poids = (Element)e.getElementsByTagName("cout").item(0);
			Text tx = new Text();
			tx.setText(poids.getAttribute("cout"));
			tx.setX(getAttrDouble(poids,"x"));
			tx.setY(getAttrDouble(poids,"y"));
			tx.setFont(new Font(22));
			arete.poids = tx;
			arete.setPoidsProperties(); //Pour que le texte du poids reste dependant de l'arete quand on la déplace
			ig.pane.getChildren().add(arete.link);
			ig.pane.getChildren().add(arete.poids);
			arete.link.toBack();//Pour cacher l'arete par rapport au sommet
			arete.setMouseActions();//Pour reagir a la souris
			ig.controller.createLinkAndAssociate(arete,getAttrInteger(poids,"cout"));
			ig.listLinkGlobal.add(arete);
		    }
		    if (n.getNodeName().equals("arc")) {
			Arc arc = new Arc();
			QuadCurve curve = (QuadCurve)arc.link;
			curve.setStartX(getAttrDouble(e,"startX"));
			curve.setStartY(getAttrDouble(e,"startY"));
			curve.setEndX(getAttrDouble(e,"endX"));
			curve.setEndY(getAttrDouble(e,"endY"));
			curve.setControlX(getAttrDouble(e,"controlX"));
			curve.setControlY(getAttrDouble(e,"controlY"));
			SommetUI sg = ig.getById(getAttrInteger(e,"sg"));
			SommetUI sd = ig.getById(getAttrInteger(e,"sd"));
			arc.sg = sg;
			arc.sd = sd;
			arc.sg.listLink.add(arc);
			arc.sd.listLink.add(arc);
			arc.S1 = sg.shape;
			arc.S2 = sd.shape;
			
			final Element poids = (Element)e.getElementsByTagName("cout").item(0);
			Text tx = new Text();
			tx.setText(poids.getAttribute("cout"));
			tx.setX(getAttrDouble(poids,"x"));
			tx.setY(getAttrDouble(poids,"y"));
			tx.setFont(new Font(22));
			arc.poids = tx;
			arc.setStartEnd();
			arc.setPoidsProperties();
			arc.makeDraggable();
			ig.pane.getChildren().add(arc.link);
			ig.pane.getChildren().add(arc.poids);
			arc.link.toBack();
			arc.setMouseActions();
			ig.controller.createLinkAndAssociate(arc,getAttrInteger(poids,"cout"));
			ig.listLinkGlobal.add(arc);
		    }
		}
	    }
	    //ig.controller.displaySommetsGraphe();
	    //ig.controller.displayAretesGraphe();
	} catch (final ParserConfigurationException e) {
	    e.printStackTrace();
	} catch (final SAXException e) {
	    e.printStackTrace();
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }




    //sauvegarde
    public void save(IG ig, String filename) {
	final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	try {
	    //création d'un parseur
	
	    final DocumentBuilder builder = factory.newDocumentBuilder();
	    		
	    //création d'un Document
	   
	    final Document document= builder.newDocument();
					
	  
	    //création de l'Element racine

	    final Element racine = document.createElement("graphe");
	    document.appendChild(racine);			
			
	    //création d'un commentaire
	    final Comment commentaire = document.createComment("Création d'un graphe");
	    racine.appendChild(commentaire);
	    
	    for(SommetUI s : ig.listSommet){
		if(s instanceof SommetCercle){
		    SommetCercle sc = (SommetCercle)s;
		    Circle c = ((Circle)sc.shape);
		    //création du cercle
		    final Element cercle = document.createElement("cercle");
		    cercle.setAttribute("id",Integer.toString(s.nb));
		    cercle.setAttribute("x",Double.toString(c.getCenterX()));
		    cercle.setAttribute("y",Double.toString(c.getCenterY()));
		    cercle.setAttribute("radius",Double.toString(c.getRadius()));
		    racine.appendChild(cercle);
		    {
			//création du nom du cercle
			TextSommet name = sc.name;
			final Element nom = document.createElement("name");
			nom.appendChild(document.createTextNode(name.getText())); 
			nom.setAttribute("x",Double.toString(name.getX()));
			nom.setAttribute("y",Double.toString(name.getY()));
			cercle.appendChild(nom);
		    }
		} else {
		    SommetRectangle sr = (SommetRectangle)s;
		    Rectangle r = ((Rectangle)sr.shape);
		    //création du rectangle
		    final Element rectangle = document.createElement("rectangle");
		    rectangle.setAttribute("id",Integer.toString(s.nb));
		    rectangle.setAttribute("x",Double.toString(r.getX()));
		    rectangle.setAttribute("y",Double.toString(r.getY()));
		    rectangle.setAttribute("width",Double.toString(r.getWidth()));
		    rectangle.setAttribute("height",Double.toString(r.getHeight()));
		    racine.appendChild(rectangle);
		    {
			TextSommet name = sr.name;
			//création du nom du rectangle
			final Element nom = document.createElement("name");
			nom.appendChild(document.createTextNode(name.getText()));
			nom.setAttribute("x",Double.toString(name.getX()));
			nom.setAttribute("y",Double.toString(name.getY()));
			rectangle.appendChild(nom);
		    }
		}
	    }
			
	    for(LinkUI l : ig.listLinkGlobal){
		if(l instanceof Arete){
		    Arete ar = (Arete)l;
		    Line line = ((Line)ar.link);
		    //création d'une arête
		    final Element arete = document.createElement("arete");
		    arete.setAttribute("startX",Double.toString(line.getStartX()));		    
		    arete.setAttribute("startY",Double.toString(line.getStartY()));
		    arete.setAttribute("endX",Double.toString(line.getEndX()));
		    arete.setAttribute("endY",Double.toString(line.getEndY()));
		    arete.setAttribute("sg",Integer.toString(ar.sg.nb));
		    arete.setAttribute("sd",Integer.toString(ar.sd.nb));
		    racine.appendChild(arete);
		    {
			Text cout = ar.poids;
			//création du cout de l'arête	
			final Element coutArete = document.createElement("cout");
		        coutArete.setAttribute("x",Double.toString(cout.getX()));
			coutArete.setAttribute("y",Double.toString(cout.getY()));
			coutArete.setAttribute("cout",cout.getText());
			arete.appendChild(coutArete);
		    }
				
		} else{
		    Arc ar = (Arc)l;
		    QuadCurve curve = ((QuadCurve)ar.link);
		    //création de l'arc
		    final Element arc = document.createElement("arc");
		    arc.setAttribute("startX",Double.toString(curve.getStartX()));
		    arc.setAttribute("startY",Double.toString(curve.getStartY()));
		    arc.setAttribute("endX",Double.toString(curve.getEndX()));
		    arc.setAttribute("endY",Double.toString(curve.getEndY()));
		    arc.setAttribute("controlX",Double.toString(curve.getControlX()));
		    arc.setAttribute("controlY",Double.toString(curve.getControlY()));
		    arc.setAttribute("sg",Integer.toString(ar.sg.nb));
		    arc.setAttribute("sd",Integer.toString(ar.sd.nb));
		    racine.appendChild(arc); //on l'ajoute à la balise racine
		    {
			Text cout = ar.poids;
			//création du cout de l'arc	
			final Element coutArc = document.createElement("cout");
			coutArc.setAttribute("x",Double.toString(cout.getX()));
			coutArc.setAttribute("y",Double.toString(cout.getY()));
			coutArc.setAttribute("cout",cout.getText());
			arc.appendChild(coutArc); //on l'ajoute à l'arc
		    }
		}
	    }
		
	    
	    //affichage
	
	    final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    final Transformer transformer = transformerFactory.newTransformer();
	    final DOMSource source = new DOMSource(document);
	    final StreamResult sortie = new StreamResult(new File(filename + ".xml"));
	    //final StreamResult result = new StreamResult(System.out);
			
	    //prologue
	    transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");			
	    		
	    //formatage
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
	    //sortie
	    transformer.transform(source, sortie);	
	}
	catch (final ParserConfigurationException e) {
	    e.printStackTrace();
	}
	catch (TransformerConfigurationException e) {
	    e.printStackTrace();
	}
	catch (TransformerException e) {
	    e.printStackTrace();
	}
		
	
    }
	

}  
