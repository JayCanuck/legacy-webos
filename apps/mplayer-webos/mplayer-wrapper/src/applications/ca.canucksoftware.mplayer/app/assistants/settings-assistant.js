function SettingsAssistant(params) {
	this.controller = params.controller;
	this.mplayer = params.mplayer;
	this.callback = params.callback || Mojo.doNothing;
}

SettingsAssistant.prototype.setup = function(widget) {
	this.widget = widget;
	this.controller.setupWidget("spinLoading2", {spinnerSize: 'large'}, {spinning:false});
	this.controller.get('loadingScrim2').hide();
	this.handlerModel = {value: false, disabled: true};
	this.controller.setupWidget("cbHandler", {}, this.handlerModel);
	this.controller.serviceRequest("palm://com.palm.applicationManager", {
		method:"listAllHandlersForUrl",
		parameters: {
			mime: "video/avi"
		},
		onSuccess: function(response) {
			if(Object.toJSON(response).indexOf("ca.canucksoftware.mplayer") != -1) {
				this.handlerModel.value = true;
			} else {
				this.handlerModel.value = false;
			}
			this.handlerModel.disabled = false;
			this.controller.modelChanged(this.handlerModel);
		}.bind(this),
		onFailure: function(err) {
			this.handlerModel.value = false;
			this.handlerModel.disabled = false;
			this.controller.modelChanged(this.handlerModel);
		}.bind(this),
	});
};

SettingsAssistant.prototype.handleHandler = function(event){
	this.controller.get('spinLoading2').mojo.start();
	this.controller.get('loadingScrim2').show();
	this.mplayer.registerAsHandler(
		event.value,
		function(response) {
			this.controller.get('loadingScrim2').hide();
			this.controller.get('spinLoading2').mojo.stop();
		}.bind(this),
		function(err) {
			Error(error.errorText);
			this.handlerModel.value = !this.handlerModel.value;
			this.controller.modelChanged(this.handlerModel);
			this.controller.get('loadingScrim2').hide();
			this.controller.get('spinLoading2').mojo.stop();
		}.bind(this)
	);
};

SettingsAssistant.prototype.handleClose = function() {
	this.callback();
	this.widget.mojo.close();
};

SettingsAssistant.prototype.activate = function(event) {
	this.handleHandler = this.handleHandler.bindAsEventListener(this);
	this.handleClose = this.handleClose.bindAsEventListener(this);
	this.controller.listen("cbHandler", Mojo.Event.propertyChange, this.handleHandler);
	this.controller.listen("closeButton", Mojo.Event.tap, this.handleClose);
};

SettingsAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("cbHandler", Mojo.Event.propertyChange, this.handleHandler);
	this.controller.stopListening("closeButton", Mojo.Event.tap, this.handleClose);
};

SettingsAssistant.prototype.cleanup = function(event) {
};
