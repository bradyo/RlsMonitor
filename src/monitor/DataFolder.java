package monitor;

import java.util.*;
import java.io.*;

public class DataFolder 
{
    public static final String DEAD_FOLDER_NAME = "dead";
    public static final String DEAD_FOLDER_NAME_ALT = "Dead";
    public static final String COMMENTS_FILE_NAME = "comments.txt";
    
    private String facilityName;
    private String experimentName;
    private List<File> workingDataFiles;
    private List<File> deadDataFiles;
    private File commentsFile;

    public DataFolder(String facilityName, String experimentName, File folder) {
        this.facilityName = facilityName;
        this.experimentName = experimentName;
        
        workingDataFiles = new ArrayList();
        for (File file : folder.listFiles()) {
            if (isDataFile(file)) {
                workingDataFiles.add(file);
            }
            if (isCommentsFile(file)) {
                commentsFile = file;
            }
        }
        
        // add "dead" data files
        deadDataFiles = new ArrayList();
        File deadFolder = new File(folder + File.separator + DEAD_FOLDER_NAME);
        if (deadFolder.isDirectory()) {
            for (File file : deadFolder.listFiles()) {
                if (isDataFile(file)) {
                    deadDataFiles.add(file);
                }
            }
        }
        File deadFolderAlt = new File(folder + File.separator + DEAD_FOLDER_NAME_ALT);
        if (deadFolderAlt.isDirectory()) {
            for (File file : deadFolderAlt.listFiles()) {
                if (isDataFile(file)) {
                    deadDataFiles.add(file);
                }
            }
        }
    }
    
    private boolean isDataFile(File file) {
        // data files are named by a range of set numbers, or single number,
        // i.e. "1-3.xls" or "4.xls"
        return file.getName().matches("^\\d+(-\\d+)?\\.xls$");
    }
    
    private boolean isCommentsFile(File file) {
        return file.getName().equals(COMMENTS_FILE_NAME);
    }
    
    public String getFacilityName() {
        return facilityName;
    }
    
    public String getExperimentName() {
        return experimentName;
    }
    
    public List<File> getAllFiles() {
        List<File> files = getDataFiles();
        if (commentsFile != null && commentsFile.isFile()) {
            files.add(commentsFile);
        }
        return files;
    }
    
    public List<File> getDataFiles() {
        List<File> dataFiles = new ArrayList();
        dataFiles.addAll(workingDataFiles);
        dataFiles.addAll(deadDataFiles);
        return dataFiles;
    }
    
    public List<File> getDeadDataFiles() {
        return deadDataFiles;
    }
    
    public File getCommentsFile() {
        return commentsFile;
    }
}