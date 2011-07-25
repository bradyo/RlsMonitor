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
    private SortedMap<String, String> sub = new TreeMap<String, String>();

    public CsvParser() {
        motherCellSets = new ArrayList();
    }
    
    public void parseFile(File file) throws Exception {
        
        FileReader fileReader = new FileReader(file); 
        CSVReader csvReader = new CSVReader(fileReader);
        String[] values = csvReader.readNext();
        csvReader.readNext();
        
        List<MotherCellSet> cellSets = new ArrayList();
        for (int row = 0; row < cellSets.length(); row++) {
            while (cellSets.hasNextLine()) {
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
            
            
            }
           
            
            startIndex = columnMap.get("lifespan");
            
            
            endIndex = values.length - 1;
        }
    
    
    }

    
    public List<MotherCellSet> getCellSets() {
        return motherCellSets;
    }
}