package monitor;

import java.io.File;
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
    
    public void updateReports(Experiment experiment) {
        System.out.println();
        System.out.println("Updating reports experiment: " + experiment.getNumber());
        
        // update the different types of reports
        updateDatabaseReport(experiment);
        updateCellReport(experiment);
        updateExtendedDatabaseReport(experiment);
        for (File templateFile : templateFolder.listFiles()) {
            updateTemplateReport(templateFile, experiment);
        }
        
        // notify core system about completed experiment
        if (experiment.isComplete()) {
            try {
                coreService.notifyExperimentComplete(experiment.getNumber());
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
    
    private void updateCellReport(Experiment experiment) {
        // set up target directory
        File targetFolder = new File(outputFolder + File.separator + "cell");
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
            CellReport report = new CellReport(experiment);
            report.write(targetFile);
        }
        catch (Exception e) {
            System.err.println("Failed to generate cell report: " + e.getMessage());
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
}
