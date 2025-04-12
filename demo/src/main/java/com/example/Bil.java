package com.example;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
    protected boolean stoppVedRødtLys = false;
    protected boolean erIKrysset = false;
    protected Trafikklys trafikklys;
    protected double hastighet = 2; // Hastigheten bilen beveger seg med

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
        if (stoppVedRødtLys) {
            return; // Stopp bilen hvis det er rødt lys
        }
        
        hastighet = 2; // Hastigheten bilen beveger seg med

        switch ((int) vinkel) {
            case 0: // Ned (nedover langs y-aksen)
                yPos += hastighet;
                break;
            case 90: // Mot venstre ("bakover" langs x-aksen)
                xPos -= hastighet;
                break;
            case 180: // Opp (oppover langs y-aksen)
                yPos -= hastighet;
                break;
            case 270: // Mot høyre ("fremover" langs x-aksen)
                xPos += hastighet;
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
        bilFigur.setArcWidth(6); //Runde kanter
        bilFigur.setArcHeight(6); //Runde kanter

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

    //metoder for å kontrollere stopp/start ved rødt lys
    // setter stoppVedRødtLys til true
    public void stoppVedRødtLys() {
        this.stoppVedRødtLys = true;
    }
    // setter stoppVedRødtLys til false
    public void startVedGrøntLys() {
        this.stoppVedRødtLys = false;
    }

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

    public boolean erIKrysset() {
        return erIKrysset;
    }

    public void setErIKrysset(boolean verdi) {
        this.erIKrysset = verdi;
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

    public double getBredde() {
        return BIL_BREDDE;
    }

    public double getHøyde() {
        return BIL_HØYDE;
    }

    public boolean sjekkOmIKrysset(Rectangle kryss) {
        return bilFigur.getBoundsInParent().intersects(kryss.getBoundsInParent());
    }
    
    public boolean bilenErNærKryss(double kryssX, double kryssY, double veiBredde) {
        double margin = 25; // Hvor langt unna bilen kan være før den skal stoppe
    
        switch ((int) vinkel) {
            case 0: // NED (beveger seg nedover langs y-aksen)
                return yPos + BIL_HØYDE >= kryssY - veiBredde / 2 - margin;
            case 90: // VENSTRE (beveger seg mot venstre langs x-aksen)
                return xPos <= kryssX + veiBredde / 2 + margin;
            case 180: // OPP (beveger seg oppover langs y-aksen)
                return yPos <= kryssY + veiBredde / 2 + margin;
            case 270: // HØYRE (beveger seg mot høyre langs x-aksen)
                return xPos + BIL_BREDDE >= kryssX - veiBredde / 2 - margin;
            default:
                return false;
        }
    }
}
