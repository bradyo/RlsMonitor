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
        
    }
    
    public List<MotherCellSet> getCellSets() {
        return motherCellSets;
    }
}