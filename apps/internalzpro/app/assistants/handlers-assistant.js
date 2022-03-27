function HandlersAssistant(params) {
	this.filemgr = params.filemgr;
	this.appAssist = Mojo.Controller.getAppController().assistant;
}

HandlersAssistant.prototype.setup = function() {
	this.controller.get('loadingScrim').hide();
	this.controller.setupWidget("spinLoading", {spinnerSize: 'large'}, {spinning:false});
	this.controller.get('title').innerText += $L("Register File Handlers?");
	this.controller.get('message').innerHTML += $L("This allows outside applications like"
			+ " Web and Email to use Internalz to open and handle particular files.")
			+ "<br/><br/>" + $L("If another application is already set as the default "
			+ "handler, this will not change that. It will just add Internalz as an "
			+ "alternative option on the Default Applications screen. If there is no"
			+ " current default handler, Internalz will assume the role.") + "<br/><br/>";
	this.controller.get('imagesInfo').innerHTML = "<strong>" + $L("Image Viewer")
			+ "</strong><br/><em class=\"handler-ext\">jpg, jpeg, png, bmp, gif</em>";
	this.controller.get('editorInfo').innerHTML = "<strong>" + $L("Text Editor")
			+ "</strong><br/><em class=\"handler-ext\">txt, js, css, json, log, conf,"
			+ " ini, sh, mk, c, cpp, cs, vb, h, patch, diff</em>";
	this.controller.get('ipkInfo').innerHTML = "<strong>" + $L("Ipk Installer")
			+ "</strong><br/><em class=\"handler-ext\">ipk</em>";

	this.controller.setupWidget("tbImages", {}, this.imagesModel = {value:true});
	this.controller.setupWidget("tbEditor", {}, this.editorModel = {value:true});
	this.controller.setupWidget("tbIpk", {}, this.ipkModel = {value:true});
	this.controller.get('okButton').innerText = $L("Continue");
};

HandlersAssistant.prototype.handleContinue = function(){
	this.controller.get('spinLoading').mojo.start();
	this.controller.get('loadingScrim').show();
	//this.handlers = new ResourceHandling();
	//this.handlers.set(true, this.imagesModel.value, this.editorModel.value,
	//		this.ipkModel.value, this.onFinish.bind(this), this.onFinish.bind(this))
	this.filemgr.registerAsHandler(true, this.imagesModel.value, this.editorModel.value,
			this.ipkModel.value, this.onFinish.bind(this), this.onFinish.bind(this));
};

HandlersAssistant.prototype.onFinish = function() {
	this.filemgr.createFile(Mojo.appPath.substring(7) + "ran-setup");
	Preferences.set("handlerSplash", {shown:true});
	this.controller.get('spinLoading').mojo.stop();
	this.controller.stageController.popScene();
};

HandlersAssistant.prototype.activate = function(event) {
	this.handleContinue = this.handleContinue.bindAsEventListener(this);
	this.controller.listen("okButton", Mojo.Event.tap, this.handleContinue);

};

HandlersAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("okButton", Mojo.Event.tap, this.handleContinue);
};

HandlersAssistant.prototype.cleanup = function(event) {
};
