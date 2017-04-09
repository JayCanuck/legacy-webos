function HeadersAssistant() {

}

HeadersAssistant.prototype.setup = function() {
	var menuModel = {
    	visible: true,
    	items: [
			{label: "Help", command:"about"}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, menuModel);
};

HeadersAssistant.prototype.activate = function(event) {
	this.homebrew = this.homebrew.bindAsEventListener(this);
	this.settingup = this.settingup.bindAsEventListener(this);
	this.installing = this.installing.bindAsEventListener(this);
	this.local = this.local.bindAsEventListener(this);
	this.advanced = this.advanced.bindAsEventListener(this);
	this.troubleshooter = this.troubleshooter.bindAsEventListener(this);
	this.controller.listen("homebrew", Mojo.Event.tap, this.homebrew);
	this.controller.listen("settingup", Mojo.Event.tap, this.settingup);
	this.controller.listen("installing", Mojo.Event.tap, this.installing);
	this.controller.listen("local", Mojo.Event.tap, this.local);
	this.controller.listen("advanced", Mojo.Event.tap, this.advanced);
	this.controller.listen("troubleshooter", Mojo.Event.tap, this.troubleshooter);
};

HeadersAssistant.prototype.homebrew = function(event) {
	this.controller.stageController.pushScene("homebrew");
};

HeadersAssistant.prototype.settingup = function(event) {
	this.controller.stageController.pushScene("settingup");
};

HeadersAssistant.prototype.installing = function(event) {
	this.controller.stageController.pushScene("installing");
};

HeadersAssistant.prototype.local = function(event) {
	this.controller.stageController.pushScene("local");
};

HeadersAssistant.prototype.advanced = function(event) {
	this.controller.stageController.pushScene("advanced");
};

HeadersAssistant.prototype.troubleshooter = function(event) {
	this.controller.showDialog({
		template: "chooser/chooser-popup",
		assistant: new ChooserAssistant({
			controller: this.controller,
			callback:function(os) {
				this.controller.stageController.pushScene("troubleshooter", {os:os});
			}.bind(this)})
		}
	);
};

HeadersAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("homebrew", Mojo.Event.tap, this.homebrew);
	this.controller.stopListening("settingup", Mojo.Event.tap, this.settingup);
	this.controller.stopListening("installing", Mojo.Event.tap, this.installing);
	this.controller.stopListening("local", Mojo.Event.tap, this.local);
	this.controller.stopListening("advanced", Mojo.Event.tap, this.advanced);
	this.controller.stopListening("troubleshooter", Mojo.Event.tap, this.troubleshooter);
};

HeadersAssistant.prototype.cleanup = function(event) {
	
};
