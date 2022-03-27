function ComicFile(file) {
	this.file = file;
	this.isCBR = this.file.toLowerCase().endsWith(".cbr") || this.file.toLowerCase().endsWith(".rar");
};

ComicFile.prototype.listEntries = function(onSuccess, onFailure) {
	var check = (this.isCBR) ? this.unrar : this.unzip;
	this.prepareDataDirectory(check, function() {
		var cmd;
		if(this.isCBR) { //.cbr comic file
			cmd = new CommandLine(this.unrar + " vb \"" + this.file + "\"");
		} else { //.cbz comic file
			cmd = new CommandLine(this.unzip + " -Z -1 \"" + this.file + "\"");
		}
		cmd.run(function(response) {
			if(response.code==0) {
				var stdoutArray = response.stdout.trim().split("\n");
				var results = [];
				for(var i=0; i<stdoutArray.length; i++) {
					stdoutArray[i] = stdoutArray[i].trim();
					var name = stdoutArray[i].toLowerCase();
					if(name.endsWith(".bmp") || name.endsWith(".png") ||
							name.endsWith(".jpg") || name.endsWith(".jpeg")) {
						results.push(stdoutArray[i]);
					}
				}
				if(results.length==0) {
					onFailure("Empty or corrupt comic file.");
				} else {
					results.sort(function(a,b) {
						if (a.toLowerCase() < b.toLowerCase()) return -1;
						if (a.toLowerCase() > b.toLowerCase()) return 1;
					});
					onSuccess(results);
				}
			} else {
				var originalError = response.stderr + response.stdout;
				this.isCBR = !this.isCBR;
				this.listEntries(function(newResults) {
					onSuccess(newResults);
				}, function() {
					onFailure(originalError)
				});
			}
		}.bind(this));
	}.bind(this));
};

ComicFile.prototype.extractSingle = function(destination, entry, onSuccess,
		onFailure) {
	if(!destination.startsWith("/")) {
		destination = "/" + destination;
	}
	if(!destination.endsWith("/")) {
		destination += "/";
	}
	var check = (this.isCBR) ? this.unrar : this.unzip;
	this.prepareDataDirectory(check, function() {
		var cmd;
		if(this.isCBR) { //.cbr comic file
			cmd = new CommandLine(this.unrar + " x -o+ -ai -ep \"" + this.file + "\" \"" +
					entry +"\" \"" + destination + "\"");
		} else { //.cbz comic file
			cmd = new CommandLine(this.unzip + " -o -DD -j \"" + this.file + "\" \"" + entry +
					"\" -d \"" + destination + "\"");
		}
		cmd.run(function(response) {
			if(response.code==0) {
				onSuccess(response.stdout);
			} else {
				var originalError = response.stderr + response.stdout;
				this.isCBR = !this.isCBR;
				this.extractSingle(destination, entry, function(newResponse) {
					onSuccess(newResponse);
				}, function() {
					onFailure(originalError)
				});
			}
		}.bind(this));
	}.bind(this));
	
};

ComicFile.prototype.extractAll = function(destination, onSuccess, onFailure) {
	if(!destination.startsWith("/")) {
		destination = "/" + destination;
	}
	if(!destination.endsWith("/")) {
		destination += "/";
	}
	var check = (this.isCBR) ? this.unrar : this.unzip;
	this.prepareDataDirectory(check, function() {
		var cmd;
		if(this.isCBR) { //.cbr comic file
			cmd = new CommandLine(this.unrar + " x -o+ -ai -ep \"" + this.file + "\" \"" +
					destination + "\"");
		} else { //.cbz comic file
			cmd = new CommandLine(this.unzip + " -o -DD -j \"" + this.file + "\" -d \"" +
					destination + "\"");
		}
		cmd.run(function(response) {
			if(response.code==0) {
				onSuccess(response.stdout);
			} else {
				var originalError = response.stderr + response.stdout;
				this.isCBR = !this.isCBR;
				this.extractAll(destination, function(newResponse) {
					onSuccess(newResponse);
				}, function() {
					onFailure(originalError)
				});
			}
		}.bind(this));
	}.bind(this));
};

ComicFile.prototype.prepareDataDirectory = function(check, callback) {
	var pathLib = require("path");
	var localUnrar = "/media/cryptofs/apps/usr/palm/services/ca.canuckcoding.comicshelf.service/unrar";
	var localUnzip = "/media/cryptofs/apps/usr/palm/services/ca.canuckcoding.comicshelf.service/unzip";
	if(!pathLib.existsSync(check)) {
		var cmd = new CommandLine("/bin/mkdir -p /media/internal/comics/.data/ ; " +
				"/usr/bin/install -m 777 \"" + localUnrar + "\" \"" + this.unrar + "\" ; " +
				"/usr/bin/install -m 777 \"" + localUnzip + "\" \"" + this.unzip + "\"");
		cmd.run(function(response) {
			callback();
		});
	} else {
		callback();
	}
};

ComicFile.prototype.unrar = "/media/internal/comics/.data/unrar";
ComicFile.prototype.unzip = "/media/internal/comics/.data/unzip";
