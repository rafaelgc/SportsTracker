/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import jgpx.util.DateTimeUtils;
import models.Workout;

/**
 * FXML Controller class
 *
 * @author Rafa
 */
public class SummaryController implements Initializable, ChangeListener<LocalDate> {

    List<Workout> workouts;
    @FXML
    private DatePicker initialDate;
    @FXML
    private DatePicker finalDate;
    @FXML
    private Label distance;
    @FXML
    private Label duration;
    @FXML
    private Label averageSpeed;
    @FXML
    private BarChart<?, ?> summaryChart;
    @FXML
    private ComboBox<String> groupBy;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initialDate.getEditor().setDisable(true);
        initialDate.getEditor().setStyle("-fx-opacity: 1.0;");
        
        finalDate.getEditor().setDisable(true);
        finalDate.getEditor().setStyle("-fx-opacity: 1.0;");
        
        groupBy.getItems().add("días");
        groupBy.getItems().add("meses");
        groupBy.getItems().add("años");
        groupBy.getSelectionModel().select(0);
    }

    public void init(List<Workout> workouts) {
        this.workouts = workouts;
        setupLabels();
        
        initialDate.valueProperty().addListener(this);
        finalDate.valueProperty().addListener(this);

        
    }

    public void setupLabels() {
        if (initialDate.getValue() == null || finalDate.getValue() == null) {
            initialDate.setValue(getFirstWorkout().getTrackData().getStartTime().toLocalDate());
            finalDate.setValue(getLastWorkout().getTrackData().getStartTime().toLocalDate());
        }

        double totalDistance = 0.d;
        double totalAverageSpeed = 0.d;
        Duration totalDuration = Duration.ZERO;
        
        summaryChart.setAnimated(false);
        summaryChart.getData().clear();
        summaryChart.setAnimated(true);
        
        XYChart.Series distanceSeries = new XYChart.Series();
        distanceSeries.setName("Distancia (km)");
        
        XYChart.Series timeSeries = new XYChart.Series();
        timeSeries.setName("Tiempo (min)");
        
        int workoutsAmount = 0;
        
        for (Workout w : workouts) {
            if (initialDate.getValue().compareTo(w.getTrackData().getStartTime().toLocalDate()) <= 0
                    && finalDate.getValue().compareTo(w.getTrackData().getEndTime().toLocalDate()) >= 0) {
                workoutsAmount++;
                totalAverageSpeed += w.getTrackData().getAverageSpeed();
                totalDistance += w.getTrackData().getTotalDistance();
                totalDuration = totalDuration.plus(w.getTrackData().getTotalDuration());
                
                distanceSeries.getData().add(new XYChart.Data<>(w.getName(), w.getTrackData().getTotalDistance() / 1000.d));
                timeSeries.getData().add(new XYChart.Data<>(w.getName(), w.getTrackData().getTotalDuration().toMinutes()));
            }
        }

        long secs = totalDuration.getSeconds();
        duration.setText(String.format("%d:%02d:%02d", secs / 3600, (secs % 3600) / 60, (secs % 60)));
        distance.setText(WorkoutController.round2(totalDistance / 1000.d) + " km");
        averageSpeed.setText(WorkoutController.round2(totalAverageSpeed / workoutsAmount) + " km/h");
        
        summaryChart.getData().add(distanceSeries);
        summaryChart.getData().add(timeSeries);
    }

    public Workout getFirstWorkout() {
        Workout first = null;

        for (Workout w : workouts) {
            if (first == null || w.getTrackData().getStartTime().compareTo(first.getTrackData().getStartTime()) < 0) {
                first = w;
            }
        }

        return first;
    }

    public Workout getLastWorkout() {
        Workout last = null;

        for (Workout w : workouts) {
            if (last == null || w.getTrackData().getStartTime().compareTo(last.getTrackData().getStartTime()) > 0) {
                last = w;
            }
        }

        return last;
    }

    @Override
    public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
        
        if (initialDate.getValue().compareTo(finalDate.getValue()) > 0) {
            initialDate.setValue(finalDate.getValue());
        }
        
        setupLabels();
    }
    


}
