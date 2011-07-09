package monitor.reporting;

import java.io.*;
import java.util.*;
import monitor.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.commons.io.FileUtils;

/**
 * Generates an Excel XLSX report file by injecting raw data into designated worksheets.
 * The template can then reference those values to generate custom plots and analysis.
 */
public class TemplateReport
{
    public static final String TEMP_FILENAME = "tmp.xlsx";
    public static final String DATA_SHEET_NAME = "PRINT";
    public static final String INFO_SHEET_NAME = "INFO";
    public static final String STATUS_SHEET_NAME = "STATUS";
    
    private Experiment experiment;
    private File templateFile;
    private File temporaryFile;
    private Workbook workbook;
    private FormulaEvaluator evaluator;

    public TemplateReport(Experiment experiment, File templateFile) throws Exception {
        this.experiment = experiment;
        this.templateFile = templateFile;
    }
    
    public void write(File outputFile) throws Exception {
        // write new report to a temporary file first, then copy. This fixes a
        // problem with the excel library saving the changes to the file
        temporaryFile = new File(TEMP_FILENAME);
        FileUtils.copyFile(templateFile, temporaryFile);
        
        // open the workbook
        workbook = new XSSFWorkbook(new FileInputStream(temporaryFile));
        evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        
        // generate report
        populateDataSheet();
        populateInfoSheet();
        populateStatusSheet();
        recalculateFormulas();
        
        // save the workbook to output file
        try {
            FileOutputStream out = new FileOutputStream(outputFile);
            workbook.write(out);
            out.close();
        }
        catch (Exception e) {
            System.err.println("failed to write report file: " + e.getMessage());
        }
        
        // prevent memory leaks?
        workbook = null;
        evaluator = null;
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        runtime.freeMemory();
    }
    
    private void populateDataSheet() throws Exception {
        // insert dissection data into print worksheet
        Sheet dataSheet = workbook.getSheet(DATA_SHEET_NAME);
        if (dataSheet == null) {
            dataSheet = workbook.createSheet(DATA_SHEET_NAME);
        }
        
        Integer maxCellSetId = experiment.getMaxCellSetId();
        for (Integer setId = 1; setId <= maxCellSetId; setId++) {
            MotherCellSet cellSet = experiment.getCellSet(setId);
            if (cellSet == null) {
                continue;
            }
            
            // since templating system format requires 20 rows per set, cell sets
            // with more than 20 cells cannot be handled properly
            List<MotherCell> motherCells = cellSet.getCells();
            if (motherCells.size() > 20) {
                String message = "templated reports cannot be generated for "
                        + "cell sets with >20 cells";
                throw new Exception(message);
            }
            
            // each template set has 20 rows
            for (int iSetRow = 0; iSetRow < 20; iSetRow++) {
                // get target row
                Row row = dataSheet.getRow((setId - 1) * 20 + iSetRow);
                if (row == null) {
                    row = dataSheet.createRow((setId - 1) * 20 + iSetRow);
                }
                
                // write the set id to the left of the first set row
                if (iSetRow == 0) {
                    Cell cell = row.createCell(0);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    cell.setCellValue(setId);
                }

                // skip rows without cells (provides padding up to 20 cells)
                if (iSetRow >= motherCells.size()) {
                    continue;
                }
                    
                // if mother cell was lost, skip writing divisions
                MotherCell motherCell = motherCells.get(iSetRow);
                if (motherCell.isLost()) {
                    continue;
                }
                
                // write divisions
                List<Integer> divisionCounts = motherCell.getDivisionCounts();
                for (int iCol = 0; iCol < divisionCounts.size(); iCol++) {
                    // first col contains row id
                    Integer divisions = divisionCounts.get(iCol);
                    Cell cell = row.createCell(iCol + 1); 
                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                    cell.setCellValue(divisions.doubleValue());
                }
                
                // prevent memory leaks?
                row = null;
            }
        }
        
        // prevent memory leaks?
        dataSheet = null;
    }
    
    private void populateInfoSheet() throws Exception {
        // populate strain info sheet
        Sheet sheet = workbook.getSheet(INFO_SHEET_NAME);
        if (sheet == null) {
            sheet = workbook.createSheet(INFO_SHEET_NAME);
        }
        
        // set column headers
        String[] headers = {"Set ID", "Label", "Strain", "Media", "Background", 
            "Mating Type", "Short Genotype", "Full Genotype", "Comment"};
        Row headerRow = getRow(sheet, 0);
        for (int i = 0; i < headers.length; i++) {
            String label = headers[i];
            setCellValue(headerRow, i, label);
        }
        
        Integer maxCellSetId = experiment.getMaxCellSetId();
        for (Integer setId = 1; setId <= maxCellSetId; setId++) {
            Row row = getRow(sheet, setId);
            setCellValue(row, 0, setId.toString());
            
            MotherCellSet cellSet = experiment.getCellSet(setId);
            if (cellSet != null) {
                setCellValue(row, 1, cellSet.getLabel());
                setCellValue(row, 2, cellSet.getStrainName());
                setCellValue(row, 3, cellSet.getMedia());
                
                Strain strain = cellSet.getStrain();
                if (strain != null) {
                    setCellValue(row, 4, strain.getBackground());
                    setCellValue(row, 5, strain.getMatingType());
                    setCellValue(row, 6, strain.getShortGenotype());
                    setCellValue(row, 7, strain.getFullGenotype());
                }
                setCellValue(row, 8, cellSet.getComment());
            }           
            
            // prevent memory leaks?
            row = null;
        }
        
        // prevent memory leaks?
        headerRow = null;
        sheet = null;
    }
    
    private void populateStatusSheet() throws Exception {
        // insert dissection data into print worksheet
        Sheet sheet = workbook.getSheet(STATUS_SHEET_NAME);
        if (sheet == null) {
            sheet = workbook.createSheet(STATUS_SHEET_NAME);
        }
        
        // set column headers
        String[] headers = {"Set ID", "Row ID", "Code", "Comment"};
        Row headerRow = getRow(sheet, 0);
        for (int i = 0; i < headers.length; i++) {
            String label = headers[i];
            setCellValue(headerRow, i, label);
        }
        
        int rowOffset = 1;
        Integer maxCellSetId = experiment.getMaxCellSetId();
        for (Integer setId = 1; setId <= maxCellSetId; setId++) {
            MotherCellSet cellSet = experiment.getCellSet(setId);
            if (cellSet == null) {
                continue;
            }
            
            // since templating system format requires 20 rows per set, cell sets
            // with more than 20 cells cannot be handled properly
            List<MotherCell> motherCells = cellSet.getCells();
            if (motherCells.size() > 20) {
                String message = "templated reports cannot be generated for "
                        + "cell sets with >20 cells";
                throw new Exception(message);
            }
            
            // each template set has 20 rows
            for (int iSetRow = 0; iSetRow < 20; iSetRow++) {
                // skip rows without cells (provides padding up to 20 cells)
                if (iSetRow >= motherCells.size()) {
                    continue;
                }
                    
                // get target row
                Row row = sheet.getRow(rowOffset);
                if (row == null) {
                    row = sheet.createRow(rowOffset);
                }
                
                // if mother cell was lost, skip writing divisions
                MotherCell motherCell = motherCells.get(iSetRow);
                if (motherCell.isFlagged()) {
                    setCellValue(row, 0, cellSet.getId().toString());
                    setCellValue(row, 1, motherCell.getId().toString());
                    setCellValue(row, 2, "flag");
                    setCellValue(row, 3, motherCell.getComment());
                    rowOffset++;
                }
                else if (motherCell.isOmitted()) {
                    setCellValue(row, 0, cellSet.getId().toString());
                    setCellValue(row, 1, motherCell.getId().toString());
                    setCellValue(row, 2, "omit");
                    setCellValue(row, 3, motherCell.getComment());
                    rowOffset++;
                }
                
                // prevent memory leaks?
                row = null;
            }
        }
        
        // prevent memory leaks?
        sheet = null;
        headerRow = null;
    }
    
    private Row getRow(Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        return row;
    }
    
    private void setCellValue(Row row, int columnIndex, String value) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(value);
    }
    
    private void recalculateFormulas() {
        // recalculate formulas. We have experienced an error where opening
        // the generated file in excel shows only zeros in the sums derived
        // from the PRINT sheet. This issue is likely due to the cahced values
        // being used (excel should recalculate these values, but doesnt).
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            recalculateSheetFormulas(sheet);
        }
    }
    
    private void recalculateSheetFormulas(Sheet sheet) {
        for (Row row : sheet) {
            for(Cell cell : row) {
                try {
                    recalculateCellFormula(cell);
                } catch (Exception e) {
                    String message = "failed to compute formula, "
                        + "worksheet: " + sheet.getSheetName()
                        + ", cell: " + cell.getColumnIndex() + "," + cell.getRowIndex()
                        + ", value: " + cell.toString();
                    System.err.println(message);
                }
            }
        }
    }
    
    private void recalculateCellFormula(Cell cell) {
        if(cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            evaluator.evaluateFormulaCell(cell);
        }
    }
}
