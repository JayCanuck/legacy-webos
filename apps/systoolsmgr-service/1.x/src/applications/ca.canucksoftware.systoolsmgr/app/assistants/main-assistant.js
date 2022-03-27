function MainAssistant() {
	this.icon = Mojo.Controller.appInfo.icon;
	this.name = Mojo.Controller.appInfo.title;
	this.id = Mojo.Controller.appInfo.serviceId;
	this.version = Mojo.Controller.appInfo.version;
	this.description = Mojo.Controller.appInfo.description;
	this.donate = Mojo.Controller.appInfo.donate;
	this.changelog = Mojo.Controller.appInfo.changelog;
	this.copyright = "&copy; Copyright " + Mojo.Controller.appInfo.vendor;
	if(Mojo.Controller.appInfo.copyright) {
		this.copyright = Mojo.Controller.appInfo.copyright;
	}
	this.serviceOn = false;
}

MainAssistant.prototype.setup = function() {
	this.controller.get("icon").style.background = "url(" + this.icon +
			") no-repeat !important";
	this.controller.get("title").innerHTML = this.name +
			"&nbsp;&nbsp;" + this.version;
	this.controller.get("status").innerHTML = "Checking service status...";
	this.controller.get("description").innerHTML = this.description;
	
	if(this.donate) {
		this.controller.get("donate").innerHTML = "<br>Help support development<br>"
				+ "<a href=\"" + this.donate + "\"><img class=\"donate\" src=\""
				+ "https://www.paypal.com/en_US/i/btn/btn_donate_LG.gif\"></a>";
	}
	
	if(this.changelog) {
		var log = "";
		for(var i=0; i<this.changelog.length; i++) {
			log += Mojo.View.render({object:{version:this.changelog[i].version},
					template: "main/changelog-header"});
			log += "<ul>";
			for(var j=0; j<this.changelog[i].changes.length; j++) {
				log += "<li>" + this.changelog[i].changes[j] + "</li>";
			}
			log += "</ul>";
		}
		log += "<br>";
		this.controller.get("changelog").innerHTML = log;
	}
	
	this.controller.get("footer").innerHTML = this.copyright;

	//Wait for a known system service to come online to get a base time to
	//check for the custom service.
	this.baseRequest = this.controller.serviceRequest("palm://com.palm.bus/signal/", {
		method: "registerServerStatus",
		parameters: {serviceName:"com.palm.applicationManager"},
		onSuccess: this.baseStatusSuccess.bind(this),
		onComplete: this.baseStatusComplete.bind(this)
	});
};

MainAssistant.prototype.baseStatusSuccess = function(response) {
	if(response.connected) {
		this.controller.window.setTimeout(this.statusTimeout.bind(this), 30000);
		this.statusRequest = this.controller.serviceRequest("palm://com.palm.bus/signal/", {
			method: "registerServerStatus",
			parameters: {serviceName:this.id},
			onSuccess: this.statusSuccess.bind(this),
			onComplete: this.statusComplete.bind(this)
		});
	}
};

MainAssistant.prototype.statusSuccess = function(response) {
	this.serviceOn |= response.connected;
	if(this.serviceOn) {
		this.controller.get("status").innerHTML = "Service running properly";
	} else if(!this.fallbackRequest) {
		this.fallbackRequest = this.controller.serviceRequest("palm://" + this.id, {
			method: "status",
			parameters: {},
			onSuccess: this.fallbackSuccess.bind(this),
			onComplete: this.fallbackComplete.bind(this)
		});
	}
};

MainAssistant.prototype.fallbackSuccess = function(response) {
	this.serviceOn = true;
	this.controller.get("status").innerHTML = "Service running properly";
};

MainAssistant.prototype.statusTimeout = function(response) {
	if(!this.serviceOn) {
		this.controller.get("status").innerHTML = "Service not running properly. "
				+ "Try restarting your device. If that fails to fix the issue, "
				+ "try reinstalling the service or contact the developer."; 
	}
};

MainAssistant.prototype.baseStatusComplete = function() {
	delete this.baseRequest;
};

MainAssistant.prototype.statusComplete = function() {
	delete this.statusRequest;
};

MainAssistant.prototype.fallbackComplete = function() {
	delete this.fallbackRequest;
};

MainAssistant.prototype.activate = function(event) {

};

MainAssistant.prototype.deactivate = function(event) {

};

MainAssistant.prototype.cleanup = function(event) {

};
