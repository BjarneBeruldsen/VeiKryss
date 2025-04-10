package com.example;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

//Importerer vinduets bredde og høyde for å tegne passende bilstørrelse
import static com.example.App.VINDU_BREDDE;
import static com.example.App.VINDU_HØYDE;
import static com.example.App.VEI_BREDDE;

public class Bil extends Figur{
    //finner bredde og lengde på bil
    private final static int BIL_BREDDE = VEI_BREDDE/3;
    private final static int BIL_HØYDE = VINDU_HØYDE/8;

    //instansvariabler
    protected Color farge;
    protected Group bilGruppe;
    protected Rectangle r;
    protected double vinkel;
    protected boolean harSvingt = false;
    protected boolean harPassert = false;
    protected Trafikklys trafikklys;
    protected double startVinkel;

    /**
     *
     * @param farge Bilens farge.
     */
    /*Konstruktør som oppretter bil
    * xPos, yPos er bilens posisjon
    * farge er bilens farge,
    * vinkel er bilens vinkel
    * trafikklys er tilhørende trafikklys basert
    * på startposisjon */
    public Bil(double xPos, double yPos, Color farge, double vinkel, Trafikklys trafikklys) {
        super(xPos, yPos);
        this.farge = farge;
        this.vinkel=vinkel;
        r = this.lagBilGruppe();
        this.trafikklys = trafikklys;
        startVinkel = vinkel;
    }

    //parameterløs konstruktør
    public Bil() {
        this(0, 0, Color.RED, 0, null);
    }

    //metode som flytter bilen. 
    // Dette gjøres basert på retningen den kjører i
    public void flyttBil() {
        double hasighet = 2; // Hastigheten bilen beveger seg med

        switch ((int) vinkel) {
            case 0: // Nedover
                yPos += hasighet;
                break;
            case 90: // Mot venstre
                xPos -= hasighet;
                break;
            case 180: // Oppover
                yPos -= hasighet;
                break;
            case 270: // Mot høyre
                xPos += hasighet;
                break;
        }

        oppdaterPosisjon(); // Oppdater grafisk posisjon
    }

    private void oppdaterPosisjon() {
        r.setX(xPos);
        r.setY(yPos);
    }

    /**
     *
     * @return en rektangel som representerer bilen
     */
    //metode for å tegne bil
    //Dette er egentlig kun et rektangel og ikke en gruppe
    public Rectangle lagBilGruppe() {
        //tegner karoseri til bilen
        r = new Rectangle(xPos, yPos, BIL_BREDDE, BIL_HØYDE );

        //setter farge
        r.setFill(farge);
        r.setRotate(vinkel);

        return r;
    }

    //get metoder

    /**
     *
     * @return En gruppe som representerer bilen
     */
    //Denne metoden er kun her, siden jeg laget en
    //abstract getFigur metode i Figur klassen
    //brukes ikke..
    public Group getFigur() {
        bilGruppe.getChildren().add(r);
        return bilGruppe;
    }

    //set metoder

    /**
     *
     * @param farge Ny farge for bilen.
     */
    //setter farge på bilen
    public void setFarge(Color farge) {
        r.setFill(farge);
    }


    //metode for å endre vinkel
    public void setVinkel(double vinkel) {
        this.vinkel = vinkel;
        r.setRotate(vinkel);
    }

    //metode som henter nåværende vinkel
    public double getVinkel() {
        return vinkel;
    }

    //metode som henter bilbredden
    public double getBilBredde() {
        return BIL_BREDDE;
    }

    //metode som henter bilhøyden
    public double getBilHøyde() {
        return BIL_HØYDE;
    }

    public boolean harSvingt() {
        return harSvingt;
    }

    //metode som returnerer om bilen allerede har svingt 1 gang
    //laget for å unngå at bilene svinger flere ganger
    public void setHarSvingt(boolean harSvingt) {
        this.harSvingt = harSvingt;
    }

    //returnerer om bilen allerede har svingt
    public boolean getHarSvingt() {
        return harSvingt;
    }

    //Set om bilen har passert sin tilhørende grense
    public void setHarPassert(boolean harPassert) {
        this.harPassert = harPassert;
    }

    //om bilen har passert sin tilhørende grense
    public boolean getHarPassert() {
        return harPassert;
    }

    //returnerer bilens tilhørende trafikklys
    public Trafikklys getTrafikklys() {
        return trafikklys;
    }

    //setter startvinkel til bilen
    public void setStartVinkel(double startVinkel) {
        this.startVinkel = startVinkel;
    }

    //henter startvinkel til bilen
    public double getStartVinkel() {
        return startVinkel;
    }

    //returnerer bilen sin nåværende xposisjon
    @Override
    public double getXPos() {
        return r.getX();
    }

    //returnerer bilen sin nåværende yposisjon
    @Override
    public double getYPos() {
        return r.getY();
    }
}
