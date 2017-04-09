function Services() {
}

Services.versionSysToolsMgr = function(callback) {
	var request = new Mojo.Service.Request(Services.systoolsmgr, {
		method: 'version',
		onSuccess: callback || Mojo.doNothing,
		onFailure: Services.genericErrorCallback
	});
	return request;
}

Services.restartDevice = function(callback) {
	var request = new Mojo.Service.Request(Services.systoolsmgr, {
		method: 'deviceRestart',
		onSuccess: callback || Mojo.doNothing,
		onFailure: Services.genericErrorCallback
	});
	return request;
}

Services.restartJavaServices = function(callback) {
	var request = new Mojo.Service.Request(Services.systoolsmgr, {
		method: 'javaRestart',
		onSuccess: callback || Mojo.doNothing,
		onFailure: Services.genericErrorCallback
	});
	return request;
}

Services.restartLuna = function(callback) {
	var request = new Mojo.Service.Request(Services.systoolsmgr, {
		method: 'lunaRestart',
		onSuccess: callback || Mojo.doNothing,
		onFailure: Services.genericErrorCallback
	});
	return request;
}

Services.syncTime = function(callback) {
	var request = new Mojo.Service.Request(Services.systoolsmgr, {
		method: 'syncTime',
		onSuccess: callback || Mojo.doNothing,
		onFailure: function(err) {Mojo.Controller.errorDialog(err.errorText);}
	});
	return request;
}

Services.turnOffDisplay = function(callback) {
	var request = new Mojo.Service.Request(Services.systoolsmgr, {
		method: 'setDisplay',
		parameters: {state:"off"},
		onSuccess: callback || Mojo.doNothing,
		onFailure: Services.genericErrorCallback
	});
	return request;
}

Services.setTimeout = function(params, callback) {
	var request = new Mojo.Service.Request(Services.alarms, {
		method: 'set',
		parameters: params,
		onSuccess: callback || Mojo.doNothing,
		onFailure: Services.genericErrorCallback
	});
	return request;
}

Services.clearTimeout = function(keyVal, callback) {
	var request = new Mojo.Service.Request(Services.alarms, {
		method: 'clear',
		parameters: {key:keyVal},
		onSuccess: callback || Mojo.doNothing,
		onFailure: Services.genericErrorCallback
	});
	return request;
}

Services.launchApp = function(appid, callback) {
	var request = new Mojo.Service.Request(Services.applications, {
		method: 'launch',
		parameters: {id:appid},
		onComplete: callback || Mojo.doNothing,
		onFailure: Services.genericErrorCallback
	});
	return request;
}

Services.genericErrorCallback = function(err) {
	Mojo.Controller.errorDialog(err.errorText);
}

Services.systoolsmgr = 'palm://ca.canucksoftware.systoolsmgr';
Services.alarms = 'palm://com.palm.power/timeout';
Services.applications = 'palm://com.palm.applicationManager';
