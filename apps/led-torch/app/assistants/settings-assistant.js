function SettingsAssistant(params) {
	this.controller = params.controller;
}

SettingsAssistant.prototype.setup = function(widget) {
	this.widget = widget;
	var enterVal = Preferences.get("entering", false);
	var exitVal = Preferences.get("exiting", false);
	this.controller.setupWidget("cbEnter",{},{value: enterVal});
	this.controller.setupWidget("cbExit",{},{value: exitVal});
}

SettingsAssistant.prototype.handleEnterChange = function(event){
	Preferences.set("entering", event.value);
}

SettingsAssistant.prototype.handleExitChange = function(event){
	Preferences.set("exiting", event.value);
}

SettingsAssistant.prototype.handleClose = function() {
	this.widget.mojo.close();
}

SettingsAssistant.prototype.activate = function(event) {
	this.handleEnterChange = this.handleEnterChange.bindAsEventListener(this);
	this.handleExitChange = this.handleExitChange.bindAsEventListener(this);
	this.handleClose = this.handleClose.bindAsEventListener(this);
	this.controller.listen("cbEnter", Mojo.Event.propertyChange, this.handleEnterChange);
	this.controller.listen("cbExit", Mojo.Event.propertyChange, this.handleExitChange);
	this.controller.listen("closeButton", Mojo.Event.tap, this.handleClose);
}

SettingsAssistant.prototype.deactivate = function(event) {
	/*this.controller.stopListening("cbEnter", Mojo.Event.propertyChange, this.handleEnterChange);
	this.controller.stopListening("cbExit", Mojo.Event.propertyChange, this.handleExitChange);
	this.controller.stopListening("closeButton", Mojo.Event.tap, this.handleClose);*/
}

SettingsAssistant.prototype.cleanup = function(event) {
}
