/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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
    @FXML
    private Label workoutsAmount;

    enum GroupingCriteria {
        DAY, MONTH, YEAR
    };

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
        setupUI();

        initialDate.valueProperty().addListener(this);
        finalDate.valueProperty().addListener(this);

    }
    
    void setupUI() {
        setupLabels();
        setupChart();
    }

    public void setupLabels() {
        if (initialDate.getValue() == null || finalDate.getValue() == null) {
            initialDate.setValue(getFirstWorkout().getTrackData().getStartTime().toLocalDate());
            finalDate.setValue(getLastWorkout().getTrackData().getStartTime().toLocalDate());
        }

        double totalDistance = 0.d;
        double totalAverageSpeed = 0.d;
        Duration totalDuration = Duration.ZERO;
        
        int workoutsAmountCounter = 0;
        
        for (Workout w : filterWorkouts()) {
            if (initialDate.getValue().compareTo(w.getTrackData().getStartTime().toLocalDate()) <= 0
                    && finalDate.getValue().compareTo(w.getTrackData().getEndTime().toLocalDate()) >= 0) {
                workoutsAmountCounter++;
                totalAverageSpeed += w.getTrackData().getAverageSpeed();
                totalDistance += w.getTrackData().getTotalDistance();
                totalDuration = totalDuration.plus(w.getTrackData().getTotalDuration());

            }
        }

        long secs = totalDuration.getSeconds();
        duration.setText(String.format("%d:%02d:%02d", secs / 3600, (secs % 3600) / 60, (secs % 60)));
        distance.setText(WorkoutController.round2(totalDistance / 1000.d) + " km");
        averageSpeed.setText(WorkoutController.round2(totalAverageSpeed / workoutsAmountCounter) + " km/h");
        workoutsAmount.setText("" + workoutsAmountCounter);

    }

    void setupChart() {
        summaryChart.setAnimated(false);
        summaryChart.getData().clear();
        summaryChart.setAnimated(true);

        XYChart.Series distanceSeries = new XYChart.Series();
        distanceSeries.setName("Distancia (km)");

        XYChart.Series timeSeries = new XYChart.Series();
        timeSeries.setName("Tiempo (min)");

        List<Group> groups = createGroups();

        for (Group g : groups) {
            List<Workout> groupWorkouts = g.getWorkouts();

            double distance = 0.d;
            long minutes = 0L;

            for (Workout w : groupWorkouts) {
                distance += w.getTrackData().getTotalDistance();
                minutes += w.getTrackData().getTotalDuration().toMinutes();
            }

            distanceSeries.getData().add(new XYChart.Data<>(g.toString(getGroupingCriteria()), distance / 1000.d));
            timeSeries.getData().add(new XYChart.Data<>(g.toString(getGroupingCriteria()), minutes));

        }

        summaryChart.getData().add(distanceSeries);
        summaryChart.getData().add(timeSeries);
    }

    List<Group> createGroups() {
        List<Group> groups = new ArrayList<>();

        for (Workout w : filterWorkouts()) {

            boolean added = false;

            //¿El entrenamiento pertenece ya a un grupo existente?
            for (Group g : groups) {
                if (g.belongs(w, getGroupingCriteria())) {
                    //Sí: se añade.
                    g.add(w);
                    added = true;
                    break;
                }
            }

            //No, se crea y se añade.
            if (!added) {
                Group newGroup = new Group(w.getTrackData().getStartTime());
                newGroup.add(w);
                insertOrdered(groups, newGroup);
            }
        }

        return groups;
    }

    public static <E extends Comparable<E>> void insertOrdered(List<E> list, E item) {
        boolean added = false;
        for (int i = 0; i < list.size() && !added; i++) {
            if (item.compareTo(list.get(i)) < 0) {
                list.add(i, item);
                added = true;
            }
        }

        if (!added) {
            list.add(item);
        }
    }

    /**
     *
     * @return Devuelve los workouts dentro del periodo especificado por el
     * usuario.
     */
    public List<Workout> filterWorkouts() {
        List<Workout> filteredWorkouts = new ArrayList<>();

        for (Workout w : workouts) {
            if (initialDate.getValue().compareTo(w.getTrackData().getStartTime().toLocalDate()) <= 0
                    && finalDate.getValue().compareTo(w.getTrackData().getEndTime().toLocalDate()) >= 0) {

                filteredWorkouts.add(w);
            }
        }

        return filteredWorkouts;
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

        setupUI();
    }
    
    @FXML
    private void groupByAction(ActionEvent event) {
        setupChart();
    }
    
    private GroupingCriteria getGroupingCriteria() {
        if (groupBy.getSelectionModel().isSelected(0)) {
            return GroupingCriteria.DAY;
        }

        if (groupBy.getSelectionModel().isSelected(1)) {
            return GroupingCriteria.MONTH;
        }

        if (groupBy.getSelectionModel().isSelected(2)) {
            return GroupingCriteria.YEAR;
        }

        return null;
    }

}

class Group implements Comparable<Group> {

    private LocalDateTime localTime;
    private List<Workout> workouts;

    public Group(LocalDateTime localTime) {
        this.localTime = localTime;
        workouts = new ArrayList<>();
    }

    public boolean belongs(Workout workout, SummaryController.GroupingCriteria criteria) {

        if (criteria == SummaryController.GroupingCriteria.DAY) {
            return localTime.getDayOfMonth() == workout.getTrackData().getStartTime().getDayOfMonth()
                    && localTime.getMonth() == workout.getTrackData().getStartTime().getMonth()
                    && localTime.getYear() == workout.getTrackData().getStartTime().getYear();
        } else if (criteria == SummaryController.GroupingCriteria.MONTH) {
            return localTime.getMonth() == workout.getTrackData().getStartTime().getMonth()
                    && localTime.getYear() == workout.getTrackData().getStartTime().getYear();
        } else if (criteria == SummaryController.GroupingCriteria.YEAR) {
            return localTime.getYear() == workout.getTrackData().getStartTime().getYear();
        }
        return false;
    }

    public void add(Workout workout) {
        workouts.add(workout);
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public String toString(SummaryController.GroupingCriteria criteria) {
        if (criteria == SummaryController.GroupingCriteria.DAY) {
            return localTime.getDayOfMonth() + "/" + localTime.getMonthValue() + "/" + localTime.getYear();
        } else if (criteria == SummaryController.GroupingCriteria.MONTH) {
            return localTime.getMonthValue() + "/" + localTime.getYear();
        } else if (criteria == SummaryController.GroupingCriteria.YEAR) {
            return localTime.getYear() + "";
        }

        return "";
    }

    @Override
    public int compareTo(Group other) {
        return localTime.compareTo(other.localTime);
    }
}
