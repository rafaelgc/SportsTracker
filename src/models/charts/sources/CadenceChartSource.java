/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models.charts.sources;

import jgpx.model.analysis.Chunk;
import models.charts.ChartSource;

/**
 *
 * @author Rafa
 */
public class CadenceChartSource extends ChartSource {
    public CadenceChartSource() {
        super("Cadencia");
    }

    @Override
    public double getAbscissaValue(Chunk chunk) {
        return chunk.getAvgCadence();
    }
}