Number.prototype.toRad = function() {
	return this * Math.PI / 180;
};

Number.prototype.toDeg = function() {
	return this * 180 / Math.PI;
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

