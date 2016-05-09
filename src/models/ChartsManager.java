/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import jgpx.model.analysis.Chunk;
import jgpx.model.analysis.TrackData;

/**
 *
 * @author Rafa
 */
public class ChartsManager {

    public enum Abscissa {
        DISTANCE, TIME
    };

    private List<XYChartManager> chartManagers;
    private Abscissa abscissa;
    
    public ChartsManager() {
        setAbscissa(Abscissa.DISTANCE);
        chartManagers = new ArrayList<>();
    }
    
    public void update(TrackData data) {
        
        ObservableList<Chunk> chunks = data.getChunks();
        
        double abscissaValue = 0.d;
        
        for (Iterator<Chunk> it = chunks.iterator(); it.hasNext();) {
            Chunk c = it.next();
            
            for (Iterator<XYChartManager> man = chartManagers.iterator(); man.hasNext();) {
                XYChartManager m = man.next();
                
                m.addData(c, abscissaValue);
                
            }
            
            if (abscissa == Abscissa.DISTANCE) {
                abscissaValue+=c.getDistance();
            }
            else {
                abscissaValue+=c.getDuration().toMinutes();
            }
            
        }
        
    }
    
    public void setAbscissa(Abscissa abs) {
        this.abscissa = abs;
        if (abs == Abscissa.DISTANCE) {
            //Cambiar nombres a los ejes.
        }
        else {
            //Cambiar nombres a los ejes.
        }
    }
    
    public void addXYChartManager(XYChartManager manager) {
        chartManagers.add(manager);
    }
}
