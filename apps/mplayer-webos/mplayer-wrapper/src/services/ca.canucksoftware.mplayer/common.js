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

Array.prototype.jsonSort = function(field, reverse, primer) {
	if(this.length>0) {
		var lowercase = function(string) { return string.toLowerCase(); };
		reverse = (reverse) ? -1 : 1;
		if(!primer) {
			var type = typeof this[0][field];
			if(type == "number") {
				primer = parseFloat;
			} else if(type == "string") {
				primer = lowercase;
			}
		}
		this.sort(function(a, b) {
			a = a[field];
			b = b[field];
			if (typeof(primer) != 'undefined'){
				a = primer(a);
				b = primer(b);
			}
			if (a<b) return reverse * -1;
			if (a>b) return reverse * 1;
			return 0;
		});
	}
};

function getAppId(assistant) {
	var id = assistant.controller.message.applicationID();
	var index = id.indexOf(" ");
	if(index>-1) {
		id = id.substring(0, index);
	}
	return id;
};

function validForPrivate(assistant) {
	var appid = getAppId(assistant);
	return (appid=="" || appid=="ca.canucksoftware.mplayer");
}
