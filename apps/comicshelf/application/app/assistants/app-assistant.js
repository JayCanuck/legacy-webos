function AppAssistant(appController){
	this.controller = appController;
	this.settings = undefined;
	this.comics = new ComicService();
	this.comics.unloadAll();
}

AppAssistant.prototype.handleLaunch = function(params) {
	if(!this.settings) {
		this.loadSettings();
	}
	this.stageName = "ComicShelf"; //default stage
	this.scene = "browser";  //default scene
	this.path = this.settings.comicDir; //default "path" parameter
	
	//check for externally set starting directory
	if(params.path) {
		this.path = params.path;
	}
	
	if(this.settings.autoResume && this.settings.autoResume.file) {
		this.scene = {name:"viewer", disableSceneScroller:true};
		this.path = this.settings.autoResume.file;
	}
	
	//check for external resource opening
	if(params.target) {
		var ext = getFileExt(params.target);
		if(!params.target.startsWith("http") && (ext=="cbr" || ext=="cbz")) {
			params.target = params.target.replace("/var/luna/data/file:", "");
			params.target = params.target.replace("file://", "");
			if(params.target.charAt(0) != "/") {
				params.target = "/" + params.target;
			}
			this.scene = {name:"viewer", disableSceneScroller:true};
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

AppAssistant.prototype.launchApp = function(stageController){
	if(Mojo.Environment.DeviceInfo.platformVersionMajor >= 3) {
		stageController.setWindowOrientation("free");
	}
	stageController.pushScene(this.scene, {comics:this.comics, path:this.path});
};

AppAssistant.prototype.loadSettings = function() {
	this.settings = Preferences.get("OneZeroZero", this.DEFAULTSETTINGS["1.0.0"]);
};

AppAssistant.prototype.saveSettings = function() {
	Preferences.set("OneZeroZero", this.unextend("1.0.0"));
};

AppAssistant.prototype.extend = function(dest, src) {
	for(var ele in src) {
		dest[ele] = src[ele];
	}
	return dest;
};

AppAssistant.prototype.unextend = function(version) {
	var json = Object.clone(this.DEFAULTSETTINGS[version]);
	for(var ele in json) {
		json[ele] = this.settings[ele];
	}
	return json;
};

AppAssistant.prototype.cleanup = function() {
	if(this.settings.autoResume) {
		this.settings.autoResume = this.comics.open;
		this.saveSettings();
	}
	this.comics.unloadAll();
	this.comics.cleanup();
};

AppAssistant.prototype.DEFAULTSETTINGS = {
	"1.0.0": {
		comicDir: "/media/internal/comics/",
		swipeDelete: false,
		fullScreen: true,
		orientation: "free",
		swipePage:true,
		gesturePage:true,
		autoResume:undefined
	}
};
