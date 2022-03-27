function PermissionsDialogAssistant(params) {
	this.onAccept = params.onAccept;
	this.controller = params.sceneAssistant.controller;
	this.val = params.value.toLowerCase();
	this.path = params.path;
	this.appAssist = Mojo.Controller.getAppController().assistant;
	this.WRITABLE = new Array("/media/cryptofs/apps/", "/media/internal/", "/var/");
	this.isReadOnly = true;
	if(this.appAssist.settings.explorer.masterMode ||
			(this.path.startsWith(this.WRITABLE[0]) ||
			this.path.startsWith(this.WRITABLE[1]) ||
			this.path.startsWith(this.WRITABLE[2]))) {
		this.isReadOnly = false;
	}
}

PermissionsDialogAssistant.prototype.setup = function(widget) {
	this.widget = widget;
	this.controller.get('title').innerText += $L("Permissions");
	if(this.isReadOnly) {
		this.controller.get('title').innerText += "  " + $L("(View Only)");
		this.controller.get("doubleButtonRow").style.display = "none";
	} else {
		this.controller.get("singleButtonRow").style.display = "none";
	}
	this.controller.get('userLabel').innerText = $L("User");
	this.controller.get('groupLabel').innerText = $L("Group");
	this.controller.get('otherLabel').innerText = $L("Other");

	//Read
	this.userReadModel = {value: (this.val.charAt(1)=="r"), disabled: this.isReadOnly};
	this.controller.setupWidget("tbUserRead", { trueLabel: "+R", falseLabel: "-R" },
			this.userReadModel);
	
	this.groupReadModel = {value: (this.val.charAt(4)=="r"), disabled: this.isReadOnly};
	this.controller.setupWidget("tbGroupRead", { trueLabel: "+R", falseLabel: "-R" },
			this.groupReadModel);
			
	this.otherReadModel = {value: (this.val.charAt(7)=="r"), disabled: this.isReadOnly};
	this.controller.setupWidget("tbOtherRead", { trueLabel: "+R", falseLabel: "-R" },
			this.otherReadModel);
		
	//Write	
	this.userWriteModel = {value: (this.val.charAt(2)=="w"), disabled: this.isReadOnly};
	this.controller.setupWidget("tbUserWrite", { trueLabel: "+W", falseLabel: "-W" },
			this.userWriteModel);
	
	this.groupWriteModel = {value: (this.val.charAt(5)=="w"), disabled: this.isReadOnly};
	this.controller.setupWidget("tbGroupWrite", { trueLabel: "+W", falseLabel: "-W" },
			this.groupWriteModel);
			
	this.otherWriteModel = {value: (this.val.charAt(8)=="w"), disabled: this.isReadOnly};
	this.controller.setupWidget("tbOtherWrite", { trueLabel: "+W", falseLabel: "-W" },
			this.otherWriteModel);
	
	//Execute		
	this.userExecModel = {value: (this.val.charAt(3)=="x"), disabled: this.isReadOnly};
	this.controller.setupWidget("tbUserExecute", { trueLabel: "+X", falseLabel: "-X" },
			this.userExecModel);
	
	this.groupExecModel = {value: (this.val.charAt(6)=="x"), disabled: this.isReadOnly};
	this.controller.setupWidget("tbGroupExecute", { trueLabel: "+X", falseLabel: "-X" },
			this.groupExecModel);
			
	this.otherExecModel = {value: (this.val.charAt(9)=="x"), disabled: this.isReadOnly};
	this.controller.setupWidget("tbOtherExecute", { trueLabel: "+X", falseLabel: "-X" },
			this.otherExecModel);

	this.controller.get('okButton').innerText = $L("OK");
	this.controller.get('acceptButton').innerText = $L("OK");
	this.controller.get('dismissButton').innerText = $L("Cancel");
};

PermissionsDialogAssistant.prototype.getChmodValue = function() {
	var user = 0;
	var group = 0;
	var other = 0;
	
	if(this.userReadModel.value)
		user += 4;
	if(this.userWriteModel.value)
		user += 2;
	if(this.userExecModel.value)
		user += 1;
	
	if(this.groupReadModel.value)
		group += 4;
	if(this.groupWriteModel.value)
		group += 2;
	if(this.groupExecModel.value)
		group += 1;
		
	if(this.otherReadModel.value)
		other += 4;
	if(this.otherWriteModel.value)
		other += 2;
	if(this.otherExecModel.value)
		other += 1;
		
	return user + "" + group + "" + other
};

PermissionsDialogAssistant.prototype.handleAccept = function(){
	this.onAccept(this.getChmodValue());
	this.widget.mojo.close();
};

PermissionsDialogAssistant.prototype.handleDismiss = function() {
	this.widget.mojo.close();
};

PermissionsDialogAssistant.prototype.activate = function(event) {
	this.handleAccept = this.handleAccept.bindAsEventListener(this);
	this.handleDismiss = this.handleDismiss.bindAsEventListener(this);
	this.controller.listen("okButton", Mojo.Event.tap, this.handleDismiss);
	this.controller.listen("acceptButton", Mojo.Event.tap, this.handleAccept);
	this.controller.listen("dismissButton", Mojo.Event.tap, this.handleDismiss);
};


PermissionsDialogAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("okButton", Mojo.Event.tap, this.handleDismiss);
	this.controller.stopListening("acceptButton", Mojo.Event.tap, this.handleAccept);
	this.controller.stopListening("dismissButton", Mojo.Event.tap, this.handleDismiss);
};

PermissionsDialogAssistant.prototype.cleanup = function(event) {
	delete this.WRITABLE;
};
