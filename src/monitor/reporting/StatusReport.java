package monitor.reporting;

import au.com.bytecode.opencsv.CSVReader;
import java.io.*;
import java.util.*;
import monitor.Experiment;
import monitor.MotherCellSet;

/**
 * Generates a report on the number of incomplete sets across experiments in
 * one data folder.
 */
public class StatusReport
{      
    public void update(File file, Experiment experiment) throws Exception {
        // read status report data into array
        HashMap<Integer,ArrayList> map = new HashMap();
        if (file.exists()) {
            CSVReader csvReader = new CSVReader(new FileReader(file));
            csvReader.readNext(); // ignore headers
            String[] rowValues;
            while ((rowValues = csvReader.readNext()) != null) {
                try {
                    Integer experimentNumber = Integer.parseInt(rowValues[0]);
                    Integer setId = Integer.parseInt(rowValues[1]);
                    ArrayList setIds;
                    if (map.containsKey(experimentNumber)) {
                        setIds = map.get(experimentNumber);
                    } else {
                        setIds = new ArrayList();
                    }
                    setIds.add(setId);
                    map.put(experimentNumber, setIds);
                }
                catch (Exception e) {
                    System.err.println("Row with invalid data skipped while updating status report");
                }
            }
        }
        
        // update values in map
        ArrayList<Integer> incompleteSetIds = new ArrayList();
        Map<Integer,MotherCellSet> cellSetsMap = experiment.getCellSetMap();
        for (Integer setId : cellSetsMap.keySet()) {
            MotherCellSet cellSet = cellSetsMap.get(setId);
            if (! cellSet.isComplete()) {
                incompleteSetIds.add(setId);
            }
        }
        map.put(experiment.getNumber(), incompleteSetIds);

        // save status data back to file
        file.createNewFile();
        Writer writer = new BufferedWriter(new FileWriter(file));
        writer.write("experiment,set\n");
        TreeSet<Integer> experimentIds = new TreeSet<Integer>(map.keySet());
        for (Integer experimentId : experimentIds) { 
            TreeSet<Integer> setIds = new TreeSet<Integer>(map.get(experimentId));
            for (Integer setId : setIds) {
                writer.write(experimentId + "," + setId + "\n");
            }
        }
        writer.close();
    }
    
}
