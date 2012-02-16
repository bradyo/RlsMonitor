package monitor.reporting;

import monitor.*;
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;

/**
 * Generates an experiment report in the Yeast RLS database CSV format.
 */
public class CellReport
{   
    private Experiment experiment = null;

    public CellReport(Experiment experiment) {
        this.experiment = experiment;
    }
    
    public void write(File outputFile) throws IOException {
        outputFile.createNewFile();

        // write header
        Writer writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write("label,strain,short_genotype,media,temperature,lifespan,complete,end_state,divisions\n");

        // write cell data rows
        for (Integer i = 1; i <= experiment.getMaxCellSetId(); i++) {
            MotherCellSet cellSet = experiment.getCellSet(i);
            if (cellSet == null) {
                continue;
            }
            
            for (MotherCell cell : cellSet.getCells()) {
                
                String label = cellSet.getLabel();
                if (label != null) {
                    writer.write(getQuoted(label));
                }
                writer.write(",");

                String strainName = cellSet.getStrainName();
                if (strainName != null) {
                    writer.write(getQuoted(strainName));
                }
                writer.write(",");

                if (cellSet.getStrain() != null) {
                    writer.write(getQuoted(cellSet.getStrain().getShortGenotype()));
                }
                writer.write(",");

                String media = cellSet.getMedia();
                if (media != null) {
                    writer.write(getQuoted(media));
                }
                writer.write(",");

                Float temperature = cellSet.getTemperature();
                if (temperature != null) {
                    writer.write(new DecimalFormat("#.###").format(temperature));
                }
                writer.write(",");
                
                Integer lifespan = cell.getLifespan();
                if (lifespan != null) {
                    String value = lifespan.toString();
                    writer.write(value);
                }
                writer.write(",");
                
                if (cell.isComplete()) {
                    writer.write("X,");
                } else {
                    writer.write(",");
                }
                
                MotherCell.EndState endState = cell.getEndState();
                if (endState != null) {
                    if (endState == MotherCell.EndState.NO_BUD) {
                        writer.write("U");
                    } else if (endState == MotherCell.EndState.SMALL_BUD) {
                        writer.write("S");
                    } else if (endState == MotherCell.EndState.LARGE_BUD) {
                        writer.write("L");
                    } else if (endState == MotherCell.EndState.CLUSTER) {
                        writer.write("C");
                    }
                }
                writer.write(",");
                
                // write division counts
                for (Integer divisionCount : cell.getDivisionCounts()) {
                    writer.write(divisionCount.toString());
                    writer.write(",");
                }
                
                writer.write("\n");
            }
        }
        writer.close();
    }
    
    private String getQuoted(String input) {
        String quoted = "\"" + input.replace("\"", "\"\"") + "\"";
        return quoted;
    }
}
