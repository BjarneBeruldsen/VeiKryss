package com.example;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

//Importerer vinduets breddefor å tegne passende trafikklystørrelse
import static com.example.App.VEI_BREDDE;

public class Trafikklys extends Figur{
    private Group gruppe = new Group();
    private Circle rød, gul, grønn;
    private Pane root;
    private int status; //status 0 = rødt, 1 = gult, 2 = grønt
    private double vinkel; // vinkel på trafikklyset

    public Trafikklys(double xPos, double yPos, double vinkel, int status) {
        super(xPos, yPos);
        this.vinkel = vinkel;
        this.status = status;
    }

    //lager trafikklys gruppe
    //setter sammen svart rektangel, rød og grønn sirkel for lys
    public Group lagTrafikklys() {
        double bredde = VEI_BREDDE/4;
        double høyde = VEI_BREDDE/2;
        double padding = 13;

        //tegner boksen
        Rectangle r = new Rectangle(bredde, høyde-2, Color.BLACK);

        //tegner rødt og grønt lys
        // NB! skal endre slik at det blir dynamisk, (ikke hardkodet/skrive -5)
        rød = new Circle(bredde/2, bredde/6+(padding)-5, bredde/4);
        gul = new Circle(bredde/2, bredde/6+(padding*2)-5, bredde/4);
        grønn = new Circle(bredde/2, bredde/6+(padding*3)-5, bredde/4);
        
        rød.setFill(Color.rgb(255, 0, 0));
        gul.setFill(Color.rgb(255,255,0));
        grønn.setFill(Color.rgb(9, 220, 0));

        gruppe.getChildren().addAll(r, rød, gul, grønn);

        gruppe.setLayoutX(xPos);
        gruppe.setLayoutY(yPos);
        gruppe.setRotate(vinkel);

        return gruppe;
    }

    //metode som endrer status (farge) på lysene
    public void endreStatus() {
        status = (status + 1) % 3;
        
        // Fjerner alle lysene
        gruppe.getChildren().removeAll(rød, gul, grønn);

        if(status == 0) { // Rødt lys
           rød.setFill(Color.rgb(255, 0, 0)); // På
           gul.setFill(Color.rgb(128, 128, 5)); // Av
           grønn.setFill(Color.rgb(9, 74, 0));  // Av
        } else if (status == 1) { // Gult lys
            rød.setFill(Color.rgb(120, 0, 0)); // Av
            gul.setFill(Color.rgb(255, 255, 0)); // På
            grønn.setFill(Color.rgb(9, 74, 0)); // Av
        } else if (status == 2) { // Grønt lys
            rød.setFill(Color.rgb(120, 0, 0)); // Av
            gul.setFill(Color.rgb(128, 128, 0)); // Av
            grønn.setFill(Color.rgb(17, 235, 0)); // På
        }
        
        // Legger til lysene igjen
        gruppe.getChildren().addAll(rød, gul, grønn);
    }

    private void oppdaterLys() {
        if (status == 0) { // Rødt lys
            rød.setFill(Color.rgb(255, 0, 0)); // På
            gul.setFill(Color.rgb(128, 128, 5)); // Av
            grønn.setFill(Color.rgb(9, 74, 0));  // Av
        } else if (status == 1) { // Gult lys
            rød.setFill(Color.rgb(120, 0, 0)); // Av
            gul.setFill(Color.rgb(255, 255, 0)); // På
            grønn.setFill(Color.rgb(9, 74, 0)); // Av
        } else if (status == 2) { // Grønt lys
            rød.setFill(Color.rgb(120, 0, 0)); // Av
            gul.setFill(Color.rgb(128, 128, 0)); // Av
            grønn.setFill(Color.rgb(17, 235, 0)); // På
        }
    }

    //setMetode for status
    public void setStatus(int nyStatus) {
        this.status = nyStatus;
        oppdaterLys();
    }

    //getMetode for status
    public int getStatus() {
        return status;
    }

    //getMetode for vinkel
    public int getVinkel() {
        return (int) vinkel;
    }

    //metode som returngerer hele trafikklysgruppen
    @Override
    public Group getFigur() {
        return gruppe;
    }
}
