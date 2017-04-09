function SettingupAssistant() {

}

SettingupAssistant.prototype.setup = function() {
	var menuModel = {
    	visible: true,
    	items: [
			{label: "Help", command:"about"}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, menuModel);
	this.controller.setupWidget("drawerStage1", {unstyled: true}, this.drawer1 = {open: true});
	this.controller.setupWidget("drawerStage2", {unstyled: true}, this.drawer2 = {open: true});
	this.controller.setupWidget("btnDevMode", {}, {label: "Tap Here To Open DevMode"});
};

SettingupAssistant.prototype.activate = function(event) {
	this.stage1 = this.stage1.bindAsEventListener(this);
	this.stage2 = this.stage2.bindAsEventListener(this);
	this.devmode = this.devmode.bindAsEventListener(this);
	this.controller.listen("stage1", Mojo.Event.tap, this.stage1);
	this.controller.listen("stage2", Mojo.Event.tap, this.stage2);
	this.controller.listen("btnDevMode", Mojo.Event.tap, this.devmode);
};

SettingupAssistant.prototype.stage1 = function(event) {
	this.controller.get("drawerStage1").mojo.toggleState();
	this.updateArrow("arrowStage1", this.drawer1.open);
};

SettingupAssistant.prototype.stage2 = function(event) {
	this.controller.get("drawerStage2").mojo.toggleState();
	this.updateArrow("arrowStage2", this.drawer2.open);
};

SettingupAssistant.prototype.devmode = function(event) {
	this.controller.serviceRequest("palm://com.palm.applicationManager", {
		method: "launch",
		parameters: {
			id: "com.palm.app.devmodeswitcher"
		}
	});
};

SettingupAssistant.prototype.updateArrow = function(id, isOpen) {
	var element = this.controller.get(id);
	if(isOpen) {
		element.removeClassName("palm-arrow-closed");
		element.addClassName("palm-arrow-expanded");
	} else {
		element.removeClassName("palm-arrow-expanded");
		element.addClassName("palm-arrow-closed");
	}
};

SettingupAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("stage1", Mojo.Event.tap, this.stage1);
	this.controller.stopListening("stage2", Mojo.Event.tap, this.stage2);
	this.controller.stopListening("btnDevMode", Mojo.Event.tap, this.devmode);
};

SettingupAssistant.prototype.cleanup = function(event) {

};
