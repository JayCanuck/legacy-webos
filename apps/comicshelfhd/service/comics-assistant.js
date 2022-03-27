function StatusAssistant() {};  //status
StatusAssistant.prototype.run = function(future){
	future.result = {returnValue:true};
};

function ListAssistant() {}; //list
ListAssistant.prototype.run = function(future){
	var fsLib = require("fs");
	var pathLib = require("path");
	var path = this.controller.args.path;
	if(!path.startsWith("/")) {
		path = "/" + path;
	}
	if(!path.endsWith("/")) {
		path += "/";
	}
	if(pathLib.existsSync(path)) {
		fsLib.readdir(path, function(err, items) {
			if(!err) {
				var dirList = [];
				var fileList = [];
				var errList = [];
				for(var i=0; i<items.length; i++) {
					try {
						var fullPath = path + items[i];
						if(!items[i].startsWith(".")) {
							var stat = fsLib.statSync(fullPath);
							if(stat.isFile()) { //is a file
								var ext = pathLib.extname(items[i]).toLowerCase();
								if(ext==".cbr" || ext==".cbz" || ext==".zip" || ext==".rar") {
									var name = items[i].substring(0, items[i].lastIndexOf("."));
									fileList.push({name:name, path:fullPath, type:ext.substring(1), filename:items[i], size:stat.size});
								}
							} else { //is a directory
								dirList.push({name:items[i], path:fullPath, type:"dir", filename:items[i] + "/", size:0});
							}
						}
					} catch(e) {
						var currErr = {};
						currErr[items[i]] = e;
						errList.push(currErr);
					}
				}
				dirList.jsonSort("name");
				fileList.jsonSort("name");
				future.result = {dirs: dirList, files: fileList, errors:errList};
			} else {
				future.result = {errorCode:"ERROR", errorText:"Unable to scan files/directories."};
			}
		});
	} else {
		if(path=="/media/internal/comics/") {
			var cmd = new CommandLine("/bin/mkdir -p /media/internal/comics/");
			cmd.run(function(response) {
				future.result = {dirs: [], files: []};
			});
		} else {
			future.result = {errorCode:"ERROR", errorText:"Directory does not exist."};
		}
	}
};

function AllComicsAssistant() {}; //allComics
AllComicsAssistant.prototype.run = function(future){
	var cmd = new CommandLine("find /media/internal/comics/* -type f \\( "
			+ "-name '*.cbr' -o -name '*.cbz' \\)");
	cmd.run(function(response) {
		if(response.code==0) {
			response.stdout = response.stdout.trim();
			if(response.stdout.length>0) {
				future.result = {files: response.stdout.split("\n")};
			} else {
				future.result = {files: []};
			}
		} else {
			future.result = {errorCode:"ERROR", errorText:"Unable to search for comics."};
		}
	});
};

function DeleteAssistant() {}; //delete
DeleteAssistant.prototype.run = function(future){
	var pathLib = require("path");
	var file = this.controller.args.file;
	if(pathLib.existsSync(file)) {
		var cmd = new CommandLine("/bin/rm -fr \"" + file + "\"", future);
		cmd.run();
	} else {
		future.result = {errorCode:"ERROR", errorText:"File does not exist."};
	}
};

function FetchCoversAssistant() {}; //fetchCovers
FetchCoversAssistant.prototype.run = function(future, subscription){
	this.pathLib = require("path");
	this.fsLib = require("fs");
	this.comics = this.controller.args.comics;
	var count = 0;
	future.result = {complete:false, secondTimer:count};
	this.intervalId = setInterval(function() {
		count++;
		var f = subscription.get();
		f.result = {complete:false, secondTimer:count};
		//this.controller.message.respond(JSON.stringify({returnValue:true, complete:false}));
	}, 1000);
	var callback = function(response) {
		//send results for current file
		if(response.errorCode) {
			if(response.dir) {
				try {
					this.fsLib.rmdirSync(response.dir);
					delete response.dir;
				} catch(e) {}
			}
			var f = subscription.get();
			f.result = response;
			//this.controller.message.respond(JSON.stringify(response));
		} else {
			var stat = this.fsLib.statSync(response.comic);
			var results = {
				comic: {
					name: response.comic.substring(response.comic.lastIndexOf("/")+1),
					directory: response.comic.substring(0, response.comic.lastIndexOf("/")+1),
				},
				cover:{
					path: response.cover,
					size: stat.size
				}
			};
			var f = subscription.get();
			f.result = results;
		}
		//go on to next comic file
		if(!this.stopProcessing) {
			this.comics.shift();
			if(this.comics.length>0) {
				this.doFetch(this.comics[0], callback.bind(this));
			} else {
				clearInterval(this.intervalId);
				future.result = {complete:true};
				var f = subscription.get();
				f.result = {complete:true, count:count};
			}
		}
	};
	if(this.comics.length>0) {
		this.doFetch(this.comics[0], callback.bind(this));
	} else {
		clearInterval(this.intervalId);
		future.result = {complete:true};
	}
};
FetchCoversAssistant.prototype.doFetch = function(file, callback) {
	var pathLib = this.pathLib;
	try {
		if(pathLib.existsSync(file)) {
			var comic = new ComicFile(file);
			var timestamp = new Date().getTime();
			var destination = "/media/internal/comics/.thumbs/" + timestamp + "/";
			var cmd = new CommandLine("/bin/mkdir -p " + destination);
			cmd.run(function(response) {
				if(pathLib.existsSync(destination)) {
					comic.listEntries(
						function(entries) {
							var coverEntry = undefined;
							var coverName = undefined;
							for(var i=0; i<entries.length; i++) {
								var entry = entries[i].substring(entries[i].lastIndexOf("/")+1);
								if(entry.startsWith(".")) {
									entries.splice(i, 1);
									i--;
								} else {
									coverEntry = entries[i];
									coverName = entry;
									break;
								}
							}
							comic.extractSingle(destination, coverEntry,
								function(response2) {
									callback({comic: file, cover: destination + coverName});
								},
								function(err2) {
									callback({errorCode:"ERROR", errorText:err, dir:destination, returnValue:false});
								});
						},
						function(err) {
							callback({errorCode:"ERROR", errorText:err, dir:destination, returnValue:false});
						}
					);
				} else {
					callback({errorCode:"ERROR", errorText:"Unable to create thumbnail directory.", returnValue:false});
				}
			});
		} else {
			callback({errorCode:"ERROR", errorText:"File does not exist.", returnValue:false});
		}
	} catch(e) {
		callback({errorCode:"ERROR", errorText:e, returnValue:false});
	}
};
FetchCoversAssistant.prototype.cancelSubscription = function() {
	this.stopProcessing = true;
	clearInterval(this.intervalId);
};

function SetThumbAssistant() {}; //setThumb
SetThumbAssistant.prototype.run = function(future){
	var file = this.controller.args.file;
	var comic = this.controller.args.comic;
	var size = 0;
	try {
		var fs = require("fs");
		var stat = fs.statSync(comic);
		size = stat.size;
	} catch(e) {}
	var oldFile = this.controller.args.oldFile;
	var timestamp = new Date().getTime();
	var destination = "/media/internal/comics/.thumbs/" + timestamp + "/";
	var cmd = new CommandLine("/bin/mkdir -p " + destination);
	cmd.run(function(response) {
		var name = file.substring(file.lastIndexOf("/")+1);
		var cmd2 = new CommandLine("/bin/cp -f " + file + " " + destination + name);
		cmd2.run(function(response2) {
			if(response2.code==0) {
				if(oldFile) {
					var removeLeftovers = new CommandLine("/bin/rm -fr " + oldFile);
					removeLeftovers.run(function(response3) {
						future.result = {comic:comic, page:file, size:size, destination: destination + name};
					});
				} else {
					future.result = {comic:comic, page:file, size:size, destination: destination + name};
				}
			} else {
				future.result = {errorText:response2.stderr + response2.stdout,
						errorCode:"ERROR"+this.code, returnValue:false};
			}
		});
	});
};

function RemoveThumbsAssistant() {}; //removeThumbs
RemoveThumbsAssistant.prototype.run = function(future){
	var pathLib = require("path");
	var paths = this.controller.args.paths;
	var exec = "";
	for(var i=0; i<paths.length; i++) {
		if(i!=0) {
			exec += " ; ";
		}
		var currPath = paths[i].substring(0, paths[i].lastIndexOf("/"));
		exec += "/bin/rm -fr \"" + currPath + "\"";
	}
	if(pathLib.existsSync("/media/internal/comics/.thumbs")) {
		var cmd = new CommandLine(exec, future);
		cmd.run();
	} else {
		future.result = {returnValue:true};
	}
};

function ClearThumbCacheAssistant() {}; //clearThumbCache
ClearThumbCacheAssistant.prototype.run = function(future){
	var pathLib = require("path");
	if(pathLib.existsSync("/media/internal/comics/.thumbs")) {
		var cmd = new CommandLine("/bin/rm -fr \"/media/internal/comics/.thumbs\"", future);
		cmd.run();
	} else {
		future.result = {returnValue:true};
	}
};

function QueryAssistant() {}; //query
QueryAssistant.prototype.run = function(future){
	var pathLib = require("path");
	var file = this.controller.args.file;
	try {
		if(pathLib.existsSync(file)) {
			var comic = new ComicFile(file);
			comic.listEntries(
				function(entries) {
					for(var i=0; i<entries.length; i++) {
						entries[i] = entries[i].substring(entries[i].lastIndexOf("/")+1);
						if(entries[i].startsWith(".")) {
							entries.splice(i, 1);
							i--;
						}
					}
					future.result = {entries:entries};
				},
				function(err) {
					future.result = {errorCode:"ERROR", errorText:err};
				}
			);
		} else {
			future.result = {errorCode:"ERROR", errorText:"File does not exist."};
		}
	} catch(e) {
		future.result = {errorCode:"ERROR", errorText:e};
	}
};

function ExtractAssistant() {}; //extract
ExtractAssistant.prototype.run = function(future, subscription) {
	var pathLib = require("path");
	var file = this.controller.args.file;
	var entry = this.controller.args.entry;
	var count = 0;
	future.result = {complete:false, secondTimer:count};
	if(pathLib.existsSync(file)) {
		this.intervalId = setInterval(function() {
			count++;
			var f = subscription.get();
			f.result = {complete:false, secondTimer:count};
			//this.controller.message.respond(JSON.stringify({returnValue:true, complete:false}));
		}, 1000);
		var intervalId = this.intervalId;
		this.comic = new ComicFile(file);
		var comicFile = this.comic;
		var timestamp = new Date().getTime();
		var destination = "/media/internal/comics/.data/" + timestamp + "/";
		var cmd = new CommandLine("/bin/mkdir -p " + destination);
		cmd.run(function(response) {
			if(pathLib.existsSync(destination)) {
				var onSuccess = function(response) {
					clearInterval(intervalId);
					var f = subscription.get();
					f.result = {destination:destination, timestamp:timestamp};
				};
				var onFailure = function(err) {
					clearInterval(intervalId);
					var f = subscription.get();
					f.result = {errorCode:"ERROR", errorText:err, returnValue:false};
				};
				if(entry) {
					comicFile.extractSingle(destination, entry, onSuccess, onFailure);
				} else {
					comicFile.extractAll(destination, onSuccess, onFailure);
				}
			} else {
				clearInterval(intervalId);
				var f = subscription.get();
				f.result = {errorCode:"ERROR", errorText:"Unable to create extraction directory.", returnValue:false};
			}
		});
	} else {
		var f = subscription.get();
		f.result = {errorCode:"ERROR", errorText:"File does not exist.", returnValue:false};
	}
};
ExtractAssistant.prototype.cancelSubscription = function() {
	clearInterval(this.intervalId);
	this.comic.killall();
};

function SaveAssistant() {}; //save
SaveAssistant.prototype.run = function(future){
	var pathLib = require("path");
	var from = this.controller.args.from;
	var to = this.controller.args.to;
	if(pathLib.existsSync(from)) {
		var cmd = new CommandLine("/bin/cp -f \"" + from + "\" \"" + to + "\"", future);
		cmd.run();
	} else {
		future.result = {errorCode:"ERROR", errorText:"File does not exist."};
	}
};

function UnloadAssistant() {}; //unload
UnloadAssistant.prototype.run = function(future){
	var pathLib = require("path");
	var timestamp = this.controller.args.timestamp;
	var directory = "/media/internal/comics/.data/" + timestamp;
	if(pathLib.existsSync(directory)) {
		var cmd = new CommandLine("/bin/rm -fr \"" + directory + "\"", future);
		cmd.run();
	} else {
		future.result = {returnValue:true};
	}
};

function UnloadAllAssistant() {}; //unloadAll
UnloadAllAssistant.prototype.run = function(future){
	var pathLib = require("path");
	if(pathLib.existsSync("/media/internal/comics/.data/")) {
		var cmd = new CommandLine("/bin/rm -fr /media/internal/comics/.data/*/", future);
		cmd.run();
	} else {
		future.result = {returnValue:true};
	}
};

function TestAssistant() {}; //test
TestAssistant.prototype.run = function(future, subscription){
	var message = this.controller.message;
	future.result = {count:1};
	var count = 2;
    this.interval = setInterval(function() {
    	var f = subscription.get();
		f.result = {count: count};
		count++;
	}, 1000);
};
TestAssistant.prototype.cancelSubscription = function(future) {
	clearInterval(this.interval);
};


