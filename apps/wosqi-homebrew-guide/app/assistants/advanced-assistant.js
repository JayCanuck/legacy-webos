function AdvancedAssistant() {

}

AdvancedAssistant.prototype.setup = function() {
	var menuModel = {
    	visible: true,
    	items: [
			{label: "Help", command:"about"}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, menuModel);
	this.controller.setupWidget("drawer1", {unstyled: true}, this.drawer1 = {open: false});
	this.controller.setupWidget("drawer2", {unstyled: true}, this.drawer2 = {open: false});
	this.controller.setupWidget("drawer3", {unstyled: true}, this.drawer3 = {open: false});
	this.controller.setupWidget("scroller1", {mode:"horizontal"}, {});
	this.controller.setupWidget("scroller2", {mode:"horizontal"}, {});
};

AdvancedAssistant.prototype.activate = function(event) {
	this.divider1 = this.divider1.bindAsEventListener(this);
	this.divider2 = this.divider2.bindAsEventListener(this);
	this.divider3 = this.divider3.bindAsEventListener(this);
	this.controller.listen("divider1", Mojo.Event.tap, this.divider1);
	this.controller.listen("divider2", Mojo.Event.tap, this.divider2);
	this.controller.listen("divider3", Mojo.Event.tap, this.divider3);
};

AdvancedAssistant.prototype.divider1 = function(event) {
	this.controller.get("drawer1").mojo.toggleState();
	this.updateArrow("arrow1", this.drawer1.open);
};

AdvancedAssistant.prototype.divider2 = function(event) {
	this.controller.get("drawer2").mojo.toggleState();
	this.updateArrow("arrow2", this.drawer2.open);
};

AdvancedAssistant.prototype.divider3 = function(event) {
	this.controller.get("drawer3").mojo.toggleState();
	this.updateArrow("arrow3", this.drawer3.open);
};

AdvancedAssistant.prototype.updateArrow = function(id, isOpen) {
	var element = this.controller.get(id);
	if(isOpen) {
		element.removeClassName("palm-arrow-closed");
		element.addClassName("palm-arrow-expanded");
	} else {
		element.removeClassName("palm-arrow-expanded");
		element.addClassName("palm-arrow-closed");
	}
};

AdvancedAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("divider1", Mojo.Event.tap, this.divider1);
	this.controller.stopListening("divider2", Mojo.Event.tap, this.divider2);
	this.controller.stopListening("divider3", Mojo.Event.tap, this.divider3);
};

AdvancedAssistant.prototype.cleanup = function(event) {
	/* this function should do any cleanup needed before the scene is destroyed as 
	   a result of being popped off the scene stack */
};
