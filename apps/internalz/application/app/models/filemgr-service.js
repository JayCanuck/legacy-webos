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

FileMgrService.prototype.list = function(params, onSuccess, onFailure) {
	return this.doRequest('list', params, onSuccess, onFailure);
};

FileMgrService.prototype.listDirs = function(params, onSuccess, onFailure) {
	return this.doRequest('listDirs', params, onSuccess, onFailure);
};

FileMgrService.prototype.listFiles = function(params, onSuccess, onFailure) {
	return this.doRequest('listFiles', params, onSuccess, onFailure);
};

FileMgrService.prototype.query = function(list, onSuccess, onFailure) {
	return this.doRequest('query', {items:list}, onSuccess, onFailure);
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

FileMgrService.prototype.getLastModified = function(file, onSuccess, onFailure) {
	return this.doRequest('getLastModified', {file: file}, onSuccess, onFailure);
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

FileMgrService.prototype.identifier = "palm://ca.canuckcoding.internalz.service";
