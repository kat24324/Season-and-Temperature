import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Demonstrate traditional season with keyboard input and screen output
        System.out.println("=== Traditional Season Demo ===");
        System.out.print("Enter country: ");
        String country = scanner.nextLine();
        System.out.print("Enter month (1-12): ");
        int month = scanner.nextInt();
        scanner.nextLine(); // consume newline
        String traditionalSeason = SeasonsClimate.getTraditionalSeason(country, month);
        System.out.println("Traditional season: " + traditionalSeason);

        // Demonstrate meteorological season
        System.out.println("\n=== Meteorological Season Demo ===");
        String metSeason = SeasonsClimate.getMeteorologicalSeason(country, month);
        System.out.println("Meteorological season: " + metSeason);

        // Demonstrate compareTemperature with file input
        System.out.println("\n=== Temperature Comparison Demo ===");
        System.out.print("Enter temperature: ");
        double temp = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter city (Perth/Adelaide/Brisbane): ");
        String city = scanner.nextLine();
        System.out.print("Enter time period (morning/evening): ");
        String period = scanner.nextLine();
        String result = SeasonsClimate.compareTemperature(temp, city, period, "city_averages.txt");
        System.out.println("Result: " + result);

        // Demonstrate compareWithPerth (writes to file)
        System.out.println("\n=== Compare with Perth (writing to file) ===");
        SeasonsClimate.compareWithPerth(temp, period, "perth_comparison.log");
        System.out.println("Result written to perth_comparison.log");

        scanner.close();
    }
}
