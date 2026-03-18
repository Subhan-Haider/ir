# API Documentation

## Auth Endpoints

### 1. `POST /api/auth/register`
Register a new user to the SmartHub system.
**Body:**
```json
{
  "username": "demo",
  "email": "demo@demo.com",
  "password": "password123"
}
```

### 2. `POST /api/auth/login`
**Body:**
```json
{
  "email": "demo@demo.com",
  "password": "password123"
}
```
**Returns:** `{"token": "eyJ..."}`

### 3. `GET /api/auth/me`
Headers: `x-auth-token: <your_token>`

---

## Device Endpoints

### 1. `GET /api/devices`
Get all devices configured by the logged-in user.
Headers: `x-auth-token: <your_token>`
**Returns:** Array of device JSONs.

### 2. `POST /api/devices`
Add a new device/remote.
**Body:**
```json
{
  "name": "Living Room TV",
  "deviceType": "ir",
  "category": "tv",
  "brand": "samsung",
  "commands": {
    "power": [9000, 4500, 560, 560],
    "volume_up": [9000, 4500, 560, 1690]
  },
  "esp32_id": "http://192.168.1.100"
}
```

### 3. `PUT /api/devices/:id`
Updates a device item.

### 4. `DELETE /api/devices/:id`
Removes a device item.

---

## ESP32 Local API (Running on ESP32 Port 80)

### 1. `GET /`
Check status.
**Returns:** `{"status":"ok","device":"SmartHub_ESP32"}`

### 2. `POST /api/ir/send`
Send an IR command. Currently supports sending RAW timing data arrays.
**Body:**
```json
{
  "protocol": "RAW",
  "rawData": [9000, 4500, 560, 560, 560, 1690, 560]
}
```
OR NEC Code:
```json
{
  "protocol": "NEC",
  "code": "0x20DF10EF",
  "bits": 32
}
```
