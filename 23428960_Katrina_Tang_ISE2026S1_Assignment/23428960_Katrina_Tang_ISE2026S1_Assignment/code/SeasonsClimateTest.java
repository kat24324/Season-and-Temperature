import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;
import java.io.*;
import java.nio.file.*;

@RunWith(JUnit4.class)
public class SeasonsClimateTest {

    // ========== Equivalence Partitioning Tests ==========

    @Test
    public void testGetMeteorologicalSeason_EP() {
        // TC_EP_01: Australia summer
        assertEquals("Summer", SeasonsClimate.getMeteorologicalSeason("Australia", 1));
        // TC_EP_02: Spain spring
        assertEquals("Spring", SeasonsClimate.getMeteorologicalSeason("Spain", 4));
        // TC_EP_03: Mauritius spring (October)
        assertEquals("Spring", SeasonsClimate.getMeteorologicalSeason("Mauritius", 10));
        // TC_EP_04: Malaysia southwest monsoon (July)
        assertEquals("Southwest Monsoon", SeasonsClimate.getMeteorologicalSeason("Malaysia", 7));
        // TC_EP_05: Invalid country (using last name)
        assertEquals("Unknown country", SeasonsClimate.getMeteorologicalSeason("Ting", 5));
        // TC_EP_06: Invalid month (>12)
        assertEquals("Invalid month", SeasonsClimate.getMeteorologicalSeason("Japan", 15));
    }

    @Test
    public void testGetTraditionalSeason_EP() {
        // TC_EP_07: Australia traditional season Birak
        assertEquals("Birak", SeasonsClimate.getTraditionalSeason("Australia", 12));
        // TC_EP_08: Malaysia has no traditional season
        assertEquals("No traditional season", SeasonsClimate.getTraditionalSeason("Malaysia", 4));
        // TC_EP_09: Invalid month (<1)
        assertEquals("Invalid month", SeasonsClimate.getTraditionalSeason("Japan", -2));
        // Additional: Australia other months
        assertEquals("Bunuru", SeasonsClimate.getTraditionalSeason("Australia", 2));
        assertEquals("Djeran", SeasonsClimate.getTraditionalSeason("Australia", 4));
        assertEquals("Makuru", SeasonsClimate.getTraditionalSeason("Australia", 6));
        assertEquals("Djilba", SeasonsClimate.getTraditionalSeason("Australia", 8));
        assertEquals("Kambarang", SeasonsClimate.getTraditionalSeason("Australia", 10));
    }

    @Test
    public void testCompareSeasons_EP() {
        // TC_EP_10: Spain and Japan both winter in January
        assertTrue(SeasonsClimate.compareSeasons("Spain", "Japan", 1));
        // TC_EP_11: Australia (winter) vs Spain (summer) in June -> false
        assertFalse(SeasonsClimate.compareSeasons("Australia", "Spain", 6));
        // TC_EP_12: Invalid country (last name)
        assertFalse(SeasonsClimate.compareSeasons("Ting", "Japan", 3));
    }

    @Test
    public void testValidateInput() {
        // Inside range
        assertTrue(SeasonsClimate.validateInput(5, 1, 12));
        // At lower bound
        assertTrue(SeasonsClimate.validateInput(1, 1, 12));
        // At upper bound
        assertTrue(SeasonsClimate.validateInput(12, 1, 12));
        // Below lower bound
        assertFalse(SeasonsClimate.validateInput(0, 1, 12));
        // Above upper bound
        assertFalse(SeasonsClimate.validateInput(13, 1, 12));
    }

    // Fixed via Option B: Self-contained file creation to avoid runtime directory mismatches
    @Test
    public void testCompareTemperature_EP() throws Exception {
        String filename = "city_averages.txt";
        
        // Setup file content programmatically dynamically inside workspace
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Perth,18.2,23.0");
            writer.println("Adelaide,16.5,21.2");
        }

        // TC_EP_15: equal to average
        String result = SeasonsClimate.compareTemperature(18.2, "Perth", "morning", filename);
        assertTrue(result.contains("Equal to average"));
        
        // TC_EP_16: invalid time period
        result = SeasonsClimate.compareTemperature(25.0, "Adelaide", "night", filename);
        assertTrue(result.contains("Invalid time period"));
        
        // Above average with significant deviation
        result = SeasonsClimate.compareTemperature(25.0, "Perth", "morning", filename);
        assertTrue(result.contains("Above average") && result.contains("Significant deviation"));
        
        // City not found
        result = SeasonsClimate.compareTemperature(20.0, "Ting", "morning", filename);
        assertEquals("City not found in averages file", result);

        // File Cleanup
        new File(filename).delete();
    }

    @Test
    public void testCompareWithPerth_EP() throws Exception {
        String testFile = "test_perth.log";
        new File(testFile).delete();

        // TC_EP_17: large difference, evening
        SeasonsClimate.compareWithPerth(40.0, "evening", testFile);
        String content = new String(Files.readAllBytes(Paths.get(testFile)));
        assertTrue(content.contains("Above Perth average (Large difference)"));

        // Invalid time period
        SeasonsClimate.compareWithPerth(25.0, "afternoon", testFile);
        content = new String(Files.readAllBytes(Paths.get(testFile)));
        assertTrue(content.contains("Invalid time period: afternoon"));

        // Invalid temperature
        SeasonsClimate.compareWithPerth(100.0, "morning", testFile);
        content = new String(Files.readAllBytes(Paths.get(testFile)));
        assertTrue(content.contains("Invalid temperature: 100.0"));

        new File(testFile).delete();
    }

    // ========== Boundary Value Analysis Tests ==========
    @Test
    public void testValidateInput_BVA() {
        // Month boundaries (min=1, max=12)
        assertFalse(SeasonsClimate.validateInput(0, 1, 12)); 
        assertTrue(SeasonsClimate.validateInput(1, 1, 12));  
        assertTrue(SeasonsClimate.validateInput(2, 1, 12));  
        assertTrue(SeasonsClimate.validateInput(11, 1, 12)); 
        assertTrue(SeasonsClimate.validateInput(12, 1, 12)); 
        assertFalse(SeasonsClimate.validateInput(13, 1, 12)); 

        // Temperature boundaries (0.7 to 46.0)
        assertFalse(SeasonsClimate.validateInput(0.6, 0.7, 46.0));
        assertTrue(SeasonsClimate.validateInput(0.7, 0.7, 46.0));
        assertTrue(SeasonsClimate.validateInput(0.8, 0.7, 46.0));
        
        // Student ID ending digits inclusion check (456 converted to 45.6)
        assertTrue(SeasonsClimate.validateInput(45.6, 0.7, 46.0));
        
        assertTrue(SeasonsClimate.validateInput(45.9, 0.7, 46.0));
        assertTrue(SeasonsClimate.validateInput(46.0, 0.7, 46.0));
        assertFalse(SeasonsClimate.validateInput(46.1, 0.7, 46.0));
    }

    // ========== White-box Path Tests ==========
    @Test
    public void testCompareWithPerth_WhiteBox() throws Exception {
        // Path 1: Invalid time period
        String testFile1 = "wb_perth1.log";
        new File(testFile1).delete();
        SeasonsClimate.compareWithPerth(25.0, "afternoon", testFile1);
        String content = new String(Files.readAllBytes(Paths.get(testFile1)));
        assertTrue(content.contains("Invalid time period"));

        // Path 2: Valid morning, diff ≤6
        String testFile2 = "wb_perth2.log";
        new File(testFile2).delete();
        SeasonsClimate.compareWithPerth(20.0, "morning", testFile2);
        content = new String(Files.readAllBytes(Paths.get(testFile2)));
        assertTrue(content.contains("Above Perth average") && !content.contains("Large difference"));

        // Path 3: Valid morning, diff >6
        String testFile3 = "wb_perth3.log";
        new File(testFile3).delete();
        SeasonsClimate.compareWithPerth(30.0, "morning", testFile3);
        content = new String(Files.readAllBytes(Paths.get(testFile3)));
        assertTrue(content.contains("Above Perth average (Large difference)"));

        // Path 4: Valid evening, diff ≤6
        String testFile4 = "wb_perth4.log";
        new File(testFile4).delete();
        SeasonsClimate.compareWithPerth(22.0, "evening", testFile4);
        content = new String(Files.readAllBytes(Paths.get(testFile4)));
        assertTrue(content.contains("Below Perth average") && !content.contains("Large difference"));

        // Path 5: Valid evening, diff >6
        String testFile5 = "wb_perth5.log";
        new File(testFile5).delete();
        SeasonsClimate.compareWithPerth(35.0, "evening", testFile5);
        content = new String(Files.readAllBytes(Paths.get(testFile5)));
        assertTrue(content.contains("Above Perth average (Large difference)"));

        // Path 6: Invalid temperature
        String testFile6 = "wb_perth6.log";
        new File(testFile6).delete();
        SeasonsClimate.compareWithPerth(-10.0, "morning", testFile6);
        content = new String(Files.readAllBytes(Paths.get(testFile6)));
        assertTrue(content.contains("Invalid temperature"));
    }

    @Test
    public void testGetMeteorologicalSeason_WhiteBox() {
        assertEquals("Summer", SeasonsClimate.getMeteorologicalSeason("Australia", 1));
        assertEquals("Autumn", SeasonsClimate.getMeteorologicalSeason("Australia", 4));
        assertEquals("Winter", SeasonsClimate.getMeteorologicalSeason("Spain", 12));
        assertEquals("Spring", SeasonsClimate.getMeteorologicalSeason("Spain", 4));
        assertEquals("Summer", SeasonsClimate.getMeteorologicalSeason("Mauritius", 1));
        assertEquals("Autumn", SeasonsClimate.getMeteorologicalSeason("Mauritius", 5));
        assertEquals("Northeast Monsoon", SeasonsClimate.getMeteorologicalSeason("Malaysia", 1));
        assertEquals("Inter-monsoon", SeasonsClimate.getMeteorologicalSeason("Malaysia", 3));
        assertEquals("Southwest Monsoon", SeasonsClimate.getMeteorologicalSeason("Malaysia", 5));
        assertEquals("Unknown country", SeasonsClimate.getMeteorologicalSeason("Ting", 6));
        assertEquals("Invalid month", SeasonsClimate.getMeteorologicalSeason("Australia", 0));
        assertEquals("Invalid month", SeasonsClimate.getMeteorologicalSeason("Australia", 13));
    }
}
