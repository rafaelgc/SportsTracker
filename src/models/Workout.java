/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import jgpx.model.analysis.TrackData;

/**
 *
 * @author Rafa
 */
public class Workout {
    private String name;
    private TrackData trackData;
    
    public Workout(String name, TrackData trackData) {
        this.name = name;
        this.trackData = trackData;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the trackData
     */
    public TrackData getTrackData() {
        return trackData;
    }

    /**
     * @param trackData the trackData to set
     */
    public void setTrackData(TrackData trackData) {
        this.trackData = trackData;
    }
    
    public String toString() {
        return name;
    }
    
    public boolean equals(Object other) {
        if (other instanceof Workout) {
            Workout w = (Workout) other;
            return w.getName().compareTo(name) == 0 && 
                    w.getTrackData().getTotalDistance() == trackData.getTotalDistance();
        }
        return false;
    }
}
