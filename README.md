# Presentation Validator

A service for automatic validation of student research and thesis presentations against structural, formatting, and styling requirements.

## Features

- Analyzes presentation structure
- Validates slide formatting (fonts, colors, headers, lists)
- Checks slide content (text length, formatting)
- Validation rules are defined using an internal Kotlin DSL
- Generates a structured report detailing violations per slide and element

## Requirements

- Java 11 or higher
- Kotlin 1.5 or higher
- Gradle (or use the Gradle wrapper included)

## Build

To build the project, run:

```bash
./gradlew build
```

## Run
Start the service locally with:

```bash
./gradlew run
```
This will launch a web server on http://localhost:8080.

## Usage
Open your browser and navigate to http://localhost:8080.

Upload your presentation file via the web interface.

Select the type of check: coursework or final qualification

View the validation report with detailed errors and warnings.