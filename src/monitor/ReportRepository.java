package monitor;

import java.io.*;
import java.util.List;
import monitor.reporting.*;

public class ReportRepository 
{
    private CoreService coreService;
    private ExperimentRepository experimentRepository;
    private File outputFolder;
    private File templateFolder;

    public ReportRepository(CoreService coreSerivice, ExperimentRepository experimentRepository,
            File templateFolder, File outputFolder) {
        this.coreService = coreSerivice;
        this.experimentRepository = experimentRepository;
        this.outputFolder = outputFolder;
        this.templateFolder = templateFolder;
    }
    
    public void updateReports(String experimentName) {
        System.out.println();
        System.out.println("Updating reports experiment: " + experimentName);
        
        Experiment experiment;
        try {
            experiment = experimentRepository.getExperiment(experimentName);
        } catch (Exception e) {
            System.err.println("failed to load experiment: " + e.getMessage());
            return;
        }
        
        // delete all experiment reports
        deleteAllReports(experiment);
        
        // generate static reports
        File folder = getExperimentReportFolder(experiment);
        try {
            DatabaseReport databaseReport = new DatabaseReport(experiment);
            String filename = getDatabaseReportFilename(experiment);
            File file = new File(folder + File.separator + filename);
            databaseReport.write(file);
        }
        catch (Exception e) {
            System.err.println("Failed to generate database report: " + e.getMessage());
        }
            
        // generate templated reports
        for (File templateFile : templateFolder.listFiles()) {
            try {
                String filename = getTemplateReportFilename(experiment, templateFile);
                File file = new File(folder + File.separator + filename);
                TemplateReport templateReport = new TemplateReport(experiment, templateFile);
                templateReport.write(file);
            }
            catch (Exception e) {
                System.err.println("Failed to generate templated report: " + e.getMessage());
            }
        }
        
        // notify core system about completed experiment
        if (experiment.isComplete()) {
            try {
                coreService.notifyExperimentComplete(experimentName);
            } catch (Exception e) {
                System.err.println("failed to notify core site of completed experiment: " + e.getMessage());
                System.err.println(e.getStackTrace());
            }
        }
    }
    
    private void deleteAllReports(Experiment experiment) {
        String experimentName = experiment.getName();
        
        // delete incomplete reports
        File folder = new File(outputFolder + File.separator + experimentName);
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                file.delete();
            }
        }
    }
    
    private File getExperimentReportFolder(Experiment experiment) {
        String experimentName = experiment.getName();
        File file = new File(outputFolder + File.separator + experimentName);
        if (! file.isDirectory()) {
            file.mkdir();
        }
        return file;
    }
    
    private String getDatabaseReportFilename(Experiment experiment) {
        String filename;
        if (experiment.isComplete()) {
            filename = experiment.getName() + ".csv";
        } else {
            filename = experiment.getName() + "_incomplete.csv";
        }
        return filename;
    }
    
    private String getTemplateReportFilename(Experiment experiment, File templateFile) {
        String filename;
        if (experiment.isComplete()) {
            filename = experiment.getName() + "_" + templateFile.getName();
        } else {
            filename = experiment.getName() + "_incomplete_" + templateFile.getName();
        }
        return filename;
    }
    
    public void updateStatusReport(DataSource dataSource) throws Exception {
        System.out.println("Updating status report for data source: " 
                + dataSource.getFacilityName());
        
        // get incomplete experiments from repository
        // here we dont need any key data so we should avoid an unnecessary
        // trip to the database
        String facilityName = dataSource.getFacilityName();
        List<Experiment> experiments = 
                experimentRepository.getExperimentsByFacility(facilityName);
        File file = new File(dataSource.getFolder() + File.separator + "incomplete.csv");
        StatusReport statusReport = new StatusReport(experiments); 
        statusReport.save(file); 
    }
}
