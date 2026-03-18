# Setup Guide

## 1. Backend API Set Up
The backend provides User Authentication and Device Storage.

### Prerequisites
- Node.js (v16+)
- MongoDB (Running locally on 27017 or Cloud Atlas URI)

### Steps
1. Navigate to `backend/`.
2. Run `npm install` to download dependencies.
3. Edit `.env` to configure your `MONGO_URI`, `PORT`, and `JWT_SECRET`.
4. Run `npm start` (or `node server.js`).
5. Your backend is now running and ready to handle devices and users.

---

## 2. ESP32 Firmware Set Up
The ESP32 acts as a Smart IR Transmitter over WiFi. 

### Prerequisites
- Arduino IDE with ESP32 board manager installed.
- ArduinoJson Library (v6)
- IRremoteESP8266 Library

### Steps
1. Open `firmware/SmartHub_ESP32/SmartHub_ESP32.ino` in Arduino IDE.
2. Change the `ssid` and `password` variables to your WiFi credentials.
3. Connect an IR LED to GPIO 4 (with a suitable resistor and possibly NPN transistor for range).
4. Flash the code to your ESP32 board.
5. Once running, open the Serial Monitor (115200 baud) to find the ESP32's assigned IP Address.

---

## 3. Android App Set Up
The central control hub.

### Prerequisites
- Android Studio Ladybug or later.

### Steps
1. Open Android Studio.
2. Select **Open an existing Project**.
3. Choose the `android/` folder.
4. Let Gradle sync and download dependencies.
5. In `android/app/src/main/java/com/smarthub/api/ApiClient.kt`, modify `BASE_URL` to point to your backend IP Address. Ensure it's reachable from the phone (e.g., your computer's local network IP).
6. Build and Run on a physical device.
7. To try local IR features, ensure your Android device has a built-in IR Blaster. Otherwise, setup the ESP32 and configure its IP to use WiFi control.
