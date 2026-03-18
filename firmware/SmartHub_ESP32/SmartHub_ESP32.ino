#include <WiFi.h>
#include <WebServer.h>
#include <ArduinoJson.h>
#include <IRremoteESP8266.h>
#include <IRsend.h>

const char* ssid = "YOUR_WIFI_SSID";
const char* password = "YOUR_WIFI_PASSWORD";

WebServer server(80);

// IR LED connected to GPIO 4
const uint16_t kIrLed = 4;
IRsend irsend(kIrLed);

void setup() {
  Serial.begin(115200);
  irsend.begin();

  WiFi.begin(ssid, password);
  Serial.print("Connecting to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nConnected to WiFi!");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  server.on("/", HTTP_GET, []() {
    server.send(200, "application/json", "{\"status\":\"ok\",\"device\":\"SmartHub_ESP32\"}");
  });

  server.on("/api/ir/send", HTTP_POST, handleSendIR);

  server.begin();
  Serial.println("HTTP server started");
}

void loop() {
  server.handleClient();
}

void handleSendIR() {
  if (server.hasArg("plain") == false) {
    server.send(400, "application/json", "{\"error\":\"Body not received\"}");
    return;
  }

  String body = server.arg("plain");
  StaticJsonDocument<1024> doc;
  DeserializationError error = deserializeJson(doc, body);

  if (error) {
    server.send(400, "application/json", "{\"error\":\"Invalid JSON\"}");
    return;
  }

  // Example expected JSON:
  // {
  //   "protocol": "NEC", // Optional, can support multiple standard protocols
  //   "code": "0x20DF10EF", // Hex string
  //   "bits": 32,
  //   "rawData": [9000, 4500, 560, 560, ...] // Used if raw
  // }
  
  if (doc.containsKey("rawData")) {
    JsonArray rawDataJson = doc["rawData"];
    uint16_t size = rawDataJson.size();
    uint16_t rawData[size];
    for(int i = 0; i < size; i++) {
      rawData[i] = rawDataJson[i].as<uint16_t>();
    }
    // sendRaw requires array, size, hz
    irsend.sendRaw(rawData, size, 38);
    server.send(200, "application/json", "{\"status\":\"success\", \"mode\":\"raw\"}");
  } else if (doc.containsKey("code")) {
    // Basic NEC sending example fallback
    uint32_t code = strtoul(doc["code"].as<const char*>(), NULL, 16);
    int bits = doc["bits"] | 32;
    irsend.sendNEC(code, bits);
    server.send(200, "application/json", "{\"status\":\"success\", \"mode\":\"nec\"}");
  } else {
    server.send(400, "application/json", "{\"error\":\"Missing rawData or code\"}");
  }
}
