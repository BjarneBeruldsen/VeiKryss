package com.example;

import javafx.scene.layout.Pane;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Veikryss {

    // Instansvariabler
    private ArrayList<Trafikklys> trafikklysTab = new ArrayList<>();
    private ArrayList<Bil> bilerTab = new ArrayList<>();
    private ArrayList<StartPosisjon> gyldigeStartPosTab = new ArrayList<>();
    private Pane panel;
    private double veiBredde;
    private double startX;
    private double startY;

    // Konstruktør for Veikryss
    public Veikryss(Pane panel, double startX, double startY, double veiBredde,
                    boolean spawnTopp, boolean spawnHøyre, boolean spawnBunn, boolean spawnVenstre) {
        this.panel = panel;
        this.startX = startX;
        this.startY = startY;
        this.veiBredde = veiBredde;

        tegnVeier(panel);
        opprettTrafikklys(startX, startY);
        opprettGyldigeStartPosisjoner(startX, startY, spawnTopp, spawnHøyre, spawnBunn, spawnVenstre);
    }

    // Tegner veiene i veikrysset
    private void tegnVeier(Pane pane) {
        Rectangle vertikalVei = new Rectangle(startX - veiBredde/2, startY - 300, veiBredde, 600);
        Rectangle horisontalVei = new Rectangle(startX - 300, startY - veiBredde/2, 600, veiBredde);
        vertikalVei.setFill(Color.rgb(105, 105, 105));
        horisontalVei.setFill(Color.rgb(105,105,105));
        pane.getChildren().addAll(vertikalVei, horisontalVei);

        for (int y = (int) startY - 300; y < startY + 300; y += 20) {
            Line vLinje = new Line(startX, y, startX, y + 10);
            vLinje.setStroke(Color.YELLOW);
            vLinje.setStrokeWidth(2);
            pane.getChildren().add(vLinje);
        }

        for (int x = (int) startX - 300; x < startX + 300; x += 20) {
            Line hLinje = new Line(x, startY, x + 10, startY);
            hLinje.setStroke(Color.YELLOW);
            hLinje.setStrokeWidth(2);
            pane.getChildren().add(hLinje);
        }
    }

    // Sjekker om koordinatene er innenfor veikrysset
    public boolean erInniKrysset(double x, double y) {
        int margin = 15;
        return Math.abs(x - startX) < margin && Math.abs(y - startY) < margin;
    }

    //TBC!!!!!!!!!!!!!!!!!!!!!
    private double hentYKoordinatForRetning(int nyRetning) {
        for (StartPosisjon sp: gyldigeStartPosTab) {
            if (sp.getRetning() == nyRetning) {
                return sp.getStartY();
            }
        }
        return startY;
    }

    //TBC!!!!!!!!!!!!!!!!!!!!
    private double hentXKoordinatForRetning(int nyRetning) {
        for (StartPosisjon sp: gyldigeStartPosTab) {
            if (sp.getRetning() == nyRetning) {
                return sp.getStartX();
            }
        }
        return startX;
    }


    // Svinger tilfeldig om bilen er i veikrysset
    public void svingBilHvisNødvendig(Bil b) {
        if (!b.harSvingt() && erInniKrysset(b.getXPos(), b.getYPos())) {
            int tilfeldig = (int) (Math.random() * 3 + 1); // 1 = rett frem, 2 = høyre, 3 = venstre
                                                           // (sett fra bilens perspektiv)
            int v = (int) b.getVinkel();

            if (tilfeldig == 2) { //Høyresving
                switch (v) {
                    case 0: b.setVinkel(270); b.setYPos(hentYKoordinatForRetning(270)); break;
                    case 90: b.setVinkel(0); b.setXPos(hentXKoordinatForRetning(0)); break;
                    case 180: b.setVinkel(90); b.setYPos(hentYKoordinatForRetning(90)); break; 
                    case 270: b.setVinkel(180); b.setXPos(hentXKoordinatForRetning(180)); break;
                }
                b.harSvingt = true;
            } else if (tilfeldig == 3) { //Venstresving
                switch (v) {
                    case 0: b.setVinkel(90); b.setYPos(hentYKoordinatForRetning(90)); break;
                    case 90: b.setVinkel(180); b.setXPos(hentXKoordinatForRetning(180)); break;
                    case 180: b.setVinkel(270); b.setYPos(hentYKoordinatForRetning(270)); break;
                    case 270: b.setVinkel(0); b.setXPos(hentXKoordinatForRetning(0)); break;
                }
                b.harSvingt = true;
            } else {
                b.harSvingt = true; //Rett frem (teknisk sett ikke svingt, men tatt valget)
            }
        }
    }
    
    // Oppretter fire trafikklys for veikrysset
    private void opprettTrafikklys(double startX, double startY) {
        // Justere avstand, skal endre etterhvert så det blir dynamisk
        double avstand = veiBredde / 2 + 30; // NB! skal endre
        double yJustering = -25;   // NB! skal endre
        double xJustering = -15;   // NB! skal endre

        // Trafikklysene plasseres rundt veikrysset
        Trafikklys t1 = new Trafikklys(startX + avstand + xJustering, startY - avstand + yJustering, 270, 1);  // Øvre høyre
        Trafikklys t2 = new Trafikklys(startX - avstand + xJustering, startY - avstand + yJustering, 180, 1); // Øvre venstre
        Trafikklys t3 = new Trafikklys(startX - avstand + xJustering, startY + avstand + yJustering, 90, 1); // Nedre venstre
        Trafikklys t4 = new Trafikklys(startX + avstand + xJustering, startY + avstand + yJustering, 0, 1); // Nedre høyre

        // Legg til trafikklysene i listen
        trafikklysTab.add(t1);
        trafikklysTab.add(t2);
        trafikklysTab.add(t3);
        trafikklysTab.add(t4);

        // Legg trafikklysene til panelet
        panel.getChildren().addAll(t1.getGruppe(), t2.getGruppe(), t3.getGruppe(), t4.getGruppe());
    }

    // Oppretter gyldige startposisjoner for biler i veikrysset
    // En bil i f.eks. nedre venstre hjørne skal ikke kunne komme (spawne) oppe eller til høyre.
    private void opprettGyldigeStartPosisjoner(double startX, double startY,
                                               boolean spawnTopp, boolean spawnHøyre,
                                               boolean spawnBunn, boolean spawnVenstre) {

        //Justeringer for å plassere bilene i riktig kjørefelt
        double justeringX = veiBredde / 4;
        double justeringY = veiBredde / 4;
        
        if (spawnTopp) {
            gyldigeStartPosTab.add(new StartPosisjon(startX - justeringX - 17,
                         startY - veiBredde - 125, 0)); // Nedover
        }
        if (spawnHøyre) {
            gyldigeStartPosTab.add(new StartPosisjon(startX + veiBredde + 125, 
                        startY - justeringY - 37, 90)); // Høyre
        }
        if (spawnBunn) {
            gyldigeStartPosTab.add(new StartPosisjon(startX + justeringX - 15,
                        startY + veiBredde + 50, 180)); // Oppover
        }
        if (spawnVenstre) {
            gyldigeStartPosTab.add(new StartPosisjon(startX - veiBredde - 160,
                        startY + justeringY - 35, 270)); // Venstre
        }
     
    }

    // 
    public void leggTilBil(Bil bil) {
        bilerTab.add(bil);
        panel.getChildren().add(bil.lagBilGruppe());
    }

    // getMetode for bilerTab
    public ArrayList<Bil> getBilerTab() {
        return bilerTab;
    }

    // getMetode for trafikklysTab
    public ArrayList<Trafikklys> getTrafikklysTab() {
        return trafikklysTab;
    }

    // Spawner en ny bil i veikrysset
    public Bil genererBil() {
        if (gyldigeStartPosTab.isEmpty()) {
            return null;
        }
        //Tilfeldig startposisjon fra gyldige posisjoner
        int tilfeldig = (int) (Math.random() * 4); // Tilfeldig startposisjon
        StartPosisjon startPos = gyldigeStartPosTab.get(tilfeldig);

        // Opprett en ny bil
        Bil bil = new Bil(
            startPos.getStartX(),
            startPos.getStartY(),
            Color.rgb((int) (Math.random() * 256),
                      (int) (Math.random() * 256), 
                      (int) (Math.random() * 256)), 
            (int) startPos.getRetning(), 
            trafikklysTab.get(tilfeldig)
        );
        bilerTab.add(bil);
        panel.getChildren().add(bil.lagBilGruppe());
        return bil;
    }

    // getMetode for startposisjonX
    public double getX() {
        return startX;
    }

    // getMetode for startposisjonY
    public double getY() {
        return startY;
    }

    // getMetode for tabell med gyldige startposisjoner for det veikrysset
    public ArrayList<StartPosisjon> getGyldigeStartPosTab() {
        return gyldigeStartPosTab;
    }

}
