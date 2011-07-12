package parser;

import java.util.*;
import monitor.*;
import monitor.parser.*;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.*;

public class CsvParserTest {
    
    public CsvParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }
    
    /**
     * Gets a list of expected mother cell sets.
     * @return List<MotherCellSet>
     */
    private List<MotherCellSet> getExpectedCellSets() {
        String[] references = {"3", "1,2", "1"};
        String[] labels = {"coq1", "atg32", "coq9"};
        String[] strainNames = {"JS27", "JS553", "DC:170F8"};
        String[] medias = {"media 1", "media 2", "media 3"};
        Float[] temperatures = {(float)30.00, (float)31.13, (float)32.00};
        Integer[][] lifespans = {
            {34, 41, 30, 15, 23, 39, 19, 42, 15, 19}, 
            {8, 14, 46, 25, 7, 25, 35, 26, 2, 29}, 
            {17, 17, 20, 34, 19, 13, 17, 18, 45, 11}
        };
        
        List<MotherCellSet> cellSets = new ArrayList();
        for (Integer rowIndex = 0; rowIndex < 3; rowIndex++) {
            MotherCellSet cellSet = new MotherCellSet();
            cellSet.setId(rowIndex + 1);
            cellSet.setReference(references[rowIndex]);
            cellSet.setLabel(labels[rowIndex]);
            cellSet.setStrainName(strainNames[rowIndex]);
            cellSet.setMedia(medias[rowIndex]);
            cellSet.setTemperature(temperatures[rowIndex]);

            // set up all cells for mother cell set
            List<MotherCell> cells = new ArrayList();
            Integer[] rowLifespans = lifespans[rowIndex];
            for (Integer cellIndex = 0; cellIndex < rowLifespans.length; cellIndex++) {
                MotherCell motherCell = new MotherCell();
                motherCell.setId(cellIndex);
                motherCell.setLifespan(rowLifespans[cellIndex]);
                cells.add(motherCell);
            }
            cellSet.setCells(cells);
            
            cellSets.add(cellSet);
        }
        return cellSets;
    }
    
    @Test
    public void testParseFile() throws Exception {
        File file = new File(new File(".").getAbsolutePath()
                + File.separator + "test-data" 
                + File.separator + "input csv"
                + File.separator + "test.csv");
        
        // create parser and 
        CsvParser parser = new CsvParser();
        parser.parseFile(file);
        
        List<MotherCellSet> expectedCellSets = getExpectedCellSets();
        List<MotherCellSet> actualCellSets = parser.getCellSets();
                
        // check that each have the same number of cell sets
        assertEquals(expectedCellSets.size(), actualCellSets.size());
        
        // check that each of the cell sets are equal
        for (Integer setIndex = 0; setIndex < expectedCellSets.size(); setIndex++) {
            MotherCellSet expectedCellSet = expectedCellSets.get(setIndex);
            MotherCellSet actualCellSet = actualCellSets.get(setIndex);
            
            // check that strain info is equal
            assertEquals(expectedCellSet.getId(), actualCellSet.getId());
            assertEquals(expectedCellSet.getLabel(), actualCellSet.getLabel());
            assertEquals(expectedCellSet.getStrainName(), actualCellSet.getStrainName());
            assertEquals(expectedCellSet.getMedia(), actualCellSet.getMedia());
            assertEquals(expectedCellSet.getTemperature(), actualCellSet.getTemperature());
            
            // check that lifespans are equal
            assertEquals(expectedCellSet.getLifespans(), actualCellSet.getLifespans());
        }
    }
}
