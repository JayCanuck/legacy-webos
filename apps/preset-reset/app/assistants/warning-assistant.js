function WarningAssistant() {
}

WarningAssistant.prototype.setup = function(){
	this.controller.setupWidget("wait",{},{label:"Wait 10min", buttonClass:"affirmative"});
	this.controller.setupWidget("cancel",{},{label:"Cancel", buttonClass:"negative"});
	var d = new Date();
	this.secBase = d.getTime() / 1000;
	d.setMinutes(d.getMinutes() + 1);
	Services.setTimeout({
		wakeup: true,
		key: "ca.canucksoftware.presetreset.timer",
		uri: "palm://com.palm.applicationManager/launch",
		params: {
			id: "ca.canucksoftware.presetreset",
			params: {action:"do_reboot"}
		},
		at: d.getUTCDateTimeString()
	});
};

WarningAssistant.prototype.activate = function(event) {
	this.controller.window.setTimeout(this.updateTime.bind(this), 1000);
	this.waitTen = this.waitTen.bindAsEventListener(this);
	this.handleCancel = this.handleCancel.bindAsEventListener(this);
	this.controller.listen("wait", Mojo.Event.tap, this.waitTen);
	this.controller.listen("cancel", Mojo.Event.tap, this.handleCancel);
};

WarningAssistant.prototype.updateTime = function(event) {
	var d = new Date();
	var time = (d.getTime()/1000) - this.secBase;
	time = 60 - Math.round(time);
	if(time > 0) {
		this.controller.get("numSec").innerText = "Your device is scheduled to reset in " +
			time + " seconds.";
		this.controller.window.setTimeout(this.updateTime.bind(this), 1000);
	} else {
		this.controller.window.close();
	}
};

WarningAssistant.prototype.waitTen = function(){
	Services.clearTimeout("ca.canucksoftware.presetreset.timer");
	Services.setTimeout({
		wakeup: true,
		key: "ca.canucksoftware.presetreset.timer",
		uri: "palm://com.palm.applicationManager/launch",
		params: {
			id: "ca.canucksoftware.presetreset",
			params: {action:"warn_reboot"}
		},
		"in": "00:10:00"
	});
	this.controller.window.close();
}

WarningAssistant.prototype.handleCancel = function(event) {
	Services.clearTimeout("ca.canucksoftware.presetreset.timer");
	var d = new Date();
	d.setMinutes(d.getMinutes() + 2);
	d = d.getNextResetDate();
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
	this.controller.window.close();
};

WarningAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("wait", Mojo.Event.tap, this.waitTen);
	this.controller.stopListening("cancel", Mojo.Event.tap, this.handleCancel);
};

WarningAssistant.prototype.cleanup = function(event) {
};
