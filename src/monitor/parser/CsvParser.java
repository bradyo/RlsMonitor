package monitor.parser;

import java.io.*;
import java.util.*;
import monitor.*;
import au.com.bytecode.opencsv.CSVReader;

/**
 * Parses MotherCellSet objects out of an experiment CSV file.
 */
public class CsvParser {
 
    private List<MotherCellSet> motherCellSets;

    public CsvParser() {
        motherCellSets = new ArrayList();
    }
    
    public void parseFile(File file) throws Exception {
        
        FileReader fileReader = new FileReader(file); 
        CSVReader csvReader = new CSVReader(fileReader);
        String[] values = csvReader.readNext();
        
        // save which columns have which row indexes in a hash map
        Map<String,Integer> headerIndexMap = new HashMap();
        for(Integer i = 0; i < values.length; i++) {
            String columnName = values[i];
            headerIndexMap.put(columnName, i);
        }
        
        // loop over data lines and create a cell set object for each
        List<MotherCellSet> cellSets = new ArrayList();
        Integer row = 1; // start at 1, since we already read header line above
        while ((values = csvReader.readNext()) != null) {
            // todo: create mother cell set
            MotherCellSet cellSet = new MotherCellSet();
            cellSet.setId(row + 1);
            cellSet.setReference(references[row]);
            cellSet.setLabel(labels[row]);
            cellSet.setStrainName(strainNames[row]);
            cellSet.setMedia(medias[row]);
            cellSet.setTemperature(temperatures[row]);

            List<MotherCell> cells = new ArrayList();
            Integer[] rowLifespans = lifespans[rowIndex];
            for (Integer cellIndex = 0; cellIndex < rowLifespans.length; cellIndex++) {
                MotherCell motherCell = new MotherCell();
                motherCell.setId(cellIndex);
                motherCell.setLifespan(rowLifespans[cellIndex]);
                cells.add(motherCell);
            }
            
            row++;
        }
    }

    
    public List<MotherCellSet> getCellSets() {
        return motherCellSets;
    }
}