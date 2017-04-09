function HomebrewAssistant() {

}

HomebrewAssistant.prototype.setup = function() {
	var menuModel = {
    	visible: true,
    	items: [
			{label: "Help", command:"about"}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, menuModel);
};

HomebrewAssistant.prototype.activate = function(event) {

};

HomebrewAssistant.prototype.deactivate = function(event) {

};

HomebrewAssistant.prototype.cleanup = function(event) {

};
