function Problem4Assistant() {

}

Problem4Assistant.prototype.setup = function() {
	var menuModel = {
    	visible: true,
    	items: [
			{label: "Help", command:"about"}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, menuModel);
};

Problem4Assistant.prototype.activate = function(event) {
	this.notSolved = this.notSolved.bindAsEventListener(this);
	this.controller.listen("notSolved", Mojo.Event.tap, this.notSolved);
};

Problem4Assistant.prototype.notSolved = function(event) {
	this.controller.stageController.pushScene("other");
};

Problem4Assistant.prototype.deactivate = function(event) {
	this.controller.stopListening("notSolved", Mojo.Event.tap, this.notSolved);
};

Problem4Assistant.prototype.cleanup = function(event) {

};
