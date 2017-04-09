function MainAssistant() {
}

MainAssistant.prototype.setup = function() {
	this.brightness = new Array(50, 75, 100, 150, 200);
	if(Mojo.Environment.DeviceInfo.modelNameAscii=="Pre3") {
		this.brightness = new Array(18, 37, 56, 75, 93, 112, 131, 150);
	}
	
	var menuAttr = {omitDefaultItems: true};
  	this.menuModel = {
    	visible: true,
    	items: [
			{label: "Settings", command: 'settings'},
			{label: "About", command: 'about'}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, menuAttr, this.menuModel);
	
	var sliderScaleAttr = {
    	minValue: 0,
        maxValue: this.brightness.length-1,
		round: true
    };
	this.sliderScaleModel = {
    	value: Preferences.get("sliderScaleIndex", Math.round((this.brightness.length-1)/2))
	};
	this.controller.setupWidget("sliderScale", sliderScaleAttr, this.sliderScaleModel);
	
	var sliderStrobeAttr = {
    	minValue: 15,
        maxValue: 275,
		round: true
    };
	this.sliderStrobeModel = {
    	value: Preferences.get("sliderStrobe", 150)
	};
	this.controller.setupWidget("sliderStrobe", sliderStrobeAttr, this.sliderStrobeModel);
	
	this.cbStrobeModel = {
    	value: false
    };
	
	this.controller.setupWidget("cbStrobe", {}, this.cbStrobeModel);
	
	this.controller.get("currBright").innerHTML = this.brightness[this.sliderScaleModel.value]
			+ " mA";
	
	this.controller.get("drawerStrobe").hide();
	
	this.btnModel = {
    	label : "Toggle Flashlight",
		buttonClass: "primary dim",
        disabled: false
	};
	
	this.controller.setupWidget("btnFlash", {}, this.btnModel);
	
	if(Preferences.get("entering", false)) {
		TorchService.setState({state:"on"});
	}
	if(Mojo.Environment.DeviceInfo.platformVersionMajor != 1) {
		this.controller.get("group2").style.display="none";
	}
}

MainAssistant.prototype.activate = function(event) {
	TorchService.getState(this.loadState.bind(this));
	
	this.handleChange = this.handleChange.bindAsEventListener(this);
	this.handleToggle = this.handleToggle.bindAsEventListener(this);
	this.handleStrobeCB = this.handleStrobeCB.bindAsEventListener(this);
	this.controller.listen("sliderScale", Mojo.Event.propertyChange, this.handleChange);
	this.controller.listen("cbStrobe", Mojo.Event.propertyChange, this.handleStrobeCB);
	this.controller.listen("sliderStrobe", Mojo.Event.propertyChange, this.handleChange);
	this.controller.listen("btnFlash", Mojo.Event.tap, this.handleToggle);
}

MainAssistant.prototype.loadState = function(payload) {
	if(payload.state == "off") {
		//should be all pre-ininitalized
	} else if(payload.state == "on") {
		this.sliderScaleModel.value = this.brightness.indexOf(payload.value);
		this.controller.modelChanged(this.sliderScaleModel);
		this.cbStrobeModel.value = false;
		this.controller.modelChanged(this.cbStrobeModel);
		this.controller.get("drawerStrobe").hide();
	} else if(payload.state == "strobe") {
		this.sliderScaleModel.value = this.brightness.indexOf(payload.value);
		this.controller.modelChanged(this.sliderScaleModel);
		this.cbStrobeModel.value = true;
		this.controller.modelChanged(this.cbStrobeModel);
		this.sliderStrobeModel.value = payload.speed;
		this.controller.modelChanged(this.sliderStrobeModel);
		this.controller.get("drawerStrobe").show();
	}	
}

MainAssistant.prototype.handleStrobeCB = function(event){
	if(event.value) {
		this.controller.get("drawerStrobe").show();
	} else {
		this.controller.get("drawerStrobe").hide();
	}
	this.handleChange();
}

MainAssistant.prototype.handleChange = function(event) {
	TorchService.getState(function(payload) {
		this.controller.get("currBright").innerHTML =
				this.brightness[this.sliderScaleModel.value] + " mA";
		if(payload.value!=0) {
			var params = {
				value:this.brightness[this.sliderScaleModel.value],
				speed:this.sliderStrobeModel.value
			};
			if(this.cbStrobeModel.value) {
				params.state = "strobe";
			} else {
				params.state = "on";
			}
			TorchService.setState(params);
		}
	}.bind(this));
}

MainAssistant.prototype.handleToggle = function(event) {
	TorchService.getState(function(payload) {
		this.btnModel.disabled = true;
		this.controller.modelChanged(this.btnModel);
		var params = {
			state:"off",
			value:this.brightness[this.sliderScaleModel.value],
			speed:this.sliderStrobeModel.value
		};
		if(payload.value==0) {
			if(this.cbStrobeModel.value) {
				params.state = "strobe";
			} else {
				params.state = "on";
			}
		}
		TorchService.setState(params, function() {
			this.btnModel.disabled = false;
			this.controller.modelChanged(this.btnModel);
		}.bind(this));
	}.bind(this));
}

MainAssistant.prototype.handleCommand = function(event){
	if (event.type == Mojo.Event.command) {
		switch (event.command) {
		case 'settings':
			this.controller.showDialog({
				template: 'settings/settings-popup',
				assistant: new SettingsAssistant({controller: this.controller})
			});
			break;
		case 'about':
			this.controller.serviceRequest('palm://ca.canucksoftware.systoolsmgr', {
				method: 'version',
				parameters: {},
				onSuccess: function(response) {
					this.controller.showAlertDialog({
						onChoose: function(value){},
						title: "",
						message: "<center><div style=\"font-size:1.35em;margin-bottom:10px\">" +
							"<strong>" + Mojo.Controller.appInfo.title + 
							"</strong>&nbsp;&nbsp;&nbsp; " + Mojo.Controller.appInfo.version +
							"</div><div style=\"font-size:1.20em;margin-bottom:10px\"><strong>" +
							"SysToolsMgr Service</strong>&nbsp;&nbsp;&nbsp; " +
							response.version + "</div><div style=\"font-size:1.1em\">" +
							"<br/><em>Freeware</em><br/><br/>" +
							"&copy; Copyright 2010, Jason Robitaille</center>",
						choices: [
							{label: "OK",value: ""}
						],
						allowHTMLMessage: true
					});
				}.bind(this)
			});
			break;
		}
	}
}

MainAssistant.prototype.deactivate = function(event) {

}

MainAssistant.prototype.cleanup = function(event) {
	Preferences.set("sliderScaleIndex", this.sliderScaleModel.value);
	Preferences.set("sliderStrobe", this.sliderStrobeModel.value);
	if(Preferences.get("exiting", false)) {
		TorchService.setState({state:"off"});
	}
}
