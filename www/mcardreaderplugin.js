var MCardReaderPlugin = function() {};

MCardReaderPlugin.prototype.doread = function(action,args,success, fail) {
    cordova.exec(success, fail, "MCardReaderPlugin",action, args);
};

var MCardReaderPlugin = new MCardReaderPlugin();
module.exports = MCardReaderPlugin;