function AppAssistant(appController){
	this.controller = appController;
	this.mplayer = new MPlayerService();
}

AppAssistant.prototype.handleLaunch = function(params) {
	var extList = ["mpeg", "mpg", "vob", "avi", "ogg", "ogv", "asf", "wmv", "qt",
			"mov", "mp4", "rm", "rv", "mkv", "flv", "wma", "oga", "asx", "ra",
			"mp3", "wav", "3gp", "flac"];
	this.stageName = "MPlayer"; //default stage
	this.scene = "browser";  //default scene
	this.path = "/media/internal/video/"; //default "path" parameter
	
	//check for external resource opening
	if(params.target) {
		var ext = getFileExt(params.target).toLowerCase();
		if(extList.indexOf(ext)>-1) {
			params.target = params.target.replace("/var/luna/data/file:", "");
			params.target = params.target.replace("file://", "");
			if(params.target.charAt(0) != "/") {
				params.target = "/" + params.target;
			}
			this.mplayer.open(params.target);
			this.controller.createStageWithCallback(params.target,
					this.closeApp.bind(this)); 
			return;
		}
	}
	var stageController = this.controller.getStageController(this.stageName);
	if(stageController) {
		stageController.activate();
	} else {
		this.controller.createStageWithCallback(this.stageName, this.launchApp.bind(this)); 
	}
};

AppAssistant.prototype.launchApp = function(stageController){
	stageController.pushScene(this.scene, {mplayer:this.mplayer, path:this.path});
};

AppAssistant.prototype.closeApp = function(stageController){
	stageController.window.close()
};
