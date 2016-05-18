/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.shape.Rectangle;
import jgpx.model.analysis.Chunk;
import jgpx.model.analysis.TrackData;
import jgpx.util.DateTimeUtils;
import models.CadenceChartManager;
import models.ChartsManager;
import models.ElevationChartManager;
import models.HeartRateChartManager;
import models.HeartRateZonesChartManager;
import models.SpeedChartManager;
import models.XYChartManager;

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
    private AreaChart<Number, Number> elevationChart;
    @FXML
    private NumberAxis elevationChartY;
    @FXML
    private NumberAxis elevationChartX;
    @FXML
    private LineChart<Number, Number> speedChart;
    @FXML
    private NumberAxis speedChartY;
    @FXML
    private NumberAxis speedChartX;
    @FXML
    private LineChart<Number, Number> heartRateChart;
    @FXML
    private NumberAxis heartRateChartY;
    @FXML
    private NumberAxis heartRateChartX;
    @FXML
    private LineChart<Number, Number> cadenceChart;
    @FXML
    private NumberAxis cadenceChartY;
    @FXML
    private NumberAxis cadenceChartX;

    @FXML
    private ToggleGroup abscissa;
    @FXML
    private TextField maxHeartRateTextField;
    @FXML
    private PieChart heartRateZonesChart;

    HeartRateZonesChartManager heartRateZonesChartManager;

    ChartsManager chartsManager;
    @FXML
    private RadioButton distanceRadioButton;
    @FXML
    private RadioButton timeRadioButton;

    TrackData trackData;
    @FXML
    private LineChart<Number, Number> summaryChart;
    @FXML
    private NumberAxis summaryChartY;
    @FXML
    private NumberAxis summaryChartX;
    @FXML
    private ToggleButton speedToggle;
    @FXML
    private ToggleButton heartRateToggle;
    @FXML
    private ToggleButton cadenceToggle;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        elevationChart.setLegendVisible(false);
        elevationChartY.setLabel("Altura (m)");

        speedChart.setLegendVisible(false);
        speedChartY.setLabel("Velocidad (km/h)");

        heartRateChart.setLegendVisible(false);
        heartRateChartY.setLabel("Freq. cardiaca (latidos/min.)");

        cadenceChart.setLegendVisible(false);
        cadenceChartY.setLabel("Cadencia");

        chartsManager = new ChartsManager();

        double opti = 0.05d;

        chartsManager.addXYChartManager(new ElevationChartManager(elevationChart, opti));
        chartsManager.addXYChartManager(new SpeedChartManager(speedChart, opti));
        chartsManager.addXYChartManager(new HeartRateChartManager(heartRateChart, opti));
        chartsManager.addXYChartManager(new CadenceChartManager(cadenceChart, opti));
        
        SpeedChartManager sumSCM = new SpeedChartManager(summaryChart, opti, "Velocidad");
        HeartRateChartManager sumHCM = new HeartRateChartManager(summaryChart, opti, "Frec. cardiaca");
        CadenceChartManager sumCCM = new CadenceChartManager(summaryChart, opti, "Cadencia");
        
        chartsManager.addXYChartManager(sumSCM);
        chartsManager.addXYChartManager(sumHCM);
        chartsManager.addXYChartManager(sumCCM);
        
        chartsManager.setAbscissa(ChartsManager.Abscissa.DISTANCE);

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
                    chartsManager.setAbscissa(ChartsManager.Abscissa.DISTANCE);
                } else {
                    chartsManager.setAbscissa(ChartsManager.Abscissa.TIME);
                }
                chartsManager.update(trackData);
            }
        });
        
        elevationChart.setCache(true);
        speedChart.setCache(true);
        cadenceChart.setCache(true);
        heartRateChart.setCache(true);
        
        speedToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                sumSCM.setVisible(newValue);
            }
        });
        
        heartRateToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                sumHCM.setVisible(newValue);
            }
        });
        
        cadenceToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                sumCCM.setVisible(newValue);
            }
        });

    }

    public void init(TrackData trackData) {
        this.trackData = trackData;

        setupLabels(trackData);

        chartsManager.update(trackData);
        heartRateZonesChartManager.update(trackData);

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

    private XYChart.Data<Number, Number> createData(double a, double b) {
        //Crea un XYChart sin el punto.
        XYChart.Data<Number, Number> d = new XYChart.Data<Number, Number>(a, b);
        d.setNode(new Rectangle(0, 0));
        return d;
    }

    private static double round2(double num) {
        return (Math.round(num * 100.d) / 100.d);
    }

}
