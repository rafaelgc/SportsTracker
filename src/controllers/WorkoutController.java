/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import jgpx.model.analysis.Chunk;
import jgpx.model.analysis.TrackData;
import jgpx.util.DateTimeUtils;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        elevationChart.setTitle("Altura x Distancia");
        elevationChart.setLegendVisible(false);
        elevationChartX.setLabel("Distancia (km)");
        elevationChartY.setLabel("Altura (m)");
        

    }

    public void init(TrackData trackData) {

        setupLabels(trackData);

        elevationChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();

        ObservableList<Chunk> chunks = trackData.getChunks();
        double distance = 0.d;

        //Para hacer el programa más eficiente, no se mostrarán todos los puntos
        //sino sólo una parte de ellos.
        final double factor = 0.05d;

        int l = (int) (1 / factor);
        int counter = 1;

        //Pero si hay muy pocos chunks, sí se muestran todos los puntos.
        if (chunks.size() < 200) {
            l = 1;
        }
        
        if (chunks.size() > 0) {

            Chunk f = chunks.get(0);
            series.getData().add(createData(
                    distance / 1000.d, f.getFirstPoint().getElevation()));
            distance += f.getDistance();

            for (Iterator<Chunk> it = chunks.iterator(); it.hasNext();) {
                Chunk c = it.next();

                if (counter == l) {
                    series.getData().add(createData(distance / 1000.d, c.getLastPoint().getElevation()));
                    counter = 0;
                }

                distance += c.getDistance();
                counter++;

            }
            elevationChart.getData().add(series);
        }

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
