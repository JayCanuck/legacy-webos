function StatusAssistant() {};  //status
StatusAssistant.prototype.run = function(future){
	future.result = {returnValue:true};
};

function VersionAssistant() {}; //version
VersionAssistant.prototype.run = function(future){
	future.result = {version:"1.1.2"};
};

function ReadAssistant() {}; //read
ReadAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.file) {
		this.file = this.controller.args.file;
		this.options = {};
		if(this.controller.args.offset && this.controller.args.length) {
			this.options.offset = this.controller.args.offset;
			this.options.length = this.controller.args.length;
		}
		this.subscribed = this.controller.args.subscribe || false;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.file)) {
			isTextFile(this.file, function(isText) {
				if(isText) {
					this.reader = new ReadWrite(this.controller.message, this.file);
					this.reader.read(this.options, this.subscribed);
				} else {
					this.future.result = {errorCode:"ERROR", errorText:"Invalid file."};
				}
			}.bind(this), this.pathLib);
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"File does not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function WriteAssistant() {}; //write
WriteAssistant.prototype.run = function(future){
	this.appid = getAppId(this);
	doWrite(this.controller.message, future, this.controller.args, this.appid, false);
};

function AppendAssistant() {}; //append
AppendAssistant.prototype.run = function(future){
	this.appid = getAppId(this);
	doWrite(this.controller.message, future, this.controller.args, this.appid, true);
};

function doWrite(message, future, args, appid, isAppend) {
	this.future = future;
	if(args.file && (args.text || args.str)) {
		this.message = message;
		this.appid = appid;
		this.file = args.file;
		this.content = args.text || args.str;
		this.append = isAppend || args.append || false;
		this.writer = new ReadWrite(this.message, this.file);
		if(this.append) {
			this.writer.append(this.content);
		} else {
			this.writer.write(this.content);
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
}

function ListAssistant() {}; //list
ListAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.path) {
		this.path = this.controller.args.path;
		if(!this.path.endsWith("/")) {
			this.path += "/";
		}
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.path)) {
			this.fs = require("fs");
			if(this.controller.args.ascending==undefined) {
				this.ascending = true;
			} else {
				this.ascending = this.controller.args.ascending;
			}
			this.sort = this.controller.args.sort || "name";
			this.skipHidden = this.controller.args.ignoreHidden || false;
			this.lookForChildren = false;
			this.searchChildrenDir = false;
			if(this.controller.args.lookForChildren
					&& this.controller.args.lookForChildren!="no"
					&& this.controller.args.lookForChildren!="none") {
				this.lookForChildren = true;
				if(this.controller.args.lookForChildren=="dirs") {
					this.searchChildrenDir = true;
				}
			}
			this.dirList = [];
			this.fileList = [];
			this.fs.readdir(this.path, function(err, files) {
				if(!err) {
					for(var i=0; i<files.length; i++) {
						files[i] = files[i].replace(/[\x00-\x1F]/g, "");
						if(((files[i].charAt(0)!="." && this.skipHidden)
								|| !this.skipHidden) && files[i].length!=0) {
							var item = {};
							item.name = files[i];
							item.path = this.path + item.name;
							try {
								var stat = this.fs.lstatSync(item.path);
								if(stat.isSymbolicLink()) {
									item.path = this.fs.realpathSync(item.path);
									stat = this.fs.lstatSync(item.path);
									item.link = "symbolic";
								} else {
									item.link = "no";
								}
								if(stat.isDirectory()) {
										item.bytes = 0;
										item.size = "-dir-";
										item.type = "-dir-";
										if(this.lookForChildren) {
											item.hasChildren = checkForChildren(item.path,
													this.searchChildrenDir, this.skipHidden,
													this.fs);
										}
										this.dirList.push(item);
								} else {
										if(stat.size) {
											item.bytes = stat.size;
											item.size = formatFilesize(item.bytes);
										} else { //rare case of null size
											item.bytes = 0;
											item.size = formatFilesize(item.bytes);
										}
										item.type = formatExt(item.name, this.pathLib);
										this.fileList.push(item);
								}
							} catch(e) {
								console.log("FileMgr error: " + e);
							}
							
						}
					}
					if(this.sort=="name") {
						this.dirList.sort(jsonArraySort(this.sort, !this.ascending,
								lowercase));
					} else {
						this.dirList.sort(jsonArraySort(this.sort, false, lowercase));
					}
					if(this.sort=="size") {
						this.fileList.sort(jsonArraySort("bytes", !this.ascending,
								parseInt));
					} else {
						this.fileList.sort(jsonArraySort(this.sort, !this.ascending,
								lowercase));
					}
					this.future.result = {dirs:this.dirList, files:this.fileList};
				} else {
					this.future.result = {errorCode:"ERROR", errorText:"Unable to "
							+ "scan files/directories."};
				}
			}.bind(this));
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"Directory does not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function ListDirAssistant() {}; //listDirs
ListDirAssistant.prototype.run = function(future){
	list(future, this.controller.args, true);
};

function ListFilesAssistant() {}; //listFiles
ListFilesAssistant.prototype.run = function(future){
	list(future, this.controller.args, false);
};

function list(future, args, dir) {
	this.future = future;
	if(args.path) {
		this.path = args.path;
		if(!this.path.endsWith("/")) {
			this.path += "/";
		}
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.path)) {
			this.dir = dir;
			this.fs = require("fs");
			if(args.ascending==undefined) {
				this.ascending = true;
			} else {
				this.ascending = args.ascending;
			}
			this.sort = args.sort || "name";
			this.skipHidden = args.ignoreHidden || false;
			this.lookForChildren = false;
			this.searchChildrenDir = false;
			if(args.lookForChildren && args.lookForChildren!="no"
					&& args.lookForChildren!="none") {
				this.lookForChildren = true;
				if(args.lookForChildren=="dirs") {
					this.searchChildrenDir = true;
				}
			}
			this.items = [];
			this.fs.readdir(this.path, function(err, files) {
				if(!err) {
					for(var i=0; i<files.length; i++) {
						files[i] = files[i].replace(/[\x00-\x1F]/g, "");
						if(((files[i].charAt(0)!="." && this.skipHidden)
								|| !this.skipHidden) && files[i].length!=0) {
							var item = {};
							item.name = files[i];
							item.path = this.path + item.name;
							try {
								var stat = this.fs.lstatSync(item.path);
								if(stat.isSymbolicLink()) {
									item.path = this.fs.realpathSync(item.path);
									stat = this.fs.lstatSync(item.path);
									item.link = "symbolic";
								} else {
									item.link = "no";
								}
								if(stat.isDirectory()==this.dir) {
									if(this.dir) {
										item.bytes = 0;
										item.size = "-dir-";
										item.type = "-dir-";
										if(this.lookForChildren) {
											item.hasChildren = checkForChildren(item.path,
													this.searchChildrenDir, this.skipHidden,
													this.fs);
										}
									} else {
										if(stat.size) {
											item.bytes = stat.size;
											item.size = formatFilesize(item.bytes);
										} else { //rare case of null size
											item.bytes = 0;
											item.size = formatFilesize(item.bytes);
										}
										item.type = formatExt(item.name, this.pathLib).toLowerCase();
									}
									this.items.push(item);
								}
							} catch(e) {
								console.log("FileMgr error: " + e);
							}
							
						}
					}
					if(this.sort=="size") {
						this.items.sort(jsonArraySort("bytes", !this.ascending, parseInt));
					} else {
						this.items.sort(jsonArraySort(this.sort, !this.ascending,
								lowercase));
					}
					this.future.result = {items:this.items};
				} else {
					this.future.result = {errorCode:"ERROR", errorText:"Unable to "
							+ "scan files/directories."};
				}
			}.bind(this));
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"Directory does not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function lowercase(string) {
	return string.toLowerCase();
}

function checkForChildren(path, lookForDir, ignoreHidden, fsLib) {
	var result = false;
	var fs = fsLib || require("fs");
	if(!path.endsWith("/")) {
		path += "/";
	}
	try {
		var children = fs.readdirSync(path);
		for(var i=0; i<children.length; i++) {
			if(!children[i].startsWith(".") || ignoreHidden==false) {
				try {
					var childStat = fs.lstatSync(path + children[i]);
					if(childStat.isDirectory()==lookForDir && !childStat.isSymbolicLink()) {
						result = true;
						break;
					}
				} catch(e2) {}
			}
		}
	} catch(e) {}
	
	return result;
};

function jsonArraySort(field, reverse, primer) {
	reverse = (reverse) ? -1 : 1;
	return function(a, b) {
		a = a[field];
		b = b[field];
		if (typeof(primer) != 'undefined'){
			a = primer(a);
			b = primer(b);
		}
		if (a<b) return reverse * -1;
		if (a>b) return reverse * 1;
		return 0;
	};
};

function QueryAssistant() {}; //query
QueryAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.items) {
		this.list = this.controller.args.items;
		this.pathLib = require("path");
		this.fs = require("fs");
		this.items = []
		this.exists = [];
		for(var i=0; i<this.list.length; i++) {
			var curr = {name:this.pathLib.basename(this.list[i]), path:this.list[i]};
			if(this.pathLib.existsSync(this.list[i])) {
				try {
					var stat = this.fs.lstatSync(this.list[i]);
					if(stat.isSymbolicLink()) {
						curr.realPath = this.fs.realpathSync(this.list[i]);
						stat = this.fs.statSync(curr.realPath);
						curr.link = "symbolic";
					} else {
						curr.realPath = this.list[i];
						curr.link = "no";
					}
					if(stat.isDirectory()) {
						curr.size = "-dir-";
						curr.type = "-dir-";
						curr.kind = "folder";
					} else {
						if(stat.size) {
							curr.size = formatFilesize(stat.size);
						} else { //rare case of null size
							curr.size = formatFilesize(0);
						}
						curr.type = formatExt(curr.name, this.pathLib).toLowerCase();
						curr.kind = "file";
					}
					this.items.push(curr);
					this.exists.push(this.list[i]);
				} catch(e) {}
			}
		}
		this.future.result = {items:this.items, exists:this.exists};
	} else {
		this.future.result = {errorCode:"ERROR",
				errorText:"Improperly formatted request."};
	}
};

function SizeAssistant() {}; //size
SizeAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.file) {
		this.file = this.controller.args.file;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.file)) {
			this.fs = require("fs");
			this.stat = this.fs.lstatSync(this.file)
			if(this.stat.isSymbolicLink()) {
				this.realPath = this.fs.realpathSync(this.file);
				this.stat = this.fs.statSync(this.realPath);
			}
			if(this.stat.isDirectory()) {
				this.cmd = new CommandLine("/usr/bin/du -s \"" + this.file + "\"");
				this.cmd.run(function(response) {
					var size = parseInt(response.stdout.split(/\s+/)[0]) * 1024;
					if(this.controller.args.formatted) {
						this.future.result = {size:formatFilesize(size)};
					} else {
						this.future.result = {size:size};
					}
				}.bind(this));
			} else {
				if(this.controller.args.formatted) {
					this.future.result = {size:formatFilesize(this.stat.size)};
				} else {
					this.future.result = {size:this.stat.size};
				}
			}
			
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"File/directory does not"
					+ " exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function DeleteAssistant() {}; //delete
DeleteAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.file) {
		this.file = this.controller.args.file;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.file)) {
			this.cmd = new CommandLine("/bin/rm -fr \"" + this.file + "\"",
					this.future);
			this.cmd.run();
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"File/directory does "
					+ "not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function ExistsAssistant() {}; //exists
ExistsAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.file) {
		this.file = this.controller.args.file;
		this.pathLib = require("path");
		this.future.result = {exists:this.pathLib.existsSync(this.file)};
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function ParentAssistant() {}; //getParent
ParentAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.file) {
		this.file = this.controller.args.file;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.file)) {
			if(this.file.endsWith("/") && this.file.length!=1) {
				this.file = this.file.substring(0, this.file.length-1);
			}
			this.parent = this.pathLib.dirname(this.file);
			if(this.parent=="") {
				this.parent = "/";
			}
			if(this.file!="/" && this.pathLib.existsSync(this.parent)) {
				this.future.result = {parent:this.parent};
			} else {
				this.future.result = {errorCode:"ERROR", errorText:"Parent directory "
						+ "does not exist."};
			}
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"File/directory does "
					+ "not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function IsDirectoryAssistant() {}; //isDirectory
IsDirectoryAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.dir) {
		this.dir = this.controller.args.dir;
		this.fsLib = require("fs");
		try {
			this.stat = this.fsLib.lstatSync(this.dir)
			if(this.stat.isSymbolicLink()) {
				this.dir = this.fsLib.realpathSync(this.dir);
				this.stat = this.fsLib.lstatSync(this.dir);
			}
			this.future.result = {isDirectory:this.stat.isDirectory()};
		} catch(e) {
			this.future.result = {isDirectory:false};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function IsFileAssistant() {}; //isFile
IsFileAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.file) {
		this.file = this.controller.args.file;
		this.fsLib = require("fs");
		try {
			this.stat = this.fsLib.lstatSync(this.file)
			if(this.stat.isSymbolicLink()) {
				this.file = this.fsLib.realpathSync(this.file);
				this.stat = this.fsLib.lstatSync(this.file);
			}
			this.future.result = {isFile:this.stat.isFile()};
		} catch(e) {
			this.future.result = {isFile:false};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function HasDirectoryAssistant() {}; //hasDirectory
HasDirectoryAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.dir) {
		this.dir = this.controller.args.dir;
		this.ignoreHidden = false;
		if(this.controller.args.ignoreHidden) {
			this.ignoreHidden = this.controller.args.ignoreHidden;
		}
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.dir)) {
			this.future.result = {dir:this.dir, has:checkForChildren(this.dir, true,
					this.ignoreHidden)}
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"Directory does not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function HasFileAssistant() {}; //hasFile
HasFileAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.dir) {
		this.dir = this.controller.args.dir;
		this.ignoreHidden = false;
		if(this.controller.args.ignoreHidden) {
			this.ignoreHidden = this.controller.args.ignoreHidden;
		}
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.dir)) {
			this.future.result = {dir:this.dir, has:checkForChildren(this.dir, false,
					this.ignoreHidden)}
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"Directory does not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function IsTextFileAssistant() {}; //isTextFile
IsTextFileAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.file) {
		this.file = this.controller.args.file;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.file)) {
			isTextFile(this.file, function(isPlainText) {
				this.future.result = {isText:isPlainText};
			}.bind(this));
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"File does not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function LastModifiedAssistant() {}; //getLastModified
LastModifiedAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.file) {
		this.file = this.controller.args.file;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.file)) {
			this.fsLib = require("fs");
			this.fsLib.stat(this.file, function(err, stats) {
				if(!err) {
					var d = new Date(stats.mtime);
					this.future.result = {lastModified:d.getTime()+""};
				} else {
					this.future.result = {errorCode:"ERROR", errorText:"Unable to scan "
							+ "file/directory."};
				}
			}.bind(this));
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"File/directory does "
					+ "not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function IsReadOnlyAssistant() {}; //isReadOnly
IsReadOnlyAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.file) {
		this.file = this.controller.args.file;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.file)) {
			var mtoolsPath = this.file.replace("/media/internal", "A:");
			this.cmd = new CommandLine("/usr/bin/mattrib \"" + mtoolsPath + "\"");
			//this.cmd.setEnv({"MTOOLSRC":"/media/cryptofs/apps/usr/palm/services/"
			//		+ "ca.canucksoftware.filemgr.service/mtools.conf"});
			this.cmd.run(function(response) {
				var check = response.stdout.substring(0, response.stdout
						.indexOf("A:/"));
				this.future.result = {readonly:(check.indexOf("R")>-1)};
			}.bind(this));
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"File/directory does "
					+ "not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function SetReadOnlyAssistant() {}; //setReadOnly
SetReadOnlyAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.file && this.controller.args.readonly!=undefined) {
		this.file = this.controller.args.file;
		this.readonly = this.controller.args.readonly;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.file)) {
			var value = "-R";
			if(this.readonly) {
				value = "+R";
			}
			var mtoolsPath = this.file.replace("/media/internal", "A:");
			this.cmd = new CommandLine("/usr/bin/mattrib " + value + " \""
					+ mtoolsPath + "\"", this.future);
			//this.cmd.setEnv({"MTOOLSRC":"/media/cryptofs/apps/usr/palm/services/"
			//		+ "ca.canucksoftware.filemgr.service/mtools.conf"});
			this.cmd.run();
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"File/directory does "
					+ "not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function CreateDirAssistant() {}; //createDir
CreateDirAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.path) {
		this.dir = this.controller.args.path;
		this.cmd = new CommandLine("/bin/mkdir -p \"" + this.dir + "\"",
				this.future);
		this.cmd.run(function(response) {
			this.future.result = {created:(response.code==0)};
		}.bind(this));
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function CreateFileAssistant() {}; //createFile
CreateFileAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.path) {
		this.file = this.controller.args.path;
		this.cmd = new CommandLine("/bin/touch \"" + this.file + "\"",
				this.future);
		this.cmd.run(function(response) {
			this.future.result = {created:(response.code==0)};
		}.bind(this));
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function RenameAssistant() {}; //rename
RenameAssistant.prototype.run = function(future){
	mv(future, this.controller.args, getAppId(this));
};

function MoveAssistant() {}; //move
MoveAssistant.prototype.run = function(future){
	mv(future, this.controller.args, getAppId(this));
};

function mv(future, args, appid) {
	this.future = future;
	this.args = args;
	this.appid = appid;
	if(this.args.from && this.args.to) {
		this.from = this.args.from;
		this.to = this.args.to;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.from)) {
			this.fsLib = require("fs");
			this.resolvedFrom = this.fsLib.realpathSync(this.from);
			if(this.args.preserveSymlink
					&& this.from!=this.resolvedFrom) {
				this.cmd = new CommandLine("/bin/mv -f \"" + this.from + "\" \""
						+ this.to + "\" ; sync", this.future);
				this.cmd.run();
			} else if(this.from!=this.to && this.resolvedFrom!=this.to) {
				if(this.pathLib.existsSync(this.to)) {
					var stats = this.fsLib.statSync(this.to);
					if(stats.isDirectory()) {
						var escapedFrom = this.resolvedFrom.replace(/ /g, "\\ ");
						this.cmd = new CommandLine("/bin/mv -f " + escapedFrom
								+ "/* \"" + this.to + "\" ; /bin/rmdir \""
								+ this.resolvedFrom + "\" ; sync", this.future);
						this.cmd.run();
					} else {
						this.cmd = new CommandLine("/bin/mv -f \""
								+ this.resolvedFrom + "\" \"" + this.to + "\" ; sync",
								this.future);
						this.cmd.run();
					}
				} else {
					this.cmd = new CommandLine("/bin/mv -f \"" + this.resolvedFrom
							+ "\" \"" + this.to + "\" ; sync", this.future);
					this.cmd.run();
				}
			} else {
				this.future.result = {errorCode:"ERROR", errorText:"Source and "
						+ "destination match."};
			}
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"File/directory does "
					+ "not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function CopyAssistant() {}; //copy
CopyAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.from && this.controller.args.to) {
		this.from = this.controller.args.from;
		this.to = this.controller.args.to;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.from)) {
			this.fsLib = require("fs");
			this.resolvedFrom = this.fsLib.realpathSync(this.from);
			if(this.controller.args.preserveSymlink
					&& this.from!=this.resolvedFrom) {
				this.cmd = new CommandLine("/bin/cp -f \"" + this.from + "\" \""
						+ this.to + "\"", this.future);
				this.cmd.run();
			} else if(this.from!=this.to && this.resolvedFrom!=this.to) {
				var stats = this.fsLib.statSync(this.resolvedFrom);
				if(stats.isDirectory()) {
					if(this.pathLib.existsSync(this.to)) {
						var escapedFrom = this.resolvedFrom.replace(/ /g, "\\ ");
						this.cmd = new CommandLine("/bin/cp -fr " + escapedFrom
								+ "/* \"" + this.to + "\"", this.future);
						this.cmd.run();
					} else {
						this.cmd = new CommandLine("/bin/cp -fr \""
								+ this.resolvedFrom + "\" \"" + this.to + "\"",
								this.future);
						this.cmd.run();
					}
				} else {
					this.cmd = new CommandLine("/bin/cp -f \"" + this.resolvedFrom
							+ "\" \"" + this.to + "\"", this.future);
					this.cmd.run();
				}
			} else {
				this.future.result = {errorCode:"ERROR", errorText:"Source and "
						+ "destination match."};
			}
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"File/directory does "
					+ "not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};
