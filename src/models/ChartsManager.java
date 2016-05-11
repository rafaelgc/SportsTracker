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
        chartManagers = new ArrayList<>();

        setAbscissa(Abscissa.DISTANCE);
    }

    public void update(TrackData data) {
        clear();
        ObservableList<Chunk> chunks = data.getChunks();

        double abscissaValue = 0.d;

        System.out.println("RELLENAR DATOS: " + chunks.size());

        System.out.println("Inicio recorrido");
        long inicio = System.nanoTime();
        for (Iterator<Chunk> it = chunks.iterator(); it.hasNext();) {
            Chunk c = it.next();
            for (Iterator<XYChartManager> man = chartManagers.iterator(); man.hasNext();) {
                XYChartManager m = man.next();

                m.addData(c, abscissaValue);

            }

            if (abscissa == Abscissa.DISTANCE) {
                abscissaValue += c.getDistance() / 1000.d;
            } else {
                abscissaValue += (c.getDuration().getSeconds()) / 60.d;
            }

        }
        

        long fin = System.nanoTime();
        System.out.println("TOTAL: " + (fin - inicio) / 1000000000.d);

    }

    public void setAbscissa(Abscissa abs) {
        this.abscissa = abs;
        String newName = "Distancia (km)";
        if (abs == Abscissa.TIME) {
            newName = "Tiempo (min)";
        }

        for (Iterator<XYChartManager> man = chartManagers.iterator(); man.hasNext();) {
            man.next().getChart().getXAxis().setLabel(newName);
        }
    }

    public void addXYChartManager(XYChartManager manager) {
        chartManagers.add(manager);
    }

    public void clear() {
        for (Iterator<XYChartManager> man = chartManagers.iterator(); man.hasNext();) {
            man.next().clear();
        }
    }
}
