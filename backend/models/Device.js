const mongoose = require('mongoose');

const DeviceSchema = new mongoose.Schema({
  user: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  name: { type: String, required: true }, // e.g. "Living Room TV"
  deviceType: { type: String, enum: ['ir', 'wifi', 'esp32'], required: true },
  category: { type: String, enum: ['tv', 'ac', 'fan', 'smart_device', 'esp32_hub', 'projector'], required: true },
  brand: { type: String }, // optional e.g. "samsung"
  commands: { type: mongoose.Schema.Types.Mixed }, // flexible JSON for commands/IR codes
  endpoint: { type: String }, // For WiFi devices (e.g. IP or URL)
  esp32_id: { type: String } // To tie IR/WiFi devices to a specific ESP32 hub
});

module.exports = mongoose.model('Device', DeviceSchema);
