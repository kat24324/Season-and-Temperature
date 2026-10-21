import java.io.*;
import java.util.*;

public class SeasonsClimate {

    // ========== Helper: Validation ==========
    /**
     * Check if a numeric value is within [min, max] (inclusive).
     */
    public static boolean validateInput(double value, double min, double max) {
        return value >= min && value <= max;
    }

    // Overloaded version for integers
    public static boolean validateInput(int value, int min, int max) {
        return value >= min && value <= max;
    }

    // ========== Module: Meteorological Season ==========
    /**
     * Returns the meteorological season for a given country and month.
     * For Malaysia and Sri Lanka, returns monsoon seasons (as per Figure 1).
     * For Mauritius, uses special seasons.
     * For others, uses standard southern/northern hemisphere seasons.
     * @param country name (must be in supported list)
     * @param month 1-12
     * @return season name or error message
     */
    public static String getMeteorologicalSeason(String country, int month) {
        // Supported countries
        Set<String> supported = new HashSet<>(Arrays.asList(
            "Australia", "Spain", "Japan", "Mauritius", "Malaysia", "Sri Lanka"));
        if (!supported.contains(country)) {
            return "Unknown country";
        }
        if (!validateInput(month, 1, 12)) {
            return "Invalid month";
        }

        // Special case: Mauritius (see Figure 1)
        if (country.equals("Mauritius")) {
            if (month == 11 || month == 12 || month == 1 || month == 2 || month == 3 || month == 4) {
                return "Summer";
            }
            if (month == 5) return "Autumn";
            if (month == 6 || month == 7 || month == 8 || month == 9) return "Winter";
            if (month == 10) return "Spring";
        }

        // Malaysia and Sri Lanka: monsoon seasons (meteorological, aligned with Figure 1)
        if (country.equals("Malaysia") || country.equals("Sri Lanka")) {
            if (month == 12 || month == 1 || month == 2) return "Northeast Monsoon";
            if (month == 3 || month == 4) return "Inter-monsoon";
            if (month >= 5 && month <= 9) return "Southwest Monsoon";
            if (month == 10 || month == 11) return "Inter-monsoon";
        }

        // Australia: southern hemisphere meteorological seasons
        if (country.equals("Australia")) {
            if (month == 12 || month == 1 || month == 2) return "Summer";
            if (month == 3 || month == 4 || month == 5) return "Autumn";
            if (month == 6 || month == 7 || month == 8) return "Winter";
            if (month == 9 || month == 10 || month == 11) return "Spring";
        }

        // Northern hemisphere (Spain, Japan) – standard meteorological seasons
        if (month == 12 || month == 1 || month == 2) return "Winter";
        if (month == 3 || month == 4 || month == 5) return "Spring";
        if (month == 6 || month == 7 || month == 8) return "Summer";
        if (month == 9 || month == 10 || month == 11) return "Autumn";

        return "Invalid month"; // fallback
    }

    // ========== Module: Traditional Season ==========
    /**
     * Returns the traditional season.
     * Only Australia has traditional seasons (Noongar).
     * All other countries return "No traditional season".
     * @param country name
     * @param month 1-12
     * @return traditional season name, "No traditional season", or error message
     */
    public static String getTraditionalSeason(String country, int month) {
        if (!validateInput(month, 1, 12)) {
            return "Invalid month";
        }
        // Only Australia has traditional seasons (Noongar)
        if (country.equals("Australia")) {
            if (month == 12 || month == 1) return "Birak";
            if (month == 2 || month == 3) return "Bunuru";
            if (month == 4 || month == 5) return "Djeran";
            if (month == 6 || month == 7) return "Makuru";
            if (month == 8 || month == 9) return "Djilba";
            if (month == 10 || month == 11) return "Kambarang";
        }
        // All other countries (Spain, Japan, Mauritius, Malaysia, Sri Lanka) have no traditional season
        return "No traditional season";
    }

    // ========== Module: Compare Seasons ==========
    /**
     * Compares meteorological seasons of two countries for a given month.
     * @param country1 first country
     * @param country2 second country
     * @param month 1-12
     * @return true if same season, false otherwise (including errors)
     */
    public static boolean compareSeasons(String country1, String country2, int month) {
        String season1 = getMeteorologicalSeason(country1, month);
        String season2 = getMeteorologicalSeason(country2, month);
        // If either returned an error message, treat as not equal
        if (season1.startsWith("Unknown") || season1.equals("Invalid month") ||
            season2.startsWith("Unknown") || season2.equals("Invalid month")) {
            return false;
        }
        return season1.equals(season2);
    }

    // ========== Module: Compare Temperature (with file input) ==========
    /**
     * Compares given temperature with city's average (morning/evening) using data from a file.
     * File format: city,morningAvg,eveningAvg (one per line, comma separated)
     * @param temperature the reading
     * @param city name (must exist in file)
     * @param timePeriod "morning" or "evening"
     * @param filename path to the averages file
     * @return result message
     */
    public static String compareTemperature(double temperature, String city, String timePeriod, String filename) {
        // Read averages from file
        Map<String, double[]> averages = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String cityName = parts[0].trim();
                    double morning = Double.parseDouble(parts[1].trim());
                    double evening = Double.parseDouble(parts[2].trim());
                    averages.put(cityName, new double[]{morning, evening});
                }
            }
        } catch (IOException e) {
            return "Error reading averages file: " + e.getMessage();
        }

        if (!averages.containsKey(city)) {
            return "City not found in averages file";
        }
        double[] avgs = averages.get(city);
        double avg;
        if (timePeriod.equalsIgnoreCase("morning")) {
            avg = avgs[0];
        } else if (timePeriod.equalsIgnoreCase("evening")) {
            avg = avgs[1];
        } else {
            return "Invalid time period. Use 'morning' or 'evening'";
        }

        // Calculate difference
        double diff = temperature - avg;
        String baseMsg;

        // Determine base message
        if (Math.abs(diff) < 0.01) {
            baseMsg = "Equal to average";
        } else if (diff > 0) {
            baseMsg = "Above average";
        } else {
            baseMsg = "Below average";
        }

        // Add extra message if deviation > 6
        String extra = (Math.abs(diff) > 6.0) ? " (Significant deviation!)" : "";
        return baseMsg + extra;
    }

    // ========== Module: Compare with Perth (file output) ==========
    /**
     * Compares given temperature with Perth's average (morning/evening) and writes result to a log file.
     * @param temperature the reading
     * @param timePeriod "morning" or "evening"
     * @param outputFilename file to write the result (append)
     */
    public static void compareWithPerth(double temperature, String timePeriod, String outputFilename) {
        // Perth averages from Figure 2: morning 18.2, evening 23.0
        double perthAvg;
        if (timePeriod.equalsIgnoreCase("morning")) {
            perthAvg = 18.2;
        } else if (timePeriod.equalsIgnoreCase("evening")) {
            perthAvg = 23.0;
        } else {
            try (FileWriter fw = new FileWriter(outputFilename, true);
                 PrintWriter pw = new PrintWriter(fw)) {
                pw.println(new Date() + " | Invalid time period: " + timePeriod);
            } catch (IOException e) { /* ignore */ }
            return;
        }

        // Validate temperature using Perth's range (0.7 to 46.0 from Figure 2)
        boolean valid = validateInput(temperature, 0.7, 46.0);
        String message;
        if (!valid) {
            message = "Invalid temperature: " + temperature + " (Perth range 0.7-46.0)";
        } else {
            double diff = temperature - perthAvg;
            if (Math.abs(diff) < 0.01) {
                message = "Equal to Perth average";
            } else if (diff > 0) {
                message = "Above Perth average";
            } else {
                message = "Below Perth average";
            }
            if (Math.abs(diff) > 6.0) {
                message += " (Large difference)";
            }
        }

        // Write to file (append with timestamp)
        try (FileWriter fw = new FileWriter(outputFilename, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(new Date() + " | " + message);
        } catch (IOException e) {
            System.err.println("Failed to write to file: " + e.getMessage());
        }
    }
}

//Version control practice
