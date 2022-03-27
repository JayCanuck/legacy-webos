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
									fileList.push({name:name, path:fullPath, type:ext.substring(1)});
								}
							} else { //is a directory
								dirList.push({name:items[i], path:fullPath, type:"dir"});
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
ExtractAssistant.prototype.run = function(future){
	var pathLib = require("path");
	var file = this.controller.args.file;
	var entry = this.controller.args.entry;
	if(pathLib.existsSync(file)) {
		var comic = new ComicFile(file);
		var timestamp = new Date().getTime();
		var destination = "/media/internal/comics/.data/" + timestamp + "/";
		var cmd = new CommandLine("/bin/mkdir -p " + destination);
		cmd.run(function(response) {
			if(pathLib.existsSync(destination)) {
				var onSuccess = function(response) {
					future.result = {destination:destination, timestamp:timestamp};
				};
				var onFailure = function(err) {
					future.result = {errorCode:"ERROR", errorText:err};
				};
				if(entry) {
					comic.extractSingle(destination, entry, onSuccess, onFailure);
				} else {
					comic.extractAll(destination, onSuccess, onFailure);
				}
			} else {
				future.result = {errorCode:"ERROR", errorText:"Unable to create extraction directory."};
			}
		});
	} else {
		future.result = {errorCode:"ERROR", errorText:"File does not exist."};
	}
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

function AAssistant() {}; //a
AAssistant.prototype.run = function(future){
	var pathLib = require("path");
	future.result = {exists:pathLib.existsSync(this.controller.args.path)};
};

function BAssistant() {}; //b
BAssistant.prototype.run = function(future){
	var cmd = new CommandLine(this.controller.args.exec, future);
	cmd.run();
};
