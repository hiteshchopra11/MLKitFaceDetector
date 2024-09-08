# Face Detection and Tagging Android App

## Project Overview

This project is a complete Android application that scans a user’s photo gallery to detect faces in each image, puts a bounding box around each face, and allows users to view and tag these faces with names. The app leverages modern Android development tools and best practices to provide a smooth and responsive user experience.

## Features

### 1. Photo Gallery Scanning
- Scans the user's photo gallery to retrieve and display all available images.

### 2. Face Detection
- Utilizes Google's ML Kit for detecting faces within images.
- Draws bounding boxes around detected faces using custom graphics operations.

### 3. Image Viewing
- Displays images in a grid layout for easy browsing.
- Users can tap on an image to view it in full-screen mode with bounding boxes visible.

### 4. Face Tagging
- Allows users to click on detected faces within an image.
- A dialog prompts users to enter a name for each face.
- The entered name is displayed alongside the bounding box in the image.

### 5. Image Saving
- Users can save tagged images to the device storage.
- A loading indicator is shown during the save operation to enhance user experience.

## Architecture and Design

### Jetpack Compose
- Utilizes Jetpack Compose for building the UI with a declarative approach, including components for displaying images, bounding boxes, and dialogs.

### MVVM Architecture
- **ViewModel:** Manages data and business logic, including face detection and image saving.
- **Compose UI:** Handles user interactions and displays data from the ViewModel.

### Performance Optimization
- Uses `LazyVerticalGrid` for displaying images to efficiently handle large datasets.
- Implements asynchronous operations for face detection and image saving to avoid blocking the UI.

## Code Organization

### Modular Components
- Code is organized into modular composable functions for improved readability and maintainability.

### Best Practices
- Utilizes state management and side effects handling with `remember` and `LaunchedEffect`.
- Ensures smooth user experience through proper handling of background tasks using coroutines.

## Submission

- **GitHub Repository:** The project is available on GitHub, including all source code and configuration files.
- **Demo Recording:** A screen recording demonstrating the app’s functionality, including image scanning, face detection, tagging, and saving, is attached.

https://github.com/user-attachments/assets/01ae4d60-6500-4d60-9476-32df3515a786
