package com.example;

import javafx.scene.layout.Pane;
import java.util.ArrayList;
import javafx.scene.paint.Color;

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
        opprettTrafikklys(startX, startY);
        opprettGyldigeStartPosisjoner(startX, startY, spawnTopp, spawnHøyre, spawnBunn, spawnVenstre);
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
        panel.getChildren().addAll(t1.lagTrafikklys(), t2.lagTrafikklys(), t3.lagTrafikklys(), t4.lagTrafikklys());
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
