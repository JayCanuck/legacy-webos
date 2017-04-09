function ChooserAssistant(params) {
	this.controller = params.controller;
	this.callback = params.callback || Mojo.doNothing;
}

ChooserAssistant.prototype.setup = function(widget) {
	this.widget = widget;
	this.controller.setupWidget("rChooser", {
		label:"OS",
		choices: [
          {label: "Windows", value: "Windows"},
          {label: "Mac", value: "Mac"},
          {label: "Ubuntu", value: "Ubuntu"}
		]}, this.chooserModel = {value: "Windows"}
	); 
};

ChooserAssistant.prototype.activate = function(event) {
	this.handleClose = this.handleClose.bindAsEventListener(this);
	this.controller.listen("closeButton", Mojo.Event.tap, this.handleClose);
};

ChooserAssistant.prototype.handleClose = function() {
	this.callback(this.chooserModel.value);
	this.widget.mojo.close();
};

ChooserAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("cbAutoRefresh", Mojo.Event.propertyChange, this.handleRefresh);
	//this.controller.stopListening("pickTime", Mojo.Event.propertyChange, this.handleAmount);
	this.controller.stopListening("closeButton", Mojo.Event.tap, this.handleClose);
};

ChooserAssistant.prototype.cleanup = function(event) {
};
