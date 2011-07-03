package monitor;

import java.io.*;

public class DataSource 
{
    private String facilityName;
    private File folder;

    public DataSource(String facilityName, File folder) {
        this.facilityName = facilityName;
        this.folder = folder;
    }
    
    public String getFacilityName() {
        return facilityName;
    }

    public File getFolder() {
        return folder;
    }
}
