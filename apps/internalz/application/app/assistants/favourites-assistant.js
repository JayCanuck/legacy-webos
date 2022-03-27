function FavouritesAssistant(params) {
	this.filemgr = params.filemgr || new FileMgrService();
	this.BASEDIR = "/media/internal/";
	this.BASEDIR2 = "/var/";
	this.BASEDIR3 = "/media/cryptofs/apps/";
	this.TEXTFILTER = ["sh", "bat", "service", "mk", "html", "htm", "php", "asp",
			"xml", "js", "css", "json", "php", "txt", "log", "conf", "ini",
			"c", "cpp", "cs", "vb", "java", "control", "list", "preinst", "postrm",
			"postinst", "prerm", "cfg", "packages", "properties", "config", "orig",
			"script"];
	this.IMAGEFILTER = ["jpg", "jpeg", "png", "bmp", "gif"];
	this.initiated = false;
}

FavouritesAssistant.prototype.setup = function() {
	this.appAssist = Mojo.Controller.getAppController().assistant;
	this.controller.get("favTitle").innerText = $L("Favorites");
	this.favListAttrs = {
		listTemplate:'favourites/listContainer', 
		itemTemplate:'favourites/listItem',
		swipeToDelete:true,
		autoconfirmDelete:true,
		hasNoWidgets:true,
		reorderable:true,
		onItemRendered: this.handleItemRender.bind(this)
	};
	
	this.orientation = this.appAssist.settings.explorer.orientation;
	if(this.orientation!="free") {
		this.controller.stageController.setWindowOrientation(this.orientation);
	} else {
		this.orientation = this.controller.stageController.getWindowOrientation();
		this.controller.stageController.setWindowOrientation(this.orientation);
	}
	var menuAttr = {omitDefaultItems: true};
  	var menuModel = {
    	visible: true,
    	items: []
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, menuAttr, menuModel);
	this.favListModel = {
		listTitle: $L('Favorites'),
		items: []
	};
	this.controller.setupWidget("listFav", this.favListAttrs, this.favListModel);
	this.adjustPathWidth();
	this.filemgr.query(this.appAssist.settings.explorer.favourites,
		function(response) {
			this.initiated = true;
			this.favListModel.items = response.items;
			for(var i=0; i<this.favListModel.items.length; i++) {
				this.favListModel.items[i].formattedPath = this.favListModel
						.items[i].path.replace("/media/internal/", $L("USB Drive") + "/");
			}
			this.controller.modelChanged(this.favListModel);
			this.appAssist.settings.explorer.favourites = response.exists;
			this.appAssist.saveSettings();
		}.bind(this),
		function(err) {
			this.initiated = true;
			Error($L(err.errorText));
		}.bind(this)
	);
};

FavouritesAssistant.prototype.activate = function(event) {
	if(this.initiated) {
		this.filemgr.query(this.appAssist.settings.explorer.favourites,
			function(response) {
				this.favListModel.items = response.items;
				for(var i=0; i<this.favListModel.items.length; i++) {
					this.favListModel.items[i].formattedPath = this.favListModel
							.items[i].path.replace("/media/internal/", $L("USB Drive") + "/");
				}
				this.controller.modelChanged(this.favListModel);
				this.appAssist.settings.explorer.favourites = response.exists;
				this.appAssist.saveSettings();
			}.bind(this),
			function(err) {
				Error($L(err.errorText));
			}.bind(this)
		);
	}
	this.handleListTap = this.handleListTap.bindAsEventListener(this);
	this.handleListDelete = this.handleListDelete.bindAsEventListener(this);
	this.handleListReorder = this.handleListReorder.bindAsEventListener(this);
	this.controller.listen("listFav", Mojo.Event.listTap, this.handleListTap);
	this.controller.listen("listFav", Mojo.Event.listDelete , this.handleListDelete);
	this.controller.listen("listFav", Mojo.Event.listReorder, this.handleListReorder);
};

FavouritesAssistant.prototype.handleItemRender = function(listWidget, itemModel, itemNode){
	if(this.appAssist.settings.explorer.wrapNames) {
		var row = itemNode;
		var height = row.offsetHeight;
		if(height > 55) {
			row.style.height = height + "px";
			var ele = row.getElementsByTagName("div");
			for(var j=0; j<ele.length; j++) {
				if(ele[j].className.indexOf("icon")!=-1) {
					ele[j].style.top = "4px";
				} else if(ele[j].className.indexOf("nameLabel")!=-1) {
					ele[j].style.top = "-9px";
				} else if(ele[j].className.indexOf("sizeLabel")!=-1) {
					ele[j].style.top = "0px";
				} else if(ele[j].className.indexOf("typeLabel")!=-1) {
					ele[j].style.top = "0px";
				} else if(ele[j].className.indexOf("pathLabel")!=-1) {
					ele[j].style.marginTop = "-21px";
				}
			}
		}
	}
};

FavouritesAssistant.prototype.handleCommand = function(event) {
	if(event.type == Mojo.Event.command) {
		if(event.command == "close") {
			this.controller.stageController.popScene();
		}
	}
};


FavouritesAssistant.prototype.handleListTap = function(event){
	this.currItem = event.item;
	this.currTarget = event.originalEvent.target;

	if(this.currItem.kind=="folder") {
		this.controller.stageController.popScene({action: "goto",
				path:this.currItem.realPath});
	} else if(this.currTarget.className.indexOf("icon")>-1) {
		this.open(this.currItem.name);
	} else {
		this.controller.popupSubmenu({
			onChoose: function(response){
				if(response == 'info') {
					this.info();
				} else if (response == 'open') {
					this.open();
				}
			}.bind(this),
			placeNear: this.currTarget,
			items: 	[	
				{label: $L('Info'), command: 'info'},
				{label: $L('Open'), command: 'open'}
			]
		});
	}
};

FavouritesAssistant.prototype.handleListDelete = function(event){
	this.appAssist.settings.explorer.favourites.splice(event.index, 1);
	this.appAssist.saveSettings();
};

FavouritesAssistant.prototype.info = function(){
	var parent = getFileDir(this.currItem.path) + "/";
	var itemObj = {name:this.currItem.name, path:this.currItem.realPath,
			type:this.currItem.type, size:this.currItem.size, link:this.currItem.link};
	this.controller.stageController.pushScene('info', {item:itemObj,
			writableDir:this.writableDir(parent), filemgr:this.filemgr, dir:parent,
			isDir:(this.currItem.kind=="folder")});
};

FavouritesAssistant.prototype.writableDir = function(parentDir) {
	return this.appAssist.settings.explorer.masterMode ||
			(parentDir.startsWith(this.BASEDIR) ||
			parentDir.startsWith(this.BASEDIR2) ||
			parentDir.startsWith(this.BASEDIR3));
};

FavouritesAssistant.prototype.adjustPathWidth = function() {
	for(var i=0; i<this.controller.document.styleSheets.length; i++) {
		var rules = this.controller.document.styleSheets[i].cssRules;
		for(var j=0; j<rules.length; j++)  {
    		if(rules[j].selectorText==".pathLabel") {
				rules[j].style.width = (this.controller.window.innerWidth-65) +
						"px !important";
				break;
			}
		}
	}
};

FavouritesAssistant.prototype.orientationChanged = function(orientation) {
	if(this.controller.stageController.activeScene().sceneName != 
			this.controller.sceneName) {
		return;
	}
	if(this.orientation === orientation) {
		return;
   	}
   	if(this.appAssist.settings.explorer.orientation=="free") {
		this.orientation = orientation;
		this.controller.stageController.setWindowOrientation(this.orientation);
		this.controller.get("listDir").mojo.invalidateItems(0);
		this.controller.get("listFiles").mojo.invalidateItems(0);
		this.controller.get("listFilesSwipeDelete").mojo.invalidateItems(0);
   	}
	this.adjustPathWidth();
};

FavouritesAssistant.prototype.open = function() {
	var currExt = this.currItem.type.toLowerCase();
	if(currExt=="cbr" || currExt=="cbz") {
		this.palmOpenFileSpecific("ca.canuckcoding.comicshelf",
				this.palmOpenFileGeneric.bind(this, this.checkForTxtFile.bind(this)));
	} else if(this.isInFilter(this.IMAGEFILTER)) {
		this.openImgFile();
	} else if(this.isInFilter(this.TEXTFILTER)) { 
		this.openTxtFile();
	} else if(currExt=="ipk"
			&& this.appAssist.settings.explorer.ipkHandler!="none") {
		this.openIpkFile(this.appAssist.settings.explorer.ipkHandler);
	}else { 
		this.palmOpenFileGeneric(this.checkForTxtFile.bind(this));
	}
};

FavouritesAssistant.prototype.openImgFile = function() {
	var imgParams = {path:this.currItem.path, filemgr:this.filemgr};
	if(this.appAssist.settings.images.newCard) {
		this.openSceneInNewCard('imageview', imgParams);
	} else {
		this.controller.stageController.swapScene('imageview', imgParams);
	}
};

FavouritesAssistant.prototype.openTxtFile = function() {
	var txtParams = {path:this.currItem.path, filemgr:this.filemgr};
	if(this.appAssist.settings.editor.newCard) {
		this.openSceneInNewCard('texteditor', txtParams);
	} else {
		this.controller.stageController.swapScene('texteditor', txtParams);
	}
};

FavouritesAssistant.prototype.openPatchFile = function() {
	var patchParams = {path:this.currItem.path, filemgr:this.filemgr};
	if(this.appAssist.settings.editor.newCard) {
		this.openSceneInNewCard('patcher', patchParams);
	} else {
		this.controller.stageController.swapScene('patcher', patchParams);
	}
};

FavouritesAssistant.prototype.openIpkFile = function() {
	var patchParams = {path:this.currItem.path, filemgr:this.filemgr};
	if(this.appAssist.settings.ipk.newCard) {
		this.openSceneInNewCard('ipkinstaller', patchParams);
	} else {
		this.controller.stageController.swapScene('ipkinstaller', patchParams);
	}
};

FavouritesAssistant.prototype.palmOpenFileGeneric = function(onFailure) {
	this.controller.serviceRequest('palm://com.palm.applicationManager', {
    	method: 'open',
    	parameters: {
        	target: 'file://' + this.currItem.realPath
        },
		onFailure: onFailure
    });
};

FavouritesAssistant.prototype.palmOpenFileSpecific = function(appId, onFailure) {
	this.controller.serviceRequest('palm://com.palm.applicationManager', {
    	method: 'open',
    	parameters: {
        	id: appId,
			params: {
				target: 'file://' + this.currItem.realPath
			}
        },
		onFailure: onFailure
    });
};

FavouritesAssistant.prototype.checkForTxtFile = function() {
	this.filemgr.isTextFile(this.currItem.path,
		function(response) {
			if(response.isText) {
				this.openTxtFile();
			} else {
				Error($L("No file handler found for #{path}")
						.interpolate({path: this.currItem.path}));
			}
		}.bind(this),
		function(err) {
			Error($L("No file handler found for #{path}")
					.interpolate({path: this.currItem.path}));
		}.bind(this)
	);
};

FavouritesAssistant.prototype.isInFilter = function(filter) {
	var result = false;
	var currExt = this.currItem.type.toLowerCase();
	for(var i=0; i<filter.length; i++) {
		if(filter[i] == currExt) {
			result = true;
			break;
		}
	}
	return result;
};

FavouritesAssistant.prototype.openSceneInNewCard = function(scene, params){
	var appController = Mojo.Controller.getAppController();
	var stageController = appController.getStageController(this.currItem.path);
	if(stageController) {
		stageController.activate();
		return;
	}
	var callback = function(controller) {
		controller.pushScene(scene, params);		
	}.bind(this);
	appController.createStageWithCallback(this.currItem.path, callback);
};

FavouritesAssistant.prototype.gracefulTransition = function(name) {
	var appController = Mojo.Controller.getAppController();
	appController.closeStage(name);
	this.controller.window.setTimeout(function(){
		this.controller.stageController.activate();
	}.bind(this), 250);
};

FavouritesAssistant.prototype.handleListReorder = function(event){
	var path = this.appAssist.settings.explorer.favourites.splice(event.fromIndex, 1)[0];
	this.appAssist.settings.explorer.favourites.splice(event.toIndex, 0, path);
	this.appAssist.saveSettings();
};

FavouritesAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("listFav", Mojo.Event.listTap, this.handleListTap);
	this.controller.stopListening("listFav", Mojo.Event.listDelete,
			this.handleListDelete);
	this.controller.stopListening("listFav", Mojo.Event.listReorder,
			this.handleListReorder);
};

FavouritesAssistant.prototype.cleanup = function(event) {
};
