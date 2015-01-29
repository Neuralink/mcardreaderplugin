var SerialPrinterPlugin = function() {};

SerialPrinterPlugin.prototype.doprint = function(action,args,success, fail) {
    cordova.exec(success, fail, "SerialPrinterPlugin",action, JSON.parse(args));
};

var SerialPrinterPlugin = new SerialPrinterPlugin();
module.exports = SerialPrinterPlugin;