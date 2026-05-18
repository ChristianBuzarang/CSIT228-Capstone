# GymQuest: Level Up Your Fitness Journey

**Course:** CSIT228 - CAPSTONE PROJECT 2026  
**Group Name:** MYLABS  
**Group Members:**
* Abaincia, Joel Constantine
* Buzarang, Christian
* Ligaray, Ericka Fatima Reign
* Matedios, Benedict Reynz
* Yee, Marc Nelson




## Project Description

GymQuest is a Java-based desktop program designed to address low member retention and heavy administrative overhead in standard fitness centers. While most systems focus solely on basic record-keeping and labor-intensive workflows, GymQuest bridges the gap between management and motivation. 

The system provides a centralized platform for admins, trainers, and members to seamlessly interact. By offering structured session bookings, customizable workout tracking, and an interactive community space, GymQuest transforms standard gym management into a collaborative, highly responsive fitness management experience.



## How It Works

GymQuest utilizes a secure, role-based system categorized into three main users: **Admins**, **Trainers**, and **Members**.

### 1. Authentication & Registration
* **Access & Registration:** Users log in from the main screen or register as a Member or Trainer if they don't have an account.
* **Role Routing:** Upon logging in, the system automatically redirects the user to their role-specific dashboard (Admin, Trainer, or Member).
* **Logout & Exit:** Users can safely log out to return to the login page or exit the program entirely.

### 2. Admin Operations
* **System Metrics:** Displays total counts of trainers and members.
* **Admin Management:** Allows adding new administrators and updating existing admin data.
* **Centralized Dashboard Tables:** Features unified tables displaying active members, trainers, admins, and archived accounts.
* **Account Archival & Reactivation:** Allows archiving users; archived accounts can only be reactivated through a face-to-face request with an admin.
* **Schedule Filters:** Views daily trainer availability with filters to check schedules for all trainers or a specific individual.

### 3. Trainer Interface
* **Client & Daily Schedules:** Displays the trainer's clients alongside a dedicated panel showing the current day's schedule.
* **Calendar & Booking Constraints:** Allows adding sessions (date, time, and duration) to the calendar. Sessions are limited to one client each and become hidden from other members once booked.
* **Workout Routine Builder:** Sets up exercises from an available list, allowing custom adjustments for sets, repetitions, or time durations.
* **Notifications & Profile:** Features an in-app notification panel and options to edit the trainer's username and profile picture.

### 4. Member Experience
* **Activity Tracking:** Displays the current day's upcoming sessions and recent fitness activities.
* **Custom Workout Builder:** Allows creating personalized routines from available exercises by adjusting sets, repetitions, or time durations.
* **Session Booking Mechanics:** Allows booking available trainers through a calendar. Once confirmed, the slot disappears from the calendar and moves to the user's upcoming sessions and recent activity lists.
* **Notifications & Profile:** Features an in-app notification panel and options to update the user's username and profile picture.

### 5. Shared Community Feature
* **Shared Community Feed:** Allows both Members and Trainers to share fitness achievements, post updates, and like or share each other's content.
* **Admin Restriction:** To maintain a clear boundary between social interaction and system management, this module is strictly inaccessible to Admin accounts.



## Proposed Features

* **Secure Authentication:** Role-based access control for Admins, Trainers, and Members ensuring complete data security and personalized views.
* **Comprehensive Directory Management:** Built-in table views featuring CRUD (Create, Read, Update, Delete) functionality alongside soft-delete archive states.
* **Smart Session Booking:** Conflict-free, real-time schedule management with immediate visibility updates upon reservation.
* **Community Social Feed:** An interactive micro-forum built into the app for shared motivation, likes, and achievement logs.
* **Dashboard Analytics:** A visual summary panel providing real-time data on gym metrics, including active user tallies, schedules, and daily availability.



## Technologies Used

* **Language:** Java 21+
* **GUI Framework:** JavaFX (utilizing FXML and SceneBuilder)
* **Database:** SQLite (for portable execution) or MySQL
* **Build Tool:** Maven/Gradle
* **Version Control:** Git/GitHub

---

## Technical Architecture & Implementation

### Object-Oriented Programming (OOP)
* **Inheritance:** A foundational abstract `User` base class, extended by distinct `Admin`, `Trainer`, and `Member` subclasses to streamline authorization and shared traits.
* **Encapsulation:** Protection of sensitive user credentials, personal profiles, and data grids using private fields and managed getters/setters.

### Graphical User Interface (GUI)
* **Responsive Layouts:** Built using JavaFX layout containers.
* **Data Presentation:** Leverages optimized `TableView` structures for seamless rendering of active, archived, and scheduled user rosters.
* **User Feedback:** Custom `Dialog` elements and popups to handle validation warnings, confirmation blocks, and profile changes.

### Software Modeling (UML)
* Use Case and Class Diagrams are integrated into the documentation folder to clearly map out relationships, structural hierarchies, and interactions between users, workout plans, and schedule entities.

### Design Patterns & Performance
* **Singleton Pattern:** Utilized to maintain a centralized, thread-safe Database Connection lifecycle throughout the application session.
* **Multithreading (Concurrency):** Execution of background tasks for database transactions, large dataset loading, and community feed updates to ensure a smooth and responsive JavaFX UI.
