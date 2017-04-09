function StatusAssistant() {};  //status
StatusAssistant.prototype.run = function(future){
	future.result = {returnValue:true};
};


function VersionAssistant() {}; //version
VersionAssistant.prototype.run = function(future){
	future.result = {version:"1.0.2"};
};


function ListAssistant() {}; //list
ListAssistant.prototype.run = function(future){
	var extList = [".mpeg", ".mpg", ".vob", ".avi", ".ogg", ".ogv", ".asf", ".wmv", ".qt",
			".mov", ".mp4", ".rm", ".rv", ".mkv", ".flv", ".wma", ".oga", ".asx", ".ra",
			".mp3", ".wav", ".3gp", ".flac"];
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
				for(var i=0; i<items.length; i++) {
					try {
						var fullPath = path + items[i];
						if(!items[i].startsWith(".")) {
							var stat = fsLib.statSync(fullPath);
							if(stat.isFile()) { //is a file
								var ext = pathLib.extname(items[i]).toLowerCase();
								if(extList.indexOf(ext)>-1) {
									var name = items[i].substring(0, items[i].lastIndexOf("."));
									fileList.push({name:name, path:fullPath, type:ext.substring(1)});
								}
							} else { //is a directory
								dirList.push({name:items[i], path:fullPath, type:"dir"});
							}
						}
					} catch(e) {}
				}
				dirList.jsonSort("name");
				fileList.jsonSort("name");
				future.result = {dirs: dirList, files: fileList};
			} else {
				future.result = {errorCode:"ERROR", errorText:"Unable to scan files/directories."};
			}
		});
	} else {
		if(path=="/media/internal/video/") {
			var cmd = new CommandLine("/bin/mkdir -p /media/internal/video/");
			cmd.run(function(response) {
				future.result = {dirs: [], files: []};
			});
		} else {
			future.result = {errorCode:"ERROR", errorText:"Directory does not exist."};
		}
	}
};


function OpenAssistant() {}; //open
OpenAssistant.prototype.run = function(future){
	var args = this.controller.args;
	if(args.file) {
		var mplayer = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.mplayer/"
				+ "bin/mplayer";
		var pathLib = require("path");
		if(pathLib.existsSync(args.file)) {
			var exec = mplayer + " -vf scale";
			if(args.subtitle) {
				exec += " -sub \"" + args.subtitle + "\"";
			}
			exec += " \"" + args.file + "\" &"
			var cmd = new CommandLine(exec);
			cmd.run(function(response) {});
			future.result = {returnValue:true};
		} else {
			future.result = {errorCode:"ERROR", errorText:"Video does not exist."};
		}
	} else {
		future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};


function ExistsAssistant() {}; //exists
ExistsAssistant.prototype.run = function(future){
	var args = this.controller.args;
	if(args.file) {
		var pathLib = require("path");
		future.result = {exists:pathLib.existsSync(args.file)};
	} else {
		future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};


function DeleteAssistant() {}; //delete
DeleteAssistant.prototype.run = function(future){
	var args = this.controller.args;
	if(args.file) {
		var file = args.file;
		var pathLib = require("path");
		if(pathLib.existsSync(file)) {
			if(file.startsWith("/media/internal/")) {
				var cmd = new CommandLine("/bin/rm -fr \"" + file + "\"", future);
				cmd.run();
			} else {
				future.result = {errorCode:"ERROR", errorText:"That section "
						+ "of the device is off limits for data writing/deleting."};
			}
		} else {
			future.result = {errorCode:"ERROR", errorText:"File/directory does "
					+ "not exist."};
		}
	} else {
		future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};


function RegisterHandlerAssistant() {}; //registerAsHandler
RegisterHandlerAssistant.prototype.run = function(future){
	var args = this.controller.args;
	if(validForPrivate(this)) {
		var script = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.mplayer/"
				+ "scripts/handling.sh";
		var value = "disable";
		if(args.value) {
			value = "enable";
		}
		var cmd = new CommandLine("/bin/sh " + script + " \"" + value + "\"", future);
		cmd.run();
	} else {
		future.result = {errorCode:"ERROR", errorText:"This is a private function "
				+ "reserved for MPlayer."};
	}
};

