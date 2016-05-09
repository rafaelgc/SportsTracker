/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.Iterator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import jgpx.model.analysis.Chunk;
import jgpx.model.analysis.TrackData;

/**
 *
 * @author Rafa
 */
public class HeartRateZonesChartManager {

    private PieChart chart;
    private ObservableList<PieChart.Data> heartRateZonesData;
    private TrackData trackData;
    private int maxHeartRate;

    public HeartRateZonesChartManager(PieChart chart) {
        this.chart = chart;
        heartRateZonesData = FXCollections.observableArrayList();
        chart.setData(heartRateZonesData);
        maxHeartRate = 0;
    }
    
    public void update(TrackData trackData) {
        this.trackData = trackData;
        if (maxHeartRate <= 0) return;
        
        long[] timeCounter = {
            0L, 0L, 0L, 0L, 0L
        };
        double[] limits = {
            0.6d, 0.7d, 0.8d, 0.9d, 1.d
        };
        String[] limitsNames = {
            "Z1 Recuperación", "Z2 Fondo", "Z3 Tempo",
            "Z4 Umbral", "Z5 Anaeróbico"
        };
        
        
        for (Iterator<Chunk> it = trackData.getChunks().iterator(); it.hasNext();) {
            Chunk c = it.next();

            for (int i = 0; i < limits.length; i++) {
                if (c.getAvgHeartRate() < limits[i] * maxHeartRate) {
                    timeCounter[i] += c.getDuration().getSeconds();
                }
            }
        }

        heartRateZonesData.clear();

        for (int i = 0; i < timeCounter.length; i++) {
            heartRateZonesData.add(new PieChart.Data(limitsNames[i], timeCounter[i]));
        }
    }
    
    public void setMaxHeartRate(int maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
        
        update(trackData);
        
    }
}
