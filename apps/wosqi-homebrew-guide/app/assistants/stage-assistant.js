function StageAssistant() {
	
}

StageAssistant.prototype.setup = function() {
	this.controller.setWindowOrientation("free");
	this.controller.pushScene("headers");
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
