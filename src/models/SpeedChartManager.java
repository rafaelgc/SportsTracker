/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import javafx.scene.chart.XYChart;
import jgpx.model.analysis.Chunk;

/**
 *
 * @author Rafa
 */
public class SpeedChartManager extends XYChartManager {
    
    public SpeedChartManager(XYChart chart, double optimizationFactor, String name) {
        super(chart, optimizationFactor, name);
    }
    
    public SpeedChartManager(XYChart chart, double optimizationFactor) {
        super(chart, optimizationFactor);
    }
    
    @Override
    public double getVerticalAxisValue(Chunk c) {
        return c.getSpeed();
    }
    
}
