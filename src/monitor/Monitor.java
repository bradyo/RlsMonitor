package monitor;

import java.io.*;
import java.sql.*;
import java.util.*;
import monitor.reporting.StatusReport;

/**
 * Tracks data sources for changes (includes dissection data files and 
 * experiment key in database).
 */
public class Monitor
{   
    private Collection<DataSource> dataSources;
    private Connection connection;
    
    private KeyRepository keyRepository;
    private DataFolderRepository dataFolderRepository;
    private ExperimentRepository experimentRepository;
    private ReportRepository reportRepository;
    
    private DataFolderTracker dataFolderTracker;
    private TemplateTracker templateTracker;

    public Monitor(Collection<DataSource> dataSources, Connection connection, 
            File templateFolder, File reportFolder, CoreService coreService) {
        this.dataSources = dataSources;
        this.connection = connection;
        
        keyRepository = new KeyRepository(connection);
        dataFolderRepository = new DataFolderRepository(dataSources);
        experimentRepository = new ExperimentRepository(keyRepository, dataFolderRepository);
        reportRepository =  new ReportRepository(coreService, experimentRepository, templateFolder, reportFolder);
        
        this.dataFolderTracker = new DataFolderTracker();
        this.templateTracker = new TemplateTracker(templateFolder);
    }
    
    public void update() {
        // update repositories
        dataFolderRepository.update();
        
        // udate trackers
        dataFolderTracker.update(dataFolderRepository.findAll()); 
        templateTracker.update(); 
        
        // clear cache in experiment repository
        experimentRepository.clearCache();

        // process data sources, generating reports
        for (DataSource s : dataSources) {
            processDataSource(s);
        }
        
        // notify database that we have handled the queued updates
        clearDatabaseUpdateQueue();
    }
    
    public void processDataSource(DataSource dataSource) {
        String facilityName = dataSource.getFacilityName();
        Collection<DataFolder> dataFolders = 
                dataFolderRepository.findAllByFacilityName(facilityName);
        for (DataFolder dataFolder : dataFolders) {
            Integer experimentNumber = dataFolder.getExperimentNumber();
            if (needsNewReports(experimentNumber)) {
                // load experiment
                Experiment experiment;
                try {
                    experiment = experimentRepository.getExperiment(experimentNumber);                    
                } catch (Exception e) {
                    System.err.println("failed to load experiment: " + e.getMessage());
                    continue;
                }
                
                // re-create experiment reports
                reportRepository.updateReports(experiment);
                
                // update status report
                File file = new File(dataSource.getFolder() + File.separator + "incomplete.csv");
                StatusReport statusReport = new StatusReport(); 
                try {
                    statusReport.update(file, experiment); 
                } catch (Exception e) {
                    System.err.println("failed to update status file: " + e.getMessage());
                }
            }
        }
    }
    
    private Boolean needsNewReports(Integer experimentNumber) {
        if (templateTracker.isChanged()) {
            return true;
        }
        if (dataFolderTracker.isChanged(experimentNumber)) {
            return true;
        }
        if (isDatabaseChanged(experimentNumber)) {
            return true;
        }
        return false;
    }
       
    private Boolean isDatabaseChanged(Integer experimentNumber) {
        Boolean hasChanged = false;
        try {
            connection.clearWarnings();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM yeast_rls_update_queue q"
                    + " LEFT JOIN yeast_rls_experiment e ON e.id = q.experiment_id"
                    + " WHERE e.number = ?");
            stmt.setInt(1, experimentNumber);
                
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                hasChanged = true;
            }
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            System.err.println("failed to query database: " + ex.getMessage());
        }
        return hasChanged;
    }
    
    private void clearDatabaseUpdateQueue() {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM yeast_rls_update_queue");
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            System.err.println("failed to clear database update queue: " + ex.getMessage());
        }
    }
}
