function StageAssistant() {}

StageAssistant.prototype.setup = function() {
	this.controller.pushScene("main");
};

StageAssistant.prototype.handleCommand = function(event){
  	if(event.type == Mojo.Event.command) {
		switch (event.command) {
			case "about":
				this.controller.pushAppSupportInfoScene();
				break;
		}
	}
};