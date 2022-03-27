var tree;
var folder;

function FolderChooserAssistant(params) {
	this.title = params.action;
	this.fName = params.name;
	this.fPath = params.path;
	this.tarIsDir = params.isDir;
	this.filemgr = params.filemgr || new FileMgrService();
	this.base = "/media/internal";
}

FolderChooserAssistant.prototype.setup = function(type) {
	this.controller.get("folderChooser").innerText = $L("Loading...");
	this.appAssist = Mojo.Controller.getAppController().assistant;
	this.showHidden = this.appAssist.settings.explorer.showHidden;
	this.orientation = this.appAssist.settings.explorer.orientation;
	if(this.orientation!="free") {
		this.controller.stageController.setWindowOrientation(this.orientation);
	} else {
		this.orientation = this.controller.stageController.getWindowOrientation();
		this.controller.stageController.setWindowOrientation(this.orientation);
	}
	
	var bodyEle = this.controller.document.getElementsByTagName("body")[0];
	var themes = ["palm-dark"];
	themes = themes.splice(themes.indexOf(this.appAssist.settings.explorer.theme), 1);
	for(var themeCounter=0; themeCounter<themes.length; themeCounter++) {
		if(bodyEle.className.indexOf(themes[themeCounter]) > -1) {
			bodyEle.removeClassName(themes[themeCounter]);
		}
	}
	if(this.appAssist.settings.explorer.theme!="palm-default") {
		bodyEle.addClassName(this.appAssist.settings.explorer.theme);
	}
	
	tree = new dTree('tree', this.controller);
	tree.config.useCookies=false;
	tree.config.showHidden = this.showHidden;
	tree.config.filemgr = this.filemgr;
	folder = "";
	if(this.title=="move")
		this.controller.get("title").innerText = $L("Move to...");
	else if(this.title=="copy")
		this.controller.get("title").innerText = $L("Copy to...");
	else
		this.controller.get("title").innerText = $L(this.title);
	
	this.controller.setupWidget("btnSelect", {},{
    	label : $L("Select"),
		buttonClass: "primary",
        disabled: false
	});
	this.controller.setupWidget("btnCancel", {},{
    	label : $L("Cancel"),
		buttonClass: "negative",
        disabled: false
	});
	
	this.controller.setupWidget("scrollerId", {mode: 'horizontal'},{});

	tree.add(0, -1, $L("USB Drive"), "javascript:folder=&quot;" + this.base + "&quot;",
			this.base);
	this.filemgr.listDirs(
		{
			path: this.base,
			lookForChildren: "dirs",
			ignoreHidden: !this.showHidden,
		},
		this.handleLoadBase.bind(this),
		function(err) {
			Error($L(err.errorText));
			this.controller.get("folderChooser").innerHTML = tree.toString();
		}.bind(this)
	);
};

FolderChooserAssistant.prototype.orientationChanged = function(orientation){
	if(this.orientation === orientation) {
		return;
   	}
   	if(this.appAssist.settings.explorer.orientation=="free") {
		this.orientation = orientation;
		this.controller.stageController.setWindowOrientation(this.orientation);
   	}
};

FolderChooserAssistant.prototype.handleLoadBase = function(response){
	for(var i=0; i<response.items.length; i++) {
		if(response.items[i].link=="no") {
			tree.addByTitle(response.items[i].path, response.items[i].name);
			if(response.items[i].hasChildren) {
				tree.addByTitle(response.items[i].path + "/Loading...",
						$L("Loading..."));
			}
		}
	}
	this.controller.get("folderChooser").innerHTML = tree.toString();
};

FolderChooserAssistant.prototype.checkForChildren = function(directory){
	this.filemgr.hasChildrenDirs(directory,
		function(response) {
			if(response.has) {
				tree.addByTitle(response.dir + "/Loading...", $L("Loading..."));
			}
			this.controller.get("folderChooser").innerHTML = tree.toString();
		}.bind(this),
		function(err) {
			Mojo.Controller.errorDialog(err.errorText);
			this.controller.get("folderChooser").innerHTML = tree.toString();
		}.bind(this)
	);
};

FolderChooserAssistant.prototype.handleSelect = function(event){
	if(folder == "") {
		return;
	}
	
	if(this.fName.length!=0) {
		this.newPath = folder + "/" + this.fName;
		if(folder=="/") {
			this.newPath = "/" + this.fName;
		}
		if(this.fPath != folder + "/" + this.fName) {
			this.filemgr.exists(folder + "/" + this.fName,
				function(response){
					if(response.exists) {
						this.conflictMsgBox();
					} else {
						this.controller.stageController.popScene({
							action: this.title,
							targetIsDir: this.tarIsDir,
							file: this.fName,
							from: this.fPath,
							to: this.newPath
						});
					}
				}.bind(this)
			);
		} else {
			this.controller.stageController.popScene();
		}
	} else {
		this.newPath = folder + "/";
		this.controller.stageController.popScene({path: this.newPath});
	}
};

FolderChooserAssistant.prototype.conflictMsgBox = function() {
	this.controller.showAlertDialog({
		onChoose: function(value){
			if(value=="yes") {
				this.controller.stageController.popScene({
					action: this.title,
					targetIsDir: this.tarIsDir,
					file: this.fName,
					from: this.fPath,
					to: folder + "/" + this.fName
				});
			}
		}.bind(this),
		title: $L("Overwrite?"),
		message: $L("#{filename} already exists. Overwrite?")
				.interpolate({filename: "\"" + folder.replace("/media/internal", $L("USB Drive"))
				+ "/" + this.fName + "\""}),
		choices: [
			{label: $L("Yes"),value: "yes", type:"affirmative"},
			{label: $L("No"),value: "no", type:"negative"}
		],
		allowHTMLMessage: true
	});
};
 
FolderChooserAssistant.prototype.handleCancel = function(event){
	this.controller.stageController.popScene();
};

FolderChooserAssistant.prototype.activate = function(event) {
	this.handleSelect = this.handleSelect.bindAsEventListener(this);
	this.handleCancel = this.handleCancel.bindAsEventListener(this);
	this.controller.listen("btnSelect", Mojo.Event.tap, this.handleSelect);
	this.controller.listen("btnCancel", Mojo.Event.tap, this.handleCancel);
};

FolderChooserAssistant.prototype.deactivate = function(event) {
	this.filemgr.gc();
	this.controller.stopListening("btnSelect", Mojo.Event.tap, this.handleSelect);
	this.controller.stopListening("btnCancel", Mojo.Event.tap, this.handleCancel);
};

FolderChooserAssistant.prototype.cleanup = function(event) {
	tree.cleanup();
	delete folder;
};
