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
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
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
public class MainScreenController implements Initializable, EventHandler<WorkerStateEvent>, ListChangeListener<Workout>, ChangeListener<Boolean> {

    private Stage stage;
    @FXML
    private ListView<Workout> workoutList;
    @FXML
    private HBox hBox;
    @FXML
    private ScrollPane workoutLayout;

    WorkoutController workoutController;
    Parent root;

    TrackDataLoader workoutLoader;
    @FXML
    private VBox workoutListLayout;
    @FXML
    private MenuItem resumeMenuItem;
    @FXML
    private Menu seeMenu;

    private List<RadioMenuItem> radioMenuItems;
    private ToggleGroup radioMenuItemsGroup;
    
    File lastFolder;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        workoutLayout.setFitToHeight(true);
        workoutLayout.setFitToWidth(true);
        workoutList.getSelectionModel().getSelectedItems().addListener(this);
        radioMenuItems = new ArrayList<>();
        radioMenuItemsGroup = new ToggleGroup();

        radioMenuItemsGroup.selectedToggleProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("EEE");
            }
        });
        
        lastFolder = null;
    }

    public void init(Stage stage) {

        this.stage = stage;

        updateWorkoutList();
    }

    private void loadWorkout(File file) {
        workoutLoader = new TrackDataLoader(file);
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
        File file = chooser.showOpenDialog(stage);
        if (file != null && file.canRead()) {
            lastFolder = file.getParentFile();
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

    private void showWorkout(TrackData trackData) {
        try {
            long ini = System.nanoTime();
            System.out.println("INICIOO ");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Workout.fxml"));
            //Tanto root como workoutController harán falta más adelante,
            //así que se guardan en variables de clase.
            root = (Parent) loader.load();
            workoutController = (WorkoutController) loader.<WorkoutController>getController();

            workoutController.init(trackData);
            workoutLayout.setContent(root);
            
            System.out.println("FINN: " + (System.nanoTime()-ini) / 1000000000.d);

        } catch (IOException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addWorkout(TrackData trackData) {
        Workout workout = new Workout(DateTimeUtils.format(trackData.getStartTime()), trackData);
        workoutList.getItems().add(workout);

        resumeMenuItem.setDisable(false);

        //Se añade el entrenamiento al menú.
        RadioMenuItem n = new RadioMenuItem(workout.toString());
        n.setUserData(workout);
        n.setSelected(true);
        n.setToggleGroup(radioMenuItemsGroup);
        n.selectedProperty().addListener(this);
        radioMenuItems.add(n);
        seeMenu.getItems().add(n);

        workoutList.getSelectionModel().select(workout);

        updateWorkoutList();
    }

    private void updateWorkoutList() {
        if (workoutList.getItems().size() > 1) {
            workoutListLayout.setVisible(true);
            workoutListLayout.setManaged(true);
        } else {
            workoutListLayout.setVisible(false);
            workoutListLayout.setManaged(false);
        }
    }

    @Override
    public void handle(WorkerStateEvent event) {
        //El archivo ya se ha cargado:

        //Se añade el entrenamiento a la lista.
        this.addWorkout(workoutLoader.getValue());

        //Se cambia el cursor al normal.
        stage.getScene().setCursor(Cursor.DEFAULT);
        
    }

    @Override
    public void onChanged(Change<? extends Workout> c) {
        showWorkout(workoutList.getSelectionModel().getSelectedItem().getTrackData());

        //Se cambia el elemento del menú seleccionado.
        for (Iterator<RadioMenuItem> it = radioMenuItems.iterator(); it.hasNext();) {
            RadioMenuItem mi = it.next();
            if (mi.getUserData().equals(workoutList.getSelectionModel().getSelectedItem())) {
                //Se seleccionará. Pero para evitar entrar en un bucle infinido, se
                //deshabilita el listener.
                mi.selectedProperty().removeListener(this);
                mi.setSelected(true);
                mi.selectedProperty().addListener(this);
                break;
            }
        }
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        //¿Cuál es el seleccionado?
        if (newValue) {
            RadioMenuItem rmi = null;
            for (Iterator<RadioMenuItem> it = radioMenuItems.iterator(); it.hasNext();) {
                RadioMenuItem mi = it.next();
                if (mi.isSelected()) {
                    rmi = mi;
                    break;
                }
            }

            if (rmi != null) {
                rmi.setSelected(true);
                workoutList.getSelectionModel().select((Workout) rmi.getUserData());
            }
        }
    }

    @FXML
    private void resume(ActionEvent event) {
    }

}

class ChartsTask extends Task<String> {

    @Override
    protected String call() throws Exception {
        return null;
    }
    
}

class TrackDataLoader extends Task<TrackData> {

    private File file;

    public TrackDataLoader(File file) {
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

        } catch (JAXBException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
