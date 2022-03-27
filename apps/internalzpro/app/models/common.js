function Error(msg) {
	if(msg != undefined) {
		if(msg.length > 0) {
			msg = msg.replace(/\n/g, " ");
			Mojo.Controller.errorDialog($L(msg));
		}
	}
};

function MsgBox(scene, msg, title, onChoose) {
	scene.controller.showAlertDialog({
		onChoose: onChoose || Mojo.doNothing,
		title: title || $L("Information"),
		message: msg,
		choices: [ {label: $L("OK"),value: ""} ],
		allowHTMLMessage: true
	});
};

function getFileName(path) {
	var name = path;
	var index = -1;
	index = name.lastIndexOf("/");
	if(index > -1) {
		name = name.substring(index+1);
	}
	index = name.lastIndexOf("\\");
	if(index > -1) {
		name = name.substring(index+1);
	}
	return name;
};

function getFileExt(path) {
	var ext = ""; //test.abc
	var index = -1;
	index = path.lastIndexOf(".");
	if(index > 0 && path.length>index+1) { //to avoid extensionless hidden files
		ext = path.substring(index+1);
	}
	return ext;
};

function getFileDir(path) {
	var name = path;
	var index = -1;
	name = name.replace(/\\/g, "/");
	index = name.lastIndexOf("/");
	if(index > -1  && index != name.length-1) {
		name = name.substring(0, index);
	}
	return name;
};

String.prototype.trim = function() {
	return this.replace(/^\s+|\s+$/g,"");
};
String.prototype.ltrim = function() {
	return this.replace(/^\s+/,"");
};
String.prototype.rtrim = function() {
	return this.replace(/\s+$/,"");
};

Object.isEmpty = function(obj) {
	for(var prop in obj) {
        if(obj.hasOwnProperty(prop))
            return false;
    }
    return true;
};

function jsonArraySort(field, reverse, primer) {
	reverse = (reverse) ? -1 : 1;
	return function(a, b) {
		a = a[field];
		b = b[field];
		if (typeof(primer) != 'undefined'){
			a = primer(a);
			b = primer(b);
		}
		if (a<b) return reverse * -1;
		if (a>b) return reverse * 1;
		return 0;
	};
};

function lowercase(string) {
	return string.toLowerCase();
}

/*String.prototype.startsWith = function(str) {
	return (this.indexOf(str) == 0);
};
String.prototype.endsWith = function(str) {
	var lastIndex = this.lastIndexOf(str);
    return (lastIndex != -1) && (lastIndex + str.length == this.length);
};
Element.prototype.hasClass = function(className) {
	var pattern = new RegExp('(^|\\s)' + className + '(\\s|$)'); //use this regexp
	return pattern.test(this.className); //to check for the class
};*/

Function.prototype.subclassOf = function(SuperClass) {
	this.prototype = new SuperClass();
	this.prototype.constructor = this;
};
