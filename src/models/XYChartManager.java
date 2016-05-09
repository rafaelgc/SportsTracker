/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import javafx.scene.chart.XYChart;
import javafx.scene.shape.Rectangle;
import jgpx.model.analysis.Chunk;

/**
 *
 * @author Rafa
 */
public class XYChartManager {

    private final XYChart<Number, Number> chart;

    //OPTIMIZACIÓN
    private double optimizationFactor;
    private int l;
    private int counter;
    private XYChart.Series<Number, Number> series;

    public XYChartManager(XYChart chart, double optimizationFactor) {
        this.chart = chart;
        this.series = new XYChart.Series<>();
        chart.getData().add(series);
        
        this.optimizationFactor = optimizationFactor;
        l = (int)(1 / optimizationFactor);
        counter = 0;
    }

    public void addData(Chunk c, double abscissaValue) {
        
        if (counter == l) {
            
            series.getData().add(createData(abscissaValue, getVerticalAxisValue(c)));
            
            counter = 0;
        }
        
        counter++;
    }
    
    public void clear() {
        series.getData().clear();
    }
    
    public XYChart getChart() {
        return chart;
    }
    
    protected double getVerticalAxisValue(Chunk c) {
        //Sobreescribir en subclases.
        return 0.d;
    }
    
    protected XYChart.Data<Number, Number> createData(double a, double b) {
        //Crea un XYChart sin el punto.
        XYChart.Data<Number, Number> d = new XYChart.Data<Number, Number>(a, b);
        d.setNode(new Rectangle(0, 0));
        return d;
    }
}
