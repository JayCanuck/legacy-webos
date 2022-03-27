function SettingsAssistant() {
}

SettingsAssistant.prototype.setup = function() {
	this.appAssist = Mojo.Controller.getAppController().assistant;

	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, {
		visible: true,
    	items: [{label: "Back", command:"back"}]
	});
	this.controller.stageController.setWindowOrientation("up");
	
	this.controller.setupWidget("toggleSwipeDelete", {}, {
			value: this.appAssist.settings.swipeDelete
		}
	);
	this.controller.setupWidget("toggleSwipePage", {}, {
			value: this.appAssist.settings.swipePage
		}
	);
	this.controller.setupWidget("toggleGesturePage", {}, {
			value: this.appAssist.settings.gesturePage
		}
	);
	this.controller.setupWidget("toggleAutoResume", {}, {
			value: (this.appAssist.settings.autoResume!=undefined)
		}
	);
	this.controller.setupWidget("selOrientation",
		{
			choices: [
				{label: "Portrait", value: "up"},
				{label: "Landscape", value: "left"},
				{label: "Free", value: "free"}
			],
			labelPlacement:Mojo.Widget.labelPlacementLeft
		},
		{
        	value: this.appAssist.settings.orientation
        }
    );
	if(Mojo.Environment.DeviceInfo.platformVersionMajor >= 3) {
		for(var i=0; i<this.controller.document.styleSheets.length; i++) {
			var rules = this.controller.document.styleSheets[i].cssRules;
			for(var j=0; j<rules.length; j++)  {
	    		if(rules[j].selectorText==".settingsToggle") {
					rules[j].style.top = "0px !important";
					break;
				}
			}
		}
		this.controller.get("gestureWrapper").hide();
		this.controller.get("orientationWrapper").hide();
		this.controller.get("autoResumeRow").addClassName("last");
	}
};

SettingsAssistant.prototype.activate = function(event) {
	this.handleSwipeDelete = this.handleSwipeDelete.bindAsEventListener(this);
	this.handleSwipePage = this.handleSwipePage.bindAsEventListener(this);
	this.handleGesturePage = this.handleGesturePage.bindAsEventListener(this);
	this.handleAutoResume = this.handleAutoResume.bindAsEventListener(this);
	this.handleOrientation = this.handleOrientation.bindAsEventListener(this);
	this.controller.listen("toggleSwipeDelete", Mojo.Event.propertyChange, this.handleSwipeDelete);
	this.controller.listen("toggleSwipePage", Mojo.Event.propertyChange, this.handleSwipePage);
	this.controller.listen("toggleGesturePage", Mojo.Event.propertyChange, this.handleGesturePage);
	this.controller.listen("toggleAutoResume", Mojo.Event.propertyChange, this.handleAutoResume);
	this.controller.listen("selOrientation", Mojo.Event.propertyChange, this.handleOrientation);
};

SettingsAssistant.prototype.handleCommand = function(event){
	if(event.type == Mojo.Event.command) {
		if(event.command=="back") {
			if(this.changed) {
				this.controller.stageController.popScene({action: "updatePrefs"});
			} else{
				this.controller.stageController.popScene();
			}
		}
	} else if(event.type == Mojo.Event.back){
		event.stop();
		if(this.changed) {
			this.controller.stageController.popScene({action: "updatePrefs"});
		} else{
			this.controller.stageController.popScene();
		}
	}
};

SettingsAssistant.prototype.handleSwipeDelete = function(event) {
	this.changed = true;
	this.appAssist.settings.swipeDelete = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleSwipePage = function(event) {
	this.appAssist.settings.swipePage = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleGesturePage = function(event) {
	this.appAssist.settings.gesturePage = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleAutoResume = function(event) {
	if(event.value) {
		this.appAssist.settings.autoResume = {file:undefined, page:undefined};
	} else {
		this.appAssist.settings.autoResume = undefined;
	}
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleOrientation = function(event) {
	this.appAssist.settings.orientation = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("toggleSwipeDelete", Mojo.Event.propertyChange, this.handleSwipeDelete);
	this.controller.stopListening("toggleSwipePage", Mojo.Event.propertyChange, this.handleSwipePage);
	this.controller.stopListening("toggleGesturePage", Mojo.Event.propertyChange, this.handleGesturePage);
	this.controller.stopListening("toggleAutoResume", Mojo.Event.propertyChange, this.handleAutoResume);
	this.controller.stopListening("selOrientation", Mojo.Event.propertyChange, this.handleOrientation);
};

SettingsAssistant.prototype.cleanup = function(event) {
};
