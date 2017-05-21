package controller;

import model.Sommet;
import model.Link;
import model.Graphe;
import view.graphical.SommetUI;
import view.graphical.LinkUI;

public class Controller {
    Graphe graphe;

    public Controller(Graphe graphe) {
	this.graphe = graphe;
    }

    public void createDataAndAssociate(SommetUI sui) {
	Sommet sdata = new Sommet(sui.nb);
	sdata.setObserver(sui);
	sui.setData(sdata);
	graphe.addSommet(sdata);
    }

    //Prend le link graphique, on prend ses deux sommets graphique, on recupere leur equivalent memoire et on creer les arete memoire (sens direct et inverse) et on ajoute les voisins
    public void createLinkAndAssociate(LinkUI lui, int poids) {
	Sommet Sg = lui.sg.getData();
	Sommet Sd = lui.sd.getData();
	Link arete = new Link(Sg,Sd,poids);
	Link areteInv = new Link(Sd,Sg,poids);
	arete.setObserver(lui);
	areteInv.setObserver(lui);
	graphe.addArete(arete);
	graphe.addArete(areteInv);
	Sg.addVoisin(Sd);
	Sd.addVoisin(Sg);
    }

    public void setCoutArete(SommetUI sg, SommetUI sd, int cout) {
	for (Link a : graphe.aretes) {
	    if ((a.start == sg.getData() && a.end == sd.getData()) || (a.start == sd.getData() && a.end == sg.getData())) {
		a.poids = cout;
	    }
	}
    }

    public void deleteData(SommetUI s) {
	graphe.removeSommet(s.getData());
    }

    public void deleteLinkAndNeighbor(SommetUI s, SommetUI so) {
	Sommet som1 = s.getData();
	Sommet som2 = so.getData();
        graphe.removeArete(som1,som2,graphe.aretes);
	som2.removeVoisin(som1);
    }
	

    public void displayData(SommetUI sui) {
	System.out.println(sui.getData());
    }

    public void displaySommetsGraphe() {
	graphe.displaySommets();
    }

    public void displayAretesGraphe() {
	graphe.displayAretes();
    }

    public void execDijkstra(SommetUI sui, boolean stepByStep) {
	graphe.dijkstra(sui.getData(),stepByStep);
    }

    public void execKruskal(boolean stepByStep) {
	graphe.kruskal(stepByStep);
    }

    public void execPrim(SommetUI sui, boolean stepByStep) {
	graphe.prim(sui.getData(),stepByStep);
    }

    public void execStepDijkstra(int advanceAlgo) {
	//System.out.println(advanceAlgo);
	graphe.displayStepDijkstra(advanceAlgo);
    }

    public void execStepKruskal(int advanceAlgo, boolean b) {
	//System.out.println(advanceAlgo);
	graphe.displayStepKruskal(advanceAlgo,b);
    }

    public void execStepPrim(int advanceAlgo, boolean b) {
	//System.out.println(advanceAlgo);
	graphe.displayStepPrim(advanceAlgo,b);
    }

    public void viderStepsDijkstra() {
	graphe.viderStepsDijkstra();
    }

    public void viderStepsKruskal() {
	graphe.viderStepsKruskal();
    }

    public void viderStepsPrim() {
	graphe.viderStepsPrim();
    }
}
