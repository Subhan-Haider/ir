# SmartHub AI – Universal Remote & Automation Platform

SmartHub AI is a full-stack, production-ready universal remote control system allowing you to control IR devices (TV, AC, Fan), WiFi devices, custom APIs, PCs, and ESP32-based IR blasters.

## Features
- **IR Remote System**: Utilizes Android ConsumerIrManager API for local IR transmission or an ESP32 for remote/extender transmission.
- **ESP32 Integration**: Send IR commands over WiFi. Great for controlling devices without a direct line-of-sight from your Android phone or from non-IR-capable phones.
- **REST API Backend**: Robust Express + MongoDB backend handling device synchronization and unified control interface.
- **Modern Android App**: Built with Kotlin, Retrofit, Coroutines, and MVVM architecture. Support for sending commands and adding virtual remotes.

## Project Structure
- `android/` - Android App Source Code (Kotlin, MVVM). Open the folder in Android Studio.
- `backend/` - Node.js + Express + MongoDB REST API.
- `firmware/SmartHub_ESP32/` - C++ / Arduino sketch for ESP32 IR Blaster.

## Setup
Please refer to [SETUP_GUIDE.md](SETUP_GUIDE.md) and [API_DOCS.md](API_DOCS.md) for detailed instructions on getting this platform running.

## Security
- API protected using JWT authentication.
- Device-level permission boundaries.
- Passwords hashed using bcryptjs.

## Future Roadmaps
- Web dashboard (React)
- AI Device Detection
- Voice Control (Google Assistant)
- Scene Automation Trigger Engine

## License
MIT
