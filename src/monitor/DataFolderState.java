package monitor;

import java.util.*;
import java.io.*;

public class DataFolderState 
{
    /**
     * Holds the last updated timestamp for each file in data folder
     */
    private HashMap<String,Long> lastUpdatedMap = new HashMap();

    public DataFolderState(DataFolder dataFolder) {
        List<File> files = dataFolder.getAllFiles();
        for (File file : files) {
            addFileToMap(file);
        }
    }
    
    private void addFileToMap(File file) {
        try {
            lastUpdatedMap.put(file.getCanonicalPath(), file.lastModified());
        }
        catch (Exception ex) {
            System.err.println("failed to add data file to map: " + ex.getMessage());
        }
    }

    @Override
    public int hashCode() {
        return lastUpdatedMap.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final DataFolderState other = (DataFolderState) obj;
        if (other != null) {
            return (this.hashCode() == other.hashCode());
        }
        return false;
    }
}
