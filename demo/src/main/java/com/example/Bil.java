//Authors: Bjarne Beruldsen, Severin Waller Sørensen

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

    //metode som får bilen til å svinge
    public void sving(Trafikklys relevantLys) {
        // Finn den opprinnelige retningen basert på trafikklyset
                    // Bruke KI for å finne en rask/enkel måte så snu retning på
        int opprinneligRetning = (relevantLys.getVinkel() + 180) % 360;
    
        // Trekker et tilfeldig tall for å bestemme svingretning
        // 1 = venstre, 2 = høyre, 3 = fortsett
        int valg = (int) (Math.random() * 3) + 1;
    
        // Bestem ny retning og posisjon basert på svingvalg
        // Hvis bilen kommer fra nord
        if (opprinneligRetning == 0) {
            switch (valg) {
                case 1: { setXPos(getXPos() + VEI_BREDDE); setYPos(getYPos() + VEI_BREDDE); setVinkel(270); }; break;
                case 2: { setXPos(getXPos() - VEI_BREDDE/2); setYPos(getYPos() + VEI_BREDDE/2); setVinkel(90); }; break;
                default: break;
            }
        }

        // Hvis bilen kommer fra øst
        if (opprinneligRetning == 90) {
            switch (valg) {
                case 1: { setXPos(getXPos() - VEI_BREDDE); setYPos(getYPos() + VEI_BREDDE); setVinkel(0); }; break;
                case 2: { setXPos(getXPos() - VEI_BREDDE/2); setYPos(getYPos() - VEI_BREDDE/2); setVinkel(180); }; break;
                default: break;
            }
        }

        // Hvis bilen kommer fra sør
        if (opprinneligRetning == 180) {
            switch (valg) {
                case 1: { setXPos(getXPos() - VEI_BREDDE); setYPos(getYPos() - VEI_BREDDE); setVinkel(90); }; break;
                case 2: { setXPos(getXPos() + VEI_BREDDE/2); setYPos(getYPos() - VEI_BREDDE/2); setVinkel(270); }; break;
                default: break;
            }
        }

        // hvis bilen kommer fra vest
        if (opprinneligRetning == 270) {
            switch (valg) {
                case 1: { setXPos(getXPos() + VEI_BREDDE); setYPos(getYPos() - VEI_BREDDE); setVinkel(180); }; break;
                case 2: { setXPos(getXPos() + VEI_BREDDE/2); setYPos(getYPos() + VEI_BREDDE/2); setVinkel(0); }; break;
                default: break;
            }
        }
    
        // Oppdater bilens grafiske posisjon etter sving
        oppdaterPosisjon();
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

    //setter ny X-verdi
    public void setXPos(Double newX) {
        this.xPos = newX;
    }

    //setter ny Y-verdi
    public void setYPos(Double newY) {
        this.yPos = newY;
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
