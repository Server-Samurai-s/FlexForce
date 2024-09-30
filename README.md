#FlexForce – Gym Application and Fitness Tracker

##Members:
Aidan Keith Naidoo - ST10109482
David Roy Mellors - ST10241466
Nilay Ramjee - ST10082679
Dylan Miller - ST10029256

##App Name: FlexForce

##App Type: Gym Application / Fitness Tracker

FlexForce is a fitness application that provides a customised training experience to assist users in reaching their fitness objectives. 
FlexForce strives to make training interesting and efficient with features including personalised routines, progress monitoring and workout challenges.

##Purpose:
The goal of FlexForce is to offer an accessible, approachable fitness monitoring solution aimed at supporting and maintaining a healthy lifestyle. 
To improve their path towards health, users may design customised training routines, track their progress, and participate in workout challenges with the application. 
Users also have predefined workouts within the application that they can choose from.

##Key Innovative Features Added:

1. Filter Exercises by Muscle Group Anatomy: Users can select body parts on an interactive anatomy figure to filter exercises for targeted muscle training.
2. Workout Streaks and High Score: Keeps users motivated as this feature encourages users as they are rewarded for consistent progress.
3. Workout Challenges: The application offers workout challenges that users can participate in to push their limits.

##Design Considerations:
FlexForce's design was influenced by a thorough research of other fitness programs, with the goal of combining its most effective elements with up-to-date innovative additions. 
To increase user engagement, our UI design places a strong emphasis on a visually appealing and straightforward layout.

##Colour Palette:
· Primary Colour - #1B1C1E
· Secondary Colour - #FFFFFF
· Background Colour - # 303030
· Text Colour - #FFFFFF

##Screens Overview:
1. Welcome Screen: Introduces the app and directs people to register or log in.
2. Registration Screen: Users can register a new account.
3. Login Screen: Users can login using their credentials
4. Home Screen: Displays workout streaks, weight, favourite exercises, fitness tracker (user enters their weight, height and body fat, set goals (goal weight and goal body fat).
5. Settings Page: Allows users to customise their profile and app settings.
6. Workout Screen: Categorised exercises for easy navigation.
7. Create Workout Screen: Users can create their personalised workouts.
8. Challenges Screen: Lists available challenges for users to participate in.
9. Statistics Screen: Displays fitness progress, streaks and BMI.
 
##Application Navigation:
With the use of a bottom navigation bar, users can easily access their profile, workouts, statistics, challenges, and settings in FlexForce thanks to its user-friendly navigation. Below is an image of the navigation bar.
 
##API Design:
The REST API is the backbone of the data flow between the Android app and the Firebase database, with the use of the cloud Firestore.
· Endpoints for synchronisation, user-specific data, and exercise retrieval.
· Developed using Node.js and Express.
· Hosted on Vercel (For free hosting).
· Incorporates Get, Post and Delete routes/ methods

##Key API Functions:
·  Workout Information Retrieval – Users can get workouts based on their workout types.
·  Preset workouts – provides predefined workouts based on the user selections.

##GitHub and GitHub Actions Utilisation:
Version control and cooperation are made easier considering having the advantage that FlexForce's source code is stored on GitHub. 
GitHub Actions is also used in the project for deployment and continuous integration.

##How to get Started:
1. Clone the repository from GitHub: 
git clone  https://github.com/Server-Samurai-s/FlexForce.git
2. Open the project in Android studio
3. Build and run the project on an Android device or emulator. 
API repository: git clone https://github.com/davidrmellors/flexforce-api.git

GitHub Actions is used in this project to automate testing and building processes. It ensures that each code push is tested and the app is built correctly across environments, reducing manual work and enhancing consistency.

Automated Testing:
Every push to the master or release/** branches triggers automated unit tests using Gradle (./gradlew test). This ensures that the main functionality of the app is verified with every code update.

Automated Build:
After successful testing, the workflow automatically builds APK and AAB files (./gradlew assembleDebug and ./gradlew bundleRelease). These files are used for distribution and testing.

Artefact Upload:
The APK and AAB files are uploaded as artefacts, making them available for download directly from GitHub.

Triggers:
The workflow is automatically triggered on every push to master or release/** branches, ensuring continuous integration and consistent validation of code changes.

##Technologies Used:
1. Firebase: Cloud firestore is used for storing user workouts and data.
2. Vercel: Used to host the API.
3. Node.js and Express: Used to build a custom REST API that connects to firebase. 
4. Android Development: Developed using Kotlin programming language for Android platform compatibility.
 
##Reference / Bibliography list
· GeeksforGeeks. 2024. How to send different types of requests (GET, POST, PUT, DELETE) in Postman, 1 January 2024. [Online]. Available at: https://www.geeksforgeeks.org/how-to-send-different-types-of-requests-get-post-put-delete-in-postman/ [Accessed 23 September 2024].
