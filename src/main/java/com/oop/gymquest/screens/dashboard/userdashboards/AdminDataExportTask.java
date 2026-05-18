package com.oop.gymquest.screens.dashboard.userdashboards;

import com.oop.gymquest.data.userdata.User;
import com.oop.gymquest.screens.utils.CustomDialog;
import javafx.application.Platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
            System.out.println("\n========== MULTITHREADING ORCHESTRATION ==========");
            System.out.println("Main Export Thread [" + Thread.currentThread().getName() + "] is delegating tasks...");

            System.out.println("[" + Thread.currentThread().getName() + "] Simulating heavy network/database processing for 3 seconds...");
            Thread.sleep(3000);

            StringBuilder dataBuilder = new StringBuilder();

            if (isSummary) {
                dataBuilder.append("=========================================\n");
                dataBuilder.append("       GYMQUEST GROUP SUMMARY EXPORT     \n");
                dataBuilder.append("=========================================\n");
                dataBuilder.append("Date: ").append(new Date()).append("\n");
                dataBuilder.append("Total Users Exported: ").append(usersToExport.size()).append("\n");
                dataBuilder.append("-----------------------------------------\n");
                String[] formattedRows = new String[usersToExport.size()];
                List<Thread> workerThreads = new ArrayList<>();
                for (int i = 0; i < usersToExport.size(); i++) {
                    final int index = i;
                    final User u = usersToExport.get(i);

                    Thread userThread = new Thread(() -> {
                        formattedRows[index] = String.format("ID: %-4d | ROLE: %-8s | NAME: %-20s | EMAIL: %s\n",
                                u.getUserId(), u.getType().toUpperCase(), u.getFullName(), u.getEmail());
                        System.out.println("[" + Thread.currentThread().getName() + "] processed data for: " + u.getFullName());
                    });

                    userThread.setName("UserProcessorThread-" + (i + 1));
                    workerThreads.add(userThread);
                    userThread.start(); // Start the thread immediately
                }
                for (Thread t : workerThreads) {
                    t.join();
                }
                for (String row : formattedRows) {
                    dataBuilder.append(row);
                    sharedLog.add(row);
                }
            } else {
                if (!usersToExport.isEmpty()) {
                    User u = usersToExport.get(0);
                    String[] fetchedData = new String[4];

                    Thread t1 = new Thread(() -> {
                        try { Thread.sleep(1000); } catch (InterruptedException e) {}
                        fetchedData[0] = "User ID: " + u.getUserId() + "\nFull Name: " + u.getFullName() + "\n";
                        System.out.println("[" + Thread.currentThread().getName() + "] successfully fetched ID and Name.");
                    });
                    t1.setName("DataFetcherThread-Name");

                    Thread t2 = new Thread(() -> {
                        try { Thread.sleep(1000); } catch (InterruptedException e) {}
                        fetchedData[1] = "Email Address: " + u.getEmail() + "\n";
                        System.out.println("[" + Thread.currentThread().getName() + "] successfully fetched Email.");
                    });
                    t2.setName("DataFetcherThread-Email");

                    Thread t3 = new Thread(() -> {
                        try { Thread.sleep(1000); } catch (InterruptedException e) {}
                        fetchedData[2] = "Account Role: " + u.getType().toUpperCase() + "\n";
                        System.out.println("[" + Thread.currentThread().getName() + "] successfully fetched Role.");
                    });
                    t3.setName("DataFetcherThread-Role");

                    Thread t4 = new Thread(() -> {
                        try { Thread.sleep(1000); } catch (InterruptedException e) {}
                        fetchedData[3] = "Account Status: " + (u.isActive() ? "Active" : "Archived") + "\n";
                        System.out.println("[" + Thread.currentThread().getName() + "] successfully fetched Status.");
                    });
                    t4.setName("DataFetcherThread-Status");
                    t1.start(); t2.start(); t3.start(); t4.start();
                    t1.join(); t2.join(); t3.join(); t4.join();

                    dataBuilder.append("=========================================\n");
                    dataBuilder.append("       GYMQUEST INDIVIDUAL EXPORT        \n");
                    dataBuilder.append("=========================================\n");
                    dataBuilder.append("Date: ").append(new Date()).append("\n");
                    for (String data : fetchedData) {
                        dataBuilder.append(data);
                    }
                }
            }
            System.out.println("All sub-threads completed. Proceeding to File Generation...");
            System.out.println("==================================================\n");

            File directory = new File("exports");
            if (!directory.exists()) directory.mkdirs();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = isSummary
                    ? "exports/Admin_GroupSummary_" + timestamp + ".txt"
                    : "exports/Admin_User_" + usersToExport.get(0).getFirstName() + "_" + timestamp + ".txt";

            File outputFile = new File(fileName);
            synchronized (syncLock) {
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    fos.write(dataBuilder.toString().getBytes());
                    System.out.println("Successfully wrote combined data using FileOutputStream.");
                } catch (IOException e) {
                    System.err.println("OutputStream Error: " + e.getMessage());
                }

                System.out.println("\n--- VERIFYING GENERATED FILE WITH FILEINPUTSTREAM ---");
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}