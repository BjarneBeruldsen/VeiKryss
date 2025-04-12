//Authors: Bjarne Beruldsen, Severin Waller Sørensen

package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
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

    // Konstanter for retningene
    final static int RETNING_NED = 0; // Beveger seg nedover
    final static int RETNING_VENSTRE = 90; // Beveger seg til venstre
    final static int RETNING_OPP = 180; // Beveger seg oppover
    final static int RETNING_HØYRE = 270; // Beveger seg til høyre
   
    private Pane hovedpanel = new Pane(); //Hovedpanelet (Der alt legges til)
    private ArrayList<Bil> bilerTab = new ArrayList<>();
    private final int margin = 10;
    private ArrayList<Veikryss> veikryssTab = new ArrayList<>();
    private ArrayList<Trafikklys> nordSørLys = new ArrayList<>();
    private ArrayList<Trafikklys> østVestLys = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {
        // Tegner gress (bakgrunn i vinduet)
        Rectangle gress = new Rectangle(0, 0, VINDU_BREDDE, VINDU_HØYDE);
        gress.setFill(Color.GREEN);
        hovedpanel.getChildren().add(gress);

        // Tegner veikryssene
        veikryssTab.add(new Veikryss(hovedpanel, 200, 150, VEI_BREDDE, true, false, false, true)); // Øvre venstre
        veikryssTab.add(new Veikryss(hovedpanel, 600, 150, VEI_BREDDE, true, true, false, false)); // Øvre høyre
        veikryssTab.add(new Veikryss(hovedpanel, 200, 450, VEI_BREDDE, false, false, true, true)); // Nedre venstre
        veikryssTab.add(new Veikryss(hovedpanel, 600, 450, VEI_BREDDE, false, true, true, false)); // Nedre høyre
        
        // Start trafikklys og bil-logikk for hvert kryss
        for (Veikryss kryss : veikryssTab) {
            startTrafikklysLogikk();
            startBilLogikk(kryss);
        }

        // Start trafikkoppdatering
        oppdaterTrafikkLogikk();

        // Flytt biler
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
                    // 💡 Grønt for nord/sør – rødt for øst/vest
                    Platform.runLater(() -> {
                        for (Trafikklys lys : nordSørLys) lys.setStatus(2); // Grønt
                        for (Trafikklys lys : østVestLys) lys.setStatus(0); // Rødt
                    });
                    Thread.sleep(5000);
        
                    // 💡 Gult for nord/sør
                    Platform.runLater(() -> {
                        for (Trafikklys lys : nordSørLys) lys.setStatus(1); // Gult
                    });
                    Thread.sleep(2000);
        
                    // 💡 Rødt for nord/sør – grønt for øst/vest
                    Platform.runLater(() -> {
                        for (Trafikklys lys : nordSørLys) lys.setStatus(0); // Rødt
                        for (Trafikklys lys : østVestLys) lys.setStatus(2); // Grønt
                    });
                    Thread.sleep(5000);
        
                    // 💡 Gult for øst/vest
                    Platform.runLater(() -> {
                        for (Trafikklys lys : østVestLys) lys.setStatus(1); // Gult
                    });
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        
    }

    // Metode som starter bil-logikken for hvert veikryss
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
                while (true) {
                    synchronized (bilerTab) {
                        for (Bil b : bilerTab) {
                            for (Veikryss kryss : veikryssTab) {
                                if (Lyslogikk.bilenErNærKrysset(b, kryss)) {
                                    // Hent relevant trafikklys og sjekk status
                                    Trafikklys relevantLys = Lyslogikk.finnRelevantLys(b, kryss);
                                    int status = relevantLys.getStatus();
                                    if (status == 0 || status == 1) { // Rødt eller gult lys
                                        b.stoppVedRødtLys(); // Stopp bilen
                                    } else {
                                        b.startVedGrøntLys(); // Fortsett å kjøre
                                    }
                                }
                            }
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
    
    // metode som oppdaterer trafikklogikken for alle veikryss
    private void oppdaterTrafikkLogikk() {
        new Thread(() -> {
            try {
                while (true) {
                    Platform.runLater(() -> {
                        for (Veikryss kryss : veikryssTab) {
                            kryss.oppdaterTrafikk(); // Oppdater trafikken for hvert veikryss
                        }
                    });
                    Thread.sleep(100); // Juster oppdateringsfrekvensen (100 ms her)
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    
}