package com.example;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

//Importerer bredden på veien for å tegne passende bilstørrelse
import static com.example.App.VEI_BREDDE;

public class Bil extends Figur{
    //finner bredde og lengde på bil
    private final static int BIL_BREDDE = VEI_BREDDE/3;
    private final static int BIL_HØYDE = BIL_BREDDE * 2;

    //instansvariabler
    protected Color farge;
    protected Group bilGruppe; //NB! ikke i bruk ATM. (bruk senere for å designe bilene)
    protected Rectangle bilFigur; //Hovedelementet (karosseri til bilen)
    protected double vinkel;
    protected boolean harSvingt = false;
    protected boolean harPassert = false;
    protected Trafikklys trafikklys;

    //parameterløs konstruktør
    public Bil() {
        this(0, 0, Color.RED, 0, null);
    }
    
    /**
     * @param farge Bilens farge.
     */
    /*Konstruktør som oppretter bil
    * xPos, yPos er bilens posisjon
    * farge er bilens farge,
    * vinkel er bilens vinkel (hvordan rektangelet er orientert)
    * trafikklys er tilhørende trafikklys basert
    * på startposisjon */
    public Bil(double xPos, double yPos, Color farge, double vinkel, Trafikklys trafikklys) {
        super(xPos, yPos);
        this.farge = farge;
        this.vinkel = vinkel;
        bilFigur = this.lagBilGruppe();
        this.trafikklys = trafikklys;
    }

    //metode som flytter bilen basert på retningen
    public void flyttBil() {
        double hasighet = 2; // Hastigheten bilen beveger seg med

        switch ((int) vinkel) {
            case 0: // Ned (nedover langs y-aksen)
                yPos += hasighet;
                break;
            case 90: // Mot venstre ("bakover" langs x-aksen)
                xPos -= hasighet;
                break;
            case 180: // Opp (oppover langs y-aksen)
                yPos -= hasighet;
                break;
            case 270: // Mot høyre ("fremover" langs x-aksen)
                xPos += hasighet;
                break;
        }
        oppdaterPosisjon(); // Oppdater grafisk posisjon
    }

    //metode som oppdaterer bilens posisjon
    private void oppdaterPosisjon() {
        bilFigur.setX(xPos);
        bilFigur.setY(yPos);
    }

    /**
     * @return en rektangel som representerer bilen
     */
    //metode for å tegne bil
    //Dette er egentlig kun et rektangel og ikke en gruppe
    //TBC: lage en gruppe med flere figurer (hjul, vinduer osv.) 
    public Rectangle lagBilGruppe() {
        //tegner karoseri til bilen
        bilFigur = new Rectangle(xPos, yPos, BIL_BREDDE, BIL_HØYDE );

        //setter farge
        bilFigur.setFill(farge);
        bilFigur.setRotate(vinkel);

        return bilFigur;
    }

    //get metoder
    /**
     * @return En gruppe som representerer bilen
     */
    //Denne metoden er kun her, siden jeg laget en
    //abstract getFigur metode i Figur klassen
    //brukes ikke..
    public Group getFigur() {
        bilGruppe.getChildren().add(bilFigur);
        return bilGruppe;
    }

    //set metoder
    /**
     * @param farge Ny farge for bilen.
     */
    //setter farge på bilen
    public void setFarge(Color farge) {
        bilFigur.setFill(farge);
    }

    //metode for å endre vinkel
    public void setVinkel(double vinkel) {
        this.vinkel = vinkel;
        bilFigur.setRotate(vinkel);
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

    //returnerer bilen sin nåværende x-posisjon
    @Override
    public double getXPos() {
        return bilFigur.getX();
    }

    //returnerer bilen sin nåværende y-posisjon
    @Override
    public double getYPos() {
        return bilFigur.getY();
    }
}
