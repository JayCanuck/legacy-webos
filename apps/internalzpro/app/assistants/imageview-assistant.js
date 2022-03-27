function ImageviewAssistant(params) {
	this.filemgr = params.filemgr || new FileMgrService();
	this.filepath = params.path;
	this.images = params.images || [this.filepath];
	this.currIndex = this.images.indexOf(this.filepath);
}


ImageviewAssistant.prototype.setup = function() {
	this.settings = Mojo.Controller.getAppController().assistant.settings;
	this.orientation = this.settings.images.orientation;
	this.ext = getFileExt(this.filepath);
	this.ext = this.ext.toLowerCase();
	if(this.ext=="jpg" || this.ext=="jpeg" || this.ext=="png" || this.ext=="bmp") {
		this.alt = false;
	} else {
		this.alt = true;
	}
	this.menusVisible = true;	
	this.controller.get("imgTitle").innerText = getFileName(this.filepath);
	if(!this.settings.images.fullScreen) {
		this.controller.get("imgTitle").hide();
	}
	
	var menuAttr = {omitDefaultItems: true};
  	var menuModel = {
    	visible: true,
    	items: [
      		{label: $L("Close Image"), command: 'close'}
    	]
  	};
	var shareItem = {
		label: $L("Share"),
		items: [
			{label: $L("Email"), command: 'email'},
			{label: $L("MMS"), command: 'mms'}
		],
	};
	var wallpaperItem = {label: $L("Set As Wallpaper"), command: 'wallpaper'};
	var shareSubmenuItem = {label: $L("Share"), submenu: 'share-menu'};
	var cmdMenuModel = {
			items: [
				{}, {}, {}
			]
		};
	if(this.filepath.startsWith("http")) {
		menuModel.items.unshift({label: $L("Download Image"), command: 'download'});
	} else if(!this.filepath.startsWith("/media/internal/")) {
		menuModel.items.unshift(shareItem);
		menuModel.items.unshift(wallpaperItem);
		menuModel.items.unshift({label: $L("Copy To User Storage"), command: 'copy'});
		if(this.settings.images.fullScreen) {
			cmdMenuModel.items[0] = shareSubmenuItem;
			cmdMenuModel.items[2] = wallpaperItem;
		}
	} else {
		menuModel.items.unshift(shareItem);
		menuModel.items.unshift(wallpaperItem);
		if(this.settings.images.fullScreen) {
			cmdMenuModel.items[0] = shareSubmenuItem;
			cmdMenuModel.items[2] = wallpaperItem;
		}
	}
	this.controller.setupWidget(Mojo.Menu.appMenu, menuAttr, menuModel);
	this.controller.setupWidget('share-menu', null, shareItem);
	this.controller.setupWidget(Mojo.Menu.commandMenu, {menuClass:"no-fade"},
				cmdMenuModel);
	
	var bodyEle = this.controller.document.getElementsByTagName("body")[0];
	if(this.settings.explorer.darkTheme
			&& bodyEle.className.indexOf("palm-dark") == -1) {
		bodyEle.addClassName("palm-dark");
	} else if(!this.settings.explorer.darkTheme
			&& bodyEle.className.indexOf("palm-dark") != -1) {
		bodyEle.removeClassName("palm-dark");
	}
	
	if(this.orientation!="free") {
		this.controller.stageController.setWindowOrientation(this.orientation);
	} else {
		this.orientation = this.controller.stageController.getWindowOrientation();
		this.controller.stageController.setWindowOrientation(this.orientation);
	}
	
	if(!this.alt) {
		this.controller.setupWidget("imgMain", {noExtractFS:false},
				{onLeftFunction:this.handleLeftMovement.bind(this),
				onRightFunction:this.handleRightMovement.bind(this)});
	}
	this.controller.setupWidget("spinLoading", {spinnerSize: 'large'}, {spinning:true});
	if(!this.filepath.startsWith("/media/internal/") && !this.filepath.startsWith("http")) {
		var dir = Mojo.appPath.substring(7) + "_temp" + this.filepath;
		dir = dir.substring(0, dir.lastIndexOf("/")+1);
		this.centerUrl = Mojo.appPath.substring(7) + "_temp" + this.filepath;
		this.images = [this.centerUrl];
		this.filemgr.createDir(dir,
			function() {
				this.filemgr.copy(this.filepath, this.centerUrl,
					this.loadImage.bind(this),
					function(err) {
						Error($L(err.errorText));
					}.bind(this)
				);
			}.bind(this),
			function(err) {
				Error($L(err.errorText));
			}.bind(this)
		);
	}
}

ImageviewAssistant.prototype.getSize = function(orientation){
	if(orientation == 'left' || orientation == 'right'){
		this.w = Mojo.Environment.DeviceInfo.screenHeight;
		this.h = Mojo.Environment.DeviceInfo.screenWidth;
	} else {
		if(this.settings.images.fullScreen) {
			this.h = Mojo.Environment.DeviceInfo.screenHeight;
		} else {
			this.h = Mojo.Environment.DeviceInfo.maximumCardHeight;
		}
		this.w = Mojo.Environment.DeviceInfo.maximumCardWidth;
	}
	if(Mojo.Environment.DeviceInfo.modelNameAscii=="Pre3") {
		this.h = this.h / 1.5;
		this.w = this.w / 1.5;
	}
}

ImageviewAssistant.prototype.orientationChanged = function(orientation){
	if(this.orientation === orientation) {
		return;
   	}
   	if(this.settings.images.orientation=="free") {
		this.orientation = orientation;
		this.controller.stageController.setWindowOrientation(this.orientation);
		this.getSize(this.orientation);
   	} else {
		this.getSize(this.settings.images.orientation);
	}
	if(!this.alt) {
		this.controller.get('imgMain').mojo.manualSize(this.w,this.h);
	}
	//var w = this.controller.window.innerWidth;
	//var h = this.controller.window.innerHeight;
}

ImageviewAssistant.prototype.handleCommand = function(event){
	if(event.type == Mojo.Event.command) {
		if (event.command == 'close') {
			this.closeViewer();
		} else if (event.command == 'download') {
			this.controller.serviceRequest("palm://com.palm.downloadmanager", {
				method: "download",
				parameters: {
					target: this.filepath,
					keepFilenameOnRedirect: true,
					subscribe: false
				},
				onSuccess: function(response) {
					this.copiedPath = response.target;
					this.reloadDialog($L("Image successfully downloaded to #{filepath}")
							.interpolate({filepath:response.target}), "downloaded");
				}.bind(this),
				onFailure: function(err) {
					Error($L("Download failed!"));
				}.bind(this)
			});
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
		} else if (event.command == 'email') {
			this.controller.serviceRequest("palm://com.palm.applicationManager", {
				method: "open",
				parameters: {
					id: "com.palm.app.email",
					params: {
						attachments: [
							{fullPath: this.filepath}
						]
					}
				}
			});
		} else if (event.command == 'mms') {
			this.controller.serviceRequest("palm://com.palm.applicationManager", {
				method: "open",
				parameters: {
					id: "com.palm.app.messaging",
					params: {
						attachment: this.filepath
					}
				}
			});
		} else if (event.command == 'wallpaper') {
			this.setWallpaper();
		}
	} else if(event.type == Mojo.Event.back){
			event.stop();
			this.closeViewer();
	}
}

ImageviewAssistant.prototype.closeViewer = function(){
	this.name = this.controller.stageController.window.name;
	this.filemgr.deleteFile(Mojo.appPath.substring(7) + "_temp/");
	if(this.name==="Internalz") {
		if(this.controller.stageController.getScenes().length == 1) {
			this.controller.stageController.swapScene("explorer", {
				filemgr:this.filemgr,
				path:this.settings.explorer.startDir
			});
		} else {
			this.controller.stageController.popScene();
		}
	} else {
		if(this.controller.stageController.getScenes().length == 1) {
			var stage = Mojo.Controller.getAppController().getStageController("Internalz");
			if(stage) {
				stage.delegateToSceneAssistant("gracefulTransition", this.name);
			} else {
				this.controller.window.close();
			}
		} else {
			this.controller.stageController.popScene();
		}
	}
}

ImageviewAssistant.prototype.loadImage = function(event) {
	//var w = this.controller.window.innerWidth;
	//var h = this.controller.window.innerHeight;
	this.getSize(this.orientation);
	if(!this.alt) {
		this.controller.get('imgMain').mojo.manualSize(this.w,this.h);
			this.controller.get('spinLoading').mojo.stop();
			this.controller.get('imgMain').style.display = "inline";
			this.controller.get('imgAlt').style.display = "none";
			this.controller.get('loadingScrim').style.display = "none";
			this.controller.get('imgMain').mojo.centerUrlProvided(this.centerUrl);
			if(this.settings.images.swipeChange) {
				if(this.images.length>1) {
					this.controller.get('imgMain').mojo
							.leftUrlProvided(this.images[this.getLeftImageIndex()]);
					this.controller.get('imgMain').mojo
							.rightUrlProvided(this.images[this.getRightImageIndex()]);
				}
			}
	} else {
		this.controller.get('spinLoading').mojo.stop();
		this.controller.get('imgAlt').style.display = "inline";
		this.controller.get('imgMain').style.display = "none";
		this.controller.get('loadingScrim').style.display = "none";
		this.controller.get("imgAlt").src = this.centerUrl;
		//if(imgW > imgH) {
			this.controller.get("imgAlt").style.width = "100%";
			this.controller.get("imgAlt").style.height = "auto";
			this.controller.get("imgAlt").style.left = "0px";
			this.controller.get("imgAlt").style.top = "0px";
			/*this.controller.get("imgAlt").style.top = ((h/2) - (imgH / 2)) + "px";
		} else {
			this.controller.get("imgAlt").style.height = "100%";
			this.controller.get("imgAlt").style.width = "auto";
			this.controller.get("imgAlt").style.top = "0px";
			this.controller.get("imgAlt").style.left = ((w/2) - (imgW / 2)) + "px";
		}*/
	}
};

ImageviewAssistant.prototype.reloadDialog = function(msg, action){
	this.controller.showAlertDialog({
		onChoose: function(value){
			if(value=="yes") {
				if(this.controller.stageController.window.name != "Internalz") {
					this.controller.stageController.window.name = this.copiedPath;
					this.copiedPath = this.copiedPath
							.substring(this.copiedPath.indexOf("/"));
				}
				this.controller.stageController.swapScene(
					{name: "imageview", transition: Mojo.Transition.crossFade},
					{filemgr: this.filemgr, path: this.copiedPath}
				);
			}
		}.bind(this),
		title: $L("Reload File?"),
		message: msg + ".<br/><br/>"
				+ $L("Would you like to reload to the " + action + " file?"),
		choices: [
			{label: $L("Yes"),value: "yes", type:"affirmative"},
			{label: $L("No"),value: "no", type:"negative"}
		],
		allowHTMLMessage: true
	});
};

ImageviewAssistant.prototype.activate = function(event){
	if(event && event.action && event.action=="copy") {
		this.copiedPath = event.to;
		this.filemgr.copy(event.from, event.to,
			function(response) {
				this.reloadDialog($L("Copy completed"), "copied");
			}.bind(this),
			function(err) {
				Error($L(err.errorText));
			}.bind(this)
		);
	}
	
	if(!this.centerUrl) {
		this.centerUrl = this.filepath;
		this.loadImage();
	}
	this.controller.enableFullScreenMode(this.settings.images.fullScreen);
	//this.resizeHandler = this.resize.bindAsEventListener(this);
    //this.controller.listen(this.controller.window, 'resize', this.resizeHandler, false);
	this.handleImageTap = this.handleImageTap.bindAsEventListener(this);
	if(!this.alt) {
		this.controller.listen("imgMain", Mojo.Event.tap, this.handleImageTap);
	} else {
		this.controller.listen("imgAlt", Mojo.Event.tap, this.handleImageTap);
	}
}

ImageviewAssistant.prototype.setWallpaper = function() {
	this.controller.serviceRequest("palm://com.palm.systemservice/wallpaper", {
		method: "importWallpaper",
		parameters: {
			target: "file://" + this.filepath
		},
		onSuccess: function(response) {
			this.controller.serviceRequest("palm://com.palm.systemservice", {
				method: 'setPreferences',
				parameters: {
					wallpaper: response.wallpaper
				},
				onSuccess: function(response2) {
					MsgBox(this, $L("Wallpaper set successfully."));
				}.bind(this),
				onFailure: function(err2) {
					Error($L(err2.errorText));
				}.bind(this)
			});
		}.bind(this),
		onFailure: function(err) {
			Error($L(err.errorText));
		}.bind(this)
	});
};

ImageviewAssistant.prototype.handleImageTap = function(event) {
	if(this.settings.images.fullScreen) {
		if(this.menusVisible) {
			this.menusVisible = false;
			this.controller.get("imgTitle").hide();
		} else {
			this.menusVisible = true;
			this.controller.get("imgTitle").show();
		}
		this.controller.setMenuVisible(Mojo.Menu.commandMenu, this.menusVisible);
	}
};

ImageviewAssistant.prototype.getLeftImageIndex = function() {
	var result = this.currIndex - 1;
	if(result==-1) {
		result = this.images.length-1;
	}
	return result;
};

ImageviewAssistant.prototype.getRightImageIndex = function() {
	var result = this.currIndex + 1;
	if(result==this.images.length) {
		result = 0;
	}
	return result;
};

ImageviewAssistant.prototype.handleLeftMovement = function() {
	this.currIndex = this.getLeftImageIndex();
	this.controller.get('imgMain').mojo
			.leftUrlProvided(this.images[this.getLeftImageIndex()]);
	this.changeImage();
};

ImageviewAssistant.prototype.handleRightMovement = function() {
	this.currIndex = this.getRightImageIndex();
	this.controller.get('imgMain').mojo
			.rightUrlProvided(this.images[this.getRightImageIndex()]);
	this.changeImage();
};

ImageviewAssistant.prototype.changeImage = function() {
	this.filepath = this.images[this.currIndex];
	this.controller.get("imgTitle").innerText = getFileName(this.filepath);
};

ImageviewAssistant.prototype.resize = function(event) {
	this.orientationChanged(event);
};

ImageviewAssistant.prototype.deactivate = function(event) {
	//this.controller.stopListening(this.controller.window, 'resize', this.resizeHandler,
	//		false);
	if(!this.alt) {
		this.controller.stopListening("imgMain", Mojo.Event.tap, this.handleImageTap);
	} else {
		this.controller.stopListening("imgAlt", Mojo.Event.tap, this.handleImageTap);
	}
};

ImageviewAssistant.prototype.cleanup = function(event) {
};
