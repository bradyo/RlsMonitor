/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package monitor.parser;

import java.io.*;
import java.util.*;
import monitor.*;

/**
 *
 * @author i2fifteen
 */
public class CsvParser {
    private List<MotherCellSet> motherCellSets;

    public CsvParser() {
        motherCellSets = new ArrayList();
    }
    
    
    //read CSV files
    
    public void parseFile(File file) {
        
        // TODO
        
    }
    
    public List<MotherCellSet> getCellSets() {
        return motherCellSets;
    }
    
}
