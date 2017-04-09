function MPlayerService() {
	this.requestId = 0;
    this.requests = {};
	this.inCleanup = false;
};

MPlayerService.prototype.status = function(onSuccess, onFailure) {
	return this.doRequest('status', {}, onSuccess, onFailure);
};

MPlayerService.prototype.version = function(onSuccess, onFailure) {
	return this.doRequest('version', {}, onSuccess, onFailure);
};

MPlayerService.prototype.list = function(path, onSuccess, onFailure){
	return this.doRequest('list', {path:path}, onSuccess, onFailure);
};

MPlayerService.prototype.open = function(file, onSuccess, onFailure){
	return this.doRequest('open', {file:file}, onSuccess, onFailure);
};

MPlayerService.prototype.exists = function(filepath, onSuccess, onFailure){
	return this.doRequest('exists', {file:filepath}, onSuccess, onFailure);
};

MPlayerService.prototype.deleteFile = function(file, onSuccess, onFailure) {
	return this.doRequest('delete', {file:file}, onSuccess, onFailure);
};

/*
 * Private API
 */

MPlayerService.prototype.registerAsHandler = function(value, onSuccess, onFailure) {
	return this.doRequest('registerAsHandler', {value:value}, onSuccess, onFailure);
};

MPlayerService.prototype.doRequest = function(method, params, onSuccess, onFailure) {
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

MPlayerService.prototype.onComplete = function(response, id) {
	delete this.requests[id];
	if(this.inCleanup && this.allCompleted()) {
		delete this;
	}
};

MPlayerService.prototype.allCompleted = function() {
	var completed = true;
	for(var curr in this.requests) {
		if(curr != undefined) {
			completed = false;
			break;
		}
	}
	return completed;
};

MPlayerService.prototype.cleanup = function() {
	this.inCleanup = true;
	if(this.allCompleted()) {
		delete this;
	}
};

MPlayerService.prototype.identifier = "palm://ca.canucksoftware.mplayer";
