package monitor;

import monitor.parser.*;
import java.io.*;
import java.util.*;

/**
 * Fetches Experiment objects from data sources.
 */
public class CsvRepository 
{
    private HashMap<Integer,Experiment> experimentMap;
    private KeyRepository keyRepository;
    private DataFolderRepository dataFolderRepository;

    public CsvRepository(KeyRepository keyRepository, 
            DataFolderRepository dataFolderRepository) {
        this.keyRepository = keyRepository;
        this.dataFolderRepository = dataFolderRepository;
        this.experimentMap = new HashMap();
    }

    public Experiment getExperiment(Integer experimentNumber) throws Exception {
        // get experiment from cache if possible, otherwise build up from data
        Experiment experiment;
        if (experimentMap.containsKey(experimentNumber)) {
            experiment = experimentMap.get(experimentNumber);
        } else {
            experiment = buildExperiment(experimentNumber);
            experimentMap.put(experimentNumber, experiment);
        }
        return experiment;
    }
    
    private Experiment buildExperiment(Integer experimentNumber) throws Exception {
        Experiment experiment = new Experiment();
        experiment.setNumber(experimentNumber);        
        
        // populate cell sets
        Map<Integer,MotherCellSetKey> cellSetKeyMap = new HashMap();
        try {
            cellSetKeyMap = keyRepository.getCellSetKeyMap(experimentNumber);
        } catch (Exception e) {
            System.err.println("failed to load key data: " + e.getMessage());
        }
        
        DataFolder dataFolder = dataFolderRepository.getDataFolder(experimentNumber);
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
        List<Integer> numbers = dataFolderRepository.getFacilityExperimentNumbers(facilityName);
        for (Integer number : numbers) {
            try {
                Experiment experiment = getExperiment(number);
                experiments.add(experiment);
            } catch (Exception e) {
                System.err.println("failed to get experiment " + number);
            }
        }
        return experiments;
    }
    
    public void clearCache() {
        experimentMap = null;
        experimentMap = new HashMap();
    }
}
