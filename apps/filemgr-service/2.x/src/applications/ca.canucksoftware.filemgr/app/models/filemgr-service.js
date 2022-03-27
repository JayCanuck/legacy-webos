function FileMgrService() {
	this.requestId = 0;
    this.requests = {};
	this.inCleanup = false;
};

FileMgrService.prototype.status = function(onSuccess, onFailure) {
	return this.doRequest('status', {}, onSuccess, onFailure);
};

FileMgrService.prototype.version = function(onSuccess, onFailure) {
	return this.doRequest('version', {}, onSuccess, onFailure);
};

FileMgrService.prototype.read = function(params, onSuccess, onFailure) {
	return this.doRequest('read', params, onSuccess, onFailure);
};

FileMgrService.prototype.write = function(params, onSuccess, onFailure) {
	return this.doRequest('write', params, onSuccess, onFailure);
};

FileMgrService.prototype.listDirs = function(params, onSuccess, onFailure) {
	return this.doRequest('listDirs', params, onSuccess, onFailure);
};

FileMgrService.prototype.listFiles = function(params, onSuccess, onFailure) {
	return this.doRequest('listFiles', params, onSuccess, onFailure);
};

FileMgrService.prototype.exists = function(filepath, onSuccess, onFailure){
	return this.doRequest('exists', {file:filepath}, onSuccess, onFailure);
};

FileMgrService.prototype.isDirectory = function(directory, onSuccess, onFailure) {
	return this.doRequest('isDirectory', {dir:directory}, onSuccess, onFailure);
};

FileMgrService.prototype.hasChildrenDirs = function(directory, onSuccess, onFailure) {
	return this.doRequest('hasDirectory', {dir:directory}, onSuccess, onFailure);
};

FileMgrService.prototype.isTextFile = function(file, onSuccess, onFailure) {
	return this.doRequest('isTextFile', {file:file}, onSuccess, onFailure);
};

FileMgrService.prototype.getParent = function(file, onSuccess, onFailure) {
	return this.doRequest('getParent', {file:file}, onSuccess, onFailure);
};

FileMgrService.prototype.getSize = function(params, onSuccess, onFailure) {
	return this.doRequest('size', params, onSuccess, onFailure);
};

FileMgrService.prototype.createDir = function(filepath, onSuccess, onFailure) {
	return this.doRequest('createDir', {path: filepath}, onSuccess, onFailure);
};

FileMgrService.prototype.createFile = function(filepath, onSuccess, onFailure) {
	return this.doRequest('createFile', {path: filepath}, onSuccess, onFailure);
};

FileMgrService.prototype.isHidden = function(file, onSuccess, onFailure) {
	return this.doRequest('isHidden', {file: file}, onSuccess, onFailure);
};

FileMgrService.prototype.setHidden = function(file, hidden, onSuccess, onFailure) {
	return this.doRequest('setHidden', {file: file, hidden:hidden}, onSuccess,
			onFailure);
};

FileMgrService.prototype.isReadOnly = function(file, onSuccess, onFailure) {
	return this.doRequest('isReadOnly', {file: file}, onSuccess, onFailure);
};

FileMgrService.prototype.setReadOnly = function(file, readonly, onSuccess, onFailure) {
	return this.doRequest('setReadOnly', {file: file, readonly: readonly}, onSuccess,
			onFailure);
};

FileMgrService.prototype.getLastModified = function(file, onSuccess, onFailure) {
	return this.doRequest('getLastModified', {file: file}, onSuccess, onFailure);
};

FileMgrService.prototype.getPermissions = function(file, onSuccess, onFailure) {
	return this.doRequest('permissions', {file: file}, onSuccess, onFailure);
};

FileMgrService.prototype.setPermissions = function(file, value, onSuccess, onFailure) {
	return this.doRequest('chmod', {file: file, value: value}, onSuccess, onFailure);
};

FileMgrService.prototype.rename = function(from, to, onSuccess, onFailure) {
	return this.doRequest('rename', {from:from, to:to, preserveSymlink:true},
			onSuccess, onFailure);
};

FileMgrService.prototype.move = function(from, to, onSuccess, onFailure) {
	return this.doRequest('move', {from:from, to:to}, onSuccess, onFailure);
};

FileMgrService.prototype.copy = function(from, to, onSuccess, onFailure) {
	return this.doRequest('copy', {from:from, to:to}, onSuccess, onFailure);
};

FileMgrService.prototype.deleteFile = function(file, onSuccess, onFailure) {
	return this.doRequest('delete', {file:file}, onSuccess, onFailure);
};

FileMgrService.prototype.gc = function() {
	return this.doRequest('gc');
};

/*
 * Private Internalz-only API
 */

FileMgrService.prototype.registerAsHandler = function(viewsource, images, text, ipk,
		onSuccess, onFailure) {
	return this.doRequest('registerAsHandler', {viewsource:viewsource, images:images,
			text:text, ipk:ipk}, onSuccess, onFailure);
};

//Package-related
FileMgrService.prototype.getPackageInfo = function(file, onSuccess, onFailure) {
	return this.doRequest('getPackageInfo', {file:file}, function(payload) {
		var lines = payload.output.split("\n");
		var info = {};
		for(var i=0; i<lines.length; i++) {
			var index = lines[i].indexOf(":");
			info[lines[i].substring(0, index).toLowerCase()] =
					lines[i].substring(index+2);
		}
		onSuccess(info);
	}.bind(this), onFailure);
};
FileMgrService.prototype.isPackageInstalled = function(appId, onSuccess, onFailure) {
	return this.doRequest('isPackageInstalled', {appId:appId}, onSuccess, onFailure);
};
FileMgrService.prototype.installPackage = function(file, appId, onSuccess, onFailure) {
	return this.doRequest('installPackage', {file:file, appId:appId}, onSuccess,
			onFailure);
};
FileMgrService.prototype.uninstallPackage = function(appId, onSuccess, onFailure) {
	return this.doRequest('uninstallPackage', {appId:appId}, onSuccess, onFailure);
};

//Patch-related
FileMgrService.prototype.isPatchInstalled = function(file, onSuccess, onFailure) {
	return this.doRequest('isPatchInstalled', {file:file}, onSuccess, onFailure);
};
FileMgrService.prototype.installPatch = function(file, onSuccess, onFailure) {
	var version = Mojo.Environment.DeviceInfo.platformVersion.replace(/[^0-9.]/g, "");
	return this.doRequest('installPatch', {file:file, version:version}, onSuccess,
			onFailure);
};
FileMgrService.prototype.uninstallPatch = function(file, onSuccess, onFailure) {
	return this.doRequest('uninstallPatch', {file:file}, onSuccess, onFailure);
};

//Restart-related
FileMgrService.prototype.javaRestart = function() {
	return this.doRequest('javaRestart');
};
FileMgrService.prototype.lunaRestart = function() {
	return this.doRequest('lunaRestart');
};
FileMgrService.prototype.deviceRestart = function() {
	return this.doRequest('deviceRestart');
};

FileMgrService.prototype.doRequest = function(method, params, onSuccess, onFailure) {
	this.requestId++;
	this.requests[this.requestId] = new Mojo.Service.Request(this.identifier, {
		method: method,
		parameters: params || {},
		onSuccess: onSuccess || Mojo.doNothing,
		onFailure: onFailure || Mojo.doNothing,
		onComplete: this.onComplete.bind(this, this.requestId)
	});
	return this.requests[this.requestId];
};

FileMgrService.prototype.onComplete = function(response, id) {
	delete this.requests[id];
	if(this.inCleanup && this.allCompleted()) {
		delete this;
	}
};

FileMgrService.prototype.allCompleted = function() {
	var completed = true;
	for(var curr in this.requests) {
		if(curr != undefined) {
			completed = false;
			break;
		}
	}
	return completed;
};

FileMgrService.prototype.cleanup = function() {
	this.inCleanup = true;
	if(this.allCompleted()) {
		delete this;
	}
};

FileMgrService.prototype.identifier = "palm://ca.canucksoftware.filemgr";
