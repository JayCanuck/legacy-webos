function AppAssistant(appController){
	this.controller = appController;
}

AppAssistant.prototype.handleLaunch = function(launchParams){
	var stage = this.controller.getStageController("filemgr-mainStage");
	if(stage) {
		stage.activate();
	} else {
		var f = function(stageController) {
			stageController.pushScene("main", launchParams);
		};
		this.controller.createStageWithCallback({name: "filemgr-mainStage",
				lightweight: true}, f);
	}
};