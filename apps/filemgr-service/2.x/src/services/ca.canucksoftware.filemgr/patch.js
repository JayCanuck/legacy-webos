function Patch(file, future) {
	this.file = file;
	this.future = future;
	this.fsLib = require("fs");
	this.pathLib = require("path");
	this.name = this.pathLib.basename(this.file);
	this.base = this.name.substring(0, this.name.lastIndexOf(".")).toLowerCase()
			.replace(/[^a-zA-Z0-9-]/g, "");
	this.name = this.base + ".patch"
	this.id = "ca.canucksoftware.patches." + this.base;
	this.meta = {};
};

Patch.prototype.install = function(version) {
	this.script = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.filemgr"
			+ "/scripts/patch.sh";
	this.version = version;
	this.fsLib.readFile(this.file, "utf8", function(err, text) {
		if(!err) {
			var patchText = text.replace(/\r\n/g, "\n").replace(/\r/g, "\n");
			var lines = patchText.split("\n");
			var parsedMeta = {};
			for(var i=0; i<lines.length; i++) {
				if(lines[i].startsWith("Name:")) {
					parsedMeta.name = this._parseLine(lines[i]);
				} else if(lines[i].startsWith("Version:")) {
					parsedMeta.version = this._parseLine(lines[i]);
					var tokens = parsedMeta.version.split("-");
					parsedMeta.version = this.version + "-" + tokens[tokens.length-1];
				} else if(lines[i].startsWith("Author:")) {
					parsedMeta.author = this._parseLine(lines[i]);
				} else if(lines[i].startsWith("Description:")) {
					parsedMeta.description = this._parseLine(lines[i]);
				} else if(lines[i].startsWith("+++")) {
					break;
				}
			}
			this.meta.name = parsedMeta.name || this.base;
			this.meta.version = parsedMeta.version || (this.version + "-1");
			this.meta.author = parsedMeta.author || "Unknown";
			this.meta.description = parsedMeta.description || "N/A";
			this.fsLib.writeFile(this.file, patchText, "utf8", function(err2) {
				this.cmd = new CommandLine("/bin/sh " + this.script + " \"" + this.file
						+ "\" \"" + this.base + "\" \"" + this.meta.name + "\" \""
						+ this.meta.version + "\" \"" + this.meta.author + "\" \""
						+ this.meta.description + "\"", this.future);
				this.cmd.run();
			}.bind(this));
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"Unable to read patch "
					+ "file."};
		}
	}.bind(this));
};

Patch.prototype._parseLine = function(line) {
	var index = line.indexOf(":");
    var result = undefined;
    if(index>-1) {
        result = line.substring(index+1).trim();
    }
    return result;
};

Patch.prototype.uninstall = function() {
	if(Package.isInstalled(this.id)) {
		this.pkg = new Package(this.future);
		this.pkg.uninstall(this.id);
	} else if(Package.isInstalled("org.webosinternals.patches." + this.base)) {
		this.id = "org.webosinternals.patches." + this.base;
		this._doUnpatch();
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Can't uninstall; patch is "
				+ "not installed"};
	}
};

Patch.isInstalled = function(file, pathLib) {
	var path = pathLib || require("path");
	var name = path.basename(file);
	var base = name.substring(0, name.lastIndexOf(".")).toLowerCase()
			.replace(/[^a-zA-Z0-9-]/g, "");
	var id = "ca.canucksoftware.patches." + base
	return Package.isInstalled(id);
};

Patch.isWebOSInternalsInstalled = function(file, pathLib) {
	var path = pathLib || require("path");
	var name = path.basename(file);
	var base = name.substring(0, name.lastIndexOf(".")).toLowerCase()
			.replace(/[^a-zA-Z0-9-]/g, "");
	var id = "org.webosinternals.patches." + base
	return Package.isInstalled(id);
};
