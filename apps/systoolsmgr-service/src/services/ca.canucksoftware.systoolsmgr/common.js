function require(key) {
	return IMPORTS.require(key);
};

String.prototype.trim = function() {
	return this.replace(/^\s+|\s+$/g,"");
};

String.prototype.startsWith = function(str) {
	return (this.indexOf(str) == 0);
};

String.prototype.endsWith = function(str) {
	var lastIndex = this.lastIndexOf(str);
    return (lastIndex != -1) && (lastIndex + str.length == this.length);
};

function getAppId(assistant) {
	var id = assistant.controller.message.applicationID();
	var index = id.indexOf(" ");
	if(index>-1) {
		id = id.substring(0, index);
	}
	return id;
};
