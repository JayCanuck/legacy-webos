function LaunchlistAssistant() {

}

LaunchlistAssistant.prototype.setup = function() {
	this.attributes = {
		itemTemplate: 'launchlist/listItem',
      	listTemplate: 'launchlist/listContainer',
      	addItemLabel: $L('Add ...'),
      	swipeToDelete: true,
		autoconfirmDelete: true,
      	reorderable: false,
		hasNoWidgets: true
  	}
	this.model = {
		listTitle: $L('App IDs'),
        items : Preferences.get("applist", {list:[]}).list
 	};
	this.controller.setupWidget("listApps", this.attributes, this.model);
};

LaunchlistAssistant.prototype.activate = function(event) {
	this.handleAdd = this.handleAdd.bindAsEventListener(this);
	this.handleDelete = this.handleDelete.bindAsEventListener(this);
	this.controller.listen("listApps", Mojo.Event.listAdd, this.handleAdd);
	this.controller.listen("listApps", Mojo.Event.listDelete, this.handleDelete);
};

LaunchlistAssistant.prototype.handleAdd = function(event) {
	this.controller.showDialog({
		template: 'input-dialog/input-dialog-popup',
		assistant: new InputDialogAssistant({
			sceneAssistant: this,
			title: "Add To Launch List",
			message: "Application ID:",
			init: "",
			onAccept: function(value) {
				this.model.items.push({id:value});
				this.controller.modelChanged(this.model);
				Preferences.set("applist", {list:this.model.items});
			}.bind(this)
		})
	});
};

LaunchlistAssistant.prototype.handleDelete = function(event) {
	this.model.items.splice(event.index, 1);
	this.controller.modelChanged(this.model);
	Preferences.set("applist", {list:this.model.items});
};

LaunchlistAssistant.prototype.deactivate = function(event) {

};

LaunchlistAssistant.prototype.cleanup = function(event) {

};
