/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models.charts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import jgpx.model.analysis.Chunk;

/**
 *
 * @author Rafa
 */
public abstract class ChartSource {

    private String name;
    private ObservableList<XYChart.Data<Number, Number>> data;

    //OPTIMIZACIÓN
    private double optimizationFactor;
    private int l;
    private int counter;
    double maxVAxis, maxAbscissa;
    //FIN OPTIMIZACIÓN

    public ChartSource(String name) {
        this.name = name;
        data = FXCollections.observableArrayList();

        //OPTIMIZACIÓN
        this.optimizationFactor = 0.2d;
        l = (int) (1 / optimizationFactor);
        counter = 0;
        maxVAxis = 0.d;
        maxAbscissa = 0.d;
    }

    public boolean canAdd(Chunk c) {
        boolean ret = false;
        
        if (getAbscissaValue(c) > maxVAxis) {
            maxVAxis = getAbscissaValue(c);
            ret = true;
        }

        if (counter == l) {
            counter = 0;
            ret = true;
        }
        
        counter++;
        
        return ret;
    }

    public String getName() {
        return name;
    }

    public ObservableList<XYChart.Data<Number, Number>> getData() {
        return data;
    }

    public abstract double getAbscissaValue(Chunk chunk);
}
