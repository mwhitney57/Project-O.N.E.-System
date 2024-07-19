<p align="center">
    <img align="center" src="https://raw.githubusercontent.com/mwhitney57/Project-O.N.E.-System/main/src/main/java/dev/mwhitney/images/openUnlocked.png" style="width: 128px; height: 128px;">
    <br>
    <h1 align="center">Project O.N.E. - System</h1>
</p>
<p align="center">
    The Java-based local system for Project O.N.E. which controls the physical components while communicating with other project subsystems.
    <br><br>
    <a target="_blank" href="https://github.com/mwhitney57/Project-O.N.E."><img src="https://img.shields.io/badge/Project%20O.N.E.-3D556B?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAAAFVBMVEUAAAArO0rA6+s0SVw5T2M9VWv///8cYhYEAAAAAXRSTlMAQObYZgAAAFBJREFUGNOFj0EKwEAMAnU0//9yDy3bZXuoOQhDEJV8a3OwSYMBy9iEtgR7AaYESzIOhRlKJJl8QJnnmgOQv48tdIYV6g8wocNb7Kh+jjvnXykdAi0mh4iNAAAAAElFTkSuQmCC" alt="A Project O.N.E. Subproject"></a>
    <img src="https://img.shields.io/badge/designed for-windows-blue?style=flat&logo=windows" alt="Designed for and Tested on Windows">
    <img src="https://img.shields.io/badge/version-0.8.4-blue" alt="System Application v0.8.4 (WIP)">
    <img src="https://img.shields.io/badge/language-java-F58219?logo=oracle" alt="Written in Java">
    <a target="_blank" href="https://github.com/mwhitney57/Project-O.N.E.-System/blob/main/LICENSE"><img src="https://img.shields.io/badge/license-GPL%203.0-yellow" alt="GPL License v3.0"></a>
</p>

### Table of Contents
- [Description](https://github.com/mwhitney57/Project-O.N.E.-System?tab=readme-ov-file#-description)
- [Features](https://github.com/mwhitney57/Project-O.N.E.-System?tab=readme-ov-file#-features)
- [Images](https://github.com/mwhitney57/Project-O.N.E.-System?tab=readme-ov-file#-images)
- [Libraries](https://github.com/mwhitney57/Project-O.N.E.-System?tab=readme-ov-file#-libraries)
- [Additional Information](https://github.com/mwhitney57/Project-O.N.E.-System?tab=readme-ov-file#%E2%84%B9%EF%B8%8F-additional-information)

### 📃 Description
This application is a part of  __Project O.N.E.__

The local system for Project O.N.E.
The system controls the physical components, such as the door lock, fingerprint sensor, and the external touchscreen display, while communicating with other project subsystems.

### ✨ Features
- Directly connects to fingerprint sensor and relay hardware to control authentication and door lock/unlocking.
- Two-tiered authentication system coupled with an optional `Emergency Use`<sup>1</sup> feature, allowing certain users to authenticate and gain access in emergency situations.
- Fully capable of being remotely controlled via connection to the [Project O.N.E. Server](https://github.com/mwhitney57/Project-O.N.E.-Server).
- Microphone and speaker for two-way audio communication and sound features.
- Designed for easy use and full compatibility with a touch-capable display.

### 📸 Images
- Home (System Unlocked)

>![SystemImage_1 (Main - Unlocked)](https://github.com/user-attachments/assets/ac386d4c-d238-4230-a9b7-709bb4838be0)

- Home (System Locked)

>![SystemImage_2 (Main - Locked)](https://github.com/user-attachments/assets/f83c54d5-6f19-421f-9c02-de73f37c33d9)

- Home (Awaiting Unlock Authentication)

>![SystemImage_3 (Main - Locked - Opening)](https://github.com/user-attachments/assets/2a5c6ef5-80ba-47f8-80aa-e54f18bac6b3)

- Settings

>![SystemImage_4 (Settings Main)](https://github.com/user-attachments/assets/400d5250-5279-4466-92eb-b18569cb3266)

- Fingerprint Settings

>![SystemImage_5 (Settings Fingerprint)](https://github.com/user-attachments/assets/9809048c-ea42-470a-9f9d-deb0dbabf41f)

- Changing Settings for a Registered Fingerprint

>![SystemImage_6 (Settings Fingerprint Customization)](https://github.com/user-attachments/assets/19fb1350-6119-4630-a405-b6a52e100913)

- Home (System Disabled)

>![SystemImage_7 (Main - Disabled)](https://github.com/user-attachments/assets/806cbafa-7e93-4504-ba18-3ea1a25fbfd5)

__The key components in the GUI break down as follows:__
- ✅ Home Screen - Open
    - Unlocks the door by retracting the solenoid, allowing for the door to be opened. However, if the system is locked, the user will be asked to authenticate themselves via a fingerprint they have registered. Administrators can do this infinitely when the system is locked. However, standard registered users are only given, at maximum, a single `Emergency Use`<sup>1</sup>. Once unlocked, the door may be opened within 3 seconds, at which point the solenoid retracts and locks the door.
- 🔒 Home Screen - System Lock/Unlock
    - Locks or unlocks the Project O.N.E. system. If the system is locked, biometric identity verification is required each time the user wishes to unlock and open the door. An unlocked system requires no identity verification, just a press of the open button. Changing the system state between `Locked` and `Unlocked` must be done by an Administrator, authenticated via their fingerprint.
- ⚙️ Home Screen - Settings
    - Enters the Settings panel. Access to the Settings and its sub-screens is restricted to Administrators who must authenticate with their fingerprint. Normal registered users cannot enter this screen.
- 👆 Settings Screen - Fingerprint Settings
    - Enters the `Fingerprint Settings` subsection of the settings.
- ⚙️ Settings Screen - Manual Unlocks Disable/Enable
    - Disables or Enables Manual Unlocks. When Manual Unlocks are disabled, the `Open Button` is disabled entirely until Manual Unlocks are enabled again. Remotely changing the state of the system, including remotely unlocking the door, remains possible.
- ❌ Settings Screen - Exit
    - Exits the Project O.N.E. system interface, closing it entirely.
- 👆 Fingerprint Settings Screen - Register Fingerprint
    - Begins the fingerprint registration process. The user will be asked to place their desired finger on the fingerprint scanner. After the first scan, they will be prompted to remove the finger and place it one more time for a final scan. After both scans are complete, the fingerprint will be registered and appear in the `Fingerprint List`.
- 📋 Fingerprint Settings Screen - Fingerprint List
    - A list of all currently-registered fingerprints in the Project O.N.E. system. The list offers a simple view of fingerprints, their ID numbers, privilege state, remaining emergency uses, and nickname. The user can scroll through the list with a swipe of their finger, and they can select a fingerprint to later `Edit` or `Remove` by tapping on it.
- ✏️ Fingerprint Settings Screen - Edit
    - Edits the selected fingerprint in the `Fingerprint List`. A pop-up<sup>2</sup> is displayed which allows the user three options at maximum. The fingerprint may have Administrator privileges given or taken away. It may be given a nickname so that it can be easily recognized. Finally, the user can easily reset the emergency uses of the fingerprint.
- ❌ Fingerprint Settings Screen - Remove
    - Removes the selected fingerprint from the `Fingerprint List` and the entire system. A confirmation pop-up<sup>2</sup> is displayed to the user to ensure that this action was not performed by mistake.

<sup>1</sup> An <code>Emergency Use</code> is a single-use access token optionally available to registered users. A registered user can spend their <code>Emergency Use</code> when the <b>system is locked</b> in case of a real-life emergency. However, <code>Emergency Uses</code> do not refresh or generate automatically. The user must have their <code>Emergency Uses</code> reset by an Administrator. Therefore, improper utilization can result in a loss of emergency access privileges, at the Administrator's discretion.
<br/>
<sup>2</sup> Pop-up dialogs are used in multiple areas across the system's user interface. Clicking anywhere outside of a pop-up window will close it.
This helps to keep the pop-ups free of clutter while keeping the interface intuitive, easy-to-use, and touchscreen compatible.

Please note that the images provided above may not be representative of the current product, as they may not be updated with each new version.

*For more information regarding how this application interfaces with the rest of the project, as well as pictures of the deployed system, please reference the primary [Project O.N.E.](https://github.com/mwhitney57/Project-O.N.E.) repository.*

### 📖 Libraries
- `slf4j` @ <a target="_blank" href="https://www.slf4j.org/index.html">https://www.slf4j.org/index.html</a>
    - Licensed under the <a target="_blank" href="https://www.slf4j.org/license.html">MIT License</a>
    - No changes to library's source code.
    
For connecting to and communicating with the fingerprint sensor and relay:
- `java-fingerprint-sensor` @ <a target="_blank" href="https://github.com/milan-fabian/java-fingerprint-sensor">https://github.com/milan-fabian/java-fingerprint-sensor</a>
    - Licensed under <a target="_blank" href="https://github.com/mwhitney57/Project-O.N.E.-System/blob/main/LICENSE_Apache">Apache 2.0</a>
    - No changes to library's source code.
    - Packaged within repository source code folders @ sk.mimac.*
- `nrjavaserial` @ <a target="_blank" href="https://github.com/NeuronRobotics/nrjavaserial">https://github.com/NeuronRobotics/nrjavaserial</a>
    - Licensed under <a target="_blank" href="https://github.com/NeuronRobotics/nrjavaserial/blob/master/LICENSE">LGPL v2.1</a>
    - No changes to library's source code.
- `pi4j` @ <a target="_blank" href="https://github.com/Pi4J/pi4j-v2">https://github.com/Pi4J/pi4j-v2</a>
    - Licensed under <a target="_blank" href="https://github.com/mwhitney57/Project-O.N.E.-System/blob/main/LICENSE_Apache">Apache 2.0</a>
    - No changes to library's source code.
    
For connecting to and communicating with the Project O.N.E. Server:
- `nv-websocket-client` @ <a target="_blank" href="https://github.com/TakahikoKawasaki/nv-websocket-client">https://github.com/TakahikoKawasaki/nv-websocket-client</a>
    - Licensed under <a target="_blank" href="https://github.com/mwhitney57/Project-O.N.E.-System/blob/main/LICENSE_Apache">Apache 2.0</a>
    - No changes to library's source code.

### ℹ️ Additional Information
The application connects to the Project O.N.E. server using an authentication token.
The token is pulled from the system environment variable `PROJECT_ONE_SYSTEM`.
