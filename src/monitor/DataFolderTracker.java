package monitor;

import java.util.*;

/**
 * Tracks experiment data folder states for experiments.
 */
public class DataFolderTracker 
{   
    private Map<Integer,DataFolderState> previousStateMap;
    private Map<Integer,DataFolderState> currentStateMap;

    public DataFolderTracker() {
        previousStateMap = new HashMap();
        currentStateMap = new HashMap();
    }
    
    public void update(Collection<DataFolder> dataFolders) {
        previousStateMap = currentStateMap;
        currentStateMap = new HashMap();
        for (DataFolder dataFolder : dataFolders) {
            Integer experimentNumber = dataFolder.getExperimentNumber();
            DataFolderState state = new DataFolderState(dataFolder);
            currentStateMap.put(experimentNumber, state);
        }
    }
    
    public Boolean isChanged(Integer experimentNumber) {
        DataFolderState currentState = currentStateMap.get(experimentNumber);
        DataFolderState previousState = previousStateMap.get(experimentNumber);
        return ! currentState.equals(previousState);
    }
}
