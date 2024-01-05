import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class log_processing {
    public static void main(String[] args) {
        String filePath = "/var/lib/tomcat10/webapps/project1/log.txt"; //replace log with the name of the file

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            List<Long> TS = new ArrayList<>();
            List<Long> TJ = new ArrayList<>();

            // Read each line from the file
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\s+");

                TS.add(Long.parseLong(values[0]));
                TJ.add(Long.parseLong(values[1]));
            }

            // Calculate averages
            double averageTS = calculateAverage(TS);
            double averageTJ = calculateAverage(TJ);

            System.out.println("Average of TS: " + averageTS + " nanoseconds");
            System.out.println("Average of TJ: " + averageTJ + " nanoseconds");

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }

    private static double calculateAverage(List<Long> values) {
        long sum = 0;
        for (Long value : values) {
            sum += value;
        }
        return (double) sum / values.size();
    }
}
