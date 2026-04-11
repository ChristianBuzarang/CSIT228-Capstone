# GymQuest: Level Up Your Fitness Journey
**Course:** CSIT228 - CAPSTONE PROJECT 2026  
**Group Name:** MYLABS

# Group Members
- Abaincia, Joel Constantine
- Buzarang, Christian 
- Ligaray, Ericka Fatima Reign 
- Matedios, Benedict Reynz 
- Yee, Marc Nelson 

# Project Description
GymQuest is a Java-based desktop application designed to solve the problem of low member engagement and manual administrative overhead in traditional fitness centers. While most systems focus solely on record-keeping, GymQuest bridges the gap between management and motivation.

The system provides a centralized platform for trainers and admins to manage operations, but its primary goal is to enhance the member experience through a Gamification Engine. By tracking attendance and workout consistency, it is transforming a standard gym membership into an interactive fitness journey.

# Proposed Features
- **Secure Authentication:** Role-based access control for Admins, Trainers, and Members to ensure data security and personalized views.
- **Member Directory:** Full CRUD (Create, Read, Update, Delete) functionality with advanced filtering for Active, Expired, and Pending memberships.
- **Session Booking:** An interactive scheduling system for members to book and manage personal training sessions.
- **Financial Tracking:** Secure recording of member payments and the generation of basic revenue and financial reports.
- **Dashboard Analytics:** A visual summary panel providing real-time data on gym health, including total members, daily attendance, and revenue trends.

# Planned Technologies
- **Language:** Java 21+
- **GUI Framework:** JavaFX (utilizing FXML and SceneBuilder)
- **Database:** SQLite (for portability) or MySQL
- **Build Tool:** Maven/Gradle
- **Version Control:** Git/GitHub

# Evaluation Criteria Mapping
- **OOP:** Implementation of Inheritance (User base class with Member and Trainer subclasses) and Encapsulation to protect sensitive member and financial data.
- **GUI:** Responsive layout utilizing JavaFX BorderPane, TableViews for data management, and custom Dialogs for user interactions.
- **UML:** Use Case and Class Diagrams are included to map the relationships between Members, Membership Plans, Trainers, and Transactions.
- **Design Pattern:**
  - **Singleton:** Ensuring a centralized, thread-safe Database Connection.
- **Multithreading:** Implementation of background tasks for loading large datasets and generating reports to ensure a smooth, non-blocking UI experience.
