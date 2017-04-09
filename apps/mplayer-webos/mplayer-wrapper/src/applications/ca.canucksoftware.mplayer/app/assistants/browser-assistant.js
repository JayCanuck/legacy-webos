function BrowserAssistant(params) {
	this.BASEDIR = "/media/internal/video/";
	this.mplayer = params.mplayer || new MPlayerService();
	this.path = params.path || this.BASEDIR;
}

BrowserAssistant.prototype.setup = function() {
	this.appAssist = Mojo.Controller.getAppController().assistant;
	this.history = [];
	
	var menuAttr = {omitDefaultItems: true};
  	var menuModel = {
    	visible: true,
    	items: [
			{label: "Open URL", command:"open-url"},
			{label: "Preferences", command:Mojo.Menu.prefsCmd},
			{label: "Help", command:Mojo.Menu.helpCmd}
		]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, menuAttr, menuModel);
	
	this.controller.setupWidget("spinLoading", {spinnerSize: 'large'}, {spinning:false});
	this.controller.get('loadingScrim').hide();
	
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

	this.fileListAttrs = {
		listTemplate:'browser/listContainer', 
		itemTemplate:'browser/listItem',
		swipeToDelete:false,
		autoconfirmDelete:false,
		hasNoWidgets:true,
		onItemRendered: this.handleItemRender.bind(this)
	};
	this.fileListModel = {
		listTitle: $L('Files'),
		items: []
	};
	this.controller.setupWidget("listFiles", this.fileListAttrs, this.fileListModel);
	
	this.loadLists();
};

BrowserAssistant.prototype.loadLists = function(event) {
	this.controller.get("directory-divider").innerHTML = '';
	this.controller.get("line-between-lists").innerHTML='';
	this.controller.get("line-after-lists").innerHTML='';
	
	this.dirListModel.items = [];
	this.controller.modelChanged(this.dirListModel);
	this.fileListModel.items = [];
	this.controller.modelChanged(this.fileListModel);
	
	if(this.path == this.BASEDIR) {
		this.controller.get("baseDir").hide();
	} else {
		this.controller.get("baseDir").show();
		this.controller.get("directory-divider").innerHTML = '<hr/>';
	}

	this.mplayer.list(
		this.path,
		this.grabItemsSuccess.bind(this),
		function(err) {
			Error(err.errorText);
			if(this.history.length>0) {
				this.path = this.history.pop();
			} else {
				this.path = this.BASEDIR;
			}
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
	for(var i=0; i<response.files.length; i++) {
		response.files[i].type = "file x-" + response.files[i].type;
	}
	
	this.fileListModel.items = response.files;
	this.controller.modelChanged(this.fileListModel);
	
	if(response.dirs.length==0 && response.files.length==0 &&
			this.path == this.BASEDIR) {
		this.controller.get("noResults").show();
	} else {
		this.controller.get("noResults").hide();
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
	this.handleBaseDir = this.handleBaseDir.bindAsEventListener(this);
	this.handleDirList = this.handleDirList.bindAsEventListener(this);
	this.handleFileList = this.handleFileList.bindAsEventListener(this);
	this.controller.listen("baseDir", Mojo.Event.tap, this.handleBaseDir);
	this.controller.listen("listDir", Mojo.Event.listTap, this.handleDirList);
	this.controller.listen("listFiles", Mojo.Event.listTap, this.handleFileList);
	this.controller.stageController.setWindowOrientation("up");
};

BrowserAssistant.prototype.handleCommand = function(event){
  	if(event.type == Mojo.Event.command) {
		if(event.command == "open-url") {
			this.controller.showDialog({
				template: 'input-dialog/input-dialog-popup',
				assistant: new InputDialogAssistant({
					sceneAssistant: this,
					title: "Open URL",
					message: $L("URL:"),
					init: "",
					onAccept: function(value) {
						this.mplayer.open(value);
					}.bind(this)
				})
			});
		} else if(event.command == Mojo.Menu.prefsCmd) {
			this.controller.showDialog({
				template: 'settings/settings-popup',
				assistant: new SettingsAssistant({
					controller: this.controller,
					mplayer: this.mplayer
				})
			});
		} else if(event.command == Mojo.Menu.helpCmd) {
			this.controller.stageController.pushAppSupportInfoScene();
		}
	} else if(event.type == Mojo.Event.back){
		if (this.history.length > 0) {
			event.stop();
			this.path = this.history.pop();
			this.loadLists();
		}
	} else if(event.type == Mojo.Event.forward) {
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
	this.path = parent;
	this.loadLists();
};

BrowserAssistant.prototype.handleDirList = function(event){
	var currItem = event.item;
	this.history.push(this.path);
	this.path = currItem.path;
	if(!this.path.endsWith("/"))
		this.path += "/";
	this.loadLists();
};

BrowserAssistant.prototype.handleFileList = function(event) {
	this.controller.get('spinLoading').mojo.start();
	this.controller.get('loadingScrim').show();
	this.mplayer.open(event.item.path);
	this.controller.stageController.window.setTimeout(function() {
		this.controller.get('loadingScrim').hide();
		this.controller.get('spinLoading').mojo.stop();
	}.bind(this), 5000);
};

BrowserAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("baseDir", Mojo.Event.tap, this.handleBaseDir);
	this.controller.stopListening("listDir", Mojo.Event.listTap, this.handleDirList);
	this.controller.stopListening("listFiles", Mojo.Event.listTap, this.handleFileList);
};

BrowserAssistant.prototype.cleanup = function(event) {

};
