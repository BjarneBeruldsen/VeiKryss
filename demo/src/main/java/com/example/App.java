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
       
        Rectangle gress = new Rectangle(0, 0, VINDU_BREDDE, VINDU_HØYDE);
        gress.setFill(Color.GREEN);
        hovedpanel.getChildren().add(gress);
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

    private void sjekkOgSving(Bil b) {
        for (Veikryss kryss : veikryssTab) {
            kryss.svingBilHvisNødvendig(b);
        }
    }
    
}