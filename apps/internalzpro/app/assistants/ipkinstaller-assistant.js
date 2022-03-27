function IpkinstallerAssistant(params) {
	this.filemgr = params.filemgr;
	this.filepath = params.path;
	this.postInstall = "none";
	this.postRemoval = "none";
}

IpkinstallerAssistant.prototype.setup = function() {
	var menuAttr = {omitDefaultItems: true};
	this.menuModel = {
    	visible: true,
    	items: [
			{label: $L("Java Restart"), command: 'java'},
      		{label: $L("Luna Restart"), command: 'luna'},
			{label: $L("Device Restart"), command: 'device'},
			{label: $L("Close"), command: 'close'}
    	]
  	};
	if(Mojo.Environment.DeviceInfo.platformVersionMajor != 1) {
		this.menuModel.items.shift();
	}
	if(!this.filepath.startsWith("/media/internal/") && !this.filepath.startsWith("http")) {
		this.menuModel.items.unshift({label: $L("Copy To User Storage"), command: 'copy'});
	}
	this.controller.setupWidget(Mojo.Menu.appMenu, menuAttr, this.menuModel);
	
	this.appAssist = Mojo.Controller.getAppController().assistant;
	this.orientation = this.appAssist.settings.ipk.orientation;
	this.controller.get("title").innerText = getFileName(this.filepath);
	
	var bodyEle = this.controller.document.getElementsByTagName("body")[0];
	if(this.appAssist.settings.explorer.darkTheme
			&& bodyEle.className.indexOf("palm-dark") == -1) {
		bodyEle.addClassName("palm-dark");
	} else if(!this.appAssist.settings.explorer.darkTheme
			&& bodyEle.className.indexOf("palm-dark") != -1) {
		bodyEle.removeClassName("palm-dark");
	}
	
	this.controller.setupWidget("spinLoading", {spinnerSize: 'large'}, {spinning:true});
	
	this.listAttrs = {
		listTemplate:'ipkinstaller/listContainer', 
		itemTemplate:'ipkinstaller/listItem',
		swipeToDelete:false,
		autoconfirmDelete:false,
		hasNoWidgets:true
	};
	this.listModel = {
		listTitle: $L('Package Information'),
		items: [
			{name: $L("Package ID"), value:""},
			{name: $L("Version"), value:""},
			{name: $L("Maintainer"), value:""},
			{name: $L("Architecture"), value:""},
			{name: $L("Installed-Size"), value:""},
			{name: $L("Filepath"), value:""},
			{name: $L("Has Postinst"), value:""},
			{name: $L("Has Prerm"), value:""}
		]
	};
	this.controller.setupWidget("listIpk", this.listAttrs, this.listModel);
	
	
	this.cmdMenuModel = {
		items: [ {}, {}, {label: $L("Install"), command:'install'}, {}, {} ]
	};
	this.controller.setupWidget(Mojo.Menu.commandMenu, {menuClass:"no-fade"},
			this.cmdMenuModel);
	this.filemgr.getPackageInfo(this.filepath, this.handleInfo.bind(this));
};

IpkinstallerAssistant.prototype.activate = function(event) {
	if(event && event.action && event.action=="copy") {
		this.copiedPath = event.to;
		this.filemgr.copy(event.from, event.to,
			function(response) {
				this.reloadDialog();
			}.bind(this),
			function(err) {
				Error($L(err.errorText));
			}.bind(this)
		);
	}
};

IpkinstallerAssistant.prototype.reloadDialog = function(){
	this.controller.showAlertDialog({
		onChoose: function(value){
			if(value=="yes") {
				if(this.controller.stageController.window.name != "Internalz") {
					this.controller.stageController.window.name = this.copiedPath;
				}
				this.controller.stageController.swapScene(
					{name: "ipkinstaller", transition: Mojo.Transition.crossFade},
					{filemgr: this.filemgr, path: this.copiedPath}
				);
				
			}
		}.bind(this),
		title: $L("Reload File?"),
		message: $L("Copy completed") + ".<br/><br/>"
				+ $L("Would you like to reload to the copied file?"),
		choices: [
			{label: $L("Yes"),value: "yes", type:"affirmative"},
			{label: $L("No"),value: "no", type:"negative"}
		],
		allowHTMLMessage: true
	});
};

IpkinstallerAssistant.prototype.orientationChanged = function(orientation){
	if(this.orientation === orientation) {
		return;
   	}
   	if(this.appAssist.settings.ipk.orientation=="free") {
		this.orientation = orientation;
		this.controller.stageController.setWindowOrientation(this.orientation);
   	}
};

IpkinstallerAssistant.prototype.handleInfo = function(response) {
	this.appId = response["package"];
	//Error(Object.toJSON(response));
	this.listModel.items[0].value = response["package"];
	this.listModel.items[1].value = response["version"];
	this.listModel.items[2].value = this.formatMaintainer(response["maintainer"]);
	this.listModel.items[3].value = response["architecture"];
	this.listModel.items[5].value = this.filepath;
	this.listModel.items[6].value = response["has postinst"];
	this.listModel.items[7].value = response["has prerm"];
	if(response["installed-size"]) {
		this.listModel.items[4].value = response["installed-size"] + " KB";
	} else {
		this.listModel.items.splice(4, 1);
	}
	if(response["source"] && response["source"].length>0) {
		try {
			var source = eval("(" + response["source"] + ")");
			if(source && source["PostInstallFlags"]) {
				this.postInstall = this.restartFlag(source["PostInstallFlags"]);
			}
			if(source && source["PostRemoveFlags"]) {
				this.postRemoval = this.restartFlag(source["PostRemoveFlags"]);
			}
		} catch(e) {}
	}
	if(response["description"] != "This is a webOS application.") {
		this.listModel.items.unshift({name: $L("Name"), value:response["description"]});
	}
	this.controller.modelChanged(this.listModel);
	this.stopSpinner();
	this.filemgr.isPackageInstalled(this.appId, this.handleIsInstalled.bind(this),
			this.genericFailure.bind(this));
};

IpkinstallerAssistant.prototype.formatMaintainer = function(maintainer) {
	var indexOpen = maintainer.lastIndexOf("<");
	var indexClose = maintainer.lastIndexOf(">");
	if(indexOpen!=-1 && indexClose!=-1 && indexOpen<indexClose &&
			indexClose==maintainer.length-1) {
		var email = maintainer.substring(indexOpen+1, indexClose);
		maintainer = maintainer.replace(/<\/?[^>]+(>|$)/g, "").trim();
		if(email.startsWith("http")) {
			maintainer = "<a href=\"" + email + "\">" + maintainer + "</a>";
		} else {
			maintainer = "<a href=\"mailto:" + email + "\">" + maintainer + "</a>";
		}
	}
	return maintainer;
};

IpkinstallerAssistant.prototype.restartFlag = function(flag) {
	var result = "none";
	if(flag == "RestartJava") {
		result = "java";
	} else if(flag == "RestartLuna") {
		result = "luna";
	} else if(flag == "RestartDevice") {
		result = "device";
	}
	return result;
};

IpkinstallerAssistant.prototype.handleIsInstalled = function(response) {
	this.isInstalled = response.installed;
	if(response.installed) {
		this.cmdMenuModel.items[1] = {label: $L("Launch"), command:'launch'};
		this.cmdMenuModel.items[3] = {label: $L("Uninstall"), command:'uninstall'};
	} else {
		this.cmdMenuModel.items[1] = {};
		this.cmdMenuModel.items[3] = {};
	}
	this.controller.modelChanged(this.cmdMenuModel);
};

IpkinstallerAssistant.prototype.genericFailure = function(err){
	Error($L(err.errorText));
}

IpkinstallerAssistant.prototype.handleCommand = function(event) {
	if(event.type==Mojo.Event.command) {
		if(event.command=='java') {
			this.filemgr.javaRestart();
		} else if(event.command == 'luna') {
			this.filemgr.lunaRestart();
		} else if(event.command == 'device') {
			this.filemgr.deviceRestart();
		} else if(event.command == 'launch') {
			this.controller.serviceRequest("palm://com.palm.applicationManager", {
				method: 'open',
    			parameters: {
        			id: this.appId
				}
			});
		} else if(event.command == 'install') {
			this.startSpinner();
			this.filemgr.installPackage(this.filepath, this.appId,
					this.handleSuccess.bind(this, "installed"),
					this.handleFailure.bind(this, "installation"));
		} else if(event.command == 'uninstall') {
			this.startSpinner();
			this.filemgr.uninstallPackage(this.appId,
					this.handleSuccess.bind(this, "uninstalled"),
					this.handleFailure.bind(this, "uninstallation"));
		} else if(event.command == 'close') {
			this.closeInstaller();
		} else if (event.command == 'copy') {
			var params = {
				action:"copy",
				name:getFileName(this.filepath),
				path: this.filepath,
				isDir:false,
				base: "/media/internal",
				filemgr:this.filemgr
			};
			this.controller.stageController.pushScene("folder-chooser", params);
		}
	} else if(event.type==Mojo.Event.back) {
		event.stop();
		this.closeInstaller();
	}
};

IpkinstallerAssistant.prototype.closeInstaller = function(){
	this.name = this.controller.stageController.window.name;
	if(this.name==="Internalz") {
		if(this.controller.stageController.getScenes().length == 1) {
			this.controller.stageController.swapScene("explorer", {
				filemgr:this.filemgr,
				path:this.appAssist.settings.explorer.startDir
			});
		} else {
			this.controller.stageController.popScene();
		}
	} else {
		var stage = Mojo.Controller.getAppController().getStageController("Internalz");
		if(stage) {
			stage.delegateToSceneAssistant("gracefulTransition", this.name);
			this.controller.window.close();
		} else {
			this.controller.window.close();
		}
	}
};

IpkinstallerAssistant.prototype.startSpinner = function() {
	this.controller.get('loadingScrim').show();
	this.controller.get('spinLoading').mojo.start();
};

IpkinstallerAssistant.prototype.stopSpinner = function() {
	this.controller.get('spinLoading').mojo.stop();
	this.controller.get('loadingScrim').hide();
};

IpkinstallerAssistant.prototype.handleSuccess = function(action, response) {
	this.stopSpinner();
	var restart;
	if(action == "installed") {
		this.isInstalled = true;
		restart = this.postInstall;
	} else {
		this.isInstalled = false;
		restart = this.postRemoval;
	}
	this.handleIsInstalled({installed:this.isInstalled});
	var message = $L("Package " + action + " successfully.");
	if(restart == "java" && Mojo.Environment.DeviceInfo.platformVersionMajor==1) {
		this.javaReboot(message);
	} else if(restart == "luna") {
		this.lunaReboot(message);
	} else if(restart == "device") {
		this.deviceReboot(message);
	} else { //no restart
		MsgBox(this, message, "Success!");
	}
};

IpkinstallerAssistant.prototype.handleFailure = function(action, err) {
	this.stopSpinner();
	this.controller.showDialog({
		template: 'error-dialog/error-dialog-popup',
		assistant: new ErrorDialogAssistant({
			sceneAssistant: this,
			message: $L("Package " + action + " failed."),
			error: err.errorText
		})
	});
};

IpkinstallerAssistant.prototype.javaReboot = function(message) {
	this.controller.showAlertDialog({
		onChoose: function(value){
			if(value=="ok") {
				this.filemgr.javaRestart();
			}
		}.bind(this),
		title: $L("Java Restart"),
		message: message + "<br><br>" +
				$L("A java restart is required. Phone connection will temporarily go"
						+ " offline, but will be restored shortly after."),
		choices: [
			{label: $L("OK"), value: "ok"},
			{label: $L("Later"), value: "later"}
		],
		allowHTMLMessage: true
	});
};

IpkinstallerAssistant.prototype.lunaReboot = function(message) {
	this.controller.showAlertDialog({
		onChoose: function(value){
			if(value=="ok") {
				this.filemgr.lunaRestart();
			}
		}.bind(this),
		title: $L("Luna Restart"),
		message: message + "<br><br>" +
				$L("A luna restart is required. All open applications will be closed"
						+ " during the restart."),
		choices: [
			{label: $L("OK"), value: "ok"},
			{label: $L("Later"), value: "later"}
		],
		allowHTMLMessage: true
	});
};

IpkinstallerAssistant.prototype.deviceReboot = function(message) {
	this.controller.showAlertDialog({
		onChoose: function(value){
			if(value=="ok") {
				this.filemgr.deviceRestart();
			}
		}.bind(this),
		title: $L("Device Restart"),
		message: message + "<br><br>" +
				$L("A full device restart is required. This will close all applications"
						+ " and take several minutes to complete."),
		choices: [
			{label: $L("OK"), value: "ok"},
			{label: $L("Later"), value: "later"}
		],
		allowHTMLMessage: true
	});
};

IpkinstallerAssistant.prototype.deactivate = function(event) {
	/* remove any event handlers you added in activate and do any other cleanup that should happen before
	   this scene is popped or another scene is pushed on top */
};

IpkinstallerAssistant.prototype.cleanup = function(event) {
	/* this function should do any cleanup needed before the scene is destroyed as 
	   a result of being popped off the scene stack */
};
