function Problem3Assistant(params) {
	this.os = params.os || "Windows";
}

Problem3Assistant.prototype.setup = function() {
	var menuModel = {
    	visible: true,
    	items: [
			{label: "Help", command:"about"}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, menuModel);
	this.controller.setupWidget("btnDevMode", {}, {label: "Open DevMode app"});
	this.controller.get("windows").hide();
	this.controller.get("mac").hide();
	this.controller.get("ubuntu").hide();
	if(this.os=="Windows") {
		this.controller.get("windows").show();
	} else if(this.os=="Mac") {
		this.controller.get("mac").show();
	} else if(this.os=="Ubuntu") {
		this.controller.get("ubuntu").show();
	}
};

Problem3Assistant.prototype.activate = function(event) {
	this.devmode = this.devmode.bindAsEventListener(this);
	this.notSolved = this.notSolved.bindAsEventListener(this);
	this.controller.listen("btnDevMode", Mojo.Event.tap, this.devmode);
	this.controller.listen("notSolved", Mojo.Event.tap, this.notSolved);
};

Problem3Assistant.prototype.devmode = function(event) {
	this.controller.serviceRequest("palm://com.palm.applicationManager", {
		method: "launch",
		parameters: {
			id: "com.palm.app.devmodeswitcher"
		}
	});
};

Problem3Assistant.prototype.notSolved = function(event) {
	this.controller.stageController.pushScene("other");
};

Problem3Assistant.prototype.deactivate = function(event) {
	this.controller.stopListening("btnDevMode", Mojo.Event.tap, this.devmode);
	this.controller.stopListening("notSolved", Mojo.Event.tap, this.notSolved);
};

Problem3Assistant.prototype.cleanup = function(event) {

};
