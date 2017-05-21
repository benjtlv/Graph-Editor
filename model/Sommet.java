package model;

import view.graphical.SommetUI;

import java.util.ArrayList;

public class Sommet {
    ArrayList<Sommet> voisins = new ArrayList<Sommet>();
    ArrayList<Link> chemin = new ArrayList<Link>();// Pour dijkstra
    SommetUI observer; //le sommet graphique associé
    int nb, taille, cout = Integer.MAX_VALUE; //taille : pour kruskal , cout : pour prim
    Sommet pere; // Pour kruskal
    
    public Sommet(int nb) {
	this.nb = nb;
	this.taille = 1;
	this.pere = this;
    }
    
    public void addVoisin(Sommet s) {
	voisins.add(s);
    }

    public void removeVoisin(Sommet s) {
	voisins.remove(s);
    }

    //Pour set le sommet graphique associé
    public void setObserver(SommetUI s) {
	this.observer = s;
    }

    //Pour changer la couleur du sommet graphique associé
    // si true -> rouge
    // si false -> noir
    public void updateObserver(boolean validate) {
	observer.update(validate);
    }

    public String toString() {
	return ""+nb;
    }

    public boolean isRacine(){
	return this.pere == this;
    }

    public Sommet racine() {
	if(this.pere != this)
	    this.pere = this.pere.racine();
	return this.pere;
    }

    public boolean find(Sommet s) {
	return this.racine() == s.racine();
    }

    public void union(Sommet s){
	if(!this.find(s)){
	    if(this.pere.taille < s.pere.taille){
		s.pere.taille += this.pere.taille;
		this.pere.pere = s.pere;
	    } else{
		this.pere.taille += s.pere.taille;
		s.pere.pere = this.pere;
	    }
	}
    }
}
