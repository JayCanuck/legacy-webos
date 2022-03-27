function InputDialogAssistant(params) {
	this.onAccept = params.onAccept;
	this.controller = params.sceneAssistant.controller;
	this.title = params.title;
	this.message = params.message;
	this.initValue = params.init;
}

InputDialogAssistant.prototype.setup = function(widget) {
	this.widget = widget;
	this.controller.get('title').innerText += this.title;
	this.controller.get('message').innerText = this.message;
	this.controller.get('textBox').value = this.initValue;
	this.controller.get('acceptButton').innerText = $L("OK");
	this.controller.get('dismissButton').innerText = $L("Cancel");
};

InputDialogAssistant.prototype.handleFilter = function(event) {
	if(!this.isValidChar(event.keyCode)) {
		event.stop();
		this.controller.get('textBox').value = this.controller.get('textBox').value
				.replace(String.fromCharCode(event.keyCode), "");
	}
};

InputDialogAssistant.prototype.isValidChar = function(keycode) {
	//valid character range: 32-255
	//disallowed characters: \ / : * ? " < > |
	return (keycode>=32 && keycode<=255 && keycode!=92 && keycode!=47  && keycode!=58
			&& keycode!=42 && keycode!=63 && keycode!=34 && keycode!=60  && keycode!=62
			&& keycode!=124);
};

InputDialogAssistant.prototype.handleAccept = function(){
	var result = this.controller.get('textBox').value.trim();
	if (result != "") {
		this.onAccept(result);
		this.widget.mojo.close();
	}
};

InputDialogAssistant.prototype.handleDismiss = function() {
	this.widget.mojo.close();
};

InputDialogAssistant.prototype.activate = function(event) {
	this.handleFilter = this.handleFilter.bindAsEventListener(this);
	this.handleAccept = this.handleAccept.bindAsEventListener(this);
	this.handleDismiss = this.handleDismiss.bindAsEventListener(this);
	this.controller.listen("textBox", "keypress", this.handleFilter);
	this.controller.listen("acceptButton", Mojo.Event.tap, this.handleAccept);
	this.controller.listen("dismissButton", Mojo.Event.tap, this.handleDismiss);
};


InputDialogAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("textBox", "keypress", this.handleFilter);
	this.controller.stopListening("acceptButton", Mojo.Event.tap, this.handleAccept);
	this.controller.stopListening("dismissButton", Mojo.Event.tap, this.handleDismiss);
};

InputDialogAssistant.prototype.cleanup = function(event) {
};
