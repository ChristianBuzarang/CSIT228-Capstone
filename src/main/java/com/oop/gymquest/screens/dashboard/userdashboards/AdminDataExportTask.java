package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.data.userdata.User;
import com.oop.gymquest.screens.utils.CustomDialog;
import javafx.application.Platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
            System.out.println("Background Thread [" + Thread.currentThread().getName() + "] started processing...");
            Thread.sleep(3000);

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
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    StringBuilder dataBuilder = new StringBuilder();
                    if (isSummary) {
                        dataBuilder.append("=========================================\n");
                        dataBuilder.append("       GYMQUEST GROUP SUMMARY EXPORT     \n");
                        dataBuilder.append("=========================================\n");
                        dataBuilder.append("Date: ").append(new Date()).append("\n");
                        dataBuilder.append("Total Users Exported: ").append(usersToExport.size()).append("\n");
                        dataBuilder.append("-----------------------------------------\n");
                        for (User u : usersToExport) {
                            String record = String.format("ID: %-4d | ROLE: %-8s | NAME: %-20s | EMAIL: %s\n",
                                    u.getUserId(), u.getType().toUpperCase(), u.getFullName(), u.getEmail());
                            dataBuilder.append(record);
                            sharedLog.add(record);
                        }
                    } else {
                        if (!usersToExport.isEmpty()) {
                            User u = usersToExport.get(0);
                            dataBuilder.append("=========================================\n");
                            dataBuilder.append("       GYMQUEST INDIVIDUAL EXPORT        \n");
                            dataBuilder.append("=========================================\n");
                            dataBuilder.append("Date: ").append(new Date()).append("\n");
                            dataBuilder.append("User ID: ").append(u.getUserId()).append("\n");
                            dataBuilder.append("Full Name: ").append(u.getFullName()).append("\n");
                            dataBuilder.append("Account Role: ").append(u.getType().toUpperCase()).append("\n");
                            dataBuilder.append("Email Address: ").append(u.getEmail()).append("\n");
                            dataBuilder.append("Account Status: ").append(u.isActive() ? "Active" : "Archived").append("\n");
                        }
                    }
                    fos.write(dataBuilder.toString().getBytes());
                    System.out.println("Successfully wrote data using FileOutputStream.");
                } catch (IOException e) {
                    System.err.println("OutputStream Error: " + e.getMessage());
                }

                System.out.println("\n--- VERIFYING FILE WITH FILEINPUTSTREAM ---");
                try (FileInputStream fis = new FileInputStream(outputFile)) {
                    int content;
                    while ((content = fis.read()) != -1) {
                        System.out.print((char) content);
                    }
                    System.out.println("\n--- END OF VERIFICATION ---");
                } catch (IOException e) {
                    System.err.println("InputStream Error: " + e.getMessage());
                }
            }
            Platform.runLater(() -> {
                String title = isSummary ? "Group Export Complete" : "Individual Export Complete";
                String msg = "Data has been successfully written & verified via File Streams to:\n\n" + outputFile.getAbsolutePath();
                CustomDialog.showConfirmation(title, msg, "OK", false);
            });
        } catch (InterruptedException e) { e.printStackTrace(); }
    }
}