function SettingsAssistant(params) {
	this.controller = params.controller;
	this.callback = params.callback;
}

SettingsAssistant.prototype.setup = function(widget) {
	this.widget = widget;
	var metric = Preferences.get("useMetric", true);
	var amount = Preferences.get("numEntries", 25);
	this.controller.setupWidget("cbMetric",{},{value: metric});
	this.controller.setupWidget("pickNumber", {label: "", min: 15, max: 30},{ value: amount});
};

SettingsAssistant.prototype.handleMetric = function(event){
	Preferences.set("useMetric", event.value);
};

SettingsAssistant.prototype.handleAmount = function(event){
	Preferences.set("numEntries", event.value);
};

SettingsAssistant.prototype.handleClose = function() {
	this.callback();
	this.widget.mojo.close();
};

SettingsAssistant.prototype.activate = function(event) {
	this.handleMetric = this.handleMetric.bindAsEventListener(this);
	this.handleAmount = this.handleAmount.bindAsEventListener(this);
	this.handleClose = this.handleClose.bindAsEventListener(this);
	this.controller.listen("cbMetric", Mojo.Event.propertyChange, this.handleMetric);
	this.controller.listen("pickNumber", Mojo.Event.propertyChange, this.handleAmount);
	this.controller.listen("closeButton", Mojo.Event.tap, this.handleClose);
};

SettingsAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("cbMetric", Mojo.Event.propertyChange, this.handleMetric);
	this.controller.stopListening("pickNumber", Mojo.Event.propertyChange, this.handleAmount);
	this.controller.stopListening("closeButton", Mojo.Event.tap, this.handleClose);
};

SettingsAssistant.prototype.cleanup = function(event) {
};
