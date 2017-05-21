package view;

import controller.Controller;
import model.Model;

public interface ModelObserver {
  //La combinaison ModelObserver/Model est un motif Observateur/Observé classique.
  
    public void update(int nbEtapes);
}
