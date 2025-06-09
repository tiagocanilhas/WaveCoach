// app/metro.config.js
const { getDefaultConfig } = require('expo/metro-config')
const path = require('path')

const projectRoot = __dirname;
const services = path.resolve(projectRoot, '../services')
const utils = path.resolve(projectRoot, '../utils')


const config = getDefaultConfig(projectRoot)

config.watchFolders = [services, utils]

config.resolver.nodeModulesPaths = [
  path.join(projectRoot, 'node_modules'),
  path.join(services, 'node_modules'),
  path.join(utils, 'node_modules'),
];

module.exports = config
