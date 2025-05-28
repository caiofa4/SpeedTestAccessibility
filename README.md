# SpeedTestAccessibility
Simple Android app that allows controlling Ookla Speed Test app using Android Accessibility API

This app uses Accessibility, to control a third party app, and also shows how to take a screenshot of this third party app.

This project will need Accessibility Permission to work.
Also, the MEDIA_PROJECTION permission will be asked, in order to take a screenshot.


If the user doesn't want to give permission every time, they can run the following commands on terminal, with the Android device attached:

- adb shell settings put secure enabled_accessibility_services {packageName}/.accessibility.AccessibilityServiceController

- adb shell settings put secure accessibility_enabled 1

- adb shell appops set {packageName} PROJECT_MEDIA allow

https://github.com/user-attachments/assets/45703c79-6e73-4039-b6c0-5c9dc7164c8b

  
![screenshot_1748435661128_2](https://github.com/user-attachments/assets/c606d7f3-0e85-44af-828e-d62a64533981)
