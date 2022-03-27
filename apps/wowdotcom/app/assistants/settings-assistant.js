function SettingsAssistant() {
}

SettingsAssistant.prototype.setup = function() {
	this.services = new Services();
	this.controller.setupWidget("selCheck",
		{
			choices: [
				{label: "5 min", value: "00:05:00"},
				{label: "15 min", value: "00:15:00"},
				{label: "30 min", value: "00:30:00"},
				{label: "1 hour", value: "01:00:00"},
				{label: "3 hours", value: "03:00:00"},
				{label: "1 day", value: "24:00:00"},
				{label: "Never", value: "00:00:00"}
			],
			labelPlacement:Mojo.Widget.labelPlacementLeft
		},
		{
        	value: Preferences.get("checkTime", "00:30:00")
        }
    );
	
	this.controller.setupWidget("selType",
		{
			choices: [
				{label:"Banner", value:"banner"},
				{label:"Dashboard", value:"dashboard"},
			],
			labelPlacement:Mojo.Widget.labelPlacementLeft
		},
		{
        	value: Preferences.get("type", "dashboard")
        }
    );
};

SettingsAssistant.prototype.activate = function(event) {
	this.handleCheck = this.handleCheck.bindAsEventListener(this);
	this.handleType = this.handleType.bindAsEventListener(this);
	this.controller.listen("selCheck", Mojo.Event.propertyChange, this.handleCheck);
	this.controller.listen("selType", Mojo.Event.propertyChange, this.handleType);
};

SettingsAssistant.prototype.handleCheck = function(event) {
	this.newCheck = event.value;
	this.services.clearBackgroundAction(function() {
		Preferences.set("checkTime", this.newCheck);
		if(this.newCheck != "00:00:00") {
			this.services.setBackgroundActionIn("checkWebsite", this.newCheck);
		}
	}.bind(this));
};

SettingsAssistant.prototype.handleType = function(event) {
	Preferences.set("type", event.value);
};

SettingsAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("selCheck", Mojo.Event.propertyChange, this.handleCheck);
	this.controller.stopListening("selType", Mojo.Event.propertyChange, this.handleType);
};

SettingsAssistant.prototype.cleanup = function(event) {
	this.services.cleanup();
};
