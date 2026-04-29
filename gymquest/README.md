# GymQuest — JavaFX Application

A full-featured fitness app built with JavaFX, converted from a React/TypeScript implementation.

## Architecture

```
src/main/java/com/gymquest/
├── GymQuestApp.java              # Entry point (extends Application)
├── model/
│   ├── AppState.java             # Singleton global state (JavaFX Properties)
│   ├── DataStore.java            # Sample data (workouts, users, posts, sessions)
│   ├── Exercise.java
│   ├── Workout.java
│   ├── TrainerSession.java
│   ├── User.java
│   └── Post.java
├── controller/
│   └── MainController.java       # Navigation orchestrator
├── view/
│   ├── LoginView.java            # Login / Register screen
│   ├── HeaderView.java           # Top navigation bar
│   ├── SideMenuView.java         # Left sidebar navigation
│   ├── DashboardView.java        # Member home dashboard
│   ├── WorkoutsView.java         # All workouts grid
│   ├── WorkoutCardView.java      # Reusable workout card widget
│   ├── WorkoutDetailView.java    # Exercise list + complete button
│   ├── CustomWorkoutCreatorView.java  # Build custom workout
│   ├── ExercisePickerDialog.java      # Exercise library picker
│   ├── BookingView.java          # Calendar + trainer booking
│   ├── CommunityView.java        # Social feed with reactions
│   ├── ProfileView.java          # Profile + badges/achievements
│   ├── AdminView.java            # Admin member/trainer management
│   ├── TrainerView.java          # Trainer schedule management
│   └── NotificationsDialog.java  # Notifications popup
└── util/
    └── UIHelper.java             # Reusable styled components
```

## Prerequisites

- **Java 21+** (LTS recommended)
- **Maven 3.8+**
- JavaFX 21 (pulled automatically by Maven)

## Build & Run

### Using Maven (recommended)
```bash
cd gymquest
mvn javafx:run
```

### Build a fat JAR
```bash
mvn package
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/gymquest-1.0-SNAPSHOT.jar
```

### Using IntelliJ IDEA
1. Open the project (File → Open → select the `gymquest` folder)
2. Maven auto-imports dependencies
3. Run `GymQuestApp` main class

### Using VS Code
1. Install the "Extension Pack for Java"
2. Open the `gymquest` folder
3. Run `GymQuestApp.java`

## Features

| Feature | Description |
|---|---|
| **Login / Register** | Member, Trainer, Admin roles |
| **Dashboard** | Streak widget, completed workouts, motivational card |
| **Workouts** | Browse all workouts, difficulty badges, lock system |
| **Workout Detail** | Exercise list with sets/reps, complete button |
| **Custom Workouts** | Build your own with the exercise picker library |
| **Calendar Booking** | Month view, select date, pick trainer + time slot |
| **Community Feed** | Milestone posts with ❤️ reaction toggle |
| **Profile** | Stats + 9 achievement badges with progress bars |
| **Admin Dashboard** | Member/trainer management with search |
| **Trainer Dashboard** | Schedule management, add new slots |
| **Notifications** | Popup panel with unread indicators |

## Design Notes

- Colors mirror the React original: `#3b82f6` (blue), `#d97706` (amber), `#1e3a5f` (dark navy)
- `AppState` uses JavaFX `Property` objects for reactive updates
- `UIHelper` centralizes all styled component factories
- Navigation via `MainController.navigateTo(page)` — no FXML required
- All data lives in `DataStore` singleton (replace with DB/API calls as needed)
