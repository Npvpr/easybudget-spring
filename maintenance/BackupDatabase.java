// package maintenance;
// for unkown reason, mysqldump will not run if run from maintenance.BackupDatabase
// This will only work when pwd is inside directory and run with "java BackupDatabase"

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupDatabase {
    public static void main(String[] args) {
        try {
            String command = "sudo mysqldump -u easybudgetadmin -peasybudgetpw easybudgetdb > ../db_backups/backup_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".sql";
            // Create a process builder that runs a bash command
            // this command can be .sh file
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);

            // Start the process
            Process process = processBuilder.start();

            // Codes below are OPTIONAL
            // Capture and print the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to finish
            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
