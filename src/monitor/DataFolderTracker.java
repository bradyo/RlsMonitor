package monitor;

import java.util.*;

/**
 * Tracks experiment data folder states for experiments.
 */
public class DataFolderTracker {
    
    private Map<String,DataFolderState> previousStateMap; // experiment name => data folder state
    private Map<String,DataFolderState> currentStateMap; // experiment name => data folder state

    public DataFolderTracker() {
        previousStateMap = new HashMap();
        currentStateMap = new HashMap();
    }
    
    public void update(Collection<DataFolder> dataFolders) {
        previousStateMap = currentStateMap;
        currentStateMap = new HashMap();
        for (DataFolder dataFolder : dataFolders) {
            String experimentName = dataFolder.getExperimentName();
            DataFolderState state = new DataFolderState(dataFolder);
            currentStateMap.put(experimentName, state);
        }
    }
    
    public Boolean isChanged(String experimentNumber) {
        DataFolderState currentState = currentStateMap.get(experimentNumber);
        DataFolderState previousState = previousStateMap.get(experimentNumber);
        return ! currentState.equals(previousState);
    }
}
