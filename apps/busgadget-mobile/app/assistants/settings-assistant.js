function SettingsAssistant(params) {
	this.controller = params.controller;
	this.callback = params.callback || Mojo.doNothing;
}

SettingsAssistant.prototype.setup = function(widget) {
	this.widget = widget;
	var refresh = Preferences.get("autoRefresh", true);
	//var amount = Preferences.get("maxTime", 2);
	this.controller.setupWidget("cbAutoRefresh",{},{value: refresh});
	//this.controller.setupWidget("pickTime", {label: "", min: 1, max: 5},{ value: amount});
};

SettingsAssistant.prototype.handleRefresh = function(event){
	Preferences.set("autoRefresh", event.value);
};

//SettingsAssistant.prototype.handleAmount = function(event){
//	Preferences.set("maxTime", event.value);
//};

SettingsAssistant.prototype.handleClose = function() {
	this.callback();
	this.widget.mojo.close();
};

SettingsAssistant.prototype.activate = function(event) {
	this.handleRefresh = this.handleRefresh.bindAsEventListener(this);
	//this.handleAmount = this.handleAmount.bindAsEventListener(this);
	this.handleClose = this.handleClose.bindAsEventListener(this);
	this.controller.listen("cbAutoRefresh", Mojo.Event.propertyChange, this.handleRefresh);
	//this.controller.listen("pickTime", Mojo.Event.propertyChange, this.handleAmount);
	this.controller.listen("closeButton", Mojo.Event.tap, this.handleClose);
};

SettingsAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("cbAutoRefresh", Mojo.Event.propertyChange, this.handleRefresh);
	//this.controller.stopListening("pickTime", Mojo.Event.propertyChange, this.handleAmount);
	this.controller.stopListening("closeButton", Mojo.Event.tap, this.handleClose);
};

SettingsAssistant.prototype.cleanup = function(event) {
};
