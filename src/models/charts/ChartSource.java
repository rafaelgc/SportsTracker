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
    
    public ChartSource(String name) {
        this.name = name;
        data = FXCollections.observableArrayList();
    }
    
    public String getName() {
        return name;
    }
    
    public ObservableList<XYChart.Data<Number, Number>> getData() {
        return data;
    }
    
    public abstract double getAbscissaValue(Chunk chunk);
}
