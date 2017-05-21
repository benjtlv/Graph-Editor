package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;

// Le graphe en MEMOIRE !!!
public class Graphe extends Model {
    ArrayList<Sommet> sommets = new ArrayList<Sommet>();
    public ArrayList<Link> aretes = new ArrayList<Link>();
    public ArrayList<ArrayList<Link>> stepsDijkstra = new ArrayList<ArrayList<Link>>();// La liste des chemins pour dijkstra d'un sommet vers un autre
    public ArrayList<Link> stepsKruskal = new ArrayList<Link>();
    public ArrayList<Link> stepsPrim = new ArrayList<Link>();

    //Pour chaque sommet on va savoir sa valeur de plus court chemin et son plus court chemin (chemin)
    class DataSommet {
	int valChemin = Integer.MAX_VALUE;
	ArrayList<Link> chemin = new ArrayList<Link>();

	public DataSommet(){}
	
	public DataSommet(int v) {
	    this.valChemin = v;
	}
    }

    public void vider() {
	sommets.removeAll(sommets);
	aretes.removeAll(aretes);
    }
	
    
    public void addSommet(Sommet s) {
	sommets.add(s);
    }

    public void addArete(Link a) {
	aretes.add(a);
    }

    public void removeSommet(Sommet s) {
	sommets.remove(s);
    }

    public Link getLink(Sommet s1, Sommet s2, ArrayList<Link> l) {
	Link link = null;
	for (Link a : l) {
	    if ((a.start == s1 && a.end == s2) || (a.start == s2 && a.end == s1)) {
		link = a;
	    }
	}
	return link;
    }

    public int getCout(Sommet s1, Sommet s2) {
	Link link = getLink(s1,s2,aretes);
	return link.poids;
    }

    public void removeArete(Sommet s, Sommet so, ArrayList<Link> l) {
	Link link = getLink(s,so,l);
	if (link != null) 
	    l.remove(link);
    }

    public void displaySommets() {
	for (int i=0; i<sommets.size(); i++) {
	    System.out.print(sommets.get(i)+" -> voisins : "+sommets.get(i).voisins+", ");
	}
	System.out.println();
    }

    public void displayAretes() {
	System.out.println("aretes : " +aretes);
    }

    //Pour avoir le sommet qui a la valeur de plus cours chemin quand on fait dijkstra
    public Sommet minDist(HashMap<Sommet,DataSommet> delta,ArrayList<Sommet> T) {
	int min = Integer.MAX_VALUE;
	Sommet s = null;
	for (Sommet so : T) {
	    if (delta.get(so).valChemin <= min) {
		min = delta.get(so).valChemin;
		s = so;
	    }
	}
	return s;
    }

    //Pour pouvoir faire des modifs lors de dijkstra sans modifier les données du graphe en memoires
    public ArrayList<Sommet> cloneSommets(){
	ArrayList<Sommet> nouv = new ArrayList<Sommet>();
	for (Sommet s : sommets) {
	    nouv.add(s);
	}
	return nouv;
    }

    public ArrayList<Link> cloneAretes(ArrayList<Link> l){
	ArrayList<Link> nouv = new ArrayList<Link>();
	for (Link a : l) {
	    nouv.add(a);
	}
	return nouv;
    }

    public boolean deltaContainsLink(DataSommet dv, HashMap<Sommet,DataSommet> map, Link li) {
	for (Sommet s : map.keySet()) {
	    DataSommet dts = map.get(s);
	    if (dts != dv) {
		ArrayList<Link> way = dts.chemin;
		for (Link l : way) {
		    if (l==li) {
			return true;
		    }
		}
	    }
	}
	return false;
    }

    public void colorierChemin(DataSommet dv, HashMap<Sommet,DataSommet> delta, ArrayList<Link> chemin, boolean validate) {
	for (Link l : chemin) {
	    if (validate==false) {
		if (!deltaContainsLink(dv,delta,l)) { //Si l'arete n'appartient pas a un autre plus court chemin
		    l.updateObserver(validate); // on colorie en noir (pour "effacer" graphiquement le chemin
		}
	    } else {
		l.updateObserver(validate);//colorie en rouge
	    }
	}
    }

    public void displayStepDijkstra(int indice) {
	//Tout remettre en noir
	for (Link l : aretes) {
	    l.updateObserver(false);
	    l.start.updateObserver(false);
	    l.end.updateObserver(false);
	}
	//et colorier le bon chemin quand on clique sur next ou prev
	ArrayList<Link> chemin = stepsDijkstra.get(indice);
	for (Link l : chemin) {
	    l.updateObserver(true);
	    l.start.updateObserver(true);
	    l.end.updateObserver(true);
	}
    }

    public void viderStepsDijkstra() {
	stepsDijkstra.removeAll(stepsDijkstra);
    }
	    
    public void dijkstra(Sommet s, boolean stepByStep) {
	//updateObserver(State.RUNNING);
	ArrayList<Sommet> sommetsTmp = cloneSommets();
	ArrayList<Link> aretesTmp = cloneAretes(aretes);
        HashMap<Sommet,DataSommet> delta = new HashMap<Sommet,DataSommet>();
	delta.put(s,new DataSommet(0));
	for (Sommet so : sommets) {
	    if (so != s) {
		delta.put(so,new DataSommet());
	    }
	}
	while (sommetsTmp.size() > 0) {
	    Sommet x = minDist(delta,sommetsTmp);
	    if (!stepByStep) 
		x.updateObserver(true); //colorier le sommet x en rouge
	    for (Sommet v : x.voisins) {
		DataSommet dv = delta.get(v);
		DataSommet dx = delta.get(x);
		int deltaY = dv.valChemin;
		int deltaX = dx.valChemin;
		int vxv = getCout(x,v);
		if (deltaY > deltaX + vxv) {
		    dv.valChemin = deltaX + vxv;
		    //System.out.println("old way from " + s.nb + " to " + v.nb + " : " + dv.chemin);
		    if (!stepByStep) 
			colorierChemin(dv,delta,dv.chemin,false);
		    dv.chemin.removeAll(dv.chemin);//Une fois le nouveau + court chemin decouvert on supprime lancien
		    dv.chemin.addAll(dx.chemin);//Le nouveau plus court chemin de v devient celui de x ------->
		    dv.chemin.add(getLink(x,v,aretesTmp));//-------> plus l'arete x-v
		    System.out.println("new way from " + s.nb + " to " + v.nb + " : " + dv.chemin);
		    ArrayList<Link> clonedv = cloneAretes(dv.chemin);//Pour que le chemin en question soit vraiment unique (sinon le dernier plus court chemin va ecraser tous les précédents découverts)
		    stepsDijkstra.add(clonedv);
		    if (!stepByStep)
			colorierChemin(dv,delta,dv.chemin,true);
		}
	    }
	    sommetsTmp.remove(x);
	}
	updateObserver(stepsDijkstra.size());
	//updateObserver(State.STOP);
    }

    public void viderStepsKruskal() {
	stepsKruskal.removeAll(stepsKruskal);
    }

    public void displayStepKruskal(int indice, boolean b) {
	Link l = stepsKruskal.get(indice);
	l.updateObserver(b);
	l.start.updateObserver(b);
	l.end.updateObserver(b);
    }

    public void kruskal(boolean stepByStep) {
	Collections.sort(aretes, new Comparator<Link>(){
		@Override
		public int compare(Link l1, Link l2) {
		    return ((Integer)l1.poids).compareTo(l2.poids);
		}
	    });
	for (Link l : aretes) {
	    Sommet st = l.start;
	    Sommet en = l.end;
	    if (!st.find(en)) {
		if (!stepByStep) {
		    l.updateObserver(true);
		    st.updateObserver(true);
		    en.updateObserver(true);
		}
		stepsKruskal.add(l);
		st.union(en);
	    }
	}
	System.out.println(stepsKruskal);
	updateObserver(stepsKruskal.size());
    }

    public void viderStepsPrim() {
	stepsPrim.removeAll(stepsPrim);
    }

    public void displayStepPrim(int indice, boolean b) {
	Link l = stepsPrim.get(indice);
	l.updateObserver(b);
	l.start.updateObserver(b);
	l.end.updateObserver(b);
    }

    public void prim(Sommet s, boolean stepByStep) {
	HashMap<Sommet,Sommet> pred = new HashMap<Sommet,Sommet>();
	for (Sommet so : sommets) {
	    pred.put(so,null);
	}
	s.cout = 0;
	LinkedList<Sommet> file = new LinkedList<Sommet>();
	for (Sommet so : sommets) {
	    if (so != s) {
		file.add(so);
	    }
	}
	file.addFirst(s);
	while (!file.isEmpty()) {
	    Sommet t = file.getFirst();
	    file.removeFirst();
	    for (Sommet v : t.voisins) {
		if (file.contains(v) && v.cout > getCout(t,v)) {
		    v.cout = getCout(t,v);
		    Link l = getLink(t,v,aretes);
		    /*if (!stepByStep) {
			l.updateObserver(true);
			t.updateObserver(true);
			v.updateObserver(true);
		    }*/
		    //stepsPrim.add(l);
		    pred.replace(v,t);
		    for (int i=0; i<file.size(); i++) {
			Collections.sort(file, new Comparator<Sommet>() {
				@Override
				public int compare(Sommet s1, Sommet s2) {
				    return ((Integer)s1.cout).compareTo(s2.cout);
				}
			    });
		    }
		}
	    }
	}
	for (Sommet som : pred.keySet()) {
	    if (pred.get(som) != null) {
		Link l = getLink(som,pred.get(som),aretes);
		if (!stepByStep) {
		    l.updateObserver(true);
		    som.updateObserver(true);
		    pred.get(som).updateObserver(true);
		}
		stepsPrim.add(l);
	    }
	}
	updateObserver(stepsPrim.size());
	for (Sommet so : sommets) {
	    so.cout = Integer.MAX_VALUE;
	}
    }
		
}
