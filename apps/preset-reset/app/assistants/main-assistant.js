function MainAssistant() {
	this.sunCount = 0;
	this.monCount = 0;
	this.tueCount = 0;
	this.wedCount = 0;
	this.thuCount = 0;
	this.friCount = 0;
	this.sunCount = 0;
}

MainAssistant.prototype.setup = function() {
	var menuAttr = {omitDefaultItems: true};
  	this.menuModel = {
    	visible: true,
    	items: [
			{label: "Sync Time Now", command: 'sync-time'},
			{label: "Reset Now", command: 'reset'},
			{label: "About", command: 'about'}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, menuAttr, this.menuModel);	
	var week = Preferences.get("week", {sun:false, mon:false, tue:false, wed:false,
			thu:false, fri:false, sat:false});
	this.controller.setupWidget("enabled", {},
        this.modelEnabled = Preferences.get("enabled", {value: false})
	);
	this.controller.setupWidget("sun", {choices: [{label: "S", value: true}]},
    	this.modelSun = {value: week.sun}
	);
	if(this.modelSun.value)
		this.sunCount = 1;
	this.controller.setupWidget("mon", {choices: [{label: "M", value: true}]},
    	this.modelMon = {value: week.mon}
	);
	if(this.modelMon.value)
		this.monCount = 1;
	this.controller.setupWidget("tue", {choices: [{label: "T", value: true}]},
    	this.modelTue = {value: week.tue}
	);
	if(this.modelTue.value)
		this.tueCount = 1;
	this.controller.setupWidget("wed", {choices: [{label: "W", value: true}]},
    	this.modelWed = {value: week.wed}
	);
	if(this.modelWed.value)
		this.wedCount = 1;
	this.controller.setupWidget("thu", {choices: [{label: "T", value: true}]},
    	this.modelThu = {value: week.thu}
	);
	if(this.modelThu.value)
		this.thuCount = 1;
	this.controller.setupWidget("fri", {choices: [{label: "F", value: true}]},
    	this.modelFri = {value: week.fri}
	);
	if(this.modelFri.value)
		this.friCount = 1;
	this.controller.setupWidget("sat", {choices: [{label: "S", value: true}]},
    	this.modelSat = {value: week.sat}
	);
	if(this.modelSat.value)
		this.satCount = 1;
	defaultTime = new Date();
	defaultTime.setHours(0);
	defaultTime.setMinutes(0);
	defaultTime.setSeconds(0);
	var msTime = Preferences.get("time", {ms:defaultTime.getTime()});
    this.controller.setupWidget("time", {label:' ', minuteInterval:1},
		this.modelTime = {time: new Date(msTime.ms)}
	);
	this.controller.setupWidget("off", {},
		this.modelOff = Preferences.get("off", {value: false})
	);
	this.controller.setupWidget("sync", {},
		this.modelSync = Preferences.get("sync", {value: false})
	);
	
	this.controller.setupWidget("btnList",{}, {label : "Apps To Launch..."});
};

MainAssistant.prototype.activate = function(event) {
	this.toggleEnabled = this.toggleEnabled.bindAsEventListener(this);
	this.toggleSun = this.toggleSun.bindAsEventListener(this);
	this.toggleMon = this.toggleMon.bindAsEventListener(this);
	this.toggleTue = this.toggleTue.bindAsEventListener(this);
	this.toggleWed = this.toggleWed.bindAsEventListener(this);
	this.toggleThu = this.toggleThu.bindAsEventListener(this);
	this.toggleFri = this.toggleFri.bindAsEventListener(this);
	this.toggleSat = this.toggleSat.bindAsEventListener(this);
	this.handleTime = this.handleTime.bindAsEventListener(this);
	this.handleOff = this.handleOff.bindAsEventListener(this);
	this.handleSync = this.handleSync.bindAsEventListener(this);
	this.handleLaunchList = this.handleLaunchList.bindAsEventListener(this);
	this.controller.listen("enabled", Mojo.Event.propertyChange, this.toggleEnabled);
	this.controller.listen("sun", Mojo.Event.tap, this.toggleSun);
	this.controller.listen("mon", Mojo.Event.tap, this.toggleMon);
	this.controller.listen("tue", Mojo.Event.tap, this.toggleTue);
	this.controller.listen("wed", Mojo.Event.tap, this.toggleWed);
	this.controller.listen("thu", Mojo.Event.tap, this.toggleThu);
	this.controller.listen("fri", Mojo.Event.tap, this.toggleFri);
	this.controller.listen("sat", Mojo.Event.tap, this.toggleSat);
	this.controller.listen("time", Mojo.Event.propertyChange, this.handleTime);
	this.controller.listen("off", Mojo.Event.propertyChange, this.handleOff);
	this.controller.listen("sync", Mojo.Event.propertyChange, this.handleSync);
	this.controller.listen("btnList", Mojo.Event.tap, this.handleLaunchList);
};

MainAssistant.prototype.handleCommand = function(event){
	if (event.type == Mojo.Event.command) {
		switch (event.command) {
		case 'sync-time':
			Services.syncTime(function(response) {
				var s = "Time " +
						response.output.substring(response.output.indexOf("offset"));
				this.controller.showBanner(s, "");
			}.bind(this));
			break;
		case 'reset':
			this.controller.showAlertDialog({
	   			onChoose: function(value) {
					if (value == 'yes') {
						Services.restartDevice();
					}
				}.bind(this),
				title: $L("Reset Now?"),
	    		message: $L("Are you sure you want to restart your device right now?"),
	    		choices:[
	        		{label:$L('Yes'), value:"yes", type:'affirmative'},  
	        		{label:$L('No'), value:"no", type:'negative'} 
	    		]
	    	});
			break;
		case 'about':
			Services.versionSysToolsMgr(
				function(response) {
					this.controller.showAlertDialog({
						onChoose: function(value){},
						title: "",
						message: "<center><div style=\"font-size:1.35em;margin-bottom:" +
							"10px\"><strong>" + Mojo.Controller.appInfo.title + 
							"</strong>&nbsp;&nbsp;&nbsp; " +
							Mojo.Controller.appInfo.version + "</div><div style=\"font" +
							"-size:1.20em;margin-bottom:10px\"><strong>SysToolsMgr " +
							"Service</strong>&nbsp;&nbsp;&nbsp; " + response.version +
							"</div><div style=\"font-size:1.1em\"><br/><em>Freeware" +
							"</em><br/><br/>&copy; Copyright 2010, Jason Robitaille" +
							"</center>",
						choices: [
							{label: "OK",value: ""}
						],
						allowHTMLMessage: true
					});
				}.bind(this)
			);
			break;
		}
	}
}

MainAssistant.prototype.toggleEnabled = function(event) {
	Preferences.set("enabled", {value:this.modelEnabled.value});
	if(this.modelEnabled.value) {
		this.setTimeout();
	} else {
		this.clearTimeout();
	}
};

MainAssistant.prototype.setTimeout = function() {
	var d = new Date().getNextResetDate();
	if(d!=null) {
		Services.setTimeout({
			wakeup: true,
			key: "ca.canucksoftware.presetreset.timer",
			uri: "palm://com.palm.applicationManager/launch",
			params: {
				id: "ca.canucksoftware.presetreset",
				params: {action:"warn_reboot"}
			},
			at: d.getUTCDateTimeString()
		});
	}
};

MainAssistant.prototype.clearTimeout = function() {
	Services.clearTimeout("ca.canucksoftware.presetreset.timer");
};

MainAssistant.prototype.updateTimeout = function() {
	if(this.modelEnabled.value) {
		this.clearTimeout();
		this.setTimeout();
	}
};

MainAssistant.prototype.toggleSun = function(event) {
	if(this.sunCount == 0) {
		this.sunCount++;
	} else {
		this.modelSun.value = false;
		this.controller.modelChanged(this.modelSun);
		this.sunCount = 0;
	}
	this.saveWeek();
};

MainAssistant.prototype.toggleMon = function(event) {
	if(this.monCount == 0) {
		this.monCount++;
	} else {
		this.modelMon.value = false;
		this.controller.modelChanged(this.modelMon);
		this.monCount = 0;
	}
	this.saveWeek();
};

MainAssistant.prototype.toggleTue = function(event) {
	if(this.tueCount == 0) {
		this.tueCount++;
	} else {
		this.modelTue.value = false;
		this.controller.modelChanged(this.modelTue);
		this.tueCount = 0;
	}
	this.saveWeek();
};

MainAssistant.prototype.toggleWed = function(event) {
	if(this.wedCount == 0) {
		this.wedCount++;
	} else {
		this.modelWed.value = false;
		this.controller.modelChanged(this.modelWed);
		this.wedCount = 0;
	}
	this.saveWeek();
};

MainAssistant.prototype.toggleThu = function(event) {
	if(this.thuCount == 0) {
		this.thuCount++;
	} else {
		this.modelThu.value = false;
		this.controller.modelChanged(this.modelThu);
		this.thuCount = 0;
	}
	this.saveWeek();
};

MainAssistant.prototype.toggleFri = function(event) {
	if(this.friCount == 0) {
		this.friCount++;
	} else {
		this.modelFri.value = false;
		this.controller.modelChanged(this.modelFri);
		this.friCount = 0;
	}
	this.saveWeek();
};

MainAssistant.prototype.toggleSat = function(event) {
	if(this.satCount == 0) {
		this.satCount++;
	} else {
		this.modelSat.value = false;
		this.controller.modelChanged(this.modelSat);
		this.satCount = 0;
	}
	this.saveWeek();
};

MainAssistant.prototype.saveWeek = function() {
	var week = {
		sun: this.modelSun.value,
		mon: this.modelMon.value,
		tue: this.modelTue.value,
		wed: this.modelWed.value,
		thu: this.modelThu.value,
		fri: this.modelFri.value,
		sat: this.modelSat.value
	};
	Preferences.set("week", week);
	this.updateTimeout();
};

MainAssistant.prototype.handleTime = function(event) {
	Preferences.set("time", {ms:this.modelTime.time.getTime()});
	this.updateTimeout()
};

MainAssistant.prototype.handleOff = function(event) {
	Preferences.set("off", {value:this.modelOff.value});
};

MainAssistant.prototype.handleSync = function(event) {
	Preferences.set("sync", {value:this.modelSync.value});
};

MainAssistant.prototype.handleLaunchList = function(event){
	this.controller.stageController.pushScene('launchlist');
};

MainAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("enabled", Mojo.Event.propertyChange, this.toggleEnabled);
	this.controller.stopListening("sun", Mojo.Event.tap, this.toggleSun);
	this.controller.stopListening("mon", Mojo.Event.tap, this.toggleMon);
	this.controller.stopListening("tue", Mojo.Event.tap, this.toggleTue);
	this.controller.stopListening("wed", Mojo.Event.tap, this.toggleWed);
	this.controller.stopListening("thu", Mojo.Event.tap, this.toggleThu);
	this.controller.stopListening("fri", Mojo.Event.tap, this.toggleFri);
	this.controller.stopListening("sat", Mojo.Event.tap, this.toggleSat);
	this.controller.stopListening("time", Mojo.Event.propertyChange, this.handleTime);
	this.controller.stopListening("off", Mojo.Event.propertyChange, this.handleOff);
	this.controller.stopListening("sync", Mojo.Event.propertyChange, this.handleSync);
};

MainAssistant.prototype.cleanup = function(event) {
};
