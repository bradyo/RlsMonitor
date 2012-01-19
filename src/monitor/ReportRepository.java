package monitor;

import java.io.File;
import java.util.List;
import monitor.reporting.DatabaseReport;
import monitor.reporting.ExtendedDatabaseReport;
import monitor.reporting.StatusReport;
import monitor.reporting.TemplateReport;

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
    
    public void updateReports(Integer experimentNumber) {
        System.out.println();
        System.out.println("Updating reports experiment: " + experimentNumber);
        
        Experiment experiment;
        try {
            experiment = experimentRepository.getExperiment(experimentNumber);
        } catch (Exception e) {
            System.err.println("failed to load experiment: " + e.getMessage());
            return;
        }
        
        // update the different types of reports
        updateDatabaseReport(experiment);
        updateExtendedDatabaseReport(experiment);
        for (File templateFile : templateFolder.listFiles()) {
            updateTemplateReport(templateFile, experiment);
        }
        
        // notify core system about completed experiment
        if (experiment.isComplete()) {
            try {
                coreService.notifyExperimentComplete(experimentNumber);
            } catch (Exception e) {
                System.err.println("failed to notify core site of completed experiment: " + e.getMessage());
                System.err.println(e.getStackTrace());
            }
        }
    }
    
    private void updateDatabaseReport(Experiment experiment) {
        // set up target directory
        File targetFolder = new File(outputFolder + File.separator + "database");
        if (! targetFolder.exists()) {
            targetFolder.mkdir();
        }
        
        // delete existing reports
        Integer experimentNumber = experiment.getNumber();
        File incompleteFile = new File(targetFolder  + File.separator 
                + experimentNumber + "-incomplete.csv");
        if (incompleteFile.exists()) {
            incompleteFile.delete();
        }
        File completeFile = new File(targetFolder + File.separator 
                + experimentNumber + ".csv");
        if (completeFile.exists()) {
            completeFile.delete();
        }
        
        // try to generate the new report
        try {
            File targetFile = incompleteFile;
            if (experiment.isComplete()) {
                targetFile = completeFile;
            }
            DatabaseReport report = new DatabaseReport(experiment);
            report.write(targetFile);
        }
        catch (Exception e) {
            System.err.println("Failed to generate database report: " + e.getMessage());
        }
    }
    
    private void updateExtendedDatabaseReport(Experiment experiment) {
        // set up target directory
        File targetFolder = new File(outputFolder + File.separator + "database-extended");
        if (! targetFolder.exists()) {
            targetFolder.mkdir();
        }
        
        // delete existing reports
        Integer experimentNumber = experiment.getNumber();
        File incompleteFile = new File(targetFolder  + File.separator 
                + experimentNumber + "-incomplete.csv");
        if (incompleteFile.exists()) {
            incompleteFile.delete();
        }
        File completeFile = new File(targetFolder + File.separator 
                + experimentNumber + ".csv");
        if (completeFile.exists()) {
            completeFile.delete();
        }
        
        // try to generate the new report
        try {
            File targetFile = incompleteFile;
            if (experiment.isComplete()) {
                targetFile = completeFile;
            }
            ExtendedDatabaseReport report = new ExtendedDatabaseReport(experiment);
            report.write(targetFile);
        }
        catch (Exception e) {
            System.err.println("Failed to generate extended database report: " + e.getMessage());
        }
    }
    
    
    private void updateTemplateReport(File templateFile, Experiment experiment) {
        // set up target directory
        if (! templateFile.getName().matches("^.+\\.xlsx$")) {
            System.err.println("Cannot generate template report for " 
                    + templateFile.getName() + ", doesnt match xlsx extension");
            return;
        }
        
        String filename = templateFile.getName();
        String reportName = filename.substring(0, filename.length() - 5);
        File targetFolder = new File(outputFolder + File.separator + reportName);
        if (! targetFolder.exists()) {
            targetFolder.mkdir();
        }
        
        // delete existing reports
        Integer experimentNumber = experiment.getNumber();
        
        File incompleteFile = new File(targetFolder, experimentNumber + "-incomplete.xlsx");
        if (incompleteFile.exists()) {
            incompleteFile.delete();
        }
        
        File completeFile = new File(targetFolder + File.separator  + experimentNumber + ".xlsx");
        if (completeFile.exists()) {
            completeFile.delete();
        }
        
        try {
            File targetFile = incompleteFile;
            if (experiment.isComplete()) {
                targetFile = completeFile;
            }
            TemplateReport report = new TemplateReport(experiment, templateFile);
            report.write(targetFile);
        }
        catch (Exception e) {
            System.err.println("Failed to generate templated report: " + e.getMessage());
        }
    }
    
    public void updateStatusReport(DataSource dataSource) throws Exception {
        System.out.println("Updating status report for data source: " 
                + dataSource.getFacilityName());
        
        // TODO: only update experiments that change, not all.
        
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
