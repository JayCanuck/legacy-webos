function ViewerAssistant(params) {
	this.comics = params.comics || new ComicService();
	this.path = params.path;
	this.name = this.path.substring(this.path.lastIndexOf("/")+1);
}

ViewerAssistant.prototype.setup = function() {
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, {
		visible: true,
    	items: [{label: "Close", command:"close"}]
	});
	
	//this.controller.stageController.window.name = this.path;
	this.settings = Mojo.Controller.getAppController().assistant.settings;
	this.orientation = this.settings.orientation;
	if(this.orientation!="free") {
		this.controller.stageController.setWindowOrientation(this.orientation);
	} else {
		this.orientation = this.controller.stageController.getWindowOrientation();
		if(Mojo.Environment.DeviceInfo.platformVersionMajor < 3) {
			this.controller.stageController.setWindowOrientation(this.orientation);
		}
	}
	this.controller.get("menupanel-toggle").innerText = this.name;
	this.menupanel = this.controller.get("menupanel-panel");
	this.menuPanelVisibleTop = this.menupanel.offsetTop;
	this.menupanel.style.top = (0 - this.menupanel.offsetHeight - this.menupanel.offsetTop)+'px';
	this.menuPanelHiddenTop = this.menupanel.offsetTop;
	this.scrim = this.controller.get("menupanel-scrim");
	this.scrim.hide();
	this.scrim.style.opacity = 0;
	this._dragHandler = this._dragHandler.bindAsEventListener(this);
	this.controller.setupWidget("shareDrawer", {unstyled:true}, {open: false});
	if(Mojo.Environment.DeviceInfo.platformVersionMajor >= 3) {
		this.controller.get("mmsPageRow").hide();
		this.controller.get("imgMain").style.position = "relative";
	}
	this.controller.setupWidget("spinLoading", {spinnerSize: 'large'}, {spinning:true});
	
	this.pagesPopupModel = {
		label: "",
		items: []
	};
	this.controller.setupWidget('page-popup', null, this.pagesPopupModel);
	
	this.cmdMenuModel = {
		items: [
			{icon:"back", command:'previous', disabled:true},
			{label: "", submenu: 'page-popup'},
			{icon:"forward", command:'next'}
		]
	};
	this.controller.setupWidget(Mojo.Menu.commandMenu, {menuClass:"no-fade"},
			this.cmdMenuModel);
	this.menusVisible = true; //extractfsParams:"1500:1500:1"
	this.controller.setupWidget("imgMain", {noExtractFS:true},
			{onLeftFunction:this.handleLeftMovement.bind(this),
			onRightFunction:this.handleRightMovement.bind(this)});
	this.controller.get('imgMain').hide();
	if(this.settings.autoResume && this.settings.autoResume.page!=undefined) {
		this.imgIndex = this.settings.autoResume.page-1
	} else {
		this.imgIndex = 0;
	}
	this.comics.open = {file:this.path, page:this.imgIndex+1};
	this.comics.query(this.path, this.handleQuery.bind(this), function(err) {
		Error(err.errorText);
	});
};

ViewerAssistant.prototype.activate = function(event) {
	this.toggleMenuPanel = this.toggleMenuPanel.bindAsEventListener(this);
	this.handleImageTap = this.handleImageTap.bindAsEventListener(this);
	this.handleShare = this.handleShare.bindAsEventListener(this);
	this.handleEmailComic = this.handleEmailComic.bindAsEventListener(this);
	this.handleEmailPage = this.handleEmailPage.bindAsEventListener(this);
	this.handleMMSPage = this.handleMMSPage.bindAsEventListener(this);
	this.handleWallpaper = this.handleWallpaper.bindAsEventListener(this);
	this.handleSave = this.handleSave.bindAsEventListener(this);
	this.handleClose = this.handleClose.bindAsEventListener(this);
	this.handleImageViewStateChanged = this.handleImageViewStateChanged.bindAsEventListener(this);
	this.controller.listen("menupanel-toggle",Mojo.Event.tap, this.toggleMenuPanel);
	this.controller.listen("menupanel-scrim", Mojo.Event.tap, this.toggleMenuPanel);
	this.controller.listen("shareRow", Mojo.Event.tap, this.handleShare);
	this.controller.listen("emailComicRow", Mojo.Event.tap, this.handleEmailComic);
	this.controller.listen("emailPageRow", Mojo.Event.tap, this.handleEmailPage);
	this.controller.listen("mmsPageRow", Mojo.Event.tap, this.handleMMSPage);
	this.controller.listen("wallpaperRow", Mojo.Event.tap, this.handleWallpaper);
	this.controller.listen("saveRow", Mojo.Event.tap, this.handleSave);
	this.controller.listen("closeRow", Mojo.Event.tap, this.handleClose);
	this.controller.listen("imgMain", Mojo.Event.tap, this.handleImageTap);
	this.controller.listen("imgMain", Mojo.Event.imageViewChanged, this.handleImageViewStateChanged);
	if(Mojo.Environment.DeviceInfo.platformVersionMajor < 3) {
		this.controller.enableFullScreenMode(this.settings.fullScreen);
	} else {
		this.handleResize = this.handleResize.bindAsEventListener(this);
		this.controller.listen(this.controller.window, "resize", this.handleResize);
	}
	
};

ViewerAssistant.prototype.handleQuery = function(response){
	for(var i=0; i<response.entries.length; i++) {
		var num = (i+1);
		this.pagesPopupModel.items.push({label: "Page " + num, command:"page-" + num});
	}
	this.controller.modelChanged(this.pagesPopupModel);
	this.cmdMenuModel.items[1].label = (this.imgIndex+1) + "/" + response.entries.length;
	this.controller.modelChanged(this.cmdMenuModel);
	this.images = response.entries;
	this.comics.extractAll(this.path, this.handleExtractAll.bind(this), function(err) {
		Error(err.errorText);
	});
};

ViewerAssistant.prototype.handleExtractAll = function(response){
	this.timestamp = response.timestamp;
	for(var i=0; i<this.images.length; i++) {
		this.images[i] = "/var/luna/data/extractfs" + response.destination +
				this.images[i] + ":0:0:1200:1200:3";
	}
	this.getSize(this.orientation);
	this.controller.get('imgMain').mojo.manualSize(this.w, this.h);
	this.controller.get("menupanel-container").style.visibility="visible";
	//this.controller.get('loadingScrim').hide();
	//this.controller.get('spinLoading').mojo.stop();
	this.loadImage();
};

ViewerAssistant.prototype.orientationChanged = function(orientation){
	if(this.orientation === orientation) {
		return;
   	}
   	if(this.settings.orientation=="free") {
		this.orientation = orientation;
		if(Mojo.Environment.DeviceInfo.platformVersionMajor < 3) {
			this.controller.stageController.setWindowOrientation(this.orientation);
		}
		this.getSize(this.orientation);
   	} else {
		this.getSize(this.settings.orientation);
	}
	this.controller.get('imgMain').mojo.manualSize(this.w, this.h);
}

ViewerAssistant.prototype.handleResize = function(event){
	var orientation = PalmSystem.screenOrientation;
	if(this.orientation!=orientation) {
		this.orientationChanged(orientation);
	} else {
		this.getSize(this.orientation);
		this.controller.get('imgMain').mojo.manualSize(this.w, this.h);
	}
}

ViewerAssistant.prototype.getSize = function(orientation){
	if(Mojo.Environment.DeviceInfo.platformVersionMajor < 3) {
		if(orientation == 'left' || orientation == 'right'){
			this.w = Mojo.Environment.DeviceInfo.screenHeight;
			this.h = Mojo.Environment.DeviceInfo.screenWidth;
		} else {
			if(this.settings.fullScreen) {
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
	} else {
		if(this.controller.window.innerHeight > this.controller.window.innerWidth) {
			//left or right
			this.w = this.controller.window.innerWidth;
			this.h = this.controller.window.innerHeight-30;
			this.controller.get("imgMain").style.top = "15px";
		} else {
			this.w = this.controller.window.innerWidth;
			this.h = this.controller.window.innerHeight;
			this.controller.get("imgMain").style.top = "0px";
		}
		this.controller.get("menupanel-toggle").innerText = "width:" + this.w + " height:" +
				this.h;
	}
}

ViewerAssistant.prototype.handleCommand = function(event){
	if(event.type == Mojo.Event.command) {
		if(event.command == 'close') {
			this.handleClose();
		} else if(event.command == 'previous') {
			this.handleLeftMovement();
		} else if(event.command == 'next') {
			this.handleRightMovement();
		} else if(event.command.startsWith("page-")) {
			var page = event.command.substring(5);
			this.imgIndex = parseInt(page)-1;
			this.loadImage();
		}
	} else if(event.type == Mojo.Event.back){
		if(this.settings.gesturePage && !this.menusVisible) {
			event.stop();
			this.handleLeftMovement();
		} else {
			this.comics.open = {file:undefined, page:undefined};
			this.comics.unload(this.timestamp);
			if(this.controller.stageController.window.name == "ComicShelf" &&
					this.controller.stageController.getScenes().length == 1) {
				event.stop();
				this.controller.stageController.swapScene("browser", {
					comics:this.comics,
					path:this.settings.comicDir
				});
			}
		}
	} else if(event.type == Mojo.Event.forward){
		if(this.settings.gesturePage && !this.menusVisible) {
			event.stop();
			this.handleRightMovement();
		}
	}
}

ViewerAssistant.prototype.animateMenuPanel = function(panel, reverse, callback){
	Mojo.Animation.animateStyle(panel, 'top', 'bezier', {
				from: this.menuPanelHiddenTop,
				to: this.menuPanelVisibleTop,
				duration: ViewerAssistant.kMenuPanelAnimationDuration,
				curve:'over-easy',
				reverse:reverse,
				onComplete:callback
			}
	);
};
	
ViewerAssistant.prototype.menuPanelOn = function(){
	var animateMenuCallback;
	var that = this;
	that.panelOpen = true;
	this.scrim.style.opacity = 0;
	this.scrim.show();
	this.enableSceneScroller();
	animateMenuCallback = function(){
		that.menupanel.show();
		that.animateMenuPanel(that.menupanel, false, Mojo.doNothing);
	};
	Mojo.Animation.Scrim.animate(this.scrim, 0, 1, animateMenuCallback);
};
	
ViewerAssistant.prototype.menuPanelOff = function(){
	this.controller.get("shareDrawer").mojo.setOpenState(false);
	var animateMenuCallback;
	var that = this;
	that.panelOpen = false;
	this.disableSceneScroller();
	animateMenuCallback = function(){
		that.menupanel.hide();
		Mojo.Animation.Scrim.animate(that.scrim, 1, 0, that.scrim.hide.bind(that.scrim));
	};
	this.animateMenuPanel(this.menupanel, true, animateMenuCallback);
};
	
ViewerAssistant.prototype.toggleMenuPanel = function(e){
	if(this.panelOpen) {
		this.menuPanelOff();
	} else {
		this.menuPanelOn();
	}
};

ViewerAssistant.prototype.disableSceneScroller = function() {
	this.controller.listen(this.controller.sceneElement, Mojo.Event.dragStart, this._dragHandler);
};

ViewerAssistant.prototype._dragHandler = function(event) {
	// prevents the scene from scrolling.
	event.stop();
};
	
ViewerAssistant.prototype.enableSceneScroller = function() {
	this.controller.stopListening(this.controller.sceneElement, Mojo.Event.dragStart, this._dragHandler);
};

ViewerAssistant.prototype.loadImage = function() {
	this.comics.open.page = this.imgIndex+1;
	this.imageview = this.controller.get('imgMain');
	this.imageview.mojo.centerUrlProvided(this.images[this.imgIndex]);
	this.loaded = true;
	if(this.images.length==1) {
		if(this.settings.swipePage) {
			//this.imageview.mojo.leftUrlProvided("images/blank.png");
			this.imageview.mojo.leftUrlProvided();
			//this.imageview.mojo.rightUrlProvided("images/blank.png");
			this.imageview.mojo.rightUrlProvided();
		}
		this.cmdMenuModel.items[0].disabled = true;
		this.cmdMenuModel.items[2].disabled = true;
	} else if(this.imgIndex == 0) { //first is displayed
		if(this.settings.swipePage) {
			//this.imageview.mojo.leftUrlProvided("images/blank.png");
			this.imageview.mojo.leftUrlProvided();
			this.imageview.mojo.rightUrlProvided(this.images[this.imgIndex+1]);
		}
		this.cmdMenuModel.items[0].disabled = true;
		this.cmdMenuModel.items[2].disabled = false;
	} else if(this.imgIndex == this.images.length-1) { //last is displayed
		if(this.settings.swipePage) {
			this.imageview.mojo.leftUrlProvided(this.images[this.imgIndex-1]);
			//this.imageview.mojo.rightUrlProvided("images/blank.png");
			this.imageview.mojo.rightUrlProvided();
		}
		this.cmdMenuModel.items[0].disabled = false;
		this.cmdMenuModel.items[2].disabled = true;
	} else {
		if(this.settings.swipePage) {
			this.imageview.mojo.leftUrlProvided(this.images[this.imgIndex-1]);
			this.imageview.mojo.rightUrlProvided(this.images[this.imgIndex+1]);
		}
		this.cmdMenuModel.items[0].disabled = false;
		this.cmdMenuModel.items[2].disabled = false;
	}
	this.cmdMenuModel.items[1].label = (this.imgIndex + 1) + "/" + this.images.length;
	this.controller.modelChanged(this.cmdMenuModel);
};

ViewerAssistant.prototype.handleImageViewStateChanged = function(event) {
	/*if(event.error) {
		if(event.url.indexOf(":")==-1) {
			for(var i=0; i<this.images.length; i++) {
				this.images[i] = "/var/luna/data/extractfs" + this.images[i] +
						":0:0:1000:1000:5";
			};
			this.loadImage();
		}
	} else {
		
	}*/
	this.controller.get('imgMain').show()
	this.controller.get('loadingScrim').hide();
	this.controller.get('spinLoading').mojo.stop();
};


ViewerAssistant.prototype.handleImageTap = function(event) {
	if(this.menusVisible) {
		this.menusVisible = false;
		this.controller.get("menupanel-toggle").hide();
	} else {
		this.menusVisible = true;
		this.controller.get("menupanel-toggle").show();
	}
	this.controller.setMenuVisible(Mojo.Menu.commandMenu, this.menusVisible);
};

ViewerAssistant.prototype.handleLeftMovement = function() {
	if(this.imgIndex>0 && this.images.length>1) {
		this.imgIndex--;
		this.loadImage();
	}
};

ViewerAssistant.prototype.handleRightMovement = function() {
	if(this.imgIndex<this.images.length-1 && this.images.length>1) {
		this.imgIndex++;
		this.loadImage();
	}
};

ViewerAssistant.prototype.handleShare = function(event) {
	this.controller.get("shareDrawer").mojo.toggleState();
};

ViewerAssistant.prototype.handleEmailComic = function(event) {
	this.controller.serviceRequest("palm://com.palm.applicationManager", {
		method: "open",
		parameters: {
			id: "com.palm.app.email",
			params: {
				attachments: [
					{fullPath: this.path}
				]
			}
		}
	});
};

ViewerAssistant.prototype.getPagePath = function(event){
	var page = this.images[this.imgIndex];
	var index = page.indexOf(":");
	if(index>-1) {
		page = page.substring(0, index).replace("/var/luna/data/extractfs", "");
	}
	return page;
};

ViewerAssistant.prototype.handleEmailPage = function(event) {
	var page = this.getPagePath();
	this.controller.serviceRequest("palm://com.palm.applicationManager", {
		method: "open",
		parameters: {
			id: "com.palm.app.email",
			params: {
				attachments: [
					{fullPath: page}
				]
			}
		}
	});
};

ViewerAssistant.prototype.handleMMSPage = function(event) {
	var page = this.getPagePath();
	this.controller.serviceRequest("palm://com.palm.applicationManager", {
		method: "open",
		parameters: {
			id: "com.palm.app.messaging",
			params: {
				attachment: page
			}
		}
	});
};

ViewerAssistant.prototype.handleWallpaper = function(event) {
	var page = this.getPagePath();
	var newPath = "/media/internal/comics/" + new Date().getTime() +
			page.substring(page.lastIndexOf("."));
	this.controller.get('spinLoading').mojo.start();
	this.controller.get('loadingScrim').show();
	this.comics.save(
		page,
		newPath,
		function(response) {
			this.controller.serviceRequest("palm://com.palm.systemservice/wallpaper", {
				method: "importWallpaper",
				parameters: {
					target: "file://" + newPath
				},
				onSuccess: function(response) {
					this.controller.serviceRequest("palm://com.palm.systemservice", {
						method: 'setPreferences',
						parameters: {
							wallpaper: response.wallpaper
						},
						onSuccess: function(response2) {
							this.controller.get('loadingScrim').hide();
							this.controller.get('spinLoading').mojo.stop();
							this.comics.deleteFile(newPath);
							MsgBox(this, "Wallpaper set successfully.");
						}.bind(this),
						onFailure: function(err2) {
							this.controller.get('loadingScrim').hide();
							this.controller.get('spinLoading').mojo.stop();
							this.comics.deleteFile(newPath);
							Error(err2.errorText);
						}.bind(this)
					});
				}.bind(this),
				onFailure: function(err) {
					this.controller.get('loadingScrim').hide();
					this.controller.get('spinLoading').mojo.stop();
					this.comics.deleteFile(newPath);
					Error(err.errorText);
				}.bind(this)
			});
		}.bind(this),
		function(err) {
			this.controller.get('loadingScrim').hide();
			this.controller.get('spinLoading').mojo.stop();
			Error(err.errorText);
		}.bind(this)
	);
};

ViewerAssistant.prototype.handleSave = function(event) {
	var page = this.getPagePath();
	var newName = "/media/internal/";
	newName += this.name.substring(0, this.name.lastIndexOf(".")).trim();
	newName += " - Page " + (this.imgIndex+1);
	newName += page.substring(page.lastIndexOf("."));
	this.controller.get('spinLoading').mojo.start();
	this.controller.get('loadingScrim').show();
	this.comics.save(
		page,
		newName,
		function(response) {
			this.controller.get('loadingScrim').hide();
			this.controller.get('spinLoading').mojo.stop()
			MsgBox(this, "Image saved successfully.");
		}.bind(this),
		function(err) {
			this.controller.get('loadingScrim').hide();
			this.controller.get('spinLoading').mojo.stop()
			Error(err.errorText);
		}.bind(this)
	);
};

ViewerAssistant.prototype.handleClose = function(event) {
	this.comics.open = {file:undefined, page:undefined};
	this.comics.unload(this.timestamp);
	if(this.controller.stageController.getScenes().length == 1) {
		this.controller.stageController.swapScene("browser", {
			comics:this.comics,
			path:this.settings.comicDir
		});
	} else {
		this.controller.stageController.popScene();
	}
};

ViewerAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("menupanel-toggle",Mojo.Event.tap, this.toggleMenuPanel);
	this.controller.stopListening("menupanel-scrim", Mojo.Event.tap, this.toggleMenuPanel);
	this.controller.stopListening("shareRow", Mojo.Event.tap, this.handleShare);
	this.controller.stopListening("emailComicRow", Mojo.Event.tap, this.handleEmailComic);
	this.controller.stopListening("emailPageRow", Mojo.Event.tap, this.handleEmailPage);
	this.controller.stopListening("mmsPageRow", Mojo.Event.tap, this.handleMMSPage);
	this.controller.stopListening("wallpaperRow", Mojo.Event.tap, this.handleWallpaper);
	this.controller.stopListening("saveRow", Mojo.Event.tap, this.handleSave);
	this.controller.stopListening("closeRow", Mojo.Event.tap, this.handleClose);
	this.controller.stopListening("imgMain", Mojo.Event.tap, this.handleImageTap);
	this.controller.stopListening("imgMain", Mojo.Event.imageViewChanged, this.handleImageViewStateChanged);
	if(Mojo.Environment.DeviceInfo.platformVersionMajor >= 3) {
		this.controller.stopListening(this.controller.window, "resize", this.handleResize);
	}
	
};

ViewerAssistant.prototype.cleanup = function(event) {

};

ViewerAssistant.kMenuPanelAnimationDuration = 0.12;
