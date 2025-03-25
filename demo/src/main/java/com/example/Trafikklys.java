package com.example;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

//Importerer vinduets breddefor å tegne passende trafikklystørrelse
import static com.example.App.VEI_BREDDE;

public class Trafikklys extends Figur{
    private Group gruppe = new Group();
    private Circle grønn; //sirkel for grønn farge
    private Circle rød; //sirkel for rød farge
    //rødt eller grønt?
    private boolean status; //status false=rød true=grønn
    //vinkel
    private double vinkel;

    public Trafikklys(double xPos, double yPos, double vinkel, boolean status) {
        super(xPos, yPos);
        this.vinkel = vinkel;
        this.status = status;
    }

    //lager trafikklys gruppe
    //setter sammen svart rektangel, rød og grønn sirkel for lys
    public Group lagTrafikklys() {
        double bredde = VEI_BREDDE/4;
        double høyde = VEI_BREDDE/2;
        double padding = 10;
        //tegner boksen
        Rectangle r = new Rectangle(bredde, høyde, Color.BLACK);

        //tegner rødt og grønt lys
        rød = new Circle(bredde/2, bredde/6+padding, bredde/4);
        grønn = new Circle(bredde/2, bredde/6+(padding*3), bredde/4);
        rød.setFill(Color.rgb(255, 0, 0));
        grønn.setFill(Color.rgb(9, 74, 0));


        gruppe.getChildren().addAll(r, grønn, rød);

        gruppe.setLayoutX(xPos);
        gruppe.setLayoutY(yPos);
        gruppe.setRotate(vinkel);

        return gruppe;
    }

    //metode som snur om på status
    //og tegner gitt farge deretter
    public void endreStatus() {
        status = !status;
        gruppe.getChildren().remove(rød);
        gruppe.getChildren().remove(grønn);
        if(status) {
            grønn.setFill(Color.rgb(17, 235, 0));
            rød.setFill(Color.rgb(120, 0, 0));

        }
        else {
            rød.setFill(Color.rgb(255, 0, 0));
            grønn.setFill(Color.rgb(9, 74, 0));
        }
        gruppe.getChildren().addAll(grønn, rød);
    }

    //metode som returnerer nåværende status
    public boolean getStatus() {
        return status;
    }

    //metode som returngerer hele trafikklysgruppen
    @Override
    public Group getFigur() {
        return gruppe;
    }
}
