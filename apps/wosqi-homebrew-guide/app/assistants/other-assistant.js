function OtherAssistant() {

}

OtherAssistant.prototype.setup = function() {
	var menuModel = {
    	visible: true,
    	items: [
			{label: "Help", command:"about"}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, menuModel);
};

OtherAssistant.prototype.activate = function(event) {

};

OtherAssistant.prototype.deactivate = function(event) {

};

OtherAssistant.prototype.cleanup = function(event) {

};
