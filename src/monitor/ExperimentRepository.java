package monitor;

import monitor.parser.*;
import java.io.*;
import java.util.*;

/**
 * Fetches Experiment objects from data sources.
 */
public class ExperimentRepository {
    
    /**
     * Cached Experiment map, saves loaded experiments.
     */
    private HashMap<String,Experiment> experimentMap; // name => expeirment
    
    /**
     * Key repository for fetching experiment key data.
     */
    private KeyRepository keyRepository;
    
    /**
     * Data folder repository for fetching data folders for experiments.
     */
    private DataFolderRepository dataFolderRepository;
    
    /**
     * CSV repository for fetching experiment data in legacy CSV format.
     */
    private CsvExperimentRepository csvExperimentRepository;
    
    
    public ExperimentRepository(KeyRepository keyRepository, 
             DataFolderRepository dataFolderRepository,
             CsvExperimentRepository csvExperimentRepository) {
        this.keyRepository = keyRepository;
        this.dataFolderRepository = dataFolderRepository;
        this.csvExperimentRepository = csvExperimentRepository;
        this.experimentMap = new HashMap();
    }

    public Experiment getExperiment(String experimentName) throws Exception {
        Experiment experiment;
        if (experimentMap.containsKey(experimentName)) {
            // get from cache if possible
            experiment = experimentMap.get(experimentName);
        } 
        else if (dataFolderRepository.hasExperiment(experimentName)) {
            // get from data folder
            experiment = buildExperiment(experimentName);
            experimentMap.put(experimentName, experiment); // cache
        }
        else if (csvExperimentRepository.hasExperiment(experimentName)) {
            // try getting from csv repository
            experiment = csvExperimentRepository.getExperiment(experimentName);
            experimentMap.put(experimentName, experiment); // cache
        }
        else {
            throw new Exception("Experiment '" + experimentName + "' does not exist");
        }
        return experiment;
    }

    private Experiment buildExperiment(String name) throws Exception {
        Experiment experiment = new Experiment();
        experiment.setName(name);        
        
        // populate cell sets
        Map<Integer,MotherCellSetKey> cellSetKeyMap = new HashMap();
        try {
            cellSetKeyMap = keyRepository.getCellSetKeyMap(name);
        } catch (Exception e) {
            System.err.println("failed to load key data: " + e.getMessage());
        }
        
        DataFolder dataFolder = dataFolderRepository.getDataFolder(name);
        boolean isComplete;
        if (dataFolder.getDeadDataFiles().isEmpty()) {
            isComplete = false;
        } else {
            // try to set false while scanning files
            isComplete = true;
        }
        for (File dataFile : dataFolder.getDataFiles()) {
            try {
                DataFileParser dataFileParser = new DataFileParser();
                dataFileParser.parseFile(dataFile);

                Map<Integer,MotherCellSet> cellSetMap = dataFileParser.getCellSetMap();
                for (Integer id : cellSetMap.keySet()) {
                    MotherCellSetKey key = cellSetKeyMap.get(id);
                    MotherCellSet cellSet = cellSetMap.get(id);
                    cellSet.setKey(key);
                    experiment.setCellSet(id, cellSet);
                    
                    if (! cellSet.isComplete()) {
                        isComplete = false;
                    }
                }
            } catch (Exception e) {
                System.err.println("failed to load data file");
            }
        }
        
        // even if all cell sets are complete, if we are missing an expected
        // cell set, the experiment is not complete
        Integer cellSetCount;
        if (! cellSetKeyMap.isEmpty()) {
            cellSetCount = cellSetKeyMap.size();
        } else {
            cellSetCount = experiment.getMaxCellSetId();
        }
        for (int i = 1; i <= cellSetCount; i++) {
            MotherCellSet cellSet = experiment.getCellSet(i);
            if (cellSet == null) {
                isComplete = false;
                break;
            }
        }
        experiment.setComplete(isComplete);
        
        // populate experiment comments
        File commentsFile = dataFolder.getCommentsFile();
        if (commentsFile != null && commentsFile.exists()) {
            StringWriter writer = new StringWriter();
            BufferedReader reader = new BufferedReader(new FileReader(commentsFile));
            String line;
            while ((line = reader.readLine()) != null) {
                writer.append(line);
                writer.append("\n");
            }
            reader.close();
            writer.close();
            experiment.setComment(writer.toString().trim());
        }
        
        return experiment;
    }

    List<Experiment> getExperimentsByFacility(String facilityName) throws Exception {
        List<Experiment> experiments = new ArrayList();
        List<String> experimentNames 
                = dataFolderRepository.getFacilityExperimentNames(facilityName);
        for (String expeirmentName : experimentNames) {
            try {
                Experiment experiment = getExperiment(expeirmentName);
                experiments.add(experiment);
            } catch (Exception e) {
                System.err.println("failed to get experiment " + expeirmentName);
            }
        }
        return experiments;
    }
    
    public void clearCache() {
        experimentMap = null;
        experimentMap = new HashMap();
    }
}
