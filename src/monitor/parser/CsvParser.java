package monitor.parser;

import java.io.*;
import java.util.*;
import monitor.*;

/**
 * Parses MotherCellSet objects out of an experiment CSV file.
 */
public class CsvParser {
 
    private List<MotherCellSet> motherCellSets;

    public CsvParser() {
        motherCellSets = new ArrayList();
    }
    
    public void parseFile(File file) {
        
        // TODO
        
    }
    
    public List<MotherCellSet> getCellSets() {
        return motherCellSets;
    }
}
