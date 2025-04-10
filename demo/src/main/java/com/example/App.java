package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;


import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    //Her står globale variabler/konstanter
    public ExecutorService executor = Executors.newCachedThreadPool(); //har ikke brukt enda
    public final static int VINDU_BREDDE = 800; //Vinduets bredde i px
    public final static int VINDU_HØYDE = 600; //Vinduets høyde i px
    public final static int VEI_BREDDE = 100; //Vinduets bredde i px
   
    private Pane hovedpanel = new Pane(); //Hovedpanelet (Der alt legges til)
    private ArrayList<Bil> bilerTab = new ArrayList<>();
    private final int margin = 10;
    private ArrayList<Veikryss> veikryssTab = new ArrayList<>();
    private ArrayList<Trafikklys> nordSørLys = new ArrayList<>();
    private ArrayList<Trafikklys> østVestLys = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {
       
        tegnBakgrunn(hovedpanel); //Tegner bakgrunn
        
        veikryssTab.add(new Veikryss(hovedpanel, 200, 150, VEI_BREDDE, true, false, false, true)); // Øvre venstre
        veikryssTab.add(new Veikryss(hovedpanel, 600, 150, VEI_BREDDE, true, true, false, false)); // Øvre høyre
        veikryssTab.add(new Veikryss(hovedpanel, 200, 450, VEI_BREDDE, false, false, true, true)); // Nedre venstre
        veikryssTab.add(new Veikryss(hovedpanel, 600, 450, VEI_BREDDE, false, true, true, false)); // Nedre høyre
        
        // Start trafikklys og bil-logikk for hvert kryss
        for (Veikryss kryss : veikryssTab) {
            startTrafikklysLogikk();
            startBilLogikk(kryss);
        }

        flyttBil();

        //Setter opp scenen
        Scene scene = new Scene(hovedpanel, VINDU_BREDDE, VINDU_HØYDE);
        stage.setScene(scene);
        stage.setTitle("Veikryss");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void startTrafikklysLogikk() {
        // Del trafikklysene inn i grupper basert på retning
        for (Veikryss kryss : veikryssTab) {
            for (Trafikklys lys : kryss.getTrafikklysTab()) {
                if (lys.getVinkel() == 0 || lys.getVinkel() == 180) { // Nord/Sør
                    nordSørLys.add(lys);
                } else if (lys.getVinkel() == 90 || lys.getVinkel() == 270) { // Øst/Vest
                    østVestLys.add(lys);
                }
            }
        }

        // Starter en tråd for å oppdatere trafikklysene
        new Thread(() -> {
            try {
                while (true) {
                    // Sett nord/sør til grønt og øst/vest til rødt
                    Platform.runLater(() -> {
                        for (Trafikklys lys : nordSørLys) {
                            lys.setStatus(2); // Grønt
                        }
                        for (Trafikklys lys : østVestLys) {
                            lys.setStatus(0); // Rødt
                        }
                    });
                    Thread.sleep(5000); // Vent 3 sekunder

                    // Sett begge grupper til gult
                    Platform.runLater(() -> {
                        for (Trafikklys lys : nordSørLys) {
                            lys.setStatus(1); // Gult
                        }
                        for (Trafikklys lys : østVestLys) {
                            lys.setStatus(1); // Gult
                        }
                    });
                    Thread.sleep(2000); // Vent 1 sekund

                    // Sett nord/sør til rødt og øst/vest til grønt
                    Platform.runLater(() -> {
                        for (Trafikklys lys : nordSørLys) {
                            lys.setStatus(0); // Rødt
                        }
                        for (Trafikklys lys : østVestLys) {
                            lys.setStatus(2); // Grønt
                        }
                    });
                    Thread.sleep(5000); // Vent 3 sekunder

                    // Sett begge grupper til gult
                    Platform.runLater(() -> {
                        for (Trafikklys lys : nordSørLys) {
                            lys.setStatus(1); // Gult
                        }
                        for (Trafikklys lys : østVestLys) {
                            lys.setStatus(1); // Gult
                        }
                    });
                    Thread.sleep(1000); // Vent 1 sekund
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void startBilLogikk(Veikryss kryss) {
        new Thread(() -> {
            try {
                while (true) {
                    Platform.runLater(() -> {
                        Bil nyBil = kryss.genererBil(); // Generer en ny bil
                        if (nyBil != null) {
                            synchronized (bilerTab) {
                                bilerTab.add(nyBil); // Legg til bilen i bilerTab
                            }
                        }
                    });
                    Thread.sleep(2000); // Juster bilspawn-intervall her
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /*Denne metoden flytter bilene. Alle bilene som opprettes i
    * tegnbiler() metoden legges i bilerTab og i tråden under
    * flyttes alle biler som ligger i bilerTab kontinuerlig ved
    * bruk av en foor-løkke og flyttbil() metoden
    * som ligger i Bil klassen. Farten på bilene kan justeres i
    * Thread.sleep(antal millisekund). Jo færre millisekund jo
    * raskere */
    private void flyttBil() {
        //flytt bil
        new Thread(() -> {
            try {
                while(true) {
                    synchronized (bilerTab) {
                        for (Bil b : bilerTab) {
                            //finner en tilfeldig retning
                            sjekkOgSving(b); // NB! skal fikse

                            Platform.runLater(() -> b.flyttBil());
                        }
                    }
                    Thread.sleep(8); //juster farten her
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    //Metode sjekkOgSving
    private void sjekkOgSving(Bil b) {
        for (Veikryss kryss : veikryssTab) { // Gå gjennom alle veikryss
            if (Math.abs(b.getXPos() - kryss.getX()) < margin && Math.abs(b.getYPos() - kryss.getY()) < margin) {
                svingTilfeldig(b, kryss); // Kall metoden for å svinge bilen med riktig veikryss
                break; // Unngå å sjekke flere kryss for samme bil
            }
        }
    }

    /*Denne metoden svinger en gitt bil
    * etter den har passert inn i kysset. Tilfeldig tall trekkes
    * ved bruk av Math.random(). De to øverste if setningene
    * gjelder for biler som kjører vertikalt. De to nederste er for bilene
    * opprinnelig kjører horisontalt */
    private void svingTilfeldig(Bil b, Veikryss kryss) {
        
        // NB! Fungerer ikke nå etter at det gikk fra ett til fire veikryss!

        int tilfeldig = (int) (Math.random() * 3) + 1; //tre forskjellige muligheter
        boolean skiftet = b.harSvingt; //legg heller til i objektet bil
        //kjører i tilfeldig retning retning høyre, rett fram eller venstre
        if(!skiftet) {
            if (b.getYPos() == VINDU_HØYDE / 2 && tilfeldig == 2) {
                b.setVinkel(90); //endrer retning til høyre
                b.setHarSvingt(true);
            }
            if (b.getYPos() == VINDU_HØYDE / 2 - b.getBilHøyde() + margin && tilfeldig == 3) {
                b.setVinkel(270);
                b.setHarSvingt(true);
            }

            if (b.getXPos() == VINDU_BREDDE / 2 && tilfeldig == 2) {
                b.setVinkel(0);
                b.setHarSvingt(true);
            }
            if (b.getXPos() == VINDU_BREDDE / 2 - b.getBilHøyde() + margin * 2 && tilfeldig == 3) {
                b.setVinkel(180);
                b.setHarSvingt(true);
            }
        }
    }

    //metode som tegner bakgrunnn (Denne er laget ved bruk av KI)
    private void tegnBakgrunn(Pane root) {
        // Tegn vertikale veier
        Rectangle verticalRoad1 = new Rectangle(200 - VEI_BREDDE / 2, 0, VEI_BREDDE, VINDU_HØYDE);
        Rectangle verticalRoad2 = new Rectangle(600 - VEI_BREDDE / 2, 0, VEI_BREDDE, VINDU_HØYDE);
        verticalRoad1.setFill(Color.DARKGRAY);
        verticalRoad2.setFill(Color.DARKGRAY);

        // Tegn horisontale veier
        Rectangle horizontalRoad1 = new Rectangle(0, 150 - VEI_BREDDE / 2, VINDU_BREDDE, VEI_BREDDE);
        Rectangle horizontalRoad2 = new Rectangle(0, 450 - VEI_BREDDE / 2, VINDU_BREDDE, VEI_BREDDE);
        horizontalRoad1.setFill(Color.DARKGRAY);
        horizontalRoad2.setFill(Color.DARKGRAY);

        root.getChildren().addAll(verticalRoad1, verticalRoad2, horizontalRoad1, horizontalRoad2);

        // Tegn midtlinjer for vertikale veier
        for (int y = 0; y < VINDU_HØYDE; y += 20) {
            Line vLine1 = new Line(200, y, 200, y + 10);
            Line vLine2 = new Line(600, y, 600, y + 10);
            vLine1.setStroke(Color.YELLOW);
            vLine2.setStroke(Color.YELLOW);
            vLine1.setStrokeWidth(3);
            vLine2.setStrokeWidth(3);
            root.getChildren().addAll(vLine1, vLine2);
        }

        // Tegn midtlinjer for horisontale veier
        for (int x = 0; x < VINDU_BREDDE; x += 20) {
            Line hLine1 = new Line(x, 150, x + 10, 150);
            Line hLine2 = new Line(x, 450, x + 10, 450);
            hLine1.setStroke(Color.YELLOW);
            hLine2.setStroke(Color.YELLOW);
            hLine1.setStrokeWidth(3);
            hLine2.setStrokeWidth(3);
            root.getChildren().addAll(hLine1, hLine2);
        }
    }

}