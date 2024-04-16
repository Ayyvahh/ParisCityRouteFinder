package com.example.parisroutefinderdsaca2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static com.example.parisroutefinderdsaca2.RouteFinder.findCheapestPathDijkstra;

public class Main extends Application {

    public static Stage mainStage;
    public static Scene mainPage, secondPage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        mainStage = primaryStage; // Assign the primaryStage to the static mainStage variable

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("firstPage.fxml"));
        mainPage = new Scene(fxmlLoader.load(), 1650, 900);

        // Add stylesheet to the scene
        mainPage.getStylesheets().add(Objects.requireNonNull(getClass().getResource("stylesheet.css")).toExternalForm());
        mainStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon1.png"))));
        mainStage.setTitle("PARIS CITY ROUTE FINDER");
        mainStage.setScene(mainPage);
        mainStage.centerOnScreen();
        mainStage.setResizable(false);
        mainStage.show();
    }


    public static void switchToSecondScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("dashboard.fxml"));
        secondPage = new Scene(fxmlLoader.load(), 1650, 900);
        secondPage.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("stylesheet.css")).toExternalForm());

        mainStage.setScene(secondPage);
        mainStage.centerOnScreen();

    }



    public static void main(String[] args) {
        launch();

        //TEST
        GraphNode<String> a=new GraphNode<>("Eiffel Tower");
        GraphNode<String> b=new GraphNode<>("Arc de Triomphe");
        GraphNode<String> c=new GraphNode<>("The Louvre");
        GraphNode<String> d=new GraphNode<>("Grand Palais");
        GraphNode<String> e=new GraphNode<>("Notre-Dame Cathedral");
        GraphNode<String> f=new GraphNode<>("Champs-Élysées");
        GraphNode<String> g=new GraphNode<>("Opera Garnier");
        GraphNode<String> h=new GraphNode<>("Place de la Concorde");
        GraphNode<String> i=new GraphNode<>("River Seine");
        GraphNode<String> j=new GraphNode<>("Basilica of the Sacré-Coeur");
        GraphNode<String> k=new GraphNode<>("The Centre Pompidou");
        GraphNode<String> l=new GraphNode<>("Pont Alexandre III");
        GraphNode<String> m=new GraphNode<>("Musée d’Orsay");

        a.connectToNodeUndirected(b, 5);
        a.connectToNodeUndirected(c, 9);
        b.connectToNodeUndirected(c, 2);
        b.connectToNodeUndirected(d, 6);
        c.connectToNodeUndirected(e, 5);
        d.connectToNodeUndirected(h, 8);
        d.connectToNodeUndirected(g, 9);
        e.connectToNodeUndirected(g, 3);
        e.connectToNodeUndirected(f, 7);
        f.connectToNodeUndirected(g, 6);
        g.connectToNodeUndirected(h, 2);
        h.connectToNodeUndirected(i, 5);
        i.connectToNodeUndirected(j, 3);
        j.connectToNodeUndirected(k, 1);
        k.connectToNodeUndirected(l, 3);
        l.connectToNodeUndirected(m, 2);

        System.out.println("The cheapest path from Silver to Gold");
        System.out.println("using Dijkstra's algorithm:");
        System.out.println("-------------------------------------");

        RouteFinder.CostedPath cpa=findCheapestPathDijkstra(a,"Musée d’Orsay");

        for(GraphNode<?> n : cpa.pathList)
            System.out.println(n.data);
        System.out.println("\nThe total path cost is: "+cpa.pathCost);
    }
}