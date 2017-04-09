function InstallingAssistant() {

}

InstallingAssistant.prototype.setup = function() {
	var menuModel = {
    	visible: true,
    	items: [
			{label: "Help", command:"about"}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, menuModel);
};

InstallingAssistant.prototype.activate = function(event) {

};

InstallingAssistant.prototype.deactivate = function(event) {

};

InstallingAssistant.prototype.cleanup = function(event) {

};
