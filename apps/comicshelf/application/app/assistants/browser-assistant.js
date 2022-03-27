function BrowserAssistant(params) {
	this.BASEDIR = "/media/internal/comics/";
	this.comics = params.comics || new ComicService();
	this.path = params.path || this.BASEDIR;
	this.currScroll = 0;
}

BrowserAssistant.prototype.setup = function() {
	this.appAssist = Mojo.Controller.getAppController().assistant;
	this.history = [];
	this.scrollHistory = [];
	var menuAttr = {omitDefaultItems: true};
  	var menuModel = {
    	visible: true,
    	items: [
			Mojo.Menu.editItem,
			{label: "Preferences", command:Mojo.Menu.prefsCmd},
			{label: "Help", command:Mojo.Menu.helpCmd}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, menuAttr, menuModel);
	
	this.controller.setupWidget("loadingSpinner",
			{spinnerSize: Mojo.Widget.spinnerSmall}, {spinning: true});
	
	this.controller.get("baseDir").hide();
	this.controller.get("directory-divider").innerHTML = '';
	this.controller.get("line-between-lists").innerHTML='';
	this.controller.get("line-after-lists").innerHTML='';
	this.controller.get("noResults").hide();
	
	this.dirListAttrs = {
		listTemplate:'browser/listContainer', 
		itemTemplate:'browser/listItem',
		swipeToDelete:false,
		autoconfirmDelete:false,
		hasNoWidgets:true,
		onItemRendered: this.handleItemRender.bind(this)
	};
	this.dirListModel = {
		listTitle: $L('Directories'),
		items: []
	};
	this.controller.setupWidget("listDir", this.dirListAttrs, this.dirListModel);

	this.fileListNoSwipeAttrs = {
		listTemplate:'browser/listContainer', 
		itemTemplate:'browser/listItem',
		swipeToDelete:false,
		autoconfirmDelete:false,
		hasNoWidgets:true,
		onItemRendered: this.handleItemRender.bind(this)
	};
	this.fileListNoSwipeModel = {
		listTitle: $L('Files'),
		items: []
	};
	this.controller.setupWidget("listFiles", this.fileListNoSwipeAttrs,
			this.fileListNoSwipeModel);
	this.fileListSwipeDeleteAttrs = {
		listTemplate:'browser/listContainer', 
		itemTemplate:'browser/listItem',
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
	
	if(this.appAssist.settings.swipeDelete) {
		this.fileListModel = this.fileListSwipeDeleteModel;
	} else {
		this.fileListModel = this.fileListNoSwipeModel;
	}
	
	this.loadLists();
};

BrowserAssistant.prototype.loadLists = function(event) {
	this.controller.get("directory-divider").innerHTML = '';
	this.controller.get("line-between-lists").innerHTML='';
	this.controller.get("line-after-lists").innerHTML='';
	
	this.dirListModel.items = [];
	this.controller.modelChanged(this.dirListModel);
	this.fileListSwipeDeleteModel.items = [];
	this.fileListNoSwipeModel.items = [];
	this.controller.modelChanged(this.fileListSwipeDeleteModel);
	this.controller.modelChanged(this.fileListNoSwipeModel);
	
	if(this.path == this.BASEDIR) {
		this.controller.get("baseDir").hide();
	} else {
		this.controller.get("baseDir").show();
		this.controller.get("directory-divider").innerHTML = '<hr/>';
	}
	
	if(this.appAssist.settings.swipeDelete) {
		this.fileListModel = this.fileListSwipeDeleteModel;
		this.controller.get("listFiles").hide();
		this.controller.get("listFilesSwipeDelete").show();
	} else {
		this.fileListModel = this.fileListNoSwipeModel;
		this.controller.get("listFilesSwipeDelete").hide();
		this.controller.get("listFiles").show();
	}

	this.comics.list(
		this.path,
		this.grabItemsSuccess.bind(this),
		function(err) {
			Error(err.errorText);
			if(this.history.length>0) {
				this.path = this.history.pop();
			} else {
				this.path = this.appAssist.DEFAULTSETTINGS.comicDir;
			}
			this.appAssist.settings.comicDir = this.path;
			this.appAssist.saveSettings();
		}.bind(this)
	);
};

BrowserAssistant.prototype.grabItemsSuccess = function(response) {
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
	
	if(response.dirs.length==0 && response.files.length==0 &&
			this.path == this.BASEDIR) {
		this.controller.get("noResults").show();
	} else {
		this.controller.get("noResults").hide();
	}
	
	if(this.currScroll !=0) {
		this.setScrollPosition(this.currScroll);
		this.currScroll = 0;
	}
	this.controller.get("loadingSpinner").hide();
	this.controller.get("loadingSpinner").mojo.stop();
};

BrowserAssistant.prototype.handleItemRender = function(listWidget, itemModel, itemNode){
	var row = itemNode;
	var height = row.offsetHeight;
	if(height > 52) {
		if(row && row.style) {
			row.style.height = height-14 + "px";
			var ele = row.getElementsByTagName("div");
			for(var j=0; j<ele.length; j++) {
				if(ele[j].className.indexOf("icon")!=-1) {
					ele[j].style.top = "4px";
				} else if(ele[j].className.indexOf("nameLabel")!=-1) {
					ele[j].style.top = "-8px";
				}
			}
		}
	}
};

BrowserAssistant.prototype.activate = function(event) {
	if(event) {
		if(event.action) {
			if(event.action = "updatePrefs") {
				this.currScroll = this.controller.getSceneScroller().mojo.getState().top;
				this.loadLists();
			}
		}
	}
	
	this.handleBaseDir = this.handleBaseDir.bindAsEventListener(this);
	this.handleDirList = this.handleDirList.bindAsEventListener(this);
	this.handleFileList = this.handleFileList.bindAsEventListener(this);
	this.handleFileDelete = this.handleFileDelete.bindAsEventListener(this);
	this.gestureStart = this.gestureStart.bindAsEventListener(this);
	this.gestureEnd = this.gestureEnd.bindAsEventListener(this);
	this.controller.listen("baseDir", Mojo.Event.tap, this.handleBaseDir);
	this.controller.listen("listDir", Mojo.Event.listTap, this.handleDirList);
	this.controller.listen("listFiles", Mojo.Event.listTap, this.handleFileList);
	this.controller.listen("listFilesSwipeDelete", Mojo.Event.listTap, this.handleFileList);
	this.controller.listen("listFilesSwipeDelete", Mojo.Event.listDelete, this.handleFileDelete);
	this.document = this.controller.stageController.document;
	this.controller.listen(this.document, "gesturestart", this.gestureStart);
	this.controller.listen(this.document, "gestureend", this.gestureEnd);
};

BrowserAssistant.prototype.handleCommand = function(event){
  	if (event.type == Mojo.Event.command) {
		switch (event.command) {
		case Mojo.Menu.prefsCmd:
			this.controller.stageController.pushScene("settings");
			break;
		case Mojo.Menu.helpCmd:
			this.controller.stageController.pushAppSupportInfoScene();
			break;
		}
	} else if(event.type == Mojo.Event.back){
		if (this.history.length > 0) {
			event.stop();
			this.path = this.history.pop();
			this.currScroll = this.scrollHistory.pop();
			this.loadLists();
		}
	} else if(event.type == Mojo.Event.forward) {
		this.currScroll = this.controller.getSceneScroller().mojo.getState().top;
		this.loadLists();
	}
};

BrowserAssistant.prototype.handleBaseDir = function(event){
	var parent = this.path;
	if(parent.endsWith("/")) {
		parent = parent.substring(0, parent.length-1);
	}
	parent = parent.substring(0, parent.lastIndexOf("/")+1);
	this.history.push(this.path);
	this.scrollHistory.push(0);
	this.path = parent;
	this.loadLists();
};

BrowserAssistant.prototype.handleDirList = function(event){
	var currItem = event.item;
	this.history.push(this.path);
	this.scrollHistory.push(this.controller.getSceneScroller().mojo.getState().top);
	this.path = currItem.path;
	if(!this.path.endsWith("/"))
		this.path += "/";
	this.loadLists();
};

BrowserAssistant.prototype.handleFileList = function(event) {
	var currItem = event.item;
	this.controller.stageController.pushScene({name:"viewer", disableSceneScroller:true},
			{comics:this.comics, path:currItem.path});
};

BrowserAssistant.prototype.handleFileDelete = function(event) {
	event.stop();
	var currItem = event.item;
	var itemIndex = this.fileListModel.items.indexOf(currItem);
	this.controller.get("listFilesSwipeDelete").mojo.noticeRemovedItems(itemIndex, 1);
	this.fileListModel.items.splice(itemIndex, 1);
	this.comics.deleteFile(currItem.path)
	
	if(this.dirListModel.items.length==0)
		this.controller.get("line-between-lists").innerHTML='';
	if(this.fileListModel.items.length==0)
		this.controller.get("line-after-lists").innerHTML='';
};

BrowserAssistant.prototype.gestureStart = function(event) {
	this.gestureStartY = event.centerY;
};

BrowserAssistant.prototype.gestureEnd = function(event) {
	this.gestureEndY = event.centerY;
	this.gestureDistance = this.gestureEndY - this.gestureStartY;
	if(this.gestureDistance>0) {
		this.controller.getSceneScroller().mojo.revealTop();
	} else if(this.gestureDistance<0) {
		this.controller.getSceneScroller().mojo.revealBottom();
	}
};

BrowserAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("baseDir", Mojo.Event.tap, this.handleBaseDir);
	this.controller.stopListening("listDir", Mojo.Event.listTap, this.handleDirList);
	this.controller.stopListening("listFiles", Mojo.Event.listTap, this.handleFileList);
	this.controller.stopListening("listFilesSwipeDelete", Mojo.Event.listTap, this.handleFileList);
	this.controller.stopListening("listFilesSwipeDelete", Mojo.Event.listDelete, this.handleFileDelete);
	this.document = this.controller.stageController.document;
	this.controller.stopListening(this.document, "gesturestart", this.gestureStart);
	this.controller.stopListening(this.document, "gestureend", this.gestureEnd);
	
	this.appAssist.settings.comicDir = this.path;
	this.appAssist.saveSettings();
};

BrowserAssistant.prototype.cleanup = function(event) {

};
