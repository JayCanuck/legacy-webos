function MainAssistant() {}

MainAssistant.prototype.setup = function() {
	var menuModel = {
    	visible: true,
    	items: [
			{label: "Help", command:"about"}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, menuModel);
	this.useTrueNorth = Preferences.get("useTrueNorth", true);
	var type = 2;
	if(this.useTrueNorth) {
		type = 1;
	}
	this.controller.setupWidget("radioType", {
    	choices: [
			{label: "True North", value: 1},
			{label: "Magnetic North", value: 2}
      	]},{value: type}
	);
};

MainAssistant.prototype.activate = function(event) {
	this.controller.stageController.window.PalmSystem.setWindowProperties({
		blockScreenTimeout: true
    });
	this.compassHandler = this.compassHandler.bindAsEventListener(this);
	this.typeHandler = this.typeHandler.bindAsEventListener(this);
	this.controller.listen(document, "compass", this.compassHandler);
	this.controller.listen("radioType", Mojo.Event.propertyChange, this.typeHandler);
};

MainAssistant.prototype.compassHandler = function(event) {
	var currHeading = undefined;
	var headingStr = "";
	if(this.useTrueNorth) {
		currHeading = event.trueHeading;
		headingStr = "True Heading: ";
	} else {
		currHeading = event.magHeading;
		headingStr = "Magnetic Heading: ";
	}
	if(currHeading!=undefined) {
		if(currHeading > 360) {
			currHeading = currHeading - 360;
		}
		headingStr += parseInt(currHeading) + "&deg;";
		this.controller.get("degrees").innerHTML = headingStr;
		this.controller.get("needle").style["-webkit-transform"] =
				"rotate3d(0,0,1,-" + currHeading + "deg)";
	}
};

MainAssistant.prototype.typeHandler = function(event){
	if(event.value == 1) {
		this.useTrueNorth = true;
		Preferences.set("useTrueNorth", this.useTrueNorth);
	} else {
		this.useTrueNorth = false;
		Preferences.set("useTrueNorth", this.useTrueNorth);
	}
};

MainAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening(document, "compass", this.compassHandler);
	this.controller.stopListening("radioType", Mojo.Event.propertyChange, this.typeHandler);
};

MainAssistant.prototype.cleanup = function(event) {};
