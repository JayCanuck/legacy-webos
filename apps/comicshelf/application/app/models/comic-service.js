function ComicService() {
	this.requestId = 0;
    this.requests = {};
	this.inCleanup = false;
};

ComicService.prototype.status = function(onSuccess, onFailure) {
	return this.doRequest('status', {}, onSuccess, onFailure);
};

ComicService.prototype.list = function(path, onSuccess, onFailure) {
	return this.doRequest('list', {path:path}, onSuccess, onFailure);
};

ComicService.prototype.deleteFile = function(file, onSuccess, onFailure) {
	return this.doRequest('delete', {file:file}, onSuccess, onFailure);
};

ComicService.prototype.query = function(file, onSuccess, onFailure) {
	return this.doRequest('query', {file:file}, onSuccess, onFailure);
};

ComicService.prototype.extractSingle = function(file, entry, onSuccess, onFailure) {
	return this.doRequest('extract', {file:file, entry:entry}, onSuccess, onFailure);
};

ComicService.prototype.extractAll = function(file, onSuccess, onFailure) {
	return this.doRequest('extract', {file:file}, onSuccess, onFailure);
};

ComicService.prototype.save = function(from, to, onSuccess, onFailure) {
	return this.doRequest('save', {from:from, to:to}, onSuccess, onFailure);
};

ComicService.prototype.unload = function(timestamp, onSuccess, onFailure) {
	return this.doRequest('unload', {timestamp:timestamp}, onSuccess, onFailure);
};

ComicService.prototype.unloadAll = function(onSuccess, onFailure) {
	return this.doRequest('unloadAll', {}, onSuccess, onFailure);
};

ComicService.prototype.doRequest = function(method, params, onSuccess, onFailure) {
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

ComicService.prototype.onComplete = function(response, id) {
	delete this.requests[id];
	if(this.inCleanup && this.allCompleted()) {
		delete this;
	}
};

ComicService.prototype.allCompleted = function() {
	var completed = true;
	for(var curr in this.requests) {
		if(curr != undefined) {
			completed = false;
			break;
		}
	}
	return completed;
};

ComicService.prototype.cleanup = function() {
	this.inCleanup = true;
	if(this.allCompleted()) {
		delete this;
	}
};

ComicService.prototype.identifier = "palm://ca.canuckcoding.comicshelf.service";
