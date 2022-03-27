function AppAssistant(appController){
	this.controller = appController;
	this.settings = {};
	this.filemgr = new FileMgrService();
}

AppAssistant.prototype.handleLaunch = function(params) {
	if(!this.settings.explorer) {
		this.loadSettings();
	}
	this.stageName = "Internalz"; //default stage
	this.scene = "explorer";  //default scene
	this.path = this.settings.explorer.startDir; //default "path" parameter
	
	//check for externally set starting directory
	if(params.path) {
		this.path = params.path;
	}
	
	//check for external resource opening
	if(params.target) {
		var ext = getFileExt(params.target);
		if(params.target.startsWith("view-source:")) {
			this.scene = "texteditor";
		} else if(params.target.startsWith("http")) {
			if(this.isImageExt(ext)) {
				this.scene = "imageview";
			} else {
				this.scene = "downloadfile";
			}
		} else {
			params.target = params.target.replace("/var/luna/data/file:", "");
			params.target = params.target.replace("file://", "");
			if(params.target.charAt(0) != "/") {
				params.target = "/" + params.target;
			}
			if(this.isImageExt(ext)) {
				this.scene = "imageview";
			} else {
				this.scene = "texteditor";
			}
		}
		this.stageName = params.target;
		this.path = params.target;
	}
	var stageController = this.controller.getStageController(this.stageName);
	if(stageController) {
		stageController.activate();
	} else {
		this.controller.createStageWithCallback(this.stageName,
				this.launchApp.bind(this)); 
	}
};

AppAssistant.prototype.isImageExt = function(ext){
	return (ext=="png" || ext=="jpg" || ext=="jpeg" || ext=="bmp" || ext=="gif");
};

AppAssistant.prototype.launchApp = function(stageController){
	stageController.pushScene(this.scene, {filemgr:this.filemgr, path:this.path});
};

AppAssistant.prototype.loadSettings = function() {
	this.settings = Preferences.get("OneZeroZero", this.DEFAULTSETTINGS["1.0.0"]);
	this.settings = this.extend(this.settings, Preferences.get("OneOneZero",
			this.DEFAULTSETTINGS["1.1.0"]));
};

AppAssistant.prototype.saveSettings = function() {
	Preferences.set("OneZeroZero", this.unextend("1.0.0"));
	Preferences.set("OneOneZero", this.unextend("1.1.0"));
};

AppAssistant.prototype.extend = function(dest, src) {
	for(var ele in src) {
		if(!dest[ele]) {
			dest[ele] = {};
		}
		for(var sub in src[ele]) {
			dest[ele][sub] = src[ele][sub];
		}
	}
	return dest;
};

AppAssistant.prototype.unextend = function(version) {
	var json = this.DEFAULTSETTINGS[version];
	for(var ele in json) {
		if(json[ele]) {
			for(var sub in json[ele]) {
				json[ele][sub] = this.settings[ele][sub];
			}
		}
	}
	return json;
};

AppAssistant.prototype.cleanup = function() {
	this.filemgr.cleanup();
};

AppAssistant.prototype.DEFAULTSETTINGS = {
	"1.0.0": {
		explorer: {
			startDir: "/media/internal/",
			subVal:1,
			sort: "name",
			ascending: true,
			homeDir: "/media/internal/",
			fontSize: "0.9em",
			showHidden: false,
			swipeDelete: false,
			orientation: "up",
			wrapNames: false,
			theme: "palm-default",
			ipkHandler: "none"
		},
		images: {
			newCard: false,
			fullScreen: true,
			orientation: "free"
		},
		editor: {
			newCard: false,
			saveOnClose: false,
			newLine: 1,
			wordWrap: false,
			orientation: "free",
			fontSize: 14
		}
	},
	"1.1.0": {
		explorer: {
			favourites:[]
		},
		images: {
			swipeChange: true
		}
	}
};
