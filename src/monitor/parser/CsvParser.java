package monitor.parser;

import java.io.*;
import java.util.*;
import monitor.*;

public class CsvParser {
 
    private Experiment experiment;
    
    
    public void parseFile(File file) {
        experiment.setNumber(513);
        experiment.setComment("github practice!");
    }
    
    public Experiment getExperiment() {
        return experiment;
    }
    
}