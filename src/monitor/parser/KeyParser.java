package monitor.parser;

import monitor.MotherCellSetKey;
import java.util.*;

/**
 * Parses experiment key data out of tab-delimited string.
 */
public class KeyParser 
{
    public static final Integer ID_INDEX = 0;
    public static final Integer REFERENCE_INDEX = 1;
    public static final Integer LABEL_INDEX = 2;
    public static final Integer STRAIN_INDEX = 3;
    public static final Integer MEDIA_INDEX = 4;
    public static final Integer TEMPERATURE_INDEX = 5;
    public static final Integer CELL_COUNT_INDEX = 6;
    
    private Map<Integer,MotherCellSetKey> cellSetKeyMap;
        
    public KeyParser() {
        cellSetKeyMap = new HashMap();
    }
    
    /**
     * Parses tab-delimited string input into cellSets.
     * @param inputString tab-delimited key data
     */
    public void parseString(String inputString) {
        Scanner scanner = new Scanner(inputString);
        
        // skip over header line
        if (scanner.hasNext()) {
            scanner.nextLine();
        }
        
        // scan over lines saving data
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            try {
                // setup key data
                MotherCellSetKey row = new MotherCellSetKey();
                
                String[] values = line.split("\t");
                Integer id = Integer.parseInt(values[ID_INDEX]);
                row.setNumber(id);
                
                String reference = values[REFERENCE_INDEX].trim();
                row.setReference(reference);
                
                String label = values[LABEL_INDEX].trim();
                row.setLabel(label);
                
                String strainName = values[STRAIN_INDEX].trim();
                row.setStrainName(strainName);
                
                String media = values[MEDIA_INDEX].trim();
                row.setMedia(media);
                
                Float temperature = Float.parseFloat(values[TEMPERATURE_INDEX]);
                row.setTemperature(temperature);
                
                Integer cellCount = Integer.parseInt(values[CELL_COUNT_INDEX]);
                row.setCellCount(cellCount);
                
                cellSetKeyMap.put(id, row);
            }
            catch (Exception e) {
                System.err.println("failed to parse line: " + e.getMessage());
                System.err.println("line contents: ");
                System.err.println(line);
            }
        }
    }
    
    public Map<Integer,MotherCellSetKey> getCellSetKeyMap() {
        return cellSetKeyMap;
    }
    
    public MotherCellSetKey getRow(Integer id) {
        return cellSetKeyMap.get(id);
    }
}
