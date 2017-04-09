function TroubleshooterAssistant(params) {
	this.os = params.os || "Windows";
}

TroubleshooterAssistant.prototype.setup = function() {
	var menuModel = {
    	visible: true,
    	items: [
			{label: "Help", command:"about"}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, menuModel);
};

TroubleshooterAssistant.prototype.activate = function(event) {
	this.problem1 = this.problem1.bindAsEventListener(this);
	this.problem2 = this.problem2.bindAsEventListener(this);
	this.problem3 = this.problem3.bindAsEventListener(this);
	this.problem4 = this.problem4.bindAsEventListener(this);
	this.problem5 = this.problem5.bindAsEventListener(this);
	this.other = this.other.bindAsEventListener(this);
	this.controller.listen("problem1", Mojo.Event.tap, this.problem1);
	this.controller.listen("problem2", Mojo.Event.tap, this.problem2);
	this.controller.listen("problem3", Mojo.Event.tap, this.problem3);
	this.controller.listen("problem4", Mojo.Event.tap, this.problem4);
	this.controller.listen("problem5", Mojo.Event.tap, this.problem5);
	this.controller.listen("other", Mojo.Event.tap, this.other);
};

TroubleshooterAssistant.prototype.problem1 = function(event) {
	this.controller.stageController.pushScene("problem1", {os:this.os});
};

TroubleshooterAssistant.prototype.problem2 = function(event) {
	this.controller.stageController.pushScene("problem2", {os:this.os});
};

TroubleshooterAssistant.prototype.problem3 = function(event) {
	this.controller.stageController.pushScene("problem3", {os:this.os});
};

TroubleshooterAssistant.prototype.problem4 = function(event) {
	this.controller.stageController.pushScene("problem4");
};

TroubleshooterAssistant.prototype.problem5 = function(event) {
	this.controller.stageController.pushScene("problem5");
};

TroubleshooterAssistant.prototype.other = function(event) {
	this.controller.stageController.pushScene("other");
};

TroubleshooterAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("problem1", Mojo.Event.tap, this.problem1);
	this.controller.stopListening("problem2", Mojo.Event.tap, this.problem2);
	this.controller.stopListening("problem3", Mojo.Event.tap, this.problem3);
	this.controller.stopListening("problem4", Mojo.Event.tap, this.problem4);
	this.controller.stopListening("problem5", Mojo.Event.tap, this.problem5);
	this.controller.stopListening("other", Mojo.Event.tap, this.other);
};

TroubleshooterAssistant.prototype.cleanup = function(event) {
	
};
