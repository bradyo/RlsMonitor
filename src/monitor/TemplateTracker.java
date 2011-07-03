package monitor;

import java.io.*;
import java.util.*;

/**
 * Tracks report templates folder state.
 */
public class TemplateTracker 
{   
    private File folder;
    private Map<String,Long> previousUpdatedMap;
    private Map<String,Long> currentUpdatedMap;

    public TemplateTracker(File folder) {
        this.folder = folder;
        previousUpdatedMap = new HashMap();
        currentUpdatedMap = new HashMap();
    }

    public void update() {
        previousUpdatedMap = currentUpdatedMap;
        currentUpdatedMap = new HashMap();
        for (File file : folder.listFiles()) {
            String filename = file.getName();
            if (isTemplateFilename(filename)) {
                currentUpdatedMap.put(filename, file.lastModified());
            }
        }
    }
    
    private Boolean isTemplateFilename(String filename) {
        if (filename.matches("^.+\\.xlsx$")) {
            return true;
        }
        return false;
    }
    
    public boolean isChanged() {
        int current = currentUpdatedMap.hashCode();
        int previous = previousUpdatedMap.hashCode();
        return (current != previous);
    }
}
