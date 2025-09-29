# ReadyChatAI SMS APP

<ul>
<li>Description:</li>

ReadyChatAI SMS is a compact Android app (Kotlin + Jetpack Compose) that demonstrates sending and receiving SMS messages with a clean Compose-based UI. It provides a masked phone input, a message composer, and a message list; incoming SMS are captured via a dynamic SmsReceiverManager and forwarded to the UI through a callback.


<li>Features:</li>

-> Compose UI for sending and receiving SMS, including a masked phone input field.

-> Dynamic SMS receiver wrapper (SmsReceiverManager) that parses multipart SMS and forwards (sender, body) via a callback.

-> sendMessage helper with runtime permission checks and optional delivery tracking via PendingIntents.


<li>Tech Stack:</li>

<div align="left">
  <img src="https://raw.githubusercontent.com/gilbarbara/logos/refs/heads/main/logos/android-icon.svg" height="45" width="42" alt="android logo"  />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/androidstudio/androidstudio-original.svg" height="40" alt="androidstudio logo"  />
  <img width="12" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/kotlin/kotlin-original.svg" height="40" alt="kotlin logo"  />
  <img width="12" />
  <img src="https://raw.githubusercontent.com/devicons/devicon/54cfe13ac10eaa1ef817a343ab0a9437eb3c2e08/icons/jetpackcompose/jetpackcompose-original.svg" height="40" alt="kotlin logo"  />
  <img width="12" />
  
</div>

###
icons/jetpackcompose/jetpackcompose-original-wordmark.svg

<li>Usage:</li>

1. Clone the repo in Android Studio.

2. Build and run on a device (or an emulator configured for SMS testing). The app requests SMS permissions at runtime and demonstrates sending/receiving flows.
   

</ul>


## Screenshots

|   |   |
|:--------------:|:---------------:|
| ![App Screenshot](https://photos.fife.usercontent.google.com/pw/AP1GczMetuNIg-AQXC16lZSwpYhMbnVzmU1PcPzolPg3aQs4EB2vOBxxnatw0A=w490-h878-s-no-gm?authuser=0) | ![App Screenshot](https://assets.zyrosite.com/cdn-cgi/image/format=auto,w=360,h=649,fit=crop,trim=0;12.277580071174377;0;0/mk3DXyWBRZCxzX6B/02_upscayl_3x_remacri-mk3DxxpRMKczOxXV.png) |
