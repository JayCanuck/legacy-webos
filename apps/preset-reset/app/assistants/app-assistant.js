function AppAssistant() {
}

AppAssistant.prototype.handleLaunch = function(launchParams){
	if(launchParams.action == "warn_reboot") {
		var warningArgs = {name: 'PresetReset-warning', height: 155, lightweight:true,
				soundclass: "none"};
		var pushWarning = function(stageController){
 		      stageController.pushScene('warning');
 		};
 		this.controller.createStageWithCallback(warningArgs, pushWarning,
				Mojo.Controller.StageType.popupAlert);
	} else if(launchParams.action == "do_reboot") {
		Services.setTimeout(
			{
				wakeup: true,
				key: "ca.canucksoftware.presetreset.timer",
				uri: "palm://com.palm.applicationManager/launch",
				params: {
					id: "ca.canucksoftware.presetreset",
					params: {action:"after_reboot"}
				},
				"in": "00:05:00"
			},
			Services.restartDevice()
		);
	} else if(launchParams.action == "after_reboot") {
		this.setNewTimeout();
		this.postResetActions();
	} else {
		var mainArgs = {name: 'PresetReset-main'};
		var pushMain = function(stageController){
			stageController.pushScene('main');
		};
		this.controller.createStageWithCallback(mainArgs, pushMain);
	}
};

AppAssistant.prototype.setNewTimeout = function(){
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

AppAssistant.prototype.postResetActions = function() {
	this.sync = Preferences.get("sync", {value: false});
	this.off = Preferences.get("off", {value: false});
	this.applist = Preferences.get("applist", {list:[]}).list;
	if(this.sync.value) {
		Services.syncTime(function(response) {
			var s = "Time " +
					response.output.substring(response.output.indexOf("offset"));
			this.controller.showBanner(s, "");
			if(this.applist.length>0) {
				Services.launchApp(this.applist[0].id, this.launchApps.bind(this));
			}
			if(this.off.value) {
				this.controller.window.setTimeout(Services.turnOffDisplay(), 30000);
			}
		}.bind(this));
	} else {
		if(this.applist.length>0) {
			Services.launchApp(this.applist[0].id, this.launchApps.bind(this));
		}
		if(this.off.value) {
			this.controller.window.setTimeout(Services.turnOffDisplay(), 30000);
		}
	}
};

AppAssistant.prototype.launchApps = function() {
	this.applist.shift();
	if(this.applist.length>0) {
		Services.launchApp(this.applist[0].id, this.launchApps.bind(this));
	}
};
