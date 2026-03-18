const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const Device = require('../models/Device');

// @route   GET api/devices
// @desc    Get all user devices
// @access  Private
router.get('/', auth, async (req, res) => {
  try {
    const devices = await Device.find({ user: req.user.id });
    res.json(devices);
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server error');
  }
});

// @route   POST api/devices
// @desc    Add new device
// @access  Private
router.post('/', auth, async (req, res) => {
  const { name, deviceType, category, brand, commands, endpoint, esp32_id } = req.body;

  try {
    const newDevice = new Device({
      user: req.user.id,
      name, deviceType, category, brand, commands, endpoint, esp32_id
    });

    const device = await newDevice.save();
    res.json(device);
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server error');
  }
});

// @route   PUT api/devices/:id
// @desc    Update device
// @access  Private
router.put('/:id', auth, async (req, res) => {
  const { name, deviceType, category, brand, commands, endpoint, esp32_id } = req.body;

  const deviceFields = {};
  if (name) deviceFields.name = name;
  if (deviceType) deviceFields.deviceType = deviceType;
  if (category) deviceFields.category = category;
  if (brand) deviceFields.brand = brand;
  if (commands) deviceFields.commands = commands;
  if (endpoint) deviceFields.endpoint = endpoint;
  if (esp32_id) deviceFields.esp32_id = esp32_id;

  try {
    let device = await Device.findById(req.params.id);
    if (!device) return res.status(404).json({ msg: 'Device not found' });

    // Make sure user owns device
    if (device.user.toString() !== req.user.id) {
      return res.status(401).json({ msg: 'Not authorized' });
    }

    device = await Device.findByIdAndUpdate(
      req.params.id,
      { $set: deviceFields },
      { new: true }
    );

    res.json(device);
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server error');
  }
});

// @route   DELETE api/devices/:id
// @desc    Delete device
// @access  Private
router.delete('/:id', auth, async (req, res) => {
  try {
    let device = await Device.findById(req.params.id);
    if (!device) return res.status(404).json({ msg: 'Device not found' });

    // Make sure user owns device
    if (device.user.toString() !== req.user.id) {
      return res.status(401).json({ msg: 'Not authorized' });
    }

    await Device.findByIdAndRemove(req.params.id);
    res.json({ msg: 'Device removed' });
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Server error');
  }
});

module.exports = router;
