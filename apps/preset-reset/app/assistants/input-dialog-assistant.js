function InputDialogAssistant(params) {
	this.onAccept = params.onAccept;
	this.controller = params.sceneAssistant.controller;
	this.title = params.title;
	this.message = params.message;
	this.initValue = params.init;
}

InputDialogAssistant.prototype.setup = function(widget) {
	this.widget = widget;
	this.controller.get('title').innerText = this.controller.get('title').innerText + this.title;
	this.controller.get('message').innerText = this.message;
	this.controller.get('textBox').value = this.initValue;
}

InputDialogAssistant.prototype.handleDismiss = function() {
	this.widget.mojo.close();
}

InputDialogAssistant.prototype.handleAccept = function(){
	var result = this.controller.get('textBox').value;
	if (result != "") {
		this.onAccept(result);
		delete this.onAccept;
		this.widget.mojo.close();
	}
}

InputDialogAssistant.prototype.activate = function(event) {
	this.handleAccept = this.handleAccept.bindAsEventListener(this);
	this.handleDismiss = this.handleDismiss.bindAsEventListener(this);
	this.controller.listen("acceptButton", Mojo.Event.tap, this.handleAccept);
	this.controller.listen("dismissButton", Mojo.Event.tap, this.handleDismiss);
}


InputDialogAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("acceptButton", Mojo.Event.tap, this.handleAccept);
	this.controller.stopListening("dismissButton", Mojo.Event.tap, this.handleDismiss);
}

InputDialogAssistant.prototype.cleanup = function(event) {
	this.controller.stopListening("acceptButton", Mojo.Event.tap, this.handleAccept);
	this.controller.stopListening("dismissButton", Mojo.Event.tap, this.handleDismiss);
}
