package model;

import view.graphical.LinkUI;

public class Link {
    public Sommet start;//Sommet MEMOIRE !!
    public Sommet end;// pareil
    public int poids;// cout de larete
    LinkUI observer; // Son arete graphique associée

    public Link(Sommet start, Sommet end, int poids) {
	this.start = start;
	this.end = end;
	this.poids = poids;
    }

    public void setPoids(int poids) {
	this.poids = poids;
    }

    public void setObserver(LinkUI o) {
	this.observer = o;
    }

    public void updateObserver(boolean validate) {
	observer.update(validate);
    }

    public String toString() {
	return "("+start.toString()+","+end.toString()+","+poids+")";
    }
}
