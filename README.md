# JDolt

JDolt is a Java client library for [Dolt](https://www.dolthub.com/), a SQL database with Git-like version control capabilities. This project provides a high-level API for interacting with Dolt databases, allowing you to perform version control operations on your database similar to Git operations on code.

## Project Overview

JDolt encapsulates Dolt's version control functionality into a Java API, making it easy to integrate database version control into your Java applications. It provides methods for common version control operations like commit, branch, merge, pull, push, and more.

## Modules

The project consists of two main modules:

1. **jdolt-core**: Contains the core functionality for interacting with Dolt databases, including:
   - API interfaces and implementations
   - Version control operations
   - Database connection management

2. **jdolt-service**: A Spring Boot application that provides a service layer on top of jdolt-core.

## Features

JDolt supports a wide range of Dolt version control operations, including:

- Branch management (create, list, delete)
- Commit changes
- Checkout branches or commits
- Merge branches
- Pull and push changes
- Reset and revert changes
- Conflict resolution
- Diff and status operations
- And many more

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- Access to a Dolt database

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/JDolt.git
   ```

2. Build the project with Maven:
   ```
   cd JDolt
   mvn clean install
   ```

### Configuration

Configure your Dolt database connection in your application. If using the jdolt-service module, you can configure it in the `application.yaml` file:

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: your_username
    password: your_password
    url: jdbc:mysql://your_dolt_server:port/your_database
```

### Usage Example

Here's a simple example of how to use JDolt in your Java application:

```java
// Initialize the DoltClient with a DataSource
DataSource dataSource = // your DataSource implementation
DoltClient doltClient = DoltClient.initialize(dataSource);

// Get the version control API
VersionControl versionControl = doltClient.versionControl();

// Create a new branch
versionControl.doltBranch().prepare()
    .createBranch("new-feature")
    .execute();

// Checkout the new branch
versionControl.doltCheckout().prepare()
    .branch("new-feature")
    .execute();

// After making changes to your database, commit them
versionControl.doltCommit().prepare()
    .message("Added new feature")
    .execute();
```

## Project Structure

```
JDolt/
├── doc/
│   └── codeTemplates/       # Code templates for creating Dolt components
├── jdolt-core/              # Core library module
│   └── src/
│       ├── main/
│       │   └── java/
│       │       └── com/
│       │           └── hxuanyu/
│       │               └── jdolt/
│       │                   ├── core/       # Core functionality
│       │                   ├── manager/    # Connection management
│       │                   └── util/       # Utility classes
│       └── test/           # Unit tests
├── jdolt-service/          # Spring Boot service module
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/
│       │   │       └── hxuanyu/
│       │   │           └── jdolt/
│       │   │               └── DoltManagerApplication.java
│       │   └── resources/
│       │       └── application.yaml
│       └── test/           # Unit tests
└── pom.xml                 # Maven parent POM
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the [Your License] - see the LICENSE file for details.

## Acknowledgments

- [Dolt](https://www.dolthub.com/) - The SQL database with Git-like version control
- The developers and contributors to this project