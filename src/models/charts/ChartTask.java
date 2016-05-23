/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models.charts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import jgpx.model.analysis.Chunk;
import jgpx.model.analysis.TrackData;

/**
 *
 * @author Rafa
 */
public abstract class ChartTask extends Task<XYChart<Number, Number>> {
    
    private TrackData trackData;
    
    private List<ChartSource> chartSources;
    private Abscissa abscissa;
    private String xName, yName;
    private boolean legendVisible;
    
    public enum Abscissa {
        DISTANCE, TIME
    };
    
    public ChartTask(TrackData td, Abscissa abscissa) {
        trackData = td;
        chartSources = new ArrayList<>();
        
        this.abscissa = abscissa;
        if (abscissa == Abscissa.DISTANCE) {
            xName = "Distancia (km)";
        } else {
            xName = "Tiempo (min)";
        }
        legendVisible = false;
    }
    
    public void setLegendVisible(boolean val) {
        legendVisible = val;
    }
    
    public void setYName(String yName) {
        this.yName = yName;
    }
    
    public void addChartSource(ChartSource source) {
        chartSources.add(source);
    }
    
    @Override
    protected XYChart<Number, Number> call() throws Exception {
        return createGraph(trackData);
    }
    
    private XYChart<Number, Number> createGraph(TrackData td) {
        long ini = System.nanoTime();
        long totalIni = ini;
        List<Chunk> chunks = td.getChunks();
        
        XYChart<Number, Number> chart = chartInstance();
        
        chart.setPrefHeight(350);
        chart.getXAxis().setLabel(xName);
        chart.getYAxis().setLabel(yName);
        chart.setLegendVisible(legendVisible);

        //SERIES
        for (int i = 0; i < chartSources.size(); i++) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setData(chartSources.get(i).getData());
            series.setName(chartSources.get(i).getName());
            chart.getData().add(series);
        }
        
        fillData(chunks);
        
        return chart;
    }
    
    protected void fillData(List<Chunk> chunks) {
        double abscissaValue = 0.d;
        
        for (ChartSource c : chartSources) {
            c.getData().clear();
        }
        
        for (Iterator<Chunk> it = chunks.iterator(); it.hasNext();) {
            Chunk c = it.next();
            
            for (ChartSource source : chartSources) {
                if (source.canAdd(c)) {
                    source.getData().add(new XYChart.Data<>(abscissaValue, source.getAbscissaValue(c)));
                }
            }
            
            if (abscissa == Abscissa.TIME) {
                abscissaValue += (c.getDuration().getSeconds()) / 60.d;
            } else {
                abscissaValue += c.getDistance() / 1000.d;
            }
            
        }
    }
    
    abstract XYChart<Number, Number> chartInstance();
}
