function InfoAssistant(params) {
	this.item = params.item;
	this.writableDir = params.writableDir;
	this.filemgr = params.filemgr;
	this.dir = params.dir
	this.isDir = params.isDir;
	this.changed = false;
	this.renaming = false;
}

InfoAssistant.prototype.setup = function() {
  	var menuAttr = {omitDefaultItems: true};
  	var menuModel = {
    	visible: true,
    	items: []
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, menuAttr, menuModel);
	this.controller.setInitialFocusedElement(null);
	this.appAssist = Mojo.Controller.getAppController().assistant;
	this.subVal = this.appAssist.settings.explorer.subType;
	this.ext = getFileExt(this.item.name);
	this.initInfo();
	this.initOrientation();
	this.initHeader();
	this.getInfo();
	if(!this.isDir) {
		var cmdMenuModel = {
			items: [
				{label: $L("Email"), command:"share"}
			]
		};
		//if(this.ext=="bmp" || this.ext=="jpg" || this.ext=="jpeg" || this.ext=="png") {
		//	cmdMenuModel.items.push({label: $L("Set As Wallpaper"), command:"wallpaper"});
		//}
		this.controller.setupWidget(Mojo.Menu.commandMenu, {menuClass:"no-fade"},
				cmdMenuModel);
	}
};

InfoAssistant.prototype.initInfo = function(){
	if(this.appAssist.settings.explorer.favourites.indexOf(this.dir + this.item.name)>=0) {
		this.controller.get("favourite").className = "favourite-icon";
	} else {
		this.controller.get("favourite").className = "unfavourite-icon";
	}
	
	this.controller.get("size").innerHTML = "<strong>" + $L("Size:") + "</strong> " +
			"calculating...";
	this.controller.get("path").innerHTML = "<strong>" + $L("Path:") + "</strong>";
	this.controller.get("symlink-to").innerHTML = "<strong>" + $L("Symlink to:") +
			"</strong><br>";
	this.controller.get("symlink-to").hide();
	this.controller.get("attibutes-header").innerHTML = "<strong>" + $L("Attributes:") +
			"</strong>";
	this.controller.get("mask").innerHTML += " " + $L("Hidden") + "";
	this.controller.get("lastmodified").innerHTML = $L("Last modified on #{date}" +
			" at #{time}").interpolate({date:"----", time:"----"});
};

InfoAssistant.prototype.initOrientation = function(){
	this.orientation = this.appAssist.settings.explorer.orientation;
	if(this.orientation!="free") {
		this.controller.stageController.setWindowOrientation(this.orientation);
	} else {
		this.orientation = this.controller.stageController.getWindowOrientation();
		this.controller.stageController.setWindowOrientation(this.orientation);
	}
};

InfoAssistant.prototype.initHeader = function(){
	if(this.isDir)
		this.controller.get("icon").className = "icon folder info-icon";
	else
		this.controller.get("icon").className = "icon file x-" + this.item.type +
				" info-icon";
	this.controller.setupWidget("name",
        this.nameAttr = {
            multiline: false,
            enterSubmits: false,
            autoFocus: false,
			emoticons: false,
			autoReplace: false,
			textCase: Mojo.Widget.steModeLowerCase,
			charsAllow: this.nameFilter.bind(this)
         },
         this.nameModel = {
             value: this.item.name,
             disabled: false
         }
    );
};

InfoAssistant.prototype.getInfo = function(event) {
	this.loadPath();
	this.controller.get("fat32").style.display = "inline";
	this.controller.setupWidget("cbMask", {},
        this.maskModel = {
			value: this.item.name.startsWith("."),
            disabled: false
		}
	);
	this.loadLastModified();
};

InfoAssistant.prototype.loadPath = function(){
	this.controller.get("path").innerHTML = "<strong>" + $L("Path:") + "</strong> " +
			this.dir.replace("/media/internal", $L("USB Drive")) + this.item.name;
	if(this.dir + this.item.name != this.item.path) { //symlink
		this.controller.get("symlink-to").innerHTML = "<strong>" + $L("Symlink to:") +
				"</strong> " + this.item.path.replace("/media/internal", $L("USB Drive"))
				+ "<br><br>";
		this.controller.get("symlink-to").show();
	} else {
		this.controller.get("symlink-to").hide();
	}
};

InfoAssistant.prototype.loadLastModified = function() {
	this.controller.stageController.window.setTimeout(function() {
		this.filemgr.getLastModified(this.dir + this.item.name, function(response) {
			var d = new Date();
			d.setTime(response.lastModified*1);
			var dateVal = Mojo.Format.formatDate(d, {date:"full"});
			var timeVal = Mojo.Format.formatDate(d, {time:"short"});
			this.controller.get("lastmodified").innerHTML = $L("Last modified on #{date}" +
				" at #{time}").interpolate({date:dateVal, time:timeVal});
			this.readSize();
		}.bind(this), this.readSize.bind(this));
	}.bind(this), 100);
};

InfoAssistant.prototype.readSize = function(err){
	if(!this.isDir) {
		this.controller.get("size").innerHTML = "<strong>" + $L("Size:") +
				"</strong> " + this.item.size;
	} else {
		this.filemgr.getSize({file:this.dir + this.item.name, formatted:true},
			function(response) {
				this.controller.get("size").innerHTML = "<strong>" + $L("Size:") +
						"</strong> " + response.size;
			}.bind(this),
			function(err) {
				this.controller.get("size").innerHTML = "<strong>" + $L("Size:") +
						"</strong> " + err.errorText;
			}.bind(this)
		);
	}
};

InfoAssistant.prototype.activate = function(event) {
	//if(event.wallpaper) {
	//	this.importWallpaper(event.wallpaper);
	//}
	this.controller.get("name").mojo.setConsumesEnterKey(false);
	this.handleFavourites = this.handleFavourites.bindAsEventListener(this);
	this.textfieldBlur = this.textfieldBlur.bindAsEventListener(this);
	this.handleToggleMask = this.handleToggleMask.bindAsEventListener(this);
	this.controller.listen("favourite", Mojo.Event.tap, this.handleFavourites);
	this.controller.listen(this.controller.get("name").getElementsByTagName("input")[0],
			"blur", this.textfieldBlur);
	this.controller.listen("cbMask", Mojo.Event.propertyChange,
			this.handleToggleMask);
};

InfoAssistant.prototype.handleCommand = function(event) {
	if(event.type == Mojo.Event.back) {
		event.stop();
		this.closeInfo();
	} else if(event.type == Mojo.Event.command) {
		if(event.command == "close") {
			this.closeInfo();
		} else if(event.command == "share") {
			this.email();
		}
	}
};

InfoAssistant.prototype.closeInfo = function() {
	this.newName = this.controller.get("name").mojo.getValue().trim();
	if(this.newName.replace(/ /g, "").replace(/\./g, "").length>0 &&
			this.newName != this.item.name) {
		this.popAfter = true;
		if(!this.renaming)
			this.textfieldBlur();
	} else {
		if(this.changed) {
			this.controller.stageController.popScene({action: "updatedInfo"});
		} else{
			this.controller.stageController.popScene();
		}
	}
};

InfoAssistant.prototype.handleFavourites = function() {
	var i = this.appAssist.settings.explorer.favourites.indexOf(this.dir + this.item.name);
	if(i>=0) {
		this.appAssist.settings.explorer.favourites.splice(i,1);
		this.appAssist.saveSettings();
		this.controller.get("favourite").className = "unfavourite-icon";
	} else {
		this.appAssist.settings.explorer.favourites.push(this.dir + this.item.name);
		this.appAssist.saveSettings();
		this.controller.get("favourite").className = "favourite-icon";
	}
};

InfoAssistant.prototype.addToFav = function() {
	if(this.controller.get("favourite").className=="favourite-icon") {
		var favIndex = this.appAssist.settings.explorer.favourites.indexOf(this.dir +
				this.item.name);
		if(favIndex<0) {
			if(this.tmpIndex==-1) {
				this.appAssist.settings.explorer.favourites.push(this.dir +
						this.item.name);
			} else {
				this.appAssist.settings.explorer.favourites.splice(this.tmpIndex, 0,
						this.dir + this.item.name);
			}
			this.appAssist.saveSettings();
		}
	}
};

InfoAssistant.prototype.removeFromFav = function() {
	if(this.controller.get("favourite").className=="favourite-icon") {
		var favIndex = this.appAssist.settings.explorer.favourites.indexOf(this.dir +
				this.item.name);
		if(favIndex>=0) {
			this.tmpIndex = favIndex;
			this.appAssist.settings.explorer.favourites.splice(favIndex,1);
			this.appAssist.saveSettings();
		} else {
			this.tmpIndex = -1;
		}
	}
};

InfoAssistant.prototype.nameFilter = function(keycode) {
	//valid character range: 32-255
	//disallowed characters: \ / : * ? " < > |
	return (keycode>=32 && keycode<=255 && keycode!=92 && keycode!=47  && keycode!=58
			&& keycode!=42 && keycode!=63 && keycode!=34 && keycode!=60  && keycode!=62
			&& keycode!=124);
};

InfoAssistant.prototype.textfieldBlur = function(event) {
	if(this.renaming) {
		this.controller.window.setTimeout(this.textfieldBlur.bind(this), 500);
		return;
	}
	this.renaming = true;
	this.newName = this.controller.get("name").mojo.getValue().trim();
	if(this.newName.replace(/ /g, "").replace(/\./g, "").length==0) {
		this.controller.get("name").mojo.setValue(this.item.name);
	} else {
		if(this.newName != this.item.name) {
			this.controller.showAlertDialog({
	   			onChoose: function(value) {
					if(value == 'yes') {
						this.filemgr.exists(this.dir + this.newName,
							function(response){
								if(response.exists) {
									this.conflictMsgBox();
								} else {
									this.removeFromFav();
									this.filemgr.rename(this.dir + this.item.name,
										this.dir + this.newName,
										function(response) {
											var path = this.item.path.substring(0,
													this.item.path.lastIndexOf("/")+1);
											if(path==this.dir) { //not a symlink
												this.item.path = this.dir + this.newName;
											}
											this.item.name = this.newName;
											this.controller.showBanner($L("Renamed: " +
													"#{filename}").interpolate(
													{filename:this.item.name}), "");
											this.addToFav();
											this.loadPath();
											this.reloadIconClass();
											this.changed = true;
											if(this.popAfter) {
												this.controller.stageController
														.popScene({action: "updatedInfo"});
											}
											this.renaming = false;
										}.bind(this),
										function(err) {
											Error($L(err.errorText));
											this.controller.get("name").mojo.setValue(
													this.item.name);
											if(this.popAfter) {
												this.controller.stageController
														.popScene({action: "updatedInfo"});
											}
											this.renaming = false;
										}.bind(this)
									);
								}
							}.bind(this)
						);
					} else {
						this.controller.get("name").mojo.setValue(this.item.name);
						if(this.popAfter) {
							this.controller.stageController.popScene();
						}
						this.renaming = false;
					}
				}.bind(this),
				title: $L("Rename?"),
	    		message: $L("Are you sure you want to rename #{oldfilename} to" +
						" #{newfilename}?").interpolate({oldfilename: "\"" + 
						this.item.name + "\"", newfilename:"\"" + this.newName + "\""}),
	    		choices:[
	        		{label: $L("Yes"), value:"yes", type:"affirmative"},  
	        		{label: $L("No"), value:"no", type:"negative"} 
	    		]
	    	});
		}
	}
};

InfoAssistant.prototype.conflictMsgBox = function() {
	this.controller.showAlertDialog({
		onChoose: function(value){
			if(value=="yes") {
				this.removeFromFav();
				this.filemgr.rename(this.dir + this.item.name,
					this.dir + this.newName,
					function(response) {
						var path = this.item.path.substring(0,
								this.item.path.lastIndexOf("/")+1);
						if(path==this.dir) { //not a symlink
							this.item.path = this.dir + this.newName;
						}
						this.item.name = this.newName;
						this.controller.showBanner($L("Renamed: #{filename}")
								.interpolate({filename:this.item.name}), "");
						this.addToFav();
						this.loadPath();
						this.reloadIconClass();
						this.changed = true;
						this.maskModel.value = this.item.name.startsWith(".");
						this.maskModel.disabled = false;
						this.controller.modelChanged(this.maskModel);
						if(this.popAfter) {
							this.controller.stageController
									.popScene({action: "updatedInfo"});
						}
						this.renaming = false;
					}.bind(this),
					function(err) {
						this.maskModel.value = this.item.name.startsWith(".");
						this.maskModel.disabled = false;
						this.controller.modelChanged(this.maskModel);
						Error($L(err.errorText));
						this.controller.get("name").mojo.setValue(this.item.name);
						this.controller.stageController.popScene({action: "updatedInfo"});
						this.renaming = false;
					}.bind(this)
				);
			} else {
				if(this.popAfter) {
					this.controller.stageController.popScene();
				}
			}
		},
		title: $L("Overwrite?"),
		message: $L("#{filename} already exists. Overwrite?").interpolate(
				{filename:"\"" + this.dir.replace("/media/internal", $L("USB Drive"))
				+ this.newName + "\""}),
		choices: [
			{label: $L("Yes"), value:"yes", type:"affirmative"},  
	        {label: $L("No"), value:"no", type:"negative"} 
		]
	});
};

InfoAssistant.prototype.reloadIconClass = function() {
	if(!this.isDir) {
		if(this.item.name.lastIndexOf(".")>0) {
			this.item.type = this.item.name.substring(this.item.name.lastIndexOf(".")+1);
		} else {
			this.item.type = "---";
		}
		this.controller.get("icon").className = "icon file x-" +  this.item.type +
				" info-icon";
	}
};

InfoAssistant.prototype.handleToggleMask = function(event){
	if(this.renaming) {
		this.maskModel.disabled = false;
		this.controller.modelChanged(this.maskModel);
		return;
	}
	this.renaming = true;
	this.maskModel.disabled = true;
	this.controller.modelChanged(this.maskModel);
	this.changed = true;
	if(event.value && !this.item.name.startsWith(".")) {
		this.newName = "." + this.item.name;
	} else if(!event.value && this.item.name.startsWith(".")) {
		this.newName = this.item.name.substring(1);
	}
	this.filemgr.exists(this.dir + this.newName,
		function(response){
			if(response.exists) {
				this.conflictMsgBox();
			} else {
				this.filemgr.rename(this.dir + this.item.name,
					this.dir + this.newName,
					function(response) {
						var path = this.item.path.substring(0,
								this.item.path.lastIndexOf("/")+1);
						if(path==this.dir) { //not a symlink
							this.item.path = this.dir + this.newName;
						}
						this.item.name = this.newName;
						this.controller.get("name").mojo.setValue(
								this.item.name);
						this.loadPath();
						this.maskModel.disabled = false;
						this.controller.modelChanged(this.maskModel);
						this.renaming = false;
						this.changed = true;
					}.bind(this),
					function(err) {
						this.maskModel.value = !this.maskModel.value;
						this.maskModel.disabled = false;
						this.controller.modelChanged(this.maskModel);
						Error($L(err.errorText));
						this.controller.get("name").mojo.setValue(
								this.item.name);
					}.bind(this)
				);
			}
		}.bind(this)
	);
};

InfoAssistant.prototype.orientationChanged = function(orientation){
	if(this.orientation === orientation) {
		return;
   	}
   	if(this.appAssist.settings.explorer.orientation=="free") {
		this.orientation = orientation;
		this.controller.stageController.setWindowOrientation(this.orientation);
   	}
};

InfoAssistant.prototype.email = function() {
	this.controller.serviceRequest("palm://com.palm.applicationManager", {
		method: "open",
		parameters: {
			id: "com.palm.app.email",
			params: {
				attachments: [
					{fullPath: this.item.path}
				]
			}
		},
		onFailure: function() {
			Error($L("Unable to share file."));
		}.bind(this)
	});
};

InfoAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("favourite", Mojo.Event.tap, this.handleFavourites);
	this.controller.stopListening(this.controller.get("name")
			.getElementsByTagName("input")[0], "blur", this.textfieldBlur);
	this.controller.stopListening('cbMask', Mojo.Event.propertyChange,
			this.handleToggleMask);
};

InfoAssistant.prototype.cleanup = function(event) {

};
