package monitor.reporting;

import monitor.*;
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;

/**
 * Generates an experiment report in the Yeast RLS database CSV format.
 */
public class ExtendedDatabaseReport
{   
    private Experiment experiment = null;

    public ExtendedDatabaseReport(Experiment experiment) {
        this.experiment = experiment;
    }
    
    public void write(File outputFile) throws IOException {
        outputFile.createNewFile();
        
        Writer writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write("id,reference,label,strain,media,temperature,cell_count,"
                + "end_lost,omit_count,end_single,end_small_bud,end_large_bud,end_cluster,"
                + "lifespans\n");

        for (Integer i = 1; i <= experiment.getMaxCellSetId(); i++) {
            writer.write(i + ",");

            MotherCellSet cellSet = experiment.getCellSet(i);
            if (cellSet == null) {
                writer.write("\n");
                continue;
            }
            
            String reference = cellSet.getReference();
            if (reference != null) {
                writer.write(getQuoted(reference));
            }
            writer.write(",");
            
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
            
            String media = cellSet.getMedia();
            if (media != null) {
                writer.write(getQuoted(media));
            }
            writer.write(",");
            
            Float temperature = cellSet.getTemperature();
            if (temperature != null) {
                writer.write(new DecimalFormat("#.##").format(temperature));
            }
            writer.write(",");
            
            Integer totalCount = cellSet.getCells().size();
            if (totalCount != null) {
                writer.write(totalCount.toString());
            }
            writer.write(",");
            
            Integer lostCount = cellSet.getEndStateCount(MotherCell.EndState.LOST);
            writer.write(lostCount.toString());
            writer.write(",");
            
            Integer omitCount = cellSet.getEndStateCount(MotherCell.EndState.OMIT);
            writer.write(omitCount.toString());
            writer.write(",");
                        
            Integer noBudCount = cellSet.getEndStateCount(MotherCell.EndState.NO_BUD);
            writer.write(noBudCount.toString());
            writer.write(",");
            
            Integer smallBudCount = cellSet.getEndStateCount(MotherCell.EndState.SMALL_BUD);
            writer.write(smallBudCount.toString());
            writer.write(",");
            
            Integer largeBudCount = cellSet.getEndStateCount(MotherCell.EndState.LARGE_BUD);
            writer.write(largeBudCount.toString());
            writer.write(",");
            
            Integer clusterCount = cellSet.getEndStateCount(MotherCell.EndState.CLUSTER);
            writer.write(clusterCount.toString());
            writer.write(",");

            for (MotherCell cell : cellSet.getCells()) {
                Integer lifespan = cell.getLifespan();
                if (lifespan != null) {
                    String value = lifespan.toString();
                    MotherCell.EndState endState = cell.getEndState();
                    if (endState == MotherCell.EndState.NO_BUD) {
                        value += "|single";
                    } else if (endState == MotherCell.EndState.SMALL_BUD) {
                        value += "|small bud";
                    } else if (endState == MotherCell.EndState.LARGE_BUD) {
                        value += "|large bud";
                    } else if (endState == MotherCell.EndState.CLUSTER) {
                        value += "|cluster";
                    }
                    writer.write(value);
                }
                writer.write(",");
            }
            
            writer.write("\n");
        }
        writer.close();
    }
    
    private String getQuoted(String input) {
        String quoted = "\"" + input.replace("\"", "\"\"") + "\"";
        return quoted;
    }
}
