function DownloadfileAssistant(params) {
	this.filemgr = params.filemgr;
	this.settings = Mojo.Controller.getAppController().assistant.settings;
	this.path = params.path;
	this.canceled = false;
	this.complete = false;
}

DownloadfileAssistant.prototype.setup = function() {
	this.controller.get("title").innerText = $L("Download File");
	var menuAttr = {omitDefaultItems: true};
  	var menuModel = {
    	items: [
      		{label: $L("Cancel"), command: 'cancel'}
    	]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, menuAttr, menuModel);
	this.controller.setupWidget("spinDownload",
        this.spinAttr = {
            spinnerSize: "large"
        },
        this.spinModel = {
            spinning: true
        }
    );
	this.controller.setupWidget("ppDownload", {},
        this.ppModel = {
			title: "0%",
            value: 0
        }
    );
	var cmdMenuModel = {
		items: [
			{},
			{},
			{label: $L("Cancel"), command:'cancel'}
		]
	};
	this.controller.setupWidget(Mojo.Menu.commandMenu, {menuClass:"no-fade"},
			cmdMenuModel);
	
	this.controller.get("url").innerText = this.path;
	
	var bodyEle = this.controller.document.getElementsByTagName("body")[0];
	var themes = ["palm-dark"];
	themes = themes.splice(themes.indexOf(this.settings.explorer.theme), 1);
	for(var themeCounter=0; themeCounter<themes.length; themeCounter++) {
		if(bodyEle.className.indexOf(themes[themeCounter]) > -1) {
			bodyEle.removeClassName(themes[themeCounter]);
		}
	}
	if(this.settings.explorer.theme!="palm-default") {
		bodyEle.addClassName(this.settings.explorer.theme);
	}
	
	this.controller.serviceRequest("palm://com.palm.downloadmanager/", {
		method: "download",
		parameters: {
			target: this.path,
			keepFilenameOnRedirect: true,
			subscribe: true
		},
		onSuccess: this.handleDownload.bind(this),
		onFailure: this.downloadFail.bind(this),
		onError: this.downloadFail.bind(this),
	});
};

DownloadfileAssistant.prototype.isImageExt = function(ext){
	return (ext=="png" || ext=="jpg" || ext=="jpeg" || ext=="bmp" || ext=="gif");
};

DownloadfileAssistant.prototype.handleDownload = function(response) {
	this.ticket = response.ticket;
	this.complete = response.completed;
	if(response.target) {
		this.localPath = response.target;
		this.controller.get("localPath").innerText = this.localPath;
	}
	
	if(!this.complete) {
		if(response.amountReceived && response.amountTotal) {
			var value = response.amountReceived / response.amountTotal;
			value = value * 1000.0;
			value = Math.round(value);
			value = value/10.0;
			if(value != undefined && value < 100.0) {
				this.ppModel.value = (response.amountReceived / response.amountTotal);
				this.ppModel.title = value + "%";
			} else if(value!=undefined) {
				this.ppModel.value = 1;
				this.ppModel.title = "100%";
			} else {
				this.ppModel.value = 0;
				this.ppModel.title = "???%";
			}
		} else {
			this.ppModel.value = 0;
			this.ppModel.title = "???%";
		}
		
		this.controller.modelChanged(this.ppModel);
	} else {
		this.ppModel.value = 1;
		this.ppModel.title = "100%";
		this.controller.modelChanged(this.ppModel);
		this.downloadComplete();
	}
};

DownloadfileAssistant.prototype.downloadComplete = function() {
	if(this.controller.stageController.window.name != "Internalz") {
		this.controller.stageController.window.name = this.localPath;
	}
	var scene = "texteditor";
	var ext = getFileExt(this.localPath);
	if(this.isImageExt(ext)) {
		scene = "imageview";
	}
	this.controller.stageController.swapScene(scene, {
		filemgr:this.filemgr,
		path:this.localPath
	});
};

DownloadfileAssistant.prototype.downloadFail = function(err) {
	MsgBox(this, $L("Download failed!"), $L("Error"),
		function() {
			this.cancelDownload();
			this.controller.window.close();
		}.bind(this)
	);
};

DownloadfileAssistant.prototype.cancelDownload = function() {
	this.cancelled = true;
	this.controller.serviceRequest("palm://com.palm.downloadmanager/", {
		method: "cancelDownload",
		parameters: {
			ticket: this.ticket,
		}
	});
	this.controller.serviceRequest("palm://com.palm.downloadmanager/", {
		method: "deleteDownloadedFile",
		parameters: {
			ticket: this.ticket,
		}
	});
};

DownloadfileAssistant.prototype.handleCommand = function(event) {
	if(event.type == Mojo.Event.command) {
		if (event.command == 'cancel') {
			this.cancelDownload();
			this.controller.window.close();
		}
	}
};

DownloadfileAssistant.prototype.activate = function(event) {
	
};

DownloadfileAssistant.prototype.deactivate = function(event) {
	
};

DownloadfileAssistant.prototype.cleanup = function(event) {
	if(!this.canceled && !this.complete) {
		this.cancelDownload();
	}
};
