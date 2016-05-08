/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ListChangeListener;
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
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import jgpx.model.analysis.TrackData;
import jgpx.model.gpx.Gpx;
import jgpx.model.gpx.Track;
import jgpx.model.jaxb.GpxType;
import jgpx.model.jaxb.TrackPointExtensionT;
import jgpx.util.DateTimeUtils;
import models.Workout;
import util.SceneTransition;

/**
 * FXML Controller class
 *
 * @author Rafa
 */
public class MainScreenController implements Initializable, EventHandler<WorkerStateEvent>, ListChangeListener<Workout> {

    private Stage stage;
    @FXML
    private ListView<Workout> trainingList;
    @FXML
    private HBox hBox;
    @FXML
    private ScrollPane workoutLayout;
    
    WorkoutController workoutController;
    
    WorkoutLoader workoutLoader;
    @FXML
    private VBox workoutListLayout;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        workoutLayout.setFitToHeight(true);
        workoutLayout.setFitToWidth(true);
        trainingList.getSelectionModel().getSelectedItems().addListener(this);
    }

    public void init(Stage stage) {

        this.stage = stage;

        updateWorkoutList();
    }

    private void loadWorkout(File file) {
        workoutLoader = new WorkoutLoader(file);
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
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo GPX (*.gpx)", "*.gpx"));
        File file = chooser.showOpenDialog(stage);
        if (file != null && file.canRead()) {

            loadWorkout(file);

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

    private void addWorkout(TrackData trackData) {
        if (trainingList.getItems().size() == 0) {
            //Si no había ningún entrenamiento cargado...
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Workout.fxml"));
                Parent root = (Parent) loader.load();
                
                workoutController = (WorkoutController) loader.<WorkoutController>getController();
                
                workoutLayout.setContent(root);

            } catch (IOException ex) {
                Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        Workout workout = new Workout(DateTimeUtils.format(trackData.getStartTime()), trackData);
        
        trainingList.getItems().add(workout);
        trainingList.getSelectionModel().select(workout);
        
        updateWorkoutList();
    }

    private void updateWorkoutList() {
        if (trainingList.getItems().size() > 1) {
            workoutListLayout.setVisible(true);
            workoutListLayout.setManaged(true);
        } else {
            workoutListLayout.setVisible(false);
            workoutListLayout.setManaged(false);
        }
    }

    @Override
    public void handle(WorkerStateEvent event) {
        //El archivo ya se ha cargado.
        this.addWorkout(workoutLoader.getValue());
        
        stage.getScene().setCursor(Cursor.DEFAULT);
    }

    @Override
    public void onChanged(Change<? extends Workout> c) {
        workoutController.init(trainingList.getSelectionModel().getSelectedItem().getTrackData());
    }

}

class WorkoutLoader extends Task<TrackData> {
    private File file;
    public WorkoutLoader(File file) {
        this.file = file;
    }
    @Override
    protected TrackData call() throws Exception {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class, TrackPointExtensionT.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<Object> root = (JAXBElement<Object>) unmarshaller.unmarshal(file);
            GpxType gpx = (GpxType) root.getValue();

            return new TrackData(new Track(gpx.getTrk().get(0)));
            //addWorkout(trackData);

        } catch (JAXBException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
}