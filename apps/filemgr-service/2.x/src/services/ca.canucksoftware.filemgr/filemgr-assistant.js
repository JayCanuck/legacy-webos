function StatusAssistant() {};  //status
StatusAssistant.prototype.run = function(future){
	future.result = {returnValue:true};
};

function VersionAssistant() {}; //version
VersionAssistant.prototype.run = function(future){
	future.result = {version:"2.0.9"};
};


function InternalzInstalledAssistant() {}; //isInternalzInstalled
InternalzInstalledAssistant.prototype.run = function(future){
	future.result = {installed:Package.isInstalled("ca.canuckcoding.internalz")};
};

function HideInternalzAssistant() {}; //hideInternalz
HideInternalzAssistant.prototype.run = function(future){
	this.future = future;
	this.script = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.filemgr"
			+ "/scripts/hide.sh";
	this.cmd = new CommandLine("/bin/sh " + this.script + " ca.canuckcoding.internalz",
			this.future);
	this.cmd.run();
};

function HideInternalzProAssistant() {}; //hideInternalzPro
HideInternalzProAssistant.prototype.run = function(future){
	this.future = future;
	this.script = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.filemgr"
			+ "/scripts/hide.sh";
	this.cmd = new CommandLine("/bin/sh " + this.script + " ca.canucksoftware.internalz",
			this.future);
	this.cmd.run();
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
		writePermissable(this.file, this.appid, function(writable) {
			if(writable) {
				this.writer = new ReadWrite(this.message, this.file);
				if(this.append) {
					this.writer.append(this.content);
				} else {
					this.writer.write(this.content);
				}
			} else {
				this.future.result = {errorCode:"ERROR", errorText:"That section "
						+ "of the device is off limits for data writing/deleting."};
			}
		}.bind(this));
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
}


function ListDirAssistant() {}; //listDirs
ListDirAssistant.prototype.run = function(future){
	list(future, this.controller.args, true);
};

function ListFilesAssistant() {}; //listFiles
ListFilesAssistant.prototype.run = function(future){
	list(future, this.controller.args, false);
};

/*function listOld(future, args, dir) {
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
			this.ascending = args.ascending || true;
			this.sort = args.sort || "name";
			this.skipHidden = args.ignoreHidden || false;
			this.lookForChildren = false;
			this.searchChildrenDir = false;
			if(args.lookForChildren && args.lookForChildren!="no"
					&& args.lookForChildren!="none") {
				this.lookForChildren = true;
				if(args.lookForChildren=="dir") {
					this.searchChildrenDir = true;
				}
			}
			getMsDosVisibleList(this.path, this.skipHidden, function(list) {
				this.visibleList = list;
				this.flags = "-Al1";
				if(this.sort=="size") {
					this.flags += "S";
				} else if(this.sort="type") {
					this.flags += "X";
				}
				if((this.ascending && this.sort=="size")
						|| (!this.ascending && this.sort!="size")) {
					this.flags += "r";
				}
				this.ls = new CommandLine("/bin/ls " + this.flags + " \"" + this.path
						+ "\"");
				this.ls.run(function(response) {
					if(response.code==0) {
						var entries = response.stdout.split("\n");
						var itemList = [];
						for(var i=0; i<entries.length; i++) {
							if(entries[i].length>0) {
								var currItem = {};
								currItem.name = entries[i].substring(entries[i]
										.lastIndexOf(":")+4);
								if(entries[i].charAt(0)=="l") {
									currItem.path = currItem.name.substring(currItem
											.name.indexOf(">")+2);
									currItem.name = currItem.name.substring(0, currItem
											.name.indexOf(" ->"));
									var stat = this.fs.statSync(currItem.path);
									if(stat.isDirectory()) {
										entries[i] = entries[i].replace("l", "d");
									} else {
										entries[i] = entries[i].replace("l", "-");
									}
									currItem.link = "symbolic";
								} else {
									currItem.path = this.path + currItem.name;
									currItem.link = "no";
								}
								if((entries[i].charAt(0)=="d")==this.dir) {
									if(!this.skipHidden || (this.skipHidden
											&& isNotHidden(this.path + currItem.name,
											this.visibleList, this.pathLib))) {
										var tokens = entries[i].split(/\s+/);
										if(entries[i].charAt(0)=="d") {
											currItem.size = "-dir-";
											currItem.type = "-dir-";
										} else {
											currItem.size = formatFilesize(tokens[2]);
											currItem.type = formatExt(currItem.name,
													this.pathLib);
										}
										if(this.lookForChildren && this.dir) {
											currItem.hasChildren =
													checkForChildren(currItem.path,
													this.searchChildrenDir, this.fs);
										}
										itemList.push(currItem);
									}
								}
							}
						}
						this.future.result = {items:itemList};
					} else {
						this.future.result = {errorCode:"ERROR", errorText:"Unable to "
								+ "scan files/directories."};
					}
				}.bind(this));
			}.bind(this));
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"Directory does not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function getMsDosVisibleList(dir, skipHidden, callback) {
	if(!skipHidden || !dir.startsWith("/media/internal")) {
		callback(undefined);
	} else {
		var mdirPath = dir.replace("/media/internal", "A:");
		var cmd = new CommandLine("/usr/bin/mdir -fb \"" + mdirPath + "\"");
		cmd.run(function(response) {
			var list = [];
			if(response.code==0) {
				if(response.stdout.startsWith("A:")) {
					var tokens = response.stdout.substring(2).split("\nA:");
					for(var i=0; i<tokens.length; i++) {
						list.push(tokens[i]);
					}
				}
			} else {
				list = undefined; //emulator
			}
			callback(list);
		});
	}
};

function isNotHidden(filepath, visibleList, pathLib) {
	result = true;
	if(visibleList==undefined) {
		var path = pathLib || require("path");
		result = !(path.basename(filepath).charAt(0)==".");
	} else {
		var fpath = filepath.replace("/media/internal", "");
		result = (visibleList.indexOf(fpath)>-1) || (visibleList.indexOf(fpath+"/")>-1);
	}
	return result;
};*/

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
			this.scanDirNode =  function(err, files) {
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
			};
			this.scanMsdosDir =  function(response) {
				if(response.code==0) {
					var lines = response.stdout.split("\n");
					for(var i=4; i<lines.length-2; i++) {
						var item = {};
						lines[i] = lines[i].trim();
						var tokens = lines[i].split(/\s+/);
						if((tokens[1]=="<DIR>" || tokens[2]=="<DIR>")==this.dir) {
							if(tokens[tokens.length-1].indexOf(":")>-1) {
								item.name = tokens[0];
								if(!this.dir && !isNaN(tokens[2])) {
									item.name += "." + tokens[1];
								}
							} else {
								item.name = lines[i].substring(lines[i].lastIndexOf(":")+5);
							}
							if(item.name!="." && item.name!="..") {
								item.path = this.path + item.name;
								item.link = "no";
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
									item.type = formatExt(item.name, this.pathLib);
									if(item.type=="---") {
										item.bytes = parseInt(tokens[1]);
									} else {
										item.bytes = parseInt(tokens[2]);
									}
									if(item.bytes==null || item.bytes==0) {
										try {
											var stat = this.fs.statSync(item.path);
											item.bytes = stat.size;
										} catch(err) {}
									}
									item.size = formatFilesize(item.bytes);
								}
								this.items.push(item);
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
					this.fs.readdir(this.path, this.scanDirNode.bind(this));
				}
			};
			var BASE = "/media/internal";
			if(this.path.startsWith(BASE) && this.skipHidden) {
				this.mtoolscmd = "/usr/bin/mdir -f";
				if(!this.skipHidden) {
					this.mtoolscmd += "a";
				}
				this.mtoolscmd += " \"" + this.path.replace(BASE, "A:") + "\"";
				this.cmd = new CommandLine(this.mtoolscmd);
				this.cmd.run(this.scanMsdosDir.bind(this));
			} else {
				this.fs.readdir(this.path, this.scanDirNode.bind(this));
			}
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
			if(path.startsWith("/media/internal/") || !children[i].startsWith(".")
					|| ignoreHidden==false) {
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
			writePermissable(this.file, getAppId(this), function(writable) {
				if(writable) {
					this.cmd = new CommandLine("/bin/rm -fr \"" + this.file + "\"",
							this.future);
					this.cmd.rescanFileindexer(true);
					this.cmd.run();
				} else {
					this.future.result = {errorCode:"ERROR", errorText:"That section "
							+ "of the device is off limits for data writing/deleting."};
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

function IsHiddenAssistant() {}; //isHidden
IsHiddenAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.file) {
		this.file = this.controller.args.file;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.file)) {
			isHidden(this.file, this.pathLib, function(hidden) {
				this.future.result = {isHidden:hidden};
			}.bind(this));
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"File/directory does "
					+ "not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function isHidden(file, pathLib, callback) {
	var BASE = "/media/internal/";
	var path = pathLib || require("path");
	if(file.startsWith(BASE) && file.length>BASE.length) {
		var mdirPath = file.replace("/media/internal", "A:");
		var cmd = new CommandLine("/usr/bin/mattrib \"" + mdirPath + "\"");
		cmd.run(function(response) {
			if(response.code==0) {
				var check = response.stdout.substring(0, response.stdout.indexOf("A:/"));
				callback((check.indexOf("H")>-1));
			} else {
				callback((path.basename(filepath).charAt(0)=="."));
			}
		});
	} else {
		callback((path.basename(filepath).charAt(0)=="."));
	}
};

function SetHiddenAssistant() {}; //setHidden
SetHiddenAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.file && this.controller.args.hidden!=undefined) {
		this.file = this.controller.args.file;
		this.hidden = this.controller.args.hidden;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.file)) {
			var BASE = "/media/internal/";
			if(this.file.startsWith(BASE) && this.file.length>BASE.length) {
				var value = "-H";
				if(this.hidden) {
					value = "+H";
				}
				var mtoolspath = this.file.replace("/media/internal", "A:");
				this.cmd = new CommandLine("/usr/bin/mattrib " + value + " \""
						+ mtoolspath + "\"", this.future);
				this.cmd.run();
			} else {
				var name = this.pathLib.basename(this.file);
				var dir = this.pathLib.dirname(this.file);
				this.file2 = dir + "/." + name;
				if(!this.hidden) {
					if(name.startsWith(".")) {
						name = name.substring(1);
					}
					this.file2 = dir + "/" + name;
				}
				if(!this.pathLib.existsSync(this.file2)) {
					this.cmd = new CommandLine("/bin/mv -f \"" + this.file + "\" \""
							+ this.file2 + "\"", this.future);
					this.cmd.run();
				} else {
					this.future.result = {errorCode:"ERROR", errorText:"Cannot hide, "
							+ "filepath already exists."};
				}
			}
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"File/directory does "
					+ "not exist."};
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
			var BASE = "/media/internal/";
			if(this.file.startsWith(BASE) && this.file.length>BASE.length) {
				var mtoolsPath = this.file.replace("/media/internal", "A:");
				this.cmd = new CommandLine("/usr/bin/mattrib \"" + mtoolsPath + "\"");
				this.cmd.run(function(response) {
					var check = response.stdout.substring(0, response.stdout
							.indexOf("A:/"));
					this.future.result = {readonly:(check.indexOf("R")>-1)};
				}.bind(this));
			} else {
				var BASE2 = "/media/cryptofs/apps/";
				if(this.file.startsWith(BASE2) && this.file.length>BASE2.length) {
					this.future.result = {readonly:false};
				} else {
					this.future.result = {errorCode:"ERROR", errorText:"File is not in "
							+ "the FAT32 partition."};
				}
			}
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
			var BASE = "/media/internal/";
			if(this.file.startsWith(BASE) && this.file.length>BASE.length) {
				var value = "-R";
				if(this.readonly) {
					value = "+R";
				}
				var mtoolsPath = this.file.replace("/media/internal", "A:");
				this.cmd = new CommandLine("/usr/bin/mattrib " + value + " \""
						+ mtoolsPath + "\"", this.future);
				this.cmd.run();
			} else {
				var BASE2 = "/media/cryptofs/apps/";
				if(this.file.startsWith(BASE2) && this.file.length>BASE2.length) {
					this.future.result = {errorCode:"ERROR", errorText:"Unable to set "
							+ "attribute in cryptofs area."};
				} else {
					this.future.result = {errorCode:"ERROR", errorText:"File is not in "
							+ "the FAT32 partition."};
				}
			}
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"File/directory does "
					+ "not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function PermissionsAssistant() {}; //permissions
PermissionsAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.file) {
		this.file = this.controller.args.file;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.file)) {
			this.cmd = new CommandLine("/bin/ls -ald \"" + this.file + "\"");
			this.cmd.run(function(response) {
				var val = response.stdout.substring(0, 10);
				this.future.result = {value:val};
			}.bind(this));
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"File/directory does "
					+ "not exist."};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function ChmodAssistant() {}; //chmod
ChmodAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.file && this.controller.args.value) {
		this.file = this.controller.args.file;
		this.value = this.controller.args.value;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.file)) {
			writePermissable(this.file, getAppId(this), function(writable) {
				if(writable) {
					this.cmd = new CommandLine("/bin/chmod -R " + this.value + " \""
							+ this.file + "\"", this.future);
					this.cmd.run();
				} else {
					this.future.result = {errorCode:"ERROR", errorText:"That section "
							+ "of the device is off limits for data writing/deleting."};
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

function CreateDirAssistant() {}; //createDir
CreateDirAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.path) {
		this.dir = this.controller.args.path;
		writePermissable(this.dir, getAppId(this), function(writable) {
			if(writable) {
				this.cmd = new CommandLine("/bin/mkdir -p \"" + this.dir + "\"",
						this.future);
				this.cmd.run(function(response) {
					this.future.result = {created:(response.code==0)};
				}.bind(this));
			} else {
				this.future.result = {errorCode:"ERROR", errorText:"That section "
						+ "of the device is off limits for data writing/deleting."};
			}
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
		writePermissable(this.file, getAppId(this), function(writable) {
			if(writable) {
				this.cmd = new CommandLine("/bin/touch \"" + this.file + "\"",
						this.future);
				this.cmd.rescanFileindexer(true);
				this.cmd.run(function(response) {
					this.future.result = {created:(response.code==0)};
				}.bind(this));
			} else {
				this.future.result = {errorCode:"ERROR", errorText:"That section "
						+ "of the device is off limits for data writing/deleting."};
			}
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
			writePermissable(this.to, this.appid, function(writable) {
				if(writable) {
					this.fsLib = require("fs");
					this.resolvedFrom = this.fsLib.realpathSync(this.from);
					if(this.args.preserveSymlink
							&& this.from!=this.resolvedFrom) {
						this.cmd = new CommandLine("/bin/mv -f \"" + this.from + "\" \""
								+ this.to + "\" ; sync", this.future);
						this.cmd.rescanFileindexer(true);
						this.cmd.run();
					} else if(this.from!=this.to && this.resolvedFrom!=this.to) {
						if(this.pathLib.existsSync(this.to)) {
							var stats = this.fsLib.statSync(this.to);
							if(stats.isDirectory()) {
								var escapedFrom = this.resolvedFrom.replace(/ /g, "\\ ");
								this.cmd = new CommandLine("/bin/mv -f " + escapedFrom
										+ "/* \"" + this.to + "\" ; /bin/rmdir \""
										+ this.resolvedFrom + "\" ; sync", this.future);
								this.cmd.rescanFileindexer(true);
								this.cmd.run();
							} else {
								this.cmd = new CommandLine("/bin/mv -f \""
										+ this.resolvedFrom + "\" \"" + this.to + "\""
										+ " ; sync", this.future);
								this.cmd.rescanFileindexer(true);
								this.cmd.run();
							}
						} else {
							this.cmd = new CommandLine("/bin/mv -f \"" + this.resolvedFrom
									+ "\" \"" + this.to + "\" ; sync", this.future);
							this.cmd.rescanFileindexer(true);
							this.cmd.run();
						}
					} else {
						this.future.result = {errorCode:"ERROR", errorText:"Source and "
								+ "destination match."};
					}
				} else {
					this.future.result = {errorCode:"ERROR", errorText:"That section "
							+ "of the device is off limits for data writing/deleting."};
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

function CopyAssistant() {}; //copy
CopyAssistant.prototype.run = function(future){
	this.future = future;
	if(this.controller.args.from && this.controller.args.to) {
		this.from = this.controller.args.from;
		this.to = this.controller.args.to;
		this.pathLib = require("path");
		if(this.pathLib.existsSync(this.from)) {
			writePermissable(this.to, getAppId(this), function(writable) {
				if(writable) {
					this.fsLib = require("fs");
					this.resolvedFrom = this.fsLib.realpathSync(this.from);
					if(this.controller.args.preserveSymlink
							&& this.from!=this.resolvedFrom) {
						this.cmd = new CommandLine("/bin/cp -f \"" + this.from + "\" \""
								+ this.to + "\"", this.future);
						this.cmd.rescanFileindexer(true);
						this.cmd.run();
					} else if(this.from!=this.to && this.resolvedFrom!=this.to) {
						var stats = this.fsLib.statSync(this.resolvedFrom);
						if(stats.isDirectory()) {
							if(this.pathLib.existsSync(this.to)) {
								var escapedFrom = this.resolvedFrom.replace(/ /g, "\\ ");
								this.cmd = new CommandLine("/bin/cp -fr " + escapedFrom
										+ "/* \"" + this.to + "\"", this.future);
								this.cmd.rescanFileindexer(true);
								this.cmd.run();
							} else {
								this.cmd = new CommandLine("/bin/cp -fr \""
										+ this.resolvedFrom + "\" \"" + this.to + "\"",
										this.future);
								this.cmd.rescanFileindexer(true);
								this.cmd.run();
							}
						} else {
							this.cmd = new CommandLine("/bin/cp -f \"" + this.resolvedFrom
									+ "\" \"" + this.to + "\"", this.future);
							this.cmd.rescanFileindexer(true);
							this.cmd.run();
						}
					} else {
						this.future.result = {errorCode:"ERROR", errorText:"Source and "
								+ "destination match."};
					}
				} else {
					this.future.result = {errorCode:"ERROR", errorText:"That section "
							+ "of the device is off limits for data writing/deleting."};
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

function RescanFileindexerAssistant() {}; //rescanFileindexer
RescanFileindexerAssistant.prototype.run = function(future){
	this.future = future;
	this.script = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.filemgr/"
			+ "scripts/fileindexer.sh";
	this.cmd = new CommandLine("/bin/sh " + script, this.future);
	this.cmd.run();
};

function GCAssistant() {}; //gc
GCAssistant.prototype.run = function(future){
	this.future = future;
	this.ls = new LunaSend("palm://com.palm.lunastats/gc", {}, this.future);
	this.ls.run();
};

function RegisterHandlerAssistant() {}; //registerAsHandler
RegisterHandlerAssistant.prototype.run = function(future){
	this.future = future;
	if(validForPrivate(this)) {
		this.script = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.filemgr/"
				+ "scripts/handling.sh";
		this.viewsource = "disable";
		this.images = "disable";
		this.text = "disable";
		this.ipk = "disable";
		if(this.controller.args.viewsource) {
			this.viewsource = "enable";
		}
		if(this.controller.args.images) {
			this.images = "enable";
		}
		if(this.controller.args.text) {
			this.text = "enable";
		}
		if(this.controller.args.ipk) {
			this.ipk = "enable";
		}
		this.cmd = new CommandLine("/bin/sh " + this.script + " \"" + this.viewsource
				+ "\" \"" + this.images + "\" \"" + this.text + "\" \"" + this.ipk + "\"",
				this.future);
		this.cmd.run();
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"This is a private function "
				+ "reserved for Internalz."};
	}
};

function MimeTableAssistant() {}; //dumpMimeTable
MimeTableAssistant.prototype.run = function(future){
	this.future = future;
	if(validForPrivate(this)) {
		this.ls = new LunaSend("palm://com.palm.applicationManager/dumpMimeTable", {},
				this.future);
		this.ls.run();
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"This is a private function "
				+ "reserved for Internalz."};
	}
};

function SwapResourceAssistant() {}; //swapResource
SwapResourceAssistant.prototype.run = function(future){
	this.future = future;
	this.mime = this.controller.args.mimeType;
	this.index = this.controller.args.index;
	if(validForPrivate(this)) {
		this.ls = new LunaSend("palm://com.palm.applicationManager/swapResource",
				{mimeType:this.mime, index:this.index}, this.future);
		this.ls.run();
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"This is a private function "
				+ "reserved for Internalz."};
	}
};

function PkgInfoAssistant() {}; //getPackageInfo
PkgInfoAssistant.prototype.run = function(future){
	this.future = future;
	this.file = this.controller.args.file;
	if(validForPrivate(this)) {
		this.pkg = new Package(this.future);
		this.pkg.getInfo(this.file);
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"This is a private function "
				+ "reserved for Internalz."};
	}
};

function IsPkgInstalledAssistant() {}; //isPackageInstalled
IsPkgInstalledAssistant.prototype.run = function(future){
	this.future = future;
	this.appid = this.controller.args.appId;
	if(validForPrivate(this)) {
		this.future.result = {installed:Package.isInstalled(this.appid)};
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"This is a private function "
				+ "reserved for Internalz."};
	}
};

function InstallPkgAssistant() {}; //installPackage
InstallPkgAssistant.prototype.run = function(future){
	this.future = future;
	this.file = this.controller.args.file;
	this.appid = this.controller.args.appId;
	if(validForPrivate(this)) {
		this.pkg = new Package(this.future);
		this.pkg.install(this.file, this.appid);
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"This is a private function "
				+ "reserved for Internalz."};
	}
};

function UninstallPkgAssistant() {}; //uninstallPackage
UninstallPkgAssistant.prototype.run = function(future){
	this.future = future;
	this.appid = this.controller.args.appId;
	if(validForPrivate(this)) {
		this.pkg = new Package(this.future);
		this.pkg.uninstall(this.appid);
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"This is a private function "
				+ "reserved for Internalz."};
	}
};

function IsPatchInstalledAssistant() {}; //isPatchInstalled
IsPatchInstalledAssistant.prototype.run = function(future){
	this.future = future;
	this.file = this.controller.args.file;
	if(validForPrivate(this)) {
		this.pathLib = require("path");
		this.future.result = {installed:Patch.isInstalled(this.file, this.pathLib),
				webosInternals:Patch.isWebOSInternalsInstalled(this.file, this.pathLib)};
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"This is a private function "
				+ "reserved for Internalz."};
	}
};

function InstallPatchAssistant() {}; //installPatch
InstallPatchAssistant.prototype.run = function(future){
	this.future = future;
	this.file = this.controller.args.file;
	this.version = this.controller.args.version;
	if(validForPrivate(this)) {
		this.patch = new Patch(this.file, this.future);
		this.patch.install(this.version);
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"This is a private function "
				+ "reserved for Internalz."};
	}
};

function UninstallPatchAssistant() {}; //uninstallPatch
UninstallPatchAssistant.prototype.run = function(future){
	this.future = future;
	this.file = this.controller.args.file;
	if(validForPrivate(this)) {
		this.patch = new Patch(this.file, this.future);
		this.patch.uninstall();
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"This is a private function "
				+ "reserved for Internalz."};
	}
};

function JavaRestartAssistant() {}; //javaRestart
JavaRestartAssistant.prototype.run = function(future){
	this.future = future;
	this.future.result = {errorCode:"ERROR", errorText:"There is no Java on webOS 2.0."};
};

function LunaRestartAssistant() {}; //lunaRestart
LunaRestartAssistant.prototype.run = function(future){
	this.future = future;
	if(validForPrivate(this)) {
		this.cmd = new CommandLine("/usr/bin/killall -HUP LunaSysMgr", this.future);
		this.cmd.run();
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"This is a private function "
				+ "reserved for Internalz."};
	}
};

function DeviceRestartAssistant() {}; //deviceRestart
DeviceRestartAssistant.prototype.run = function(future){
	this.future = future;
	if(validForPrivate(this)) {
		this.ls = new LunaSend("palm://com.palm.power/shutdown/machineReboot",
				{reason:"FileMgr service has requested a device reboot"}, this.future);
		this.ls.run();
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"This is a private function "
				+ "reserved for Internalz."};
	}
};

