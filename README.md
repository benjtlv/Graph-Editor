////////////////////// GRAPH EDITOR ///////////////////////

The graph editor is a user interface whose purpose is to construct graphs, save them in XML files, and load them from XML files. The graphical framework used is JavaFx.
You got to have Java 8 if you want to use it


////// HOW TO COMPILE /////

javac Editeur.java

///// HOW TO EXECUTE /////

java Editeur

///// HOW TO USE IT /////

You can create vertices and edges both of them with two different kind of shapes. For the shapes you can create circles and rectangles, and for the edges you can create straight lines or curved lines. The button of creation are located on the top menu bar.

You can edit the size of a vertice using the scroll button of the mouse, either at the initialization, or either by clicking on it with the right button and selecting "edit size"

You can change the position of a vertice, by clicking on it without releasing the button, and dragging it wherever you want.

You can also edit the text inside a vertice, either at the initialization , or either by clicking on it with the right button and selecting "edit name".

You can edit the weights on the edges, by clicking on it with the right button and selecting "edit". For the curved lines, you can adjust the form by clicking on it without releasing the button, and dragging it wherever you want.

You can execute three famous algorithms on graphs : Dijkstra most short ways, Kruskal and Prim for finding the minimal covering trees.
You just have to click with the right button on any vertice, and select the algorithm you want to execute. You have two options : either you can execute the algorithm at once and see the final result, or either you can execute it step by step. If you choose the last option, you have two buttons for the execution to go backward (prev) and go forward (next). The vertices and the edges selected by the algorithm will be colored in red. If you want to going back editing your graph, click on the stop button

Finally you can save graphs in xml format. Please do not add the .xml extension by your self. It is done automatically. You can load graphs from these files. You can also create a new graph (the editor will ask for confirmation before)
