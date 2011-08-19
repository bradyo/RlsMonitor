package monitor;

import monitor.parser.*;
import java.io.*;
import java.util.*;

/**
 * Fetches Experiment objects from data sources.
 */
public class CsvExperimentRepository 
{
    private HashMap<String,Experiment> experimentMap; // experiment name => experiment
    private KeyRepository keyRepository;

    public CsvExperimentRepository(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
        this.experimentMap = new HashMap();
    }

    public Experiment getExperiment(String experimentName) throws Exception {
        return null;
    }
}
