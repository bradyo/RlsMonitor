package monitor;

import java.util.*;
import java.io.*;

public class DataFolderRepository 
{
    public static final String COMPLETED_FOLDER = "completed";
    
    private Collection<DataSource> dataSources;
    private Map<Integer,DataFolder> dataFolderMap;

    public DataFolderRepository(Collection<DataSource> dataSources) {
        this.dataSources = dataSources;
        this.dataFolderMap = new HashMap();
    }
    
    public void update() {
        dataFolderMap = new HashMap();
        for (DataSource dataSource : dataSources) {
            addDataSourceToMap(dataSource);
        }
    }
    
    private void addDataSourceToMap(DataSource dataSource) {
        // add experiments in data source root directory
        String facilityName = dataSource.getFacilityName();
        File rootFolder = dataSource.getFolder();
        if (rootFolder.isDirectory()) {
            addFolderToMap(facilityName, rootFolder);
        }
        // add experiments in data source "completed" sub-folder
        File completedFolder = new File(rootFolder + File.separator + COMPLETED_FOLDER);
        if (completedFolder.isDirectory()) {
            addFolderToMap(facilityName, completedFolder);
        }
    }

    private void addFolderToMap(String facilityName, File folder) {
        for (File file : folder.listFiles()) {
            if (isExperimentFolder(file)) {
                Integer experimentNumber = Integer.parseInt(file.getName());
                DataFolder dataFolder = new DataFolder(facilityName, experimentNumber, file);
                dataFolderMap.put(experimentNumber, dataFolder);
            }
        }
    }
    
    private Boolean isExperimentFolder(File file) {
        if (file.isDirectory() && file.getName().matches("^\\d+$")) {
            return true;
        }
        return false;
    }
    
    public DataFolder getDataFolder(Integer experimentNumber) {
        return dataFolderMap.get(experimentNumber);
    }
       
    public Collection<DataFolder> findAll() {
        return dataFolderMap.values();
    }
    
    public Collection<DataFolder> findAllByFacilityName(String facilityName) {
        Collection<DataFolder> dataFolders = new ArrayList();
        for (DataFolder dataFolder : dataFolderMap.values()) {
            if (dataFolder.getFacilityName().equals(facilityName)) {
                dataFolders.add(dataFolder);
            }
        }
        return dataFolders;
    }
    
    public List<Integer> getFacilityExperimentNumbers(String facilityName) {
        List<Integer> numbers = new ArrayList();
        for (DataFolder dataFolder : dataFolderMap.values()) {
            if (dataFolder.getFacilityName().equals(facilityName)) {
                numbers.add(dataFolder.getExperimentNumber());
            }
        }
        return numbers;
    }
}
