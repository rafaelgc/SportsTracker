/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import jgpx.model.analysis.TrackData;
import jgpx.model.gpx.Track;
import jgpx.model.jaxb.GpxType;
import jgpx.model.jaxb.TrackPointExtensionT;
import jgpx.util.DateTimeUtils;
import models.Workout;

/**
 * FXML Controller class
 *
 * @author Rafa
 */
public class MainScreenController implements Initializable, EventHandler<WorkerStateEvent>, ChangeListener<Boolean> {

    private Stage stage;
    @FXML
    private HBox hBox;
    @FXML
    private ScrollPane workoutLayout;

    WorkoutController workoutController;
    SummaryController summaryController;
    Parent root;

    TrackDataLoader workoutLoader;
    @FXML
    private MenuItem resumeMenuItem;

    private List<RadioMenuItem> radioMenuItems;
    private ToggleGroup radioMenuItemsGroup;

    List<Workout> workouts;

    File lastFolder;
    @FXML
    private Menu viewMenu;
    @FXML
    private Button loadWorkoutButton;

    boolean summaryVisible;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        workoutLayout.setFitToHeight(true);
        workoutLayout.setFitToWidth(true);
        radioMenuItems = new ArrayList<>();
        radioMenuItemsGroup = new ToggleGroup();
        workouts = new ArrayList<>();
        lastFolder = null;
        summaryVisible = false;
    }

    public void init(Stage stage) {

        this.stage = stage;
    }

    private void loadWorkout(List<File> files) {
        workoutLoader = new TrackDataLoader(files);
        workoutLoader.setOnSucceeded(this);
        Thread thread = new Thread(workoutLoader);
        thread.setDaemon(true);
        thread.start();
        stage.getScene().setCursor(Cursor.WAIT);
    }

    @FXML
    private void loadWorkoutAction(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Cargar entrenamiento");
        chooser.setInitialDirectory(lastFolder);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo GPX (*.gpx)", "*.gpx"));
        List<File> files = chooser.showOpenMultipleDialog(stage);

        if (files != null && files.size() > 0) {
            loadWorkoutButton.setDisable(true);
            lastFolder = files.get(0).getParentFile();
            loadWorkout(files);
        }
    }

    @FXML
    private void exitAction(ActionEvent event) {
        stage.close();
    }

    @FXML
    private void aboutAction(ActionEvent event) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Acerca de");
        dialog.setHeaderText(null);
        dialog.setContentText("Rafael González Carrizo. Mayo, 2016");
        dialog.showAndWait();
    }

    private void showWorkout(TrackData trackData) {
        try {
            long ini = System.nanoTime();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Workout.fxml"));
            //Tanto root como workoutController harán falta más adelante,
            //así que se guardan en variables de clase.
            root = (Parent) loader.load();
            workoutController = (WorkoutController) loader.<WorkoutController>getController();

            workoutController.init(trackData);
            workoutLayout.setContent(root);

            summaryVisible = false;

        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void addWorkout(TrackData trackData) {
        Workout workout = new Workout(DateTimeUtils.format(trackData.getStartTime()), trackData);

        workouts.add(workout);

        resumeMenuItem.setDisable(false);

        //Se añade el entrenamiento al menú.
        RadioMenuItem n = new RadioMenuItem(workout.toString());
        n.setUserData(workout);
        n.setSelected(true);
        n.setToggleGroup(radioMenuItemsGroup);
        n.selectedProperty().addListener(this);
        radioMenuItems.add(n);
        viewMenu.getItems().add(n);

    }

    @Override
    public void handle(WorkerStateEvent event) {
        //Los archivos ya se han cargado:

        List<TrackData> tracks = workoutLoader.getValue();
        if (tracks.size() > 0) {
            for (int i = 0; i < tracks.size(); i++) {
                addWorkout(tracks.get(i));
            }

            if (!summaryVisible) {
                this.showWorkout(tracks.get(tracks.size() - 1));
            } else if (summaryController != null) {
                summaryController.setupUI();
                deselectRadioMenuItems();
            }

        }
        //Se cambia el cursor al normal.
        stage.getScene().setCursor(Cursor.DEFAULT);

    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue) {
            RadioMenuItem rmi = null;
            //¿Cuál es el seleccionado?
            for (Iterator<RadioMenuItem> it = radioMenuItems.iterator(); it.hasNext();) {
                RadioMenuItem mi = it.next();
                if (mi.isSelected()) {
                    rmi = mi;
                    break;
                }
            }

            if (rmi != null) {
                this.showWorkout(((Workout) rmi.getUserData()).getTrackData());
            }

        }
    }

    @FXML
    private void resume(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Summary.fxml"));

            //Se deselecciona el entrenamiento del menú.
            //Tanto root como workoutController harán falta más adelante,
            //así que se guardan en variables de clase.
            root = (Parent) loader.load();
            summaryController = (SummaryController) loader.<SummaryController>getController();
            summaryController.init(workouts);
            workoutLayout.setContent(root);
            
            deselectRadioMenuItems();
            
            summaryVisible = true;

        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void deselectRadioMenuItems() {
        for (Iterator<RadioMenuItem> it = radioMenuItems.iterator(); it.hasNext();) {
            RadioMenuItem mi = it.next();
            mi.setSelected(false);
        }
    }

}

class TrackDataLoader extends Task<List<TrackData>> {

    private List<File> files;

    public TrackDataLoader(List<File> files) {
        this.files = files;
    }

    @Override
    protected List<TrackData> call() throws Exception {
        List<TrackData> result = new ArrayList<>();

        for (Iterator<File> it = files.iterator(); it.hasNext();) {
            File file = it.next();
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, TrackPointExtensionT.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                JAXBElement<Object> root = (JAXBElement<Object>) unmarshaller.unmarshal(file);
                GpxType gpx = (GpxType) root.getValue();

                result.add(new TrackData(new Track(gpx.getTrk().get(0))));
            } catch (JAXBException ex) {
                Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return result;

    }

}
