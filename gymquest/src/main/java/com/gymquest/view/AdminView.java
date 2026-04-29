package com.gymquest.view;

import com.gymquest.model.AppState;
import com.gymquest.model.DataStore;
import com.gymquest.model.User;
import com.gymquest.util.UIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.stream.Collectors;

public class AdminView {

    private final ScrollPane root;
    private String activeTab = "members";
    private String searchTerm = "";
    private VBox userListBox;
    private List<User> users;

    public AdminView(AppState state) {
        users = DataStore.getInstance().getUsers();

        VBox content = new VBox(20);
        content.setPadding(new Insets(24));

        // Header
        VBox header = UIHelper.card();
        HBox hrow = new HBox(20);
        hrow.setAlignment(Pos.CENTER_LEFT);
        VBox titleBox = new VBox(4);
        titleBox.getChildren().addAll(
            UIHelper.title("Admin Dashboard"),
            UIHelper.subtitle("Manage members and trainers")
        );
        Region sp = UIHelper.spacer();
        Label mascot = new Label("👨‍💼");
        mascot.setStyle("-fx-font-size: 48px;");
        hrow.getChildren().addAll(titleBox, sp, mascot);
        header.getChildren().add(hrow);
        content.getChildren().add(header);

        // Stats
        HBox statsRow = new HBox(16);
        long totalM = users.stream().filter(u -> u.getType().equals("member")).count();
        long activeM = users.stream().filter(u -> u.getType().equals("member") && u.getStatus().equals("active")).count();
        long totalT = users.stream().filter(u -> u.getType().equals("trainer")).count();
        long activeT = users.stream().filter(u -> u.getType().equals("trainer") && u.getStatus().equals("active")).count();

        HBox s1 = UIHelper.statsCard("Total Members", String.valueOf(totalM), "#e0f2fe", "#bae6fd", "#3b82f6");
        HBox s2 = UIHelper.statsCard("Active Members", String.valueOf(activeM), "#fef3c7", "#fde68a", "#d97706");
        HBox s3 = UIHelper.statsCard("Total Trainers", String.valueOf(totalT), "#e0f2fe", "#bae6fd", "#3b82f6");
        HBox s4 = UIHelper.statsCard("Active Trainers", String.valueOf(activeT), "#fef3c7", "#fde68a", "#d97706");
        for (HBox s : new HBox[]{s1, s2, s3, s4}) HBox.setHgrow(s, Priority.ALWAYS);
        statsRow.getChildren().addAll(s1, s2, s3, s4);
        content.getChildren().add(statsRow);

        // Tab + search
        VBox tableCard = UIHelper.card();

        HBox tabRow = new HBox(8);
        tabRow.setAlignment(Pos.CENTER_LEFT);

        Button membersTab = tabBtn("Members", "members");
        Button trainersTab = tabBtn("Trainers", "trainers");
        updateTabStyle(membersTab, true);
        updateTabStyle(trainersTab, false);

        membersTab.setOnAction(e -> {
            activeTab = "members";
            updateTabStyle(membersTab, true);
            updateTabStyle(trainersTab, false);
            refreshUsers();
        });
        trainersTab.setOnAction(e -> {
            activeTab = "trainers";
            updateTabStyle(membersTab, false);
            updateTabStyle(trainersTab, true);
            refreshUsers();
        });

        Region tabSp = UIHelper.spacer();
        Button addBtn = UIHelper.primaryBtn("+ Add User");

        tabRow.getChildren().addAll(membersTab, trainersTab, tabSp, addBtn);
        tableCard.getChildren().add(tabRow);

        TextField search = new TextField();
        search.setPromptText("🔍  Search by name or email...");
        search.setStyle("-fx-padding: 10 14; -fx-border-color: #bae6fd; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10;");
        search.textProperty().addListener((obs, o, n) -> {
            searchTerm = n.toLowerCase();
            refreshUsers();
        });
        tableCard.getChildren().add(search);

        userListBox = new VBox(8);
        refreshUsers();
        tableCard.getChildren().add(userListBox);

        content.getChildren().add(tableCard);

        root = new ScrollPane(content);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #f0f8ff; -fx-background: #f0f8ff;");
    }

    private void refreshUsers() {
        userListBox.getChildren().clear();
        users.stream()
            .filter(u -> u.getType().equals(activeTab.equals("members") ? "member" : "trainer"))
            .filter(u -> u.getName().toLowerCase().contains(searchTerm) || u.getEmail().toLowerCase().contains(searchTerm))
            .forEach(u -> userListBox.getChildren().add(buildUserRow(u)));
    }

    private HBox buildUserRow(User user) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14));
        row.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 12; -fx-border-color: #bae6fd; -fx-border-width: 2;");

        StackPane avatar = UIHelper.avatarCircle(user.getAvatar(), 44, "#3b82f6", "#60a5fa");

        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label name = UIHelper.label(user.getName());
        name.setStyle(name.getStyle() + " -fx-font-weight: bold;");
        Label email = UIHelper.muted(user.getEmail());
        info.getChildren().addAll(name, email);

        Region sp = UIHelper.spacer();

        VBox statusBox = new VBox(4);
        statusBox.setAlignment(Pos.CENTER_RIGHT);
        Label statusBadge = UIHelper.badge(
            user.getStatus(),
            user.getStatus().equals("active") ? "#d1fae5" : "#fee2e2",
            user.getStatus().equals("active") ? "#065f46" : "#991b1b"
        );
        Label joined = UIHelper.muted("Joined " + user.getJoinDate());
        statusBox.getChildren().addAll(statusBadge, joined);

        Button more = new Button("⋮");
        more.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 18px;");

        row.getChildren().addAll(avatar, info, sp, statusBox, more);
        return row;
    }

    private Button tabBtn(String text, String tab) {
        Button btn = new Button(text);
        btn.setUserData(tab);
        return btn;
    }

    private void updateTabStyle(Button btn, boolean active) {
        btn.setStyle(
            (active
                ? "-fx-background-color: #3b82f6; -fx-text-fill: white;"
                : "-fx-background-color: transparent; -fx-text-fill: #64748b;") +
            " -fx-background-radius: 10; -fx-padding: 10 20; -fx-cursor: hand; -fx-font-size: 14px;"
        );
    }

    public ScrollPane getRoot() { return root; }
}
