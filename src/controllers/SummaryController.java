/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import jgpx.util.DateTimeUtils;
import models.Workout;

/**
 * FXML Controller class
 *
 * @author Rafa
 */
public class SummaryController implements Initializable {

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void init(List<Workout> workouts) {
        this.workouts = workouts;

        setupLabels();
    }

    public void setupLabels() {
        initialDate.setValue(getFirstWorkout().getTrackData().getStartTime().toLocalDate());
        finalDate.setValue(getLastWorkout().getTrackData().getStartTime().toLocalDate());

        double totalDistance = 0.d;
        double totalAverageSpeed = 0.d;
        Duration totalDuration = Duration.ZERO;
        for (Workout w : workouts) {
            totalAverageSpeed += w.getTrackData().getAverageSpeed();
            totalDistance += w.getTrackData().getTotalDistance();
            totalDuration = totalDuration.plus(w.getTrackData().getTotalDuration());
        }

        long secs = totalDuration.getSeconds();
        duration.setText(String.format("%d:%02d:%02d", secs / 3600, (secs % 3600) / 60, (secs % 60)));
        distance.setText(WorkoutController.round2(totalDistance / 1000.d) + " km");
        averageSpeed.setText(WorkoutController.round2(totalAverageSpeed / workouts.size()) + " km/h");
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

}
