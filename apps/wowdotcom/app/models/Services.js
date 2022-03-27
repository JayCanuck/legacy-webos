function Services() {
	this.requestId = 0;
    this.requests = {};
	this.inCleanup = false;
}

Services.prototype.setBackgroundActionIn = function(action, when) {
	var params = {
		wakeup: false,
		key: Mojo.Controller.appInfo.id + ".timer",
		uri: Services.applications + "/launch",
		params: {
			id: Mojo.Controller.appInfo.id,
			params: {action:action}
		},
		"in": when
	};
	return this.setTimeout(params);
};

Services.prototype.setTimeout = function(params, callback) {
	this.requestId++;
	var request = new Mojo.Service.Request(this.alarms, {
		method: 'set',
		parameters: params,
		onSuccess: callback || Mojo.doNothing,
		onFailure: this.genericErrorCallback,
		onComplete: this.onComplete.bind(this, this.requestId)
	});
	return request;
};

Services.prototype.clearBackgroundAction = function(callback){
	return this.clearTimeout(Mojo.Controller.appInfo.id + ".timer",
			callback || Mojo.doNothing);
};

Services.prototype.clearTimeout = function(keyVal, callback) {
	this.requestId++;
	var request = new Mojo.Service.Request(this.alarms, {
		method: 'clear',
		parameters: {key:keyVal},
		onSuccess: callback || Mojo.doNothing,
		onFailure: this.genericErrorCallback,
		onComplete: this.onComplete.bind(this, this.requestId)
	});
	return request;
};

Services.prototype.openWebpage = function(webpage, callback) {
	this.requestId++;
	var request = new Mojo.Service.Request(this.applications, {
		method: 'launch',
		parameters: {
			id: Mojo.Controller.appInfo.id,
			params: {url:webpage}
		},
		onSuccess: callback || Mojo.doNothing,
		onFailure: this.genericErrorCallback,
		onComplete: this.onComplete.bind(this, this.requestId)
	});
	return request;
};

Services.prototype.openExternalWebpage = function(webpage, callback) {
	this.requestId++;
	var request = new Mojo.Service.Request(this.applications, {
		method: 'open',
		parameters: {
			id: "com.palm.app.browser",
			params: {target:webpage}
		},
		onSuccess: callback || Mojo.doNothing,
		onFailure: this.genericErrorCallback,
		onComplete: this.onComplete.bind(this, this.requestId)
	});
	return request;
};

Services.prototype.genericErrorCallback = function(err) {
	Mojo.Controller.errorDialog(err.errorText);
};

Services.prototype.onComplete = function(response, id) {
	delete this.requests[id];
	if(this.inCleanup && this.allCompleted()) {
		delete this;
	}
};

Services.prototype.allCompleted = function() {
	var completed = true;
	for(var curr in this.requests) {
		if(curr != undefined) {
			completed = false;
			break;
		}
	}
	return completed;
};

Services.prototype.cleanup = function() {
	this.inCleanup = true;
	if(this.allCompleted()) {
		delete this;
	}
};

Services.prototype.alarms = 'palm://com.palm.power/timeout';
Services.prototype.applications = 'palm://com.palm.applicationManager';
