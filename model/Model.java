package model;

import view.ModelObserver;

public abstract class Model {
    ModelObserver observer;

    protected void updateObserver(int nbEtapes) {
	observer.update(nbEtapes);
    }

    //pour dire que l'associé graphique de Model(précisemment le graphe mémoire) sera ModelObserver (Concretement IG)
    public void setObserver(ModelObserver m) {
	this.observer = m;
    }
}
