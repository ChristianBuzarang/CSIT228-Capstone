package com.oop.gymquest.screens.profile;

import com.oop.gymquest.data.userdata.User;
import javafx.application.Platform;
import java.util.List;

public class DataExportTask extends Thread {
    private final User userToExport;
    private final Object syncLock;
    private final List<String> sharedLog;

    public DataExportTask(User user, Object syncLock, List<String> sharedLog) {
        this.userToExport = user;
        this.syncLock = syncLock;
        this.sharedLog = sharedLog;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);

            synchronized (syncLock) {
                sharedLog.add("Exported Profile Data for: " + userToExport.getFullName());
                sharedLog.add("Exported Workout History.");
                sharedLog.add("Exported Community Posts.");
                System.out.println("Logs updated safely by background thread.");
            }

            Platform.runLater(() -> {
                com.oop.gymquest.screens.utils.CustomDialog.showConfirmation(
                        "Export Complete", "Your data has been successfully exported!", "OK", false
                );
            });
        } catch (InterruptedException e) { e.printStackTrace(); }
    }
}