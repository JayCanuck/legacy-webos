function ErrorDialogAssistant(params) {
	this.message = params.message + "<br><br>" || "";
	this.error = params.error;
	this.controller = params.sceneAssistant.controller;
}

ErrorDialogAssistant.prototype.setup = function(widget) {
	this.widget = widget;
	this.controller.setupWidget("scrollErr", {mode: 'free'}, {}); 
	this.controller.get('errTitle').innerText += $L("Error");
	this.controller.get('message').innerHTML = this.message;
	this.controller.get('errorText').innerText = this.error;
	this.controller.get('okButton').innerText = $L("OK");
};


ErrorDialogAssistant.prototype.handleAccept = function(){
	this.widget.mojo.close();
};

ErrorDialogAssistant.prototype.activate = function(event) {
	this.handleAccept = this.handleAccept.bindAsEventListener(this);
	this.controller.listen("okButton", Mojo.Event.tap, this.handleAccept);
};


ErrorDialogAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("okButton", Mojo.Event.tap, this.handleAccept);
};

ErrorDialogAssistant.prototype.cleanup = function(event) {
};
