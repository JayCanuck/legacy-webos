function ExplorerAssistant(params){
	this.filemgr = params.filemgr;
	this.currDir = params.path;
	this.BASEDIR = "/media/internal/";
	this.TEXTFILTER = new Array("sh", "bat", "service", "mk", "html", "htm", "php", "asp", "xml", "js", "css", "json", "php", "txt", "log", "conf", "ini", "c", "cpp", "cs", "vb", "java", "control", "list", "preinst", "postrm", "postinst", "prerm", "cfg", "packages", "properties", "config", "orig", "script");
	this.IMAGEFILTER = new Array("jpg", "jpeg", "png", "bmp", "gif");
	this.currDir;
	this.fileListModel;
	this.subVal;
	this.sort;
	this.ascending;
	this.currTarget;
	this.currItem;
	this.history;
	this.imgList;
	this.scrollHistory;
	this.appAssist;
	this.currScroll = 0;
}

ExplorerAssistant.prototype.setup = function() {
	this.appAssist = Mojo.Controller.getAppController().assistant;
	this.subVal = this.appAssist.settings.explorer.subVal;
	this.sort = this.appAssist.settings.explorer.sort;
	this.ascending = this.appAssist.settings.explorer.ascending;
	this.showHidden = this.appAssist.settings.explorer.showHidden;
	this.orientation = this.appAssist.settings.explorer.orientation;
	if(this.orientation!="free") {
		this.controller.stageController.setWindowOrientation(this.orientation);
	} else {
		this.orientation = this.controller.stageController.getWindowOrientation();
		this.controller.stageController.setWindowOrientation(this.orientation);
	}
	this.controller.get("nameLabel").innerText = $L("Name");
	if(this.subVal==1) {
		this.controller.get("subLabel").innerText = $L("Size");
		this.setClassVisible("sizeLabel", true);
	} else {
		this.controller.get("subLabel").innerText = $L("Type");
		this.setClassVisible("typeLabel", true);
	}
	var bodyEle = this.controller.document.getElementsByTagName("body")[0];
	var themes = ["palm-dark"];
	themes = themes.splice(themes.indexOf(this.appAssist.settings.explorer.theme), 1);
	for(var themeCounter=0; themeCounter<themes.length; themeCounter++) {
		if(bodyEle.className.indexOf(themes[themeCounter]) > -1) {
			bodyEle.removeClassName(themes[themeCounter]);
		}
	}
	if(this.appAssist.settings.explorer.theme!="palm-default") {
		bodyEle.addClassName(this.appAssist.settings.explorer.theme);
	}
	
	this.history = [];
	this.scrollHistory = [];
	this.controller.get("subtitle").innerText = this.currDir.replace(this.BASEDIR, $L("USB Drive") + "/");

	var menuAttr = {omitDefaultItems: true};
	this.menuItems = [
		{label: $L("Create Directory"), command: 'newDir'},
		{label: $L("Create File"), command: 'newFile'},
		{label: $L("Preferences"), command:Mojo.Menu.prefsCmd},
		{label: $L("Help"), command:Mojo.Menu.helpCmd}
	];
  	this.menuModel = {
    	visible: true,
    	items: this.menuItems
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, menuAttr, this.menuModel);
	
	this.controller.setupWidget("loadingSpinner", {spinnerSize: Mojo.Widget.spinnerSmall},
        	{spinning: true}); 
	
	this.initLists();
};

ExplorerAssistant.prototype.setClassVisible = function(name, visible) {
	var display = "none";
	if(visible) {
		display = "inline";
	}
	for(var i=0; i<this.controller.document.styleSheets.length; i++) {
		var rules = this.controller.document.styleSheets[i].cssRules;
		for(var j=0; j<rules.length; j++)  {
    		if(rules[j].selectorText == "." + name) {
				rules[j].style.display = display;
				break;
			}
		}
	}
};

/*ExplorerAssistant.prototype.setupCmdMenu = function() {
	var cmdMenuAttr = {menuClass: 'filepathBtn'};
	var cmdMenuModel = {
		items: [
			{label: currDir, width: 320, command:'filepathBtn'}
		]
	};
	this.controller.setupWidget(Mojo.Menu.commandMenu, cmdMenuAttr, cmdMenuModel);
};*/

ExplorerAssistant.prototype.initLists = function() {
	this.controller.get("baseDir").hide();
	this.controller.get("directory-divider").innerHTML = '';
	this.controller.get("line-between-lists").innerHTML='';
	this.controller.get("line-after-lists").innerHTML='';

	//Format base widget, add data when service request finishes
	this.dirListAttrs = {
		listTemplate:'explorer/listContainer', 
		itemTemplate:'explorer/listItemDir',
		swipeToDelete:false,
		autoconfirmDelete:false,
		hasNoWidgets:true,
		onItemRendered: this.handleItemRender.bind(this)
	};
	this.dirListModel = {
		listTitle: $L('Directories'),
		items: []
	};
	//{name: $L("Loading directories..."), link:"no"}
	this.controller.setupWidget("listDir", this.dirListAttrs, this.dirListModel);

	//Format base widget, add data when service request finishes
	this.fileListNoSwipeAttrs = {
		listTemplate:'explorer/listContainer', 
		itemTemplate:'explorer/listItemFile',
		swipeToDelete:false,
		autoconfirmDelete:false,
		hasNoWidgets:true,
		onItemRendered: this.handleItemRender.bind(this)
	};
	this.fileListNoSwipeModel = {
		listTitle: $L('Files'),
		items: []
	};
	//{name: $L("Loading files..."), size:"", type:"", link:"no"}
	this.controller.setupWidget("listFiles", this.fileListNoSwipeAttrs,
			this.fileListNoSwipeModel);
	this.fileListSwipeDeleteAttrs = {
		listTemplate:'explorer/listContainer', 
		itemTemplate:'explorer/listItemFile',
		swipeToDelete:true,
		autoconfirmDelete:false,
		hasNoWidgets:true,
		onItemRendered: this.handleItemRender.bind(this)
	};
	this.fileListSwipeDeleteModel = {
		listTitle: $L('Files'),
		items: []
	};
	this.controller.setupWidget("listFilesSwipeDelete", this.fileListSwipeDeleteAttrs,
			this.fileListSwipeDeleteModel);
	
	if(this.appAssist.settings.explorer.swipeDelete) {
		this.fileListModel = this.fileListSwipeDeleteModel;
	} else {
		this.fileListModel = this.fileListNoSwipeModel;
	}	
	this.sortVal = 1;

	for(var i=0; i<this.controller.document.styleSheets.length; i++) {
		var rules = this.controller.document.styleSheets[i].cssRules;
		for(var j=0; j<rules.length; j++)  {
    		if(rules[j].selectorText==".nameLabel") {
				rules[j].style.fontSize = this.appAssist.settings.explorer.fontSize +
						" !important";
				break;
			}
		}
	}
	if(this.appAssist.settings.explorer.wrapNames) {

		for(var i=0; i<this.controller.document.styleSheets.length; i++) {
			var rules = this.controller.document.styleSheets[i].cssRules;
			for(var j=0; j<rules.length; j++)  {
	    		if(rules[j].selectorText==".nameEntryStyle") {
					rules[j].style.whiteSpace = "normal !important";
					rules[j].style.wordWrap="break-word !important";
					break;
				}
			}
		}
	}

	this.filemgr.isDirectory(this.currDir,
		function(response) {
			if(!response.isDirectory) {
				this.controller.get("baseDir").hide();
				Error($L("#{directory} not found.").interpolate({directory:this.currDir}));
				this.currDir = this.BASEDIR;
			}
			this.loadLists();
		}.bind(this),
		function(err) {
			if(this.currDir == this.BASEDIR) {
				this.controller.get("baseDir").hide();
			}
			Error($L(err.errorText));
			this.controller.get("subtitle").innerText = $L("An error has occurred");
		}.bind(this)
	);
};

ExplorerAssistant.prototype.toggleName = function(event){
	event.stop();
	this.sort = "name";
	this.ascending = !this.ascending;
	this.doSort();
};

ExplorerAssistant.prototype.toggleSub = function(event){
	event.stop();
	if(this.subVal==1) {
		this.sort = "size";
	} else {
		this.sort = "type";
	}
	this.ascending = !this.ascending;
	this.doSort();
};

ExplorerAssistant.prototype.doSort = function(){
	if(this.sort=="size") {
		this.dirListModel.items.sort(jsonArraySort("bytes", !this.ascending, parseInt));
		this.fileListModel.items.sort(jsonArraySort("bytes", !this.ascending, parseInt));
	} else {
		this.dirListModel.items.sort(jsonArraySort(this.sort, !this.ascending,
				lowercase));
		this.fileListModel.items.sort(jsonArraySort(this.sort, !this.ascending,
				lowercase));
	}
	this.controller.get("listDir").mojo.noticeUpdatedItems(0, this.dirListModel.items);
	if(this.appAssist.settings.explorer.swipeDelete) {
		this.controller.get("listFilesSwipeDelete").mojo.noticeUpdatedItems(0,
				this.fileListModel.items);
	} else {
		this.controller.get("listFiles").mojo.noticeUpdatedItems(0,
				this.fileListModel.items);
	}
};

ExplorerAssistant.prototype.toggleSubType = function(event){
	event.stop();
	if(this.subVal==1) {
		this.subVal = 2;
		this.controller.get("subLabel").innerText = $L("Type");
		this.setClassVisible("sizeLabel", false);
		this.setClassVisible("typeLabel", true);
	} else {
		this.subVal = 1;
		this.controller.get("subLabel").innerText = $L("Size");
		this.setClassVisible("typeLabel", false);
		this.setClassVisible("sizeLabel", true);
	}
	this.appAssist.settings.explorer.subVal = this.subVal;
	this.appAssist.saveSettings();
	//this.loadLists();
};

ExplorerAssistant.prototype.loadLists = function(){
	this.controller.get("directory-divider").innerHTML = '';
	this.controller.get("line-between-lists").innerHTML='';
	this.controller.get("line-after-lists").innerHTML='';
	
	this.dirListModel.items = [];
	this.controller.modelChanged(this.dirListModel);
	this.fileListSwipeDeleteModel.items = [];
	this.fileListNoSwipeModel.items = [];
	this.controller.modelChanged(this.fileListSwipeDeleteModel);
	this.controller.modelChanged(this.fileListNoSwipeModel);
	
	if(this.currDir == this.BASEDIR) {
		this.controller.get("baseDir").hide();
	} else {
		this.controller.get("baseDir").show();
		this.controller.get("directory-divider").innerHTML = '<hr/>';
	}
	
	if(this.appAssist.settings.explorer.swipeDelete) {
		this.fileListModel = this.fileListSwipeDeleteModel;
		this.controller.get("listFiles").hide();
		this.controller.get("listFilesSwipeDelete").show();
	} else {
		this.fileListModel = this.fileListNoSwipeModel;
		this.controller.get("listFilesSwipeDelete").hide();
		this.controller.get("listFiles").show();
	}

	this.filemgr.list(
		{
			path: this.currDir,
			sort: this.sort,
			ascending: this.ascending,
			ignoreHidden: !this.showHidden,
		},
		this.grabItemsSuccess.bind(this),
		function(err) {
			Error($L(err.errorText));
			if(this.history.length>0) {
				this.currDir = this.history.pop();
			} else {
				this.currDir = this.appAssist.DEFAULTSETTINGS.explorer.startDir;
			}
			this.controller.get("subtitle").innerText = this.currDir
					.replace(this.BASEDIR, $L("USB Drive") + "/");;
			this.appAssist.settings.explorer.startDir = this.currDir;
			this.appAssist.saveSettings();
		}.bind(this)
	);
};

ExplorerAssistant.prototype.grabItemsSuccess = function(response) {
	if(response.dirs.length!=0) {
		this.controller.get("line-between-lists").innerHTML='<hr/>';
	}
	this.dirListModel.items = response.dirs;
	this.controller.modelChanged(this.dirListModel);

	if(response.files.length!=0) {
		this.controller.get("line-after-lists").innerHTML='<hr/>';
	}
	
	this.fileListModel.items = response.files;
	this.controller.modelChanged(this.fileListModel);
	this.imgList = [];
	for(var i=0; i<this.fileListModel.items.length; i++) {
		var ext = getFileExt(this.fileListModel.items[i].name);
		if(ext=="png" || ext=="jpg" || ext=="jpeg" || ext=="bmp") {
			this.imgList.push(this.fileListModel.items[i].path);
		}
	}
	
	if(this.currScroll !=0) {
		this.setScrollPosition(this.currScroll);
		this.currScroll = 0;
	}
	this.controller.get("loadingSpinner").hide();
	this.controller.get("loadingSpinner").mojo.stop();
};

ExplorerAssistant.prototype.handleItemRender = function(listWidget, itemModel, itemNode){
	if(this.appAssist.settings.explorer.wrapNames) {
		var row = itemNode;
		var height = row.offsetHeight;
		if(height > 52) {
			row.style.height = height-14 + "px";
			var ele = row.getElementsByTagName("div");
			for(var j=0; j<ele.length; j++) {
				if(ele[j].className.indexOf("icon")!=-1) {
					ele[j].style.top = "4px";
				} else if(ele[j].className.indexOf("nameLabel")!=-1) {
					ele[j].style.top = "-9px";
				}
			}
		}
	}
};

ExplorerAssistant.prototype.updateListRowSizeIfNeeded = function(listId){
	if(this.appAssist.settings.explorer.wrapNames) {
		var rows = this.controller.get(listId).getElementsByClassName("palm-row-wrapper");
		for(var i=0; i<rows.length; i++) {
			rows[i].style.height = "";
			var ele = rows[i].getElementsByTagName("div");
			for(var k=0; k<ele.length; k++) {
				ele[k].style.top = "";
			}
			var height = rows[i].offsetHeight;
			if(height > 52) {
				rows[i].style.height = height-14 + "px";
				for(var j=0; j<ele.length; j++) {
					if(ele[j].className.indexOf("icon")!=-1) {
						ele[j].style.top = "4px";
					} else if(ele[j].className.indexOf("nameLabel")!=-1) {
						ele[j].style.top = "-9px";
					}
				}
			}
		}
	}
};

ExplorerAssistant.prototype.handleCommand = function(event){
  	if (event.type == Mojo.Event.command) {
		switch (event.command) {
		case 'newDir':
			this.newDir();
			break;
		case 'newFile':
			this.newFile();
			break;
		case Mojo.Menu.prefsCmd:
			this.controller.stageController.pushScene("settings", {filemgr:this.filemgr});
			break;
		case Mojo.Menu.helpCmd:
			this.controller.stageController.pushAppSupportInfoScene();
			break;
		}
	} else if(event.type == Mojo.Event.back){
		if (this.history.length > 0) {
			event.stop();
			this.currDir = this.history.pop();
			this.currScroll = this.scrollHistory.pop();
			this.controller.get("subtitle").innerText = this.currDir
					.replace(this.BASEDIR, $L("USB Drive") + "/");
			this.loadLists();
		}
	} else if(event.type == Mojo.Event.forward) {
		this.currScroll = this.controller.getSceneScroller().mojo.getState().top;
		this.loadLists();
	}
};

ExplorerAssistant.prototype.setScrollPosition = function(position){
	var state = this.controller.getSceneScroller().mojo.getState();
	state.top = position;
	this.controller.getSceneScroller().mojo.setState(state, false);
};

ExplorerAssistant.prototype.handleBaseDir = function(event){
	if(this.currDir == this.BASEDIR) {
		return;
	}
	this.filemgr.getParent(this.currDir,
		this.getParentSuccess.bind(this),
		function(err) {
			Error($L(err.errorText));
		}.bind(this)
	);
};

ExplorerAssistant.prototype.handleDirList = function(event){
	this.currItem = event.item;
	this.history.push(this.currDir);
	this.scrollHistory.push(this.controller.getSceneScroller().mojo.getState().top);
	this.currDir = this.currItem.path;
	if(!this.currDir.endsWith("/"))
		this.currDir += "/";
	this.controller.get("subtitle").innerText = this.currDir
			.replace(this.BASEDIR, $L("USB Drive") + "/");
	this.loadLists();
};

ExplorerAssistant.prototype.handleFileList = function(event) {
	this.currItem = event.item;
	this.currTarget = event.originalEvent.target;

	var filePopupModel = [		
			{label: $L('Info'), command: 'info'},
			{label: $L('Open'), command: 'open'},
			{label: $L('Move'),	command: 'move'},
			{label: $L('Copy'), command: 'copy'},
			{label: $L('Delete'), command: 'delete'}
		];
	if(this.currTarget.className.indexOf("icon")>-1) {
		this.open(this.currItem.name);
	} else {
		this.controller.popupSubmenu({
			onChoose: function(response){
				if (response == 'info') {
					this.info(false);
				} else if (response == 'open') {
					this.open();
				} else if (response == 'move') {
					this.move(false);
				} else if (response == 'copy') {
					this.copy(false);
				} else if (response == 'delete') {
					this.controller.showAlertDialog({
			   			onChoose: function(value) {
							if (value == 'yes') {
								var listId = "listFiles";
								var itemIndex = this.fileListModel.items
										.indexOf(this.currItem);
								if(this.appAssist.settings.explorer.swipeDelete) {
									listId = "listFilesSwipeDelete";
								}
								this.controller.get(listId).mojo
										.noticeRemovedItems(itemIndex, 1);
								this.fileListModel.items.splice(itemIndex, 1);
								this.del();
							}
						}.bind(this),
						title: $L("Delete?"),
			    		message: $L("Are you sure you want to delete #{directoryname} " +
								"and all of its contents? This cannot be undone.")
								.interpolate({directoryname:"\"" + this.currItem.name +
								"\""}),
			    		choices:[
			        		{label:$L('Yes'), value:"yes", type:'affirmative'},  
			        		{label:$L('No'), value:"no", type:'negative'} 
			    		]
			    	});
				}
			},
			placeNear: this.currTarget,
			items: filePopupModel
		});
	}
};

ExplorerAssistant.prototype.getParentSuccess = function(response) {
	this.history.push(this.currDir);
	this.scrollHistory.push(0);
	this.currDir = response.parent;
	if(!this.currDir.endsWith("/"))
		this.currDir += "/";
	this.controller.get("subtitle").innerText = this.currDir
			.replace(this.BASEDIR, $L("USB Drive") + "/");;
	this.loadLists();
};

ExplorerAssistant.prototype.info = function(isDir){
	this.appAssist.settings.explorer.startDir = this.currDir;
	this.appAssist.saveSettings();
	this.controller.stageController.pushScene('info', {item: this.currItem,
			writableDir:true, filemgr:this.filemgr, dir:this.currDir,
			isDir:isDir});
};

ExplorerAssistant.prototype.open = function() {
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

ExplorerAssistant.prototype.openImgFile = function() {
	var imgParams = {path:this.currItem.path, filemgr:this.filemgr};
	if(this.appAssist.settings.images.newCard) {
		this.openSceneInNewCard('imageview', imgParams);
	} else {
		this.controller.stageController.pushScene('imageview', imgParams);
	}
};

ExplorerAssistant.prototype.openTxtFile = function() {
	var txtParams = {path:this.currItem.path, filemgr:this.filemgr};
	if(this.appAssist.settings.editor.newCard) {
		this.openSceneInNewCard('texteditor', txtParams);
	} else {
		this.controller.stageController.pushScene('texteditor', txtParams);
	}
};

ExplorerAssistant.prototype.palmOpenFileGeneric = function(onFailure) {
	this.controller.serviceRequest('palm://com.palm.applicationManager', {
    	method: 'open',
    	parameters: {
        	target: 'file://' + this.currItem.path
        },
		onFailure: onFailure
    });
};

ExplorerAssistant.prototype.palmOpenFileSpecific = function(appId, onFailure) {
	this.controller.serviceRequest('palm://com.palm.applicationManager', {
    	method: 'open',
    	parameters: {
        	id: appId,
			params: {
				target: 'file://' + this.currItem.path
			}
        },
		onFailure: onFailure
    });
};

ExplorerAssistant.prototype.openIpkFile = function(appId) {
	this.controller.serviceRequest('palm://com.palm.applicationManager', {
    	method: 'open',
    	parameters: {
        	id: appId,
			params: {
				target: 'file://' + this.currItem.path
			}
        },
		onFailure: function(err) {
			var appName = "<Unknown>";
			if(appId=="ca.canucksoftware.internalz") {
				appName = "Internalz Pro";
			} else if(appId=="org.webosinternals.preware") {
				appName = "Preware";
			}
			Error($L("#{appname} not found").interpolate({appname:appName}));
		}
    });
};

ExplorerAssistant.prototype.checkForTxtFile = function() {
	this.filemgr.isTextFile(this.currItem.path,
		function(response) {
			if(response.isText) {
				this.openTxtFile();
			} else {
				Error($L("No file handler found for #{path}")
						.interpolate({path: this.currDir + this.currItem.name}));
			}
		}.bind(this),
		function(err) {
			Error($L("No file handler found for #{path}")
					.interpolate({path: this.currDir + this.currItem.name}));
		}.bind(this)
	);
};

ExplorerAssistant.prototype.orientationChanged = function(orientation){
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
	
};

ExplorerAssistant.prototype.openSceneInNewCard = function(scene, params){
	var stageName = this.currItem.path;
	if(scene=="imageview" && this.appAssist.settings.images.swipeChange &&
			stageName.startsWith("/media/internal/")) {
		stageName = (new Date().getTime()) + stageName;
	}
	var appController = Mojo.Controller.getAppController();
	var stageController = appController.getStageController(stageName);
	if(stageController) {
		stageController.activate();
		return;
	}
	var callback = function(controller) {
		controller.pushScene(scene, params);		
	}.bind(this);
	appController.createStageWithCallback(stageName, callback);
};

ExplorerAssistant.prototype.gracefulTransition = function(name) {
	var appController = Mojo.Controller.getAppController();
	appController.closeStage(name);
	this.controller.window.setTimeout(function(){
		this.controller.stageController.activate();
	}.bind(this), 250);
};

ExplorerAssistant.prototype.isExt = function(name, ext) {
	var ends = false;
	if(name.lastIndexOf(".")>-1)
		if(name.substring(name.lastIndexOf(".")+1)==ext)
			ends = true;
	return ends;
};

ExplorerAssistant.prototype.isInFilter = function(filter) {
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

ExplorerAssistant.prototype.updateSize = function(path) {
	this.tempIndex = this.itemIndexFromPath(path, this.fileListModel);
	if(this.tempIndex == -1) { //not in list, refresh list
		this.loadLists();
	} else {
		this.filemgr.getSize(
			{
				file: path,
				formatted: true
			},
			function(response) {
				var obj = this.fileListModel.items[this.tempIndex];
				obj.size = response.size;
				if(this.appAssist.settings.explorer.swipeDelete) {
					this.controller.get("listFilesSwipeDelete")
							.mojo.noticeUpdatedItems(this.tempIndex, [obj]);
				} else {
					this.controller.get("listFiles")
							.mojo.noticeUpdatedItems(this.tempIndex, [obj]);
				}
			}.bind(this),
			function() {
				this.loadLists();
			}.bind(this)
		);
	}
}

ExplorerAssistant.prototype.handleFileDelete = function(event) {
	event.stop();
	this.currItem = event.item;
	var itemIndex = this.fileListModel.items.indexOf(this.currItem);
	this.controller.get("listFilesSwipeDelete").mojo.noticeRemovedItems(itemIndex, 1);
	this.fileListModel.items.splice(itemIndex, 1);
	this.del();
};

ExplorerAssistant.prototype.del = function() {
	if(this.dirListModel.items.length==0)
		this.controller.get("line-between-lists").innerHTML='';
	if(this.fileListModel.items.length==0)
		this.controller.get("line-after-lists").innerHTML='';
		
	this.filemgr.deleteFile(this.currDir + this.currItem.name,
		function(response) {
			var file = response.commands[response.commands.length-1];
			file = this.getName(file);
			this.controller.showBanner("Deleted: " + file, "");
		}.bind(this),
		function(err) {
			if(err.errorText.indexOf("File/directory does not exist.")!=0)
				Error($L(err.errorText));
		}.bind(this)
    );
};

ExplorerAssistant.prototype.handleHold = function(event) {
	event.stop();
	this.currTarget=event.target;
	this.currItem = this.controller.get('listDir').mojo.getItemByNode(this.currTarget);

	var dirPopupModel = [
			{label: $L('Info'), command: 'dirInfo'},
			{label: $L('Open'), command: 'dirOpen'},
			{label: $L('Move'),	command: 'dirMove'},
			{label: $L('Copy'), command: 'dirCopy'},
			{label: $L('Delete'), command: 'dirDelete'}
		];
	if (this.currItem.name != "..") {
		this.controller.popupSubmenu({
			onChoose: function(response){
				if (response == 'dirInfo') {
					this.info(true);
				} else if (response == 'dirOpen') {
					this.handleDirList({item: this.currItem});
				} else if (response == 'dirMove') {
					this.move(true);
				} else if (response == 'dirCopy') {
					this.copy(true);
				} else if (response == 'dirDelete') {
					this.controller.showAlertDialog({
			   			onChoose: function(value) {
							if(value=='yes') {
								var itemIndex = this.dirListModel.items
										.indexOf(this.currItem);
								this.controller.get("listDir").mojo
										.noticeRemovedItems(itemIndex, 1);
								this.dirListModel.items.splice(itemIndex, 1);
								this.del();
							}
						},
						title: $L("Delete?"),
			    		message: $L("Are you sure you want to delete #{filename}? " +
								"This cannot be undone.").interpolate({filename: "\"" +
								this.currItem.name + "\""}),
			    		choices:[
			        		{label:$L('Yes'), value:"yes", type:'affirmative'},  
			        		{label:$L('No'), value:"no", type:'negative'} 
			    		]
			    	});
				}
			},
			placeNear: this.currTarget,
			items: dirPopupModel
		});
	}
};

ExplorerAssistant.prototype.move = function(isDir){
	this.appAssist.settings.explorer.startDir = this.currDir;
	this.appAssist.saveSettings();
	var params = {
		action:"move",
		name:this.currItem.name,
		path:this.currItem.path,
		isDir:isDir,
		filemgr:this.filemgr,
	};
	this.controller.stageController.pushScene("folder-chooser", params);
};

ExplorerAssistant.prototype.copy = function(isDir){
	this.appAssist.settings.explorer.startDir = this.currDir;
	this.appAssist.saveSettings();
	var params = {
		action:"copy",
		name:this.currItem.name,
		path:this.currItem.path,
		isDir:isDir,
		filemgr:this.filemgr
	};
	this.controller.stageController.pushScene("folder-chooser", params);
};

ExplorerAssistant.prototype.newDir = function(){
	this.appAssist.settings.explorer.startDir = this.currDir;
	this.appAssist.saveSettings();
	this.controller.showDialog({
		template: 'input-dialog/input-dialog-popup',
		assistant: new InputDialogAssistant({
			sceneAssistant: this,
			title: $L("Create Directory"),
			message: $L("New directory's name:"),
			init: "",
			onAccept: function(value) {
				this.temp = value;
				var index = this.indexOfItem(this.temp, this.dirListModel);
				if(index>-1) {
					Error($L("Cannot create directory: #{directoryname} already exists.")
							.interpolate({directoryname: "\"" + this.temp + "\""}));
				} else {
					this.filemgr.createDir(this.currDir + value + "/",
						function(response) {
							if(response.created) {
								this.loadLists();
								this.controller.showBanner($L("Directory created: " +
										"#{directoryname}").interpolate({directoryname:
										this.temp}), "");
							} else {
								Error($L("Unable to create directory."));
							}
						}.bind(this),
						function(err) {
							Error($L(err.errorText));
						}.bind(this)
					);
				}
			}.bind(this)
		})
	});
};

ExplorerAssistant.prototype.newFile = function(){
	this.appAssist.settings.explorer.startDir = this.currDir;
	this.appAssist.saveSettings();
	this.controller.showDialog({
		template: 'input-dialog/input-dialog-popup',
		assistant: new InputDialogAssistant({
			sceneAssistant: this,
			title: $L("Create File"),
			message: $L("New file's name:"),
			init: "",
			onAccept: function(value) {
				this.temp = value;
				var index = this.indexOfItem(this.temp, this.fileListModel);
				if(index>-1) {
					Error($L("Cannot create file: #{filename} already exists.")
							.interpolate({filename: "\"" + this.temp + "\""}));
				} else {
					this.filemgr.createFile(this.currDir + this.temp,
						function(response) {
							if(response.created) {
								this.loadLists();
								this.controller.showBanner($L("File created: " +
										"#{filename}").interpolate({filename:this.temp}),
										"");
							} else {
								Error($L("Unable to create file."));
							}
						}.bind(this),
						function(err) {
							Error($L(err.errorText));
						}.bind(this)
					);
				}
			}.bind(this)
		})
	});
};

ExplorerAssistant.prototype.indexOfItem = function(name, model){
	var result = -1;
	for(var i=0; i<model.items.length; i++) {
		if(model.items[i].name == name) {
			result = i;
			break;
		}
	}
	return result;
};

ExplorerAssistant.prototype.itemIndexFromPath = function(path, model){
	var result = -1;
	for(var i=0; i<model.items.length; i++) {
		if(model.items[i].path == path) {
			result = i;
			break;
		}
	}
	return result;
};

ExplorerAssistant.prototype.getName = function(path){
	var result = path;
	if(result.charAt(result.length-1)=="/") {
		result = result.substring(0,result.length-1);
	}
	if(result.lastIndexOf("/")>-1) {
		result = result.substring(result.lastIndexOf("/")+1);
	}
	return result;
};

ExplorerAssistant.prototype.handleHome = function(event){
	var home = this.appAssist.settings.explorer.homeDir;
	if (this.currDir != home) {
		this.history.push(this.currDir);
		this.currDir = home;
		this.controller.get("subtitle").innerText = this.currDir
				.replace(this.BASEDIR, $L("USB Drive") + "/");
		this.loadLists();
	}
};

ExplorerAssistant.prototype.handleFav = function(event){
	this.controller.stageController.pushScene("favourites", {filemgr:this.filemgr});
};

ExplorerAssistant.prototype.activate = function(event) {
	if(event!=null){
		if(event.action=="move") {
			if(event.targetIsDir) {
				var itemIndex = this.dirListModel.items.indexOf(this.currItem);
				this.controller.get("listDir").mojo.noticeRemovedItems(itemIndex, 1);
				this.dirListModel.items.splice(itemIndex, 1);
				if(this.dirListModel.items.length!=0)
					this.controller.get("line-between-lists").innerHTML='<hr/>';
				else
					this.controller.get("line-between-lists").innerHTML='';
			} else {
				var itemIndex = this.fileListModel.items.indexOf(this.currItem);
				if(this.appAssist.settings.explorer.swipeDelete) {
					this.controller.get("listFilesSwipeDelete").mojo
							.noticeRemovedItems(itemIndex, 1);
				} else {
					this.controller.get("listFiles").mojo
							.noticeRemovedItems(itemIndex, 1);
				}
				this.fileListModel.items.splice(itemIndex, 1);
				if(this.fileListModel.items.length!=0)
					this.controller.get("line-after-lists").innerHTML='<hr/>';
				else
					this.controller.get("line-after-lists").innerHTML='';
			}
			this.filemgr.move(event.from, event.to,
				function(response) {
					var file = response.commands[response.commands.length-1];
					if(file=="sync") {
						file = response.commands[response.commands.length-3];
					}
					file = this.getName(file);
					this.controller.showBanner($L("Moved: #{filename}")
							.interpolate({filename:file}), "");
				}.bind(this),
				function(err) {
					if((err.errorText.length==0) ||
							(err.errorText.indexOf("cp: recursion detected")==0)) {
						this.controller.showBanner($L("Move completed"), "");
					} else {
						Error($L(err.errorText));
					}
				}.bind(this)
			);
		} else if(event.action=="copy") {
			this.filemgr.copy(event.from, event.to,
				function(response) {
					var file = response.commands[response.commands.length-1];
					file = this.getName(file);
					this.controller.showBanner($L("Copied: #{filename}")
							.interpolate({filename:file}), "");
				}.bind(this),
				function(err) {
					if((err.errorText.length==0) ||
							(err.errorText.indexOf("cp: recursion detected")==0)) {
						this.controller.showBanner($L("Copy completed"), "");
					} else {
						Error($L(err.errorText));
					}
				}.bind(this)
			);
		} else if(event.action=="updatePrefs") {
			this.orientation = this.appAssist.settings.explorer.orientation;
			this.showHidden = this.appAssist.settings.explorer.showHidden;
			this.currScroll = this.controller.getSceneScroller().mojo.getState().top;
			for(var i=0; i<this.controller.document.styleSheets.length; i++) {
				var rules = this.controller.document.styleSheets[i].cssRules;
				for(var j=0; j<rules.length; j++)  {
		    		if(rules[j].selectorText==".nameLabel") {
						rules[j].style.fontSize = this.appAssist.settings.explorer.fontSize +
								" !important";
						break;
					}
				}
			}
			for(var i=0; i<this.controller.document.styleSheets.length; i++) {
				var rules = this.controller.document.styleSheets[i].cssRules;
				for(var j=0; j<rules.length; j++)  {
		    		if(rules[j].selectorText==".nameEntryStyle") {
						if(this.appAssist.settings.explorer.wrapNames) {
							rules[j].style.whiteSpace = "normal !important";
							rules[j].style.wordWrap="break-word !important";
						} else {
							rules[j].style.whiteSpace = "";
							rules[j].style.wordWrap="";
						}
						break;
					}
				}
			}
			this.loadLists();
		} else if(event.action=="updateSize") {
			this.updateSize(event.path);
		} else if(event.action=="updatedInfo") {
			this.currScroll = this.controller.getSceneScroller().mojo.getState().top;
			this.loadLists();
		} else if(event.action=="goto") {
			this.history.push(this.currDir);
			this.scrollHistory.push(this.controller.getSceneScroller().mojo.getState().top);
			this.currDir = event.path;
			if(this.currDir!="/")
				this.currDir = this.currDir + "/";
			this.controller.get("subtitle").innerText = this.currDir;
			this.loadLists();
		}
	}
	if(this.orientation!="free") {
		this.controller.stageController.setWindowOrientation(this.orientation);
	} else {
		this.orientation = this.controller.stageController.getWindowOrientation();
		this.controller.stageController.setWindowOrientation(this.orientation);
	}
	this.handleBaseDir = this.handleBaseDir.bindAsEventListener(this);
	this.handleDirList = this.handleDirList.bindAsEventListener(this);
	this.toggleName = this.toggleName.bindAsEventListener(this);
	this.toggleSub = this.toggleSub.bindAsEventListener(this);
	this.handleHold = this.handleHold.bindAsEventListener(this);
	this.handleFileList = this.handleFileList.bindAsEventListener(this);
	this.handleFileDelete = this.handleFileDelete.bindAsEventListener(this);
	this.toggleSubType = this.toggleSubType.bindAsEventListener(this);
	this.handleHome = this.handleHome.bindAsEventListener(this);
	this.handleFav = this.handleFav.bindAsEventListener(this);
	this.gestureStart = this.gestureStart.bindAsEventListener(this);
	this.gestureEnd = this.gestureEnd.bindAsEventListener(this);
	this.controller.listen("baseDir", Mojo.Event.tap, this.handleBaseDir);
	this.controller.listen("listDir", Mojo.Event.listTap, this.handleDirList);
	this.controller.listen("listFiles", Mojo.Event.listTap, this.handleFileList);
	this.controller.listen("listFilesSwipeDelete", Mojo.Event.listTap, this.handleFileList);
	this.controller.listen("listFilesSwipeDelete", Mojo.Event.listDelete, this.handleFileDelete);
	this.controller.listen("nameLabel", Mojo.Event.tap, this.toggleName);
	this.controller.listen("subLabel", Mojo.Event.tap, this.toggleSub);
	this.controller.listen("home", Mojo.Event.tap, this.handleHome);
	this.controller.listen("star", Mojo.Event.tap, this.handleFav);
	this.controller.listen("subLabel", Mojo.Event.hold, this.toggleSubType);
	this.controller.listen("listDir",  Mojo.Event.hold, this.handleHold);
	this.document = this.controller.stageController.document;
	this.controller.listen(this.document, "gesturestart", this.gestureStart);
	this.controller.listen(this.document, "gestureend", this.gestureEnd);
};

ExplorerAssistant.prototype.gestureStart = function(event) {
	this.gestureStartY = event.centerY;
};

ExplorerAssistant.prototype.gestureEnd = function(event) {
	this.gestureEndY = event.centerY;
	this.gestureDistance = this.gestureEndY - this.gestureStartY;
	if(this.gestureDistance>0) {
		this.controller.getSceneScroller().mojo.revealTop();
	} else if(this.gestureDistance<0) {
		this.controller.getSceneScroller().mojo.revealBottom();
	}
};

ExplorerAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("baseDir", Mojo.Event.tap, this.handleBaseDir);
	this.controller.stopListening("listDir", Mojo.Event.listTap, this.handleDirList);
	this.controller.stopListening("listFiles", Mojo.Event.listTap, this.handleFileList);
	this.controller.stopListening("listFilesSwipeDelete", Mojo.Event.listTap, this.handleFileList);
	this.controller.stopListening("listFilesSwipeDelete", Mojo.Event.listDelete, this.handleFileDelete);
	this.controller.stopListening("nameLabel", Mojo.Event.tap, this.toggleName);
	this.controller.stopListening("subLabel", Mojo.Event.tap, this.toggleSub);
	this.controller.stopListening("subLabel", Mojo.Event.hold, this.toggleSubType);
	this.controller.stopListening("home", Mojo.Event.tap, this.handleHome);
	this.controller.stopListening("star", Mojo.Event.tap, this.handleFav);
	this.controller.stopListening("listDir",  Mojo.Event.hold, this.handleHold);
	this.document = this.controller.stageController.document;
	this.controller.stopListening(this.document, "gesturestart", this.gestureStart);
	this.controller.stopListening(this.document, "gestureend", this.gestureEnd);
};

ExplorerAssistant.prototype.cleanup = function(event) {
	this.appAssist.settings.explorer.subVal = this.subVal;
	this.appAssist.settings.explorer.sort = this.sort;
	this.appAssist.settings.explorer.ascending = this.ascending;
	this.appAssist.settings.explorer.startDir = this.currDir;
	this.appAssist.saveSettings();
};
