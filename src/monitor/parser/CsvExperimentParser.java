package monitor.parser;

import java.io.*;
import java.util.*;
import monitor.*;
import au.com.bytecode.opencsv.CSVReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses MotherCellSet objects out of an experiment CSV file.
 */
public class CsvExperimentParser {
 
    private Experiment experiment;
    
    public void parseFile(File file) throws Exception {
        // build experiment object
        experiment = new Experiment();
        experiment.setName(getExperimentName(file));
        experiment.setComplete(true);
        
        FileReader fileReader = new FileReader(file); 
        CSVReader csvReader = new CSVReader(fileReader);
        String[] values = csvReader.readNext();
        
        // save which columns have which row indexes in a hash map
        Map<String,Integer> headerIndexMap = new HashMap();
        for(Integer i = 0; i < values.length; i++) {
            String columnName = values[i].toLowerCase();
            if (columnName.equals("name")) {
                columnName = "label";
            }
            headerIndexMap.put(columnName, i);
        }
        
        // loop over data lines and create a cell set object for each
        Integer row = 1; // start at 1, since we already read header line above
        while ((values = csvReader.readNext()) != null) {
            MotherCellSet cellSet = new MotherCellSet();
            cellSet.setId(row);
            
            Integer referenceIndex = headerIndexMap.get("reference");
            cellSet.setReference(values[referenceIndex]);
            
            Integer labelIndex = headerIndexMap.get("label");
            String label = values[labelIndex];
            cellSet.setLabel(label);
            
            Integer strainIndex = headerIndexMap.get("strain");
            String strainName = values[strainIndex];
            cellSet.setStrainName(strainName);
            
            Integer mediaIndex = headerIndexMap.get("media");
            String media = values[mediaIndex];
            cellSet.setMedia(media);
            
            Integer tempIndex = headerIndexMap.get("temperature");
            float temp = Float.parseFloat(values[tempIndex]);
            temp = (float)(Math.round(temp * 100.0) / 100.0);
            cellSet.setTemperature(temp);

            // add cells
            List<MotherCell> cells = new ArrayList();
            Integer startIndex = headerIndexMap.get("lifespans");
            Integer endIndex = values.length - 1;
            for (Integer columnIndex = startIndex; columnIndex <= endIndex; columnIndex++) {
                MotherCell motherCell = new MotherCell();
                motherCell.setId(columnIndex - startIndex + 1);
                
                Integer lifespan;
                try {
                    lifespan = Integer.parseInt(values[columnIndex]);
                } catch (Exception e) {
                    // ignore value
                    continue;
                }
                
                motherCell.setLifespan(lifespan);
                cells.add(motherCell);
            }
            cellSet.setCells(cells);
            experiment.setCellSet(row, cellSet);
            
            row++;
        }
    }
    
    private String getExperimentName(File file) throws Exception {
        String filename = file.getName();
        Pattern pattern = Pattern.compile("^(expt)(.+)\\.csv$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(filename);
        if (! matcher.find()) {
            throw new Exception("Not a valid csv experiment filename: " + filename);
        }
        return matcher.group(1);
    }

    public Experiment getExperiment() {
        return experiment;
    }
}