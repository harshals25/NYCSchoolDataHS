# NYC Schools Information App

The NYC Schools Information App is an Android application developed using Kotlin, Jetpack Compose, MVVM architecture, and Retrofit. It provides users with access to information about New York City (NYC) schools, including a list of schools and their corresponding SAT scores.

## Overview

The app consists of two main screens:

### Main Screen (School List)
- Upon launching the app, users are presented with a list of NYC schools.
- The list is paginated, allowing users to scroll through a large number of schools.
- Users can tap on a school to view more details, including its SAT scores.

### School Information Screen
- After selecting a school from the list, users are navigated to a screen displaying additional information about the selected school.
- This screen fetches and displays the SAT scores for the selected school, providing users with insights into its academic performance.


## Technologies Used

- **Kotlin**: The primary programming language used for Android app development.
- **Jetpack Compose**: A modern UI toolkit for building native Android apps using a declarative UI approach.
- **MVVM architecture**: The Model-View-ViewModel architectural pattern, which helps to separate the UI logic from the business logic.
- **Retrofit**: A type-safe HTTP client for Android and Java, used for making network requests and handling API responses.
- **ViewModel**: Part of the Android Architecture Components, ViewModel provides data to the UI and survives configuration changes.
- **LiveData**: An observable data holder class from the Android Architecture Components, used to notify UI components about data changes.
- **Coroutines**: Kotlin's native solution for asynchronous programming, used for managing background tasks efficiently.


## Getting Started

To run the project locally, follow these steps:

1. Clone the repository:
git clone https://github.com/harshals25/NYCSchoolDataHS.git

2. Open the project in Android Studio.

3. Build and run the project on an Android device or emulator.

## Usage

- Launch the app to view a list of NYC schools.
- Scroll through the list to load more schools using pagination.
- Tap on a school to view its SAT scores and additional information.

## Contributing

Contributions are welcome! If you'd like to contribute to this project, feel free to open a pull request.

## License

This project is licensed under the [MIT License](LICENSE).

