/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    double maxVAxis, maxAbscissa;
    //FIN OPTIMIZACIÓN
    
    private XYChart.Series<Number, Number> series;
    //private ObservableList<XYChart.Data<Number, Number>> data;
    
    //
    List<XYChart.Data<Number, Number>> tmp;

    public XYChartManager(XYChart chart, double optimizationFactor) {
        this.chart = chart;
        this.series = new XYChart.Series<>();
        chart.getData().add(series);
        
        //data = FXCollections.observableArrayList();
        
        this.optimizationFactor = optimizationFactor;
        l = (int)(1 / optimizationFactor);
        counter = 0;
        maxVAxis = 0.d;
        maxAbscissa = 0.d;
    }
    
    public void addData(Chunk c, double abscissaValue) {
        
        /*
        Cuando se optimiza es probable que se pierda información de especial
        interés como el punto máximo de la gráfica. Así que conviene implementar
        un mecanismo para no perder ese punto.
        
        Lo que hago es almacenar el punto máximo encontrado hasta este momento.
        Cuando ese punto es superado por otro, ese otro (que podría ser el
        mayor absoluto) es sistemáticamente añadido.
        
        */
        boolean add = false;
        if (getVerticalAxisValue(c) > maxVAxis) {
            maxVAxis = getVerticalAxisValue(c);
            maxAbscissa = abscissaValue;
            add = true;
        }
        
        
        if (counter == l || add) {
            //series.getData().add(createData(abscissaValue, getVerticalAxisValue(c)));
            tmp.add(createData(abscissaValue, getVerticalAxisValue(c)));
            counter = 0;
        }
        
        counter++;
    }
    
    void start() {
        tmp = new ArrayList<>();
    }
    
    void end() {
        //System.out.println("INICIO END");
        long inicio = System.nanoTime();
        series.getData().addAll(FXCollections.observableArrayList(tmp));
        long fin = System.nanoTime();
        //System.out.println("FIN END: " + (fin - inicio) / 1000000000.d + " : " + tmp.size());
    }
    
    public void clear() {
        maxVAxis = 0.d;
        chart.setAnimated(false);
        series.getData().clear();
        chart.setAnimated(true);
    }
    
    public XYChart<Number, Number> getChart() {
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
