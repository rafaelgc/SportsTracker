/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models.charts;

import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import jgpx.model.analysis.TrackData;

/**
 *
 * @author Rafa
 */
public class AreaChartTask extends ChartTask {

    public AreaChartTask(TrackData td, Abscissa abscissa) {
        super(td, abscissa);
    }


    @Override
    XYChart<Number, Number> chartInstance() {
        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();
        
        AreaChart chart = new AreaChart(x, y);
        chart.setCreateSymbols(false);
        
        return chart;
    }
    
}
