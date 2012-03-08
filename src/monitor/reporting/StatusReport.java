package monitor.reporting;

import monitor.*;
import java.io.*;
import java.util.*;

/**
 * Generates a report on the number of incomplete sets across experiments in
 * one data folder.
 */
public class StatusReport
{   
    private List<Experiment> experiments;

    public StatusReport(List<Experiment> experiments) throws Exception {
        this.experiments = experiments;
    }
    
    public void save(File file) throws Exception {
        file.createNewFile();
        
        Writer writer = new BufferedWriter(new FileWriter(file));
        writer.write("experiment,set\n");
        
        for (Experiment experiment : experiments) {
            Map<Integer,MotherCellSet> cellSetsMap = experiment.getCellSetMap();
            for (Integer setNumber : cellSetsMap.keySet()) {
                MotherCellSet cellSet = cellSetsMap.get(setNumber);
                if (!cellSet.isComplete()) {
                    writer.write(experiment.getNumber() + "," + cellSet.getId() + "\n");
                }
            }
        }
        writer.close();
    }
    
    public void update(File file) throws Exception {
        if (! file.exists()) {
            return this.save(file);
        }
    }
    
}
