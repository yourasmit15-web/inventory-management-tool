# Inventory Management Tool

A desktop inventory management application built with **Java Swing**, **SQLite**, **Maven**, and **BCrypt**. It provides a practical interface for managing products, monitoring stock, searching inventory, and controlling access through user roles.

> **Project:** Inventory Management Tool  
> **Type:** Java desktop application  
> **Build:** Maven  
> **Database:** SQLite

## ✨ Features

- 🔐 User authentication with password hashing
- 👥 Role-based access control
- 📦 Product creation, editing, deletion, and inventory management
- 📊 Stock status visibility
- 🔎 Product search and filtering
- 💾 Lightweight SQLite database storage
- 🖥️ Java Swing desktop interface
- ⚙️ Maven-based project structure
- 🤖 GitHub Actions build workflow

## 🧰 Technology Stack

| Technology | Purpose |
|---|---|
| Java | Application development |
| Swing | Desktop user interface |
| SQLite | Local database |
| BCrypt | Password hashing |
| Maven | Dependency and build management |
| GitHub Actions | Automated build workflow |

## 📁 Project Structure

```text
inventory-management-tool/
├── .github/
│   └── workflows/
│       └── build.yml
├── src/
│   └── main/
│       └── java/com/inventory/
│           ├── App.java
│           ├── db/
│           │   └── DatabaseManager.java
│           ├── model/
│           │   ├── Product.java
│           │   └── User.java
│           ├── ui/
│           │   ├── LoginFrame.java
│           │   ├── MainFrame.java
│           │   ├── ProductDialog.java
│           │   └── ProductTableModel.java
│           └── util/
│               └── PasswordUtil.java
├── .gitignore
├── pom.xml
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Java JDK installed
- Maven installed and available on your PATH

### Build

```bash
mvn clean package
```

### Run

Run the application from your IDE using `com.inventory.App`, or run the generated Maven artifact according to the project's build configuration.

## 🔐 Security

Passwords are handled through BCrypt hashing rather than storing plaintext passwords in the application logic. Database access is encapsulated by the application's database layer.

## 🔄 CI

The repository includes a GitHub Actions workflow under `.github/workflows/build.yml` for automated project builds.

## 🎯 Project Purpose

The Inventory Management Tool is designed as a compact, practical desktop solution for learning and demonstrating software development concepts including GUI programming, database integration, authentication, CRUD operations, role-based access, and automated builds.

## 📌 Repository Status

This repository contains the original application source code from the supplied project archive. The application source has been uploaded without intentional functional changes; repository documentation is provided to make the project easier to understand, build, and maintain.

## 👨‍💻 Author

**Asmit Mishra**

GitHub: [@yourasmit15-web](https://github.com/yourasmit15-web)

---

⭐ If you find this project useful, consider starring the repository.
