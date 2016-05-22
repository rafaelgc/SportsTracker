/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import models.charts.AreaChartTask;
import models.charts.ChartTask;
import models.charts.sources.ElevationChartSource;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import jgpx.model.analysis.Chunk;
import jgpx.model.analysis.TrackData;
import jgpx.util.DateTimeUtils;
import models.charts.sources.CadenceChartSource;
import models.charts.ChartSource;
import models.charts.sources.HeartRateChartSource;
import models.charts.HeartRateZonesChartManager;
import models.charts.LineChartTask;
import models.charts.sources.SpeedChartSource;

/**
 * FXML Controller class
 *
 * @author Rafa
 */
public class WorkoutController implements Initializable {

    @FXML
    private Label dateTime;
    @FXML
    private Label distance;
    @FXML
    private Label duration;
    @FXML
    private Label movementTime;
    @FXML
    private Label ascent;
    @FXML
    private Label averageCadence;
    @FXML
    private Label averageSpeed;
    @FXML
    private Label descent;
    @FXML
    private Label maxCadence;
    @FXML
    private Label maxSpeed;
    @FXML
    private Label maxHeartRate;
    @FXML
    private Label averageHeartRate;
    @FXML
    private Label minHeartRate;

    @FXML
    private ToggleGroup abscissa;
    @FXML
    private TextField maxHeartRateTextField;
    @FXML
    private PieChart heartRateZonesChart;

    HeartRateZonesChartManager heartRateZonesChartManager;

    @FXML
    private RadioButton distanceRadioButton;
    @FXML
    private RadioButton timeRadioButton;

    TrackData trackData;
    @FXML
    private ToggleButton speedToggle;
    @FXML
    private ToggleButton heartRateToggle;
    @FXML
    private ToggleButton cadenceToggle;
    @FXML
    private VBox elevationChartLayout;
    @FXML
    private VBox speedChartLayout;
    @FXML
    private VBox heartRateChartLayout;
    @FXML
    private VBox cadenceLayout;
    @FXML
    private VBox summaryChartLayout;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        maxHeartRateTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("[0-9]*")) {
                    maxHeartRateTextField.setText(oldValue);
                } else {
                    try {
                        int parsed = Integer.parseInt(newValue);
                        heartRateZonesChartManager.setMaxHeartRate(parsed);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        });

        heartRateZonesChartManager = new HeartRateZonesChartManager(heartRateZonesChart);

        abscissa.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == distanceRadioButton) {
                    setupCharts(ChartTask.Abscissa.DISTANCE);
                } else {
                    setupCharts(ChartTask.Abscissa.TIME);
                }
            }
        });
        
        speedToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                createSummaryChart(abscissa.getSelectedToggle()==distanceRadioButton?ChartTask.Abscissa.DISTANCE:ChartTask.Abscissa.TIME);

            }
        });

        heartRateToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                createSummaryChart(abscissa.getSelectedToggle()==distanceRadioButton?ChartTask.Abscissa.DISTANCE:ChartTask.Abscissa.TIME);
            }
        });

        cadenceToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                createSummaryChart(abscissa.getSelectedToggle()==distanceRadioButton?ChartTask.Abscissa.DISTANCE:ChartTask.Abscissa.TIME);
            }
        });

    }

    public void init(TrackData trackData) {
        this.trackData = trackData;

        setupLabels(trackData);
        setupCharts(ChartTask.Abscissa.DISTANCE);

        heartRateZonesChartManager.update(trackData);

    }

    private void setupCharts(ChartTask.Abscissa abscissa) {
        //ALTURA
        elevationChartLayout.getChildren().clear();
        elevationChartLayout.getChildren().add(new Label("Espera..."));

        AreaChartTask elevationTask = new AreaChartTask(trackData, abscissa);
        elevationTask.setYName("Altura (m)");
        elevationTask.addChartSource(new ElevationChartSource());
        elevationTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                try {
                    elevationChartLayout.getChildren().clear();
                    elevationChartLayout.getChildren().add(elevationTask.get());
                } catch (Exception ex) {
                    Logger.getLogger(WorkoutController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Thread thread = new Thread(elevationTask);
        thread.setDaemon(true);
        thread.start();

        //VELOCIDAD
        speedChartLayout.getChildren().clear();
        speedChartLayout.getChildren().add(new Label("Espera..."));

        LineChartTask speedTask = new LineChartTask(trackData, abscissa);
        speedTask.setYName("Velocidad (km/h)");
        speedTask.addChartSource(new SpeedChartSource());
        speedTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                try {
                    speedChartLayout.getChildren().clear();
                    speedChartLayout.getChildren().add(speedTask.get());
                } catch (Exception ex) {
                    Logger.getLogger(WorkoutController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Thread thread2 = new Thread(speedTask);
        thread2.setDaemon(true);
        thread2.start();

        //FREC. CARDIACA
        heartRateChartLayout.getChildren().clear();
        heartRateChartLayout.getChildren().add(new Label("Espera..."));

        LineChartTask heartRateTask = new LineChartTask(trackData, abscissa);
        heartRateTask.setYName("Frec. cardiaca (latidos/min)");
        heartRateTask.addChartSource(new HeartRateChartSource());
        heartRateTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                try {
                    heartRateChartLayout.getChildren().clear();
                    heartRateChartLayout.getChildren().add(heartRateTask.get());
                } catch (Exception ex) {
                    Logger.getLogger(WorkoutController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Thread thread3 = new Thread(heartRateTask);
        thread3.setDaemon(true);
        thread3.start();

        //CADENCIA
        cadenceLayout.getChildren().clear();
        cadenceLayout.getChildren().add(new Label("Espera..."));

        LineChartTask cadenceTask = new LineChartTask(trackData, abscissa);
        cadenceTask.setYName("Cadencia");
        cadenceTask.addChartSource(new CadenceChartSource());
        cadenceTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                try {
                    cadenceLayout.getChildren().clear();
                    cadenceLayout.getChildren().add(cadenceTask.get());
                } catch (Exception ex) {
                    Logger.getLogger(WorkoutController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Thread thread4 = new Thread(cadenceTask);
        thread4.setDaemon(true);
        thread4.start();

        createSummaryChart(abscissa);

    }

    private void setupLabels(TrackData trackData) {
        dateTime.setText(DateTimeUtils.format(trackData.getStartTime()));
        distance.setText(round2(trackData.getTotalDistance() / 1000.d) + " km");

        long secs = trackData.getTotalDuration().getSeconds();
        duration.setText(String.format("%d:%02d:%02d", secs / 3600, (secs % 3600) / 60, (secs % 60)));

        secs = trackData.getTotalDuration().getSeconds();
        trackData.getMovingTime();
        movementTime.setText(String.format("%d:%02d:%02d", secs / 3600, (secs % 3600) / 60, (secs % 60)));

        ascent.setText(round2(trackData.getTotalAscent()) + " m");
        descent.setText(round2(trackData.getTotalAscent()) + " m");
        averageCadence.setText(trackData.getAverageCadence() + "");
        maxCadence.setText(trackData.getMaxCadence() + "");
        averageSpeed.setText(round2(trackData.getAverageSpeed()) + " km/h");
        maxSpeed.setText(round2(trackData.getMaxSpeed()) + " km/h");
        maxHeartRate.setText(trackData.getMaxHeartrate() + "");
        averageHeartRate.setText(trackData.getAverageHeartrate() + "");
        minHeartRate.setText(trackData.getMinHeartRate() + "");
    }

    private void createSummaryChart(ChartTask.Abscissa abscissa) {
        //GRAFICA RESUMEN
        summaryChartLayout.getChildren().clear();
        summaryChartLayout.getChildren().add(new Label("Espera..."));

        LineChartTask summaryTask = new LineChartTask(trackData, abscissa);
        summaryTask.setYName("");
        if (cadenceToggle.isSelected()) {
            summaryTask.addChartSource(new CadenceChartSource());
        }
        if (heartRateToggle.isSelected()) {
            summaryTask.addChartSource(new HeartRateChartSource());
        }
        if (speedToggle.isSelected()) {
            summaryTask.addChartSource(new SpeedChartSource());
        }
        summaryTask.setLegendVisible(true);
        summaryTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                try {
                    summaryChartLayout.getChildren().clear();
                    summaryChartLayout.getChildren().add(summaryTask.get());
                } catch (Exception ex) {
                    Logger.getLogger(WorkoutController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        Thread thread5 = new Thread(summaryTask);
        thread5.setDaemon(true);
        thread5.start();
    }

    public static double round2(double num) {
        return (Math.round(num * 100.d) / 100.d);
    }

}
