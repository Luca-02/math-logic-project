# Mathematical Logic Project
This is a master's degree project for Mathematical Logic course (academic year 2024/25).


## Project Description
The aim of the project is to implement the three following calculation methods in Java following the rules and methodologies described in the `dispense.pdf` handouts:
- **R Calculus**
- **Sorted Calculus**
- **S Calculus**

## Project Structure
The structure of the project is as follows:

1. **Logical Structures**: Contains the classes that represent the logical structures on which the calculations will be performed.
2. **Implementation of calculations**: Includes the main classes responsible for implementing the three calculation methods listed.
3. **Utilities and support**: Collects the classes that support the main calculations, including those that implement secondary or auxiliary operations necessary for the correct functioning of the main calculations.

## Requirements
- [Java 17 or higher](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven 4.0 or higher](https://maven.apache.org/download.cgi)

## Setup
To install the project, follow these steps:

1. Clone the repository:
    ```sh
    git clone https://github.com/Luca-02/math-logic-project.git
    ```
2. Navigate to the project directory:
    ```sh
    cd math-logic-project
    ```
3. Build the project using Maven:
    ```sh
    mvn clean install
    ```

## Testing
To run the tests, use the following Maven command:
```sh
mvn test
