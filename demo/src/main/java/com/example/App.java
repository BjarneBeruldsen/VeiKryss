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
    //Her står globale variabler
    public ExecutorService executor = Executors.newCachedThreadPool(); //har ikke brukt enda
    public final static int VINDU_BREDDE = 600; //Vinduets bredde i px
    public final static int VINDU_HØYDE = 500; //Vinduets høyde i px
    public final static int VEI_BREDDE = 100; //Vinduets bredde i px
    private Trafikklys t1, t2, t3, t4; //trafikklys
    private ArrayList<Trafikklys> trafikklysTab = new ArrayList<>();
    private ArrayList<Bil> bilerTab = new ArrayList<>();
    private ArrayList<StartPosisjon> startPosTab = new ArrayList<>();
    private Pane hovedpanel = new Pane(); //Hovedpanelet (Der alt legges til)
    private Bil bil = new Bil();
    private double margin = 10; //setter margin til 10 piksler
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        //Tegner bakgrunn
        tegnBakgrunn(hovedpanel);

        //tegner trafikklys
        tegnTrafikkLys();

        //executer tråd for endring av status
        endreTrafikkLys();

        //Tegner biler
        tegnBiler();

        Scene scene = new Scene(hovedpanel, VINDU_BREDDE, VINDU_HØYDE);
        stage.setScene(scene);
        stage.setTitle("Veikryss");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    /*Denne metoden går igjennom trafikklystabellen
    * og endrer farge fra rødt til grønt ved hjelp av en
    * for-løkke og endrestatus() metoden som ligger i trafikklys
    * Dette gjøres hvert 3.sekund, men kan endres i Thread.sleep()*/
    public void endreTrafikkLys() {
        //tråd som skifter mellom rød og grønn (boolean true/false)
        new Thread(() -> {
            try {
                while(true) {
                    for(Trafikklys t: trafikklysTab) {
                        Platform.runLater(() -> t.endreStatus());
                    }
                    Thread.sleep(3000); //endre skifting hastighet
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /*Denne metoden tegner biler i en av fire tilfeldige
    * posisjoner. Ved hjelp av tråden i denne metoden
    * legges biler til hvert 2.sekund */
    public void tegnBiler() {
        //lager en tabell for de ulike startposisjonene
        //Det er fire ulike startposisjoner for hver vei
        //Det er fire forskjellige vinkler
        startPosTab.add(new StartPosisjon(VINDU_BREDDE/2+margin,
                VINDU_HØYDE+bil.getBilHøyde(), 0));
        startPosTab.add(new StartPosisjon(0-bil.getBilHøyde(),
                VINDU_HØYDE/2-margin, 90));
        startPosTab.add(new StartPosisjon(VINDU_BREDDE + bil.getBilHøyde(),
                VINDU_HØYDE/2-(bil.getBilBredde())*2+margin, 270));
        startPosTab.add(new StartPosisjon(VINDU_BREDDE/2-margin-bil.getBilBredde(),
                0-bil.getBilHøyde(), 180));




        /*Denne tråden legger kontinuerlig til biler
        * til hovedpanelet hvert 2. sekund.
        * De tegnes i en av fire posisjoner
        * som er lagt til i startposisjontabellen.  */
        new Thread(() -> {
            while(true) {
                //tilfeldig retning 1-4 basert på indeksen til startPosTab
                int tilfeldig = (int)(Math.random() * 4);
                bil = new Bil(startPosTab.get(tilfeldig).getStartX(), startPosTab.get(tilfeldig).getStartY(),
                        Color.rgb((int) (Math.random()*256), (int) (Math.random()*256),
                                (int) (Math.random()*256)), startPosTab.get(tilfeldig).getRetning(), trafikklysTab.get(tilfeldig));
                synchronized (bilerTab) {
                    bilerTab.add(bil);
                }
                Platform.runLater(() -> hovedpanel.getChildren().add(bil.lagBilGruppe()));
                try {
                    Thread.sleep(2000); //juster bilspawn intervall her
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        flyttBil();
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
                            //vent på rødt lys
                            //finner en tilfeldig retning
                            svingTilfeldig(b);

                            Platform.runLater(() -> b.flyttBil());
                        }
                    }
                    Thread.sleep(4); //juster farten her
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /*Denne metoden svinger en gitt bil
    * etter den har passert inn i kysset. Tilfeldig tall trekkes
    * ved bruk av Math.random(). De to øverste if setningene
    * gjelder for biler som kjører vertikalt. De to nederste er for bilene
    * opprinnelig kjører horisontalt  */
    private void svingTilfeldig(Bil b) {
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


    /*Denne metoden oppretter fire trafikklys
    * og plasseres på høyre side for hver vei ovenfor krysset */
    private void tegnTrafikkLys() {
        t1 = new Trafikklys(VINDU_BREDDE/2+VEI_BREDDE, VEI_BREDDE, 0, true);
        t2 = new Trafikklys(VINDU_BREDDE/2+VEI_BREDDE, VINDU_HØYDE/2+VEI_BREDDE, 90, false);
        t3 = new Trafikklys(VINDU_BREDDE/2-VEI_BREDDE-VEI_BREDDE/4,
                VINDU_HØYDE/2-VEI_BREDDE-VEI_BREDDE/4, 270, false);
        t4 = new Trafikklys(VINDU_BREDDE/2-VEI_BREDDE-VEI_BREDDE/4,
                VINDU_HØYDE/2+VEI_BREDDE, 180, true);
        trafikklysTab.add(t1);
        trafikklysTab.add(t2);
        trafikklysTab.add(t3);
        trafikklysTab.add(t4);
        hovedpanel.getChildren().addAll(t1.lagTrafikklys(), t2.lagTrafikklys(),
                t3.lagTrafikklys(), t4.lagTrafikklys());
    }



    //metode som tegner bakgrunnn (Denne er laget ved bruk av KI)
    private void tegnBakgrunn(Pane root) {
        // Tegn vertikal vei
        Rectangle verticalRoad = new Rectangle(VINDU_BREDDE / 2 - VEI_BREDDE / 2, 0, VEI_BREDDE, VINDU_HØYDE);
        verticalRoad.setFill(Color.DARKGRAY);

        // Tegn horisontal vei
        Rectangle horizontalRoad = new Rectangle(0, VINDU_HØYDE / 2 - VEI_BREDDE / 2, VINDU_BREDDE, VEI_BREDDE);
        horizontalRoad.setFill(Color.DARKGRAY);

        root.getChildren().addAll(verticalRoad, horizontalRoad);

        // Tegn midtlinjer
        for (int y = 0; y < VINDU_HØYDE; y += 20) {
            Line vLine = new Line(VINDU_BREDDE / 2, y, VINDU_BREDDE / 2, y + 10);
            vLine.setStroke(Color.YELLOW);
            vLine.getStrokeDashArray().addAll(10.0, 10.0);
            vLine.setStrokeWidth(3);
            root.getChildren().add(vLine);
        }

        for (int x = 0; x < VINDU_BREDDE; x += 20) {
            Line hLine = new Line(x, VINDU_HØYDE / 2, x + 10, VINDU_HØYDE / 2);
            hLine.setStroke(Color.YELLOW);
            hLine.getStrokeDashArray().addAll(10.0, 10.0);
            hLine.setStrokeWidth(3);
            root.getChildren().add(hLine);
        }
    }

}