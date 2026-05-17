package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.data.userdata.User;
import com.oop.gymquest.screens.utils.CustomDialog;
import javafx.application.Platform;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdminDataExportTask extends Thread {
    private final List<User> usersToExport;
    private final boolean isSummary;
    private final Object syncLock;
    private final List<String> sharedLog;

    public AdminDataExportTask(List<User> usersToExport, boolean isSummary, Object syncLock, List<String> sharedLog) {
        this.usersToExport = usersToExport;
        this.isSummary = isSummary;
        this.syncLock = syncLock;
        this.sharedLog = sharedLog;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1500);

            File directory = new File("exports");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = isSummary
                    ? "exports/Admin_GroupSummary_" + timestamp + ".txt"
                    : "exports/Admin_User_" + usersToExport.get(0).getFirstName() + "_" + timestamp + ".txt";

            File outputFile = new File(fileName);

            synchronized (syncLock) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                    if (isSummary) {
                        writer.println("=========================================");
                        writer.println("       GYMQUEST GROUP SUMMARY EXPORT     ");
                        writer.println("=========================================");
                        writer.println("Date: " + new Date());
                        writer.println("Total Users Exported: " + usersToExport.size());
                        writer.println("-----------------------------------------");

                        for (User u : usersToExport) {
                            String record = String.format("ID: %-4d | ROLE: %-8s | NAME: %-20s | EMAIL: %s",
                                    u.getUserId(), u.getType().toUpperCase(), u.getFullName(), u.getEmail());
                            writer.println(record);
                            sharedLog.add(record);
                        }
                    } else {
                        if (!usersToExport.isEmpty()) {
                            User u = usersToExport.get(0);
                            writer.println("=========================================");
                            writer.println("       GYMQUEST INDIVIDUAL EXPORT        ");
                            writer.println("=========================================");
                            writer.println("Date: " + new Date());
                            writer.println("User ID: " + u.getUserId());
                            writer.println("Full Name: " + u.getFullName());
                            writer.println("Account Role: " + u.getType().toUpperCase());
                            writer.println("Email Address: " + u.getEmail());
                            writer.println("Account Status: " + (u.isActive() ? "Active" : "Archived"));

                            sharedLog.add("Exported " + u.getFullName());
                        }
                    }
                } catch (IOException e) {
                    System.err.println("File Export Error: " + e.getMessage());
                }
            }

            Platform.runLater(() -> {
                String title = isSummary ? "Group Export Complete" : "Individual Export Complete";
                String msg = "Data has been successfully saved to:\n\n" + outputFile.getAbsolutePath();

                CustomDialog.showConfirmation(title, msg, "OK", false);
            });
        } catch (InterruptedException e) { e.printStackTrace(); }
    }
}