function require(key) {
	return IMPORTS.require(key);
};

String.prototype.trim = function() {
	return this.replace(/^\s+|\s+$/g,"");
};

String.prototype.startsWith = function(str) {
	return (this.indexOf(str) == 0);
};

String.prototype.endsWith = function(str) {
	var lastIndex = this.lastIndexOf(str);
    return (lastIndex != -1) && (lastIndex + str.length == this.length);
};

Array.prototype.jsonSort = function(field, reverse, primer) {
	if(this.length>0) {
		var lowercase = function(string) { return string.toLowerCase(); };
		reverse = (reverse) ? -1 : 1;
		if(!primer) {
			var type = typeof this[0][field];
			if(type == "number") {
				primer = parseFloat;
			} else if(type == "string") {
				primer = lowercase;
			}
		}
		this.sort(function(a, b) {
			a = a[field];
			b = b[field];
			if (typeof(primer) != 'undefined'){
				a = primer(a);
				b = primer(b);
			}
			if (a<b) return reverse * -1;
			if (a>b) return reverse * 1;
			return 0;
		});
	}
};

function getAppId(assistant) {
	var id = assistant.controller.message.applicationID();
	var index = id.indexOf(" ");
	if(index>-1) {
		id = id.substring(0, index);
	}
	return id;
};

function validForPrivate(assistant) {
	var appid = getAppId(assistant);
	return (appid=="" || appid=="ca.canuckcoding.internalz");
}

function log(s) {
	try {
		var stream = require("fs").createWriteStream("/media/internal/comics/.log", {flags:"a"});
		stream.write(new Date().getTime() + ": " + s + "\n");
		stream.end();
	} catch(e) {}
}

function isTextFile(file, callback, pathLib) {
	var exts = ["sh", "bat", "service", "mk", "html", "mhtml", "htm", "xml", "js",
			"css", "json", "php", "txt", "log", "conf", "ini", "c", "cpp", "cs",
			"vb", "java", "patch", "control", "list", "postinst", "prerm", "cfg",
			"packages", "properties", "config", "orig", "script", "diff"];
	var path = pathLib || require("path");
	if(exts.indexOf(path.extname(file).substring(1))>-1) {
		callback(true);
	} else {
		var cmd = new CommandLine("tr -dc \\\\0 < \"" + file + "\" | wc -c");
		cmd.run(function(response) {
			callback((response.stdout.trim()=="0"));
		});
	}
};

function isSymlink(file, fs) {
	var fsLib = fs || require("fs");
	var stats = fsLib.lstatSync(file);
	return stats.isSymbolicLink();
};

function formatExt(name, pathLib) {
	var lib = pathLib || require("path");
	var result = lib.extname(name);
	if(result.length==0) {
		result = "---";
	}
	if(result.startsWith(".")) {
		result = result.substring(1);
	}
	return result;
};

function formatFilesize(size) {
	var result;
	var roundNicely = function(number) {
		var rounded;
		if(number<10) {
		    rounded = number.toFixed(2) + "";
			rounded = rounded.replace(".00", "")
		} else if(number<100) {
		    rounded = number.toFixed(1) + "";
			rounded = rounded.replace(".0", "")
		} else {
		    rounded = number.toFixed(0) + "";
		}
		return rounded;
	};
	if(size<1000) {
		result = size + "B";
	} else if((size/1024)<1000) {
		result = roundNicely((size/1024.0)) + "KB";
	} else if(((size/1024)/1024)<1000) {
		result = roundNicely(((size/1024.0)/1024.0)) + "MB";
	} else {
		result = roundNicely((((size/1024.0)/1024.0)/1024.0)) + "GB";
	}
	return result;
};
