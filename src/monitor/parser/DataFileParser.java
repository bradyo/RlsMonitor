package monitor.parser;

import monitor.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

/**
 * Parses an experiment data file into CellSet object map.
 */
public class DataFileParser 
{
    public static final Integer ID_COLUMN_INDEX = 1;
    public static final Integer DATA_START_COLUMN_INDEX = 2;
    public static final Integer DATA_START_ROW_INDEX = 2;
    
    public static final String START_CODE_H = "h";
    public static final String START_CODE_N = "n";
    public static final String START_CODE_A = "a";
    public static final String KILLED_CODE = "x";
    public static final String LOST_CODE = "lost";
    public static final String OMIT_CODE = "omit";
    public static final String FLAG_CODE = "flag";
    public static final String END_CODE_NO_BUD = "u";
    public static final String END_CODE_SMALL_BUD = "s";
    public static final String END_CODE_LARGE_BUD = "l";
    public static final String END_CODE_CLUSTER = "c";

    private Map<Integer,MotherCellSet> cellSetMap;
    

    public DataFileParser() {
        cellSetMap = new HashMap();
    }
    
    public void parseFile(File dataFile) throws Exception {
        System.out.println("processing " + dataFile);
        
        // get data set ids from filename
        String filename = dataFile.getName();
        ArrayList<Integer> setIds = getFilenameSetIds(filename);
        if (setIds.isEmpty()) {
            throw new Exception("data file does not give id range");
        }

        // parse the data file and collect data sets
        InputStream in = new FileInputStream(dataFile.getPath());
        Workbook wb = new HSSFWorkbook(in);

        // extract the PRINT worksheet (contains cell division counts)
        Sheet sheet = wb.getSheetAt(0);
        if (sheet == null) {
            wb = null;
            throw new Exception("failed to get worksheet 0");
        }
        
        // get comment from header
        Row headerRow = sheet.getRow(0);
        String comment = getComment(headerRow);
        
        // create cell sets for each id
        for (Integer setId : setIds) {
            MotherCellSet motherCellSet = new MotherCellSet();
            motherCellSet.setComment(comment);
            motherCellSet.setId(setId);
            cellSetMap.put(setId, motherCellSet);
        }

        // read rows and fill in cell sets
        Integer rowIndex = DATA_START_ROW_INDEX;
        Integer minSetIndex = setIds.get(0);
        Integer maxSetIndex = setIds.get(setIds.size() - 1);
        Integer setIndex = minSetIndex;
        MotherCellSet targetCellSet = cellSetMap.get(setIndex);
        boolean done = false;
        while (! done && rowIndex < sheet.getLastRowNum()) {
            Row row = sheet.getRow(rowIndex);
            MotherCell motherCell = getCell(row);
            
            if (motherCell.getId() == null) { 
                done = true;
            }
            
            if (! done && motherCell.getId() == 1 && rowIndex != DATA_START_ROW_INDEX) {
                if (setIndex < maxSetIndex) {
                    // switch to new set
                    setIndex++;
                    targetCellSet = cellSetMap.get(setIndex);
                } else {
                    done = true;
                }
            }
            
            if (! done) {
                List<MotherCell> cells = targetCellSet.getCells();
                cells.add(motherCell);
            }
            rowIndex++;
            
            // prevent memory leaks in POI library
            row = null;
        }
        
        // prevent memory leaks in POI library
        headerRow = null;
        sheet = null;
        wb = null;
        
        
    }
    
    private String getComment(Row row) {
        String comment = null;
        String[] headerValues = getRowValues(row);
        if (headerValues.length > 1) {
            comment = headerValues[1];
        }
        return comment;
    }
    
    private ArrayList<Integer> getFilenameSetIds(String filename) {
        // clean up filename
        filename = filename.replaceAll(" ", "");

        // get set ids for filenames with a range of ids
        Pattern pattern = Pattern.compile("^(\\d+)-(\\d+).xls$");
        Matcher matcher = pattern.matcher(filename);
        if (matcher.matches()) {
            Integer startId = Integer.valueOf(matcher.group(1));
            Integer endId = Integer.valueOf(matcher.group(2));
            if (startId < endId) {
                // build the ids array and return
                ArrayList results = new ArrayList();
                for (int i = startId; i <= endId; i++) {
                    results.add(new Integer(i));
                }
                return results;
            }
        }

        // get set id for filename with only one id
        pattern = Pattern.compile("^(\\d+).xls$");
        matcher = pattern.matcher(filename);
        if (matcher.matches()) {
            Integer id = Integer.valueOf(matcher.group(1));

            ArrayList results = new ArrayList();
            results.add(new Integer(id));
            return results;
        }
        return new ArrayList();
    }
    
    /**
     * Creates a mother cell object from a spreadsheet row.
     */
    private monitor.MotherCell getCell(Row row) {
        monitor.MotherCell cell = new monitor.MotherCell();
        
        // work with string values from Spreadsheet row
        String[] values = getRowValues(row);
        
        // get cell id
        try {
            Integer cellId = Integer.parseInt(values[ID_COLUMN_INDEX]);
            cell.setId(cellId);
        } catch (Exception e) {
            // no leading cell id, skip row
            return cell;
        }
        
        if (DATA_START_COLUMN_INDEX > values.length - 1) {
            return cell;
        }
        
        // get division counts and end code
        List<Integer>divisionCounts = new ArrayList();
        Integer columnIndex = DATA_START_COLUMN_INDEX;
        boolean doneScanning = false;
        while (! doneScanning) {
            String value = values[columnIndex];
            if (value == null) {
                value = "0";
            } else {
                value.trim();
            }
            
            if (value.equalsIgnoreCase(START_CODE_A)) {
                // buds are absent, same as 0 divisions
                value = "0";
            }
            
            if (value.equalsIgnoreCase(START_CODE_N) 
                    || value.equalsIgnoreCase(START_CODE_H)) {
                // if N reached (new mother cell), reset divisions
                divisionCounts.clear();
            } 
            else if (value.equalsIgnoreCase(LOST_CODE)) {
                cell.setEndState(monitor.MotherCell.EndState.LOST);
                doneScanning = true;
            }
            else if (value.equalsIgnoreCase(OMIT_CODE)) {
                cell.setEndState(monitor.MotherCell.EndState.OMIT);
                
                // take next value as comment
                if (columnIndex + 1 < values.length) {
                    String comment = values[columnIndex + 1];
                    cell.setComment(comment);
                }
                doneScanning = true;
            }
            else if (value.equalsIgnoreCase(FLAG_CODE)) {
                cell.setEndState(monitor.MotherCell.EndState.FLAGGED);
                
                // take next value as comment
                if (columnIndex + 1 < values.length) {
                    String comment = values[columnIndex + 1];
                    cell.setComment(comment);
                }
                doneScanning = true;
            }
            else if (value.substring(0, 1).equalsIgnoreCase(KILLED_CODE)) {
                cell.setEndState(MotherCell.EndState.DEAD);
                
                // get end state
                String endStateCode = null;
                if (value.length() == 2) {
                    // extract end code from second character in current value,
                    // for values like "xu"
                    endStateCode = value.substring(1, 2);
                    
                    // take next value as comment
                    if (columnIndex + 1 < values.length) {
                        String comment = values[columnIndex + 1];
                        cell.setComment(comment);
                    }
                } else {
                    // grab next value for end code
                    if (columnIndex + 1 < values.length) {
                        endStateCode = values[columnIndex + 1];
                    }
                    
                    // take second value as comment
                    if (columnIndex + 2 < values.length) {
                        String comment = values[columnIndex + 2];
                        cell.setComment(comment);
                    }
                }
                
                monitor.MotherCell.EndState endState = null;
                if (endStateCode != null) {
                    if (endStateCode.equalsIgnoreCase(END_CODE_NO_BUD)) {
                        endState = monitor.MotherCell.EndState.NO_BUD;
                    } else if (endStateCode.equalsIgnoreCase(END_CODE_SMALL_BUD)) {
                        endState = monitor.MotherCell.EndState.SMALL_BUD;
                    } else if (endStateCode.equalsIgnoreCase(END_CODE_LARGE_BUD)) {
                        endState = monitor.MotherCell.EndState.LARGE_BUD;
                    } else if (endStateCode.equalsIgnoreCase(END_CODE_CLUSTER)) {
                        endState = monitor.MotherCell.EndState.CLUSTER;
                    }
                }
                if (endState != null) {
                    cell.setEndState(endState);
                }
                
                doneScanning = true;
            }
            else {
                try {
                    Integer integerValue = Integer.valueOf(value);
                    divisionCounts.add(integerValue);
                } catch (NumberFormatException e) {
                    System.err.println("unrecognized value in data row: " + e.getMessage());
                }
            }

            // increment scanning position
            columnIndex++;
            if (columnIndex >= values.length) {
                doneScanning = true;
            }
        }
        cell.setDivisionCounts(divisionCounts);
        
        return cell;
    }

    private String[] getRowValues(Row row) {
        // find how many data cells there are, need at least 3 values since
        // there are two header columns
        int cellCount = row.getLastCellNum();
        if (cellCount < 2) {
            return new String[0];
        }
        String[] rowValues = new String[cellCount];

        // loop over column cells, skip two label columns
        for (int i = 0; i < cellCount; i++) {
            // Apache POI Cell class clashes with our yeast mother Cell class,
            // use fully qualified name
            org.apache.poi.ss.usermodel.Cell cell = row.getCell(i);
            String rowValue = null;
            if (cell != null) {
                int cellType = cell.getCellType();
                if (cellType == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA) {
                    cellType = cell.getCachedFormulaResultType();
                }
                switch(cellType) {
                    case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                        rowValue = cell.getStringCellValue().trim();
                        break;
                    case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                        double doubleValue = cell.getNumericCellValue();
                        int intValue = (int)(doubleValue);
                        if (doubleValue == intValue) {
                            rowValue = Integer.toString(intValue);
                        } else {
                            rowValue = Double.toString(doubleValue);
                        }
                        break;
                    case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN:
                        rowValue = Boolean.toString(cell.getBooleanCellValue());
                        break;
                }
            }
            rowValues[i] = rowValue;
        }
        return rowValues;
    }
    
    public Map<Integer,MotherCellSet> getCellSetMap() {
        return cellSetMap;
    }
}
