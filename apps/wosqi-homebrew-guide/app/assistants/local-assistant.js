function LocalAssistant() {

}

LocalAssistant.prototype.setup = function() {
	var menuModel = {
    	visible: true,
    	items: [
			{label: "Help", command:"about"}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, menuModel);
};

LocalAssistant.prototype.activate = function(event) {

};

LocalAssistant.prototype.deactivate = function(event) {

};

LocalAssistant.prototype.cleanup = function(event) {

};
