# Inventory Management Tool

A fresher-friendly desktop **Inventory Management System** built with **Java 17, Swing, SQLite, Maven, BCrypt, and FlatLaf**.

## Features

- Secure login with BCrypt password hashing
- Two roles: **Admin** and **User**
- Admin-only product mutations
- Add, edit, delete, and restock products
- Product search and category filtering
- Low-stock and out-of-stock monitoring
- Inventory value calculation
- Local SQLite database; no MySQL server required
- Executable fat JAR produced by Maven
- Automated build + unit tests with GitHub Actions

## Technology Stack

| Technology | Version / Purpose |
|---|---|
| Java | 17 |
| Swing | Desktop GUI |
| SQLite JDBC | 3.45.1.0 |
| BCrypt | 0.4 |
| FlatLaf | 3.4.1 |
| Maven | Build/dependency management |
| JUnit 5 | Automated tests |
| GitHub Actions | CI |

## Project Structure

```text
inventory-management-tool/
├── .github/workflows/build.yml
├── src/
│   ├── main/java/com/inventory/
│   │   ├── App.java
│   │   ├── db/DatabaseManager.java
│   │   ├── model/Product.java
│   │   ├── model/User.java
│   │   ├── ui/LoginFrame.java
│   │   ├── ui/MainFrame.java
│   │   ├── ui/ProductDialog.java
│   │   ├── ui/ProductTableModel.java
│   │   └── util/PasswordUtil.java
│   └── test/java/com/inventory/
│       ├── model/ProductTest.java
│       └── util/PasswordUtilTest.java
├── .gitignore
├── pom.xml
└── README.md
```

## Windows Setup

### 1. Install JDK 17

Verify the JDK:

```bat
java -version
javac -version
```

Both commands should report Java 17.

### 2. Install Maven

Verify Maven:

```bat
mvn -version
```

If Windows says:

```text
'mvn' is not recognized as an internal or external command
```

Maven is not installed or its `bin` directory is not in PATH.

After installing Maven, **close Terminal and open a new Terminal** before testing `mvn -version` again.

### 3. Clone the project

```bat
git clone https://github.com/yourasmit15-web/inventory-management-tool.git
cd inventory-management-tool
```

### 4. Run tests and build

```bat
mvn clean test
mvn clean package
```

The packaged application is:

```text
target\inventory-app.jar
```

### 5. Start the application

```bat
java -jar target\inventory-app.jar
```

The application creates `inventory.db` automatically in the directory from which it is started.

## Demo Accounts

The first database initialization creates:

| Username | Password | Role |
|---|---|---|
| admin | admin123 | Admin |
| user | user123 | User |

**Admin:** can add, edit, delete, and restock products.

**User:** can log in, search, filter, refresh, and view inventory information. Product mutation controls are disabled.

These are development/demo credentials. Change the seeding strategy before using the application for real business data.

## Database

The application uses SQLite, so no separate MySQL installation or database server is required.

On first launch:

1. `inventory.db` is created.
2. `users` and `products` tables are created.
3. Demo users are inserted if the user table is empty.
4. Sample products are inserted during first-time initialization.

Do not delete `inventory.db` if you want to keep your local inventory data.

## Build Output

Maven's assembly plugin creates a self-contained JAR:

```text
target/inventory-app.jar
```

It includes the application dependencies, so the normal launch command is simply:

```bat
java -jar target\inventory-app.jar
```

A Java 17 runtime is still required.

## Testing

Run:

```bat
mvn test
```

Current automated tests cover:

- Product stock-status rules
- Product total-value calculation
- BCrypt password hashing and verification

## Architecture

```text
App
 │
 ▼
LoginFrame ──────► DatabaseManager ──────► SQLite
 │                       │
 │                       ├── Authentication
 ▼                       ├── Product CRUD
MainFrame ◄──────────────┼── Search/filter
 │                       └── Stock statistics
 ├── ProductTableModel
 └── ProductDialog
```

The UI is separated from the database layer and domain models, making the project easier to explain and extend.

## Important Design Decisions

### Why SQLite?

This is a desktop application intended for a BCA-level project. SQLite keeps installation simple because the database is a local file rather than a separate server.

### Why BCrypt?

Passwords should not be stored as plaintext. BCrypt stores a one-way password hash and verifies login attempts against that hash.

### Why Maven?

Maven manages third-party dependencies such as SQLite JDBC, BCrypt, and FlatLaf and produces a repeatable build.

## Troubleshooting

### Maven not recognized

Run:

```bat
mvn -version
```

If it fails, install Maven and add its `bin` directory to the Windows PATH, then restart Terminal.

### Java version is wrong

Run:

```bat
java -version
```

The project targets Java 17. Use a JDK 17 installation and make sure `JAVA_HOME`/PATH point to it.

### Application opens but login fails

If this is a fresh installation, close the application and run it again. The initial database seed creates the demo accounts.

### Database problems after changing the schema

For a development reset only, close the application and remove:

```text
inventory.db
```

The next launch will create a fresh database with the demo data.

**Warning:** deleting `inventory.db` permanently removes the local inventory data stored in that file.

## CI

GitHub Actions uses JDK 17 and runs:

```text
mvn -B clean package
```

The build includes automated tests and produces the executable JAR as a workflow artifact.

## Viva Explanation

A simple explanation for a BCA viva:

> "This is a Java Swing desktop inventory management system. The presentation layer uses Swing, the application logic is organized around the UI and database classes, and SQLite stores users and products locally. BCrypt is used for password hashing. Maven manages dependencies and packaging, while GitHub Actions automatically builds and tests the project."

## Author

**Asmit Mishra**

GitHub: https://github.com/yourasmit15-web
