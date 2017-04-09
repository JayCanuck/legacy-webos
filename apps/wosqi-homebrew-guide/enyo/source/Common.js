function doNothing() {};

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

Object.isEmpty = function(obj) {
	for(var prop in obj) {
        if(obj.hasOwnProperty(prop))
            return false;
    }
    return true;
};

Element.prototype.hasClass = function(className) {
	var pattern = new RegExp('(^|\\s)' + className + '(\\s|$)'); //use this regexp
	return pattern.test(this.className); //to check for the class
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

Function.prototype.bind = function(scope){
	return enyo.bind(scope, this);
};
