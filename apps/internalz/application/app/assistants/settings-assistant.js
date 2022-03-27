function SettingsAssistant(params) {
	this.changed = false;
	this.filemgr = params.filemgr;
}

SettingsAssistant.prototype.setup = function() {
	this.appAssist = Mojo.Controller.getAppController().assistant;
	this.controller.setInitialFocusedElement(null);
	
	this.controller.setupWidget("spinLoading", {spinnerSize: 'large'}, {spinning:false});
	this.controller.get("loadingScrim").hide();
	
	this.controller.get("homeLabel").innerText = this.appAssist.settings
			.explorer.homeDir.replace("/media/internal", $L("USB Drive"));
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, {
		visible: true,
    	items: []
	});
	this.orientation = this.appAssist.settings.explorer.orientation;
	if(this.orientation!="free") {
		this.controller.stageController.setWindowOrientation(this.orientation);
	} else {
		this.orientation = this.controller.stageController.getWindowOrientation();
		this.controller.stageController.setWindowOrientation(this.orientation);
	}
	
	var theme = this.appAssist.settings.explorer.theme;
	
	this.controller.setupWidget("selTheme",
		{
			choices: [
				{label: $L("Palm Default"), value: "palm-default"},
				{label: $L("Palm Dark"), value: "palm-dark"}
			],
			labelPlacement:Mojo.Widget.labelPlacementLeft
		},
		{
        	value: theme
        }
    );
	
	//file explorer options
	this.controller.setupWidget("toggleHidden", {}, {
			value: this.appAssist.settings.explorer.showHidden
		}
	);
	if(this.appAssist.settings.explorer.fontSize=="1.0em") {
		this.handleFont({value:"0.9em"})
	}
	this.controller.setupWidget("selFont",
		{
			choices: [
				{label: $L("Tiny"), value: "0.6em"},
				{label: $L("Small"), value: "0.8em"},
				{label: $L("Normal"), value: "0.9em"},
				{label: $L("Large"), value: "1.1em"}
			],
			labelPlacement:Mojo.Widget.labelPlacementLeft
		},
		{
        	value: this.appAssist.settings.explorer.fontSize
        }
    );
	this.controller.setupWidget("toggleWrapNames", {}, {
			value: this.appAssist.settings.explorer.wrapNames
		}
	);
	this.controller.setupWidget("toggleSwipeDelete", {}, {
			value: this.appAssist.settings.explorer.swipeDelete
		}
	);
	this.controller.setupWidget("selIpk",
		{
			choices: [
				{label: $L("None"), value: "none"},
				{label: $L("Internalz Pro"), value: "ca.canucksoftware.internalz"},
				{label: $L("Preware"), value: "org.webosinternals.preware"}
			],
			labelPlacement:Mojo.Widget.labelPlacementLeft
		},
		{
        	value: this.appAssist.settings.explorer.ipkHandler
        }
    );
	this.controller.setupWidget("selOrientFile",
		{
			choices: [
				{label: $L("Portrait"), value: "up"},
				{label: $L("Free"), value: "free"}
			],
			labelPlacement:Mojo.Widget.labelPlacementLeft
		},
		{
        	value: this.appAssist.settings.explorer.orientation
        }
    );
	//image viewer options
	this.controller.setupWidget("toggleImageCard", {}, {
			value: this.appAssist.settings.images.newCard
		}
	);
	this.controller.setupWidget("toggleImageFull", {}, {
			value: this.appAssist.settings.images.fullScreen
		}
	);
	this.controller.setupWidget("toggleImageSwipe", {}, {
			value: this.appAssist.settings.images.swipeChange
		}
	);
	this.controller.setupWidget("selOrientImage",
		{
			choices: [
				{label: $L("Portrait"), value: "up"},
				{label: $L("Landscape"), value: "left"},
				{label: $L("Free"), value: "free"}
			],
			labelPlacement:Mojo.Widget.labelPlacementLeft
		},
		{
        	value: this.appAssist.settings.images.orientation
        }
    );
	//text editor options
	this.controller.setupWidget("toggleEditorCard", {}, {
			value: this.appAssist.settings.editor.newCard
		}
	);
	/*this.controller.setupWidget("toggleEditorAutosave", {}, {
			value: this.appAssist.settings.editor.saveOnClose
		}
	);*/
	this.controller.setupWidget("selNewline",
		{
			choices: [
				{label: $L("Windows"), value: 1},
				{label: $L("Mac OS X"), value: 2},
				{label: $L("Mac OS 9"), value: 3},
				{label: $L("Linux"), value: 4}
			],
			labelPlacement:Mojo.Widget.labelPlacementLeft
		},
		{
        	value: this.appAssist.settings.editor.newLine
        }
    );
	if(typeof this.appAssist.settings.editor.fontSize != "number") {
		this.appAssist.settings.editor.fontSize = 14;
		this.appAssist.saveSettings();
	}
	/*this.controller.setupWidget("selEditorFont",
		{
			label: "px",
			min: 10,
			max: 18,
			labelPlacement:Mojo.Widget.labelPlacementRight
		},
		{
			value: this.appAssist.settings.editor.fontSize
		}
	);*/
	this.controller.setupWidget("selEditorFont",
		{
			choices: [
				{label: "10px", value: 10},
				{label: "11px", value: 11},
				{label: "12px", value: 12},
				{label: "13px", value: 13},
				{label: "14px", value: 14},
				{label: "15px", value: 15},
				{label: "16px", value: 16},
				{label: "17px", value: 17},
				{label: "18px", value: 18}
			],
			labelPlacement:Mojo.Widget.labelPlacementLeft
		},
		{
        	value: this.appAssist.settings.editor.fontSize
        }
    );
	this.controller.setupWidget("toggleEditorWordWrap", {}, {
			value: this.appAssist.settings.editor.wordWrap
		}
	);
	this.controller.setupWidget("selOrientText",
		{
			choices: [
				{label: $L("Portrait"), value: "up"},
				{label: $L("Landscape"), value: "left"},
				{label: $L("Free"), value: "free"}
			],
			labelPlacement:Mojo.Widget.labelPlacementLeft
		},
		{
        	value: this.appAssist.settings.editor.orientation
        }
    );
};

SettingsAssistant.prototype.activate = function(event) {
	if(event!=null) {
		this.controller.get("homeLabel").innerText = event.path
				.replace("/media/internal", $L("USB Drive"));
		this.appAssist.settings.explorer.homeDir = event.path;
		this.appAssist.saveSettings();
	}
	this.handleHomeTap = this.handleHomeTap.bindAsEventListener(this);
	this.handleTheme = this.handleTheme.bindAsEventListener(this);
	this.handleHidden = this.handleHidden.bindAsEventListener(this);
	this.handleFont = this.handleFont.bindAsEventListener(this);
	this.handleWrapNames = this.handleWrapNames.bindAsEventListener(this);
	this.handleSwipeDelete = this.handleSwipeDelete.bindAsEventListener(this);
	this.handleIpkHandler = this.handleIpkHandler.bindAsEventListener(this);
	this.handleOrientFile = this.handleOrientFile.bindAsEventListener(this);
	this.handleImageCard = this.handleImageCard.bindAsEventListener(this);
	this.handleImageFull = this.handleImageFull.bindAsEventListener(this);
	this.handleImageSwipe = this.handleImageSwipe.bindAsEventListener(this);
	this.handleOrientImage = this.handleOrientImage.bindAsEventListener(this);
	this.handleEditorCard = this.handleEditorCard.bindAsEventListener(this);
	this.handleNewline = this.handleNewline.bindAsEventListener(this);
	this.handleEditorFont = this.handleEditorFont.bindAsEventListener(this);
	this.handleEditorWordWrap = this.handleEditorWordWrap.bindAsEventListener(this);
	this.handleOrientText = this.handleOrientText.bindAsEventListener(this);
	this.controller.listen("homeDir", Mojo.Event.tap, this.handleHomeTap);
	this.controller.listen("selTheme", Mojo.Event.propertyChange, this.handleTheme);
	this.controller.listen("toggleHidden", Mojo.Event.propertyChange, this.handleHidden);
	this.controller.listen("selFont", Mojo.Event.propertyChange, this.handleFont);
	this.controller.listen("toggleWrapNames", Mojo.Event.propertyChange, this.handleWrapNames);
	this.controller.listen("toggleSwipeDelete", Mojo.Event.propertyChange, this.handleSwipeDelete);
	this.controller.listen("selIpk", Mojo.Event.propertyChange, this.handleIpkHandler);
	this.controller.listen("selOrientFile", Mojo.Event.propertyChange, this.handleOrientFile);
	this.controller.listen("toggleImageCard", Mojo.Event.propertyChange, this.handleImageCard);
	this.controller.listen("toggleImageFull", Mojo.Event.propertyChange, this.handleImageFull);
	this.controller.listen("toggleImageSwipe", Mojo.Event.propertyChange, this.handleImageSwipe);
	this.controller.listen("selOrientImage", Mojo.Event.propertyChange, this.handleOrientImage);
	this.controller.listen("toggleEditorCard", Mojo.Event.propertyChange, this.handleEditorCard);
	this.controller.listen("selNewline", Mojo.Event.propertyChange, this.handleNewline);
	this.controller.listen("selEditorFont", Mojo.Event.propertyChange, this.handleEditorFont);
	this.controller.listen("toggleEditorWordWrap", Mojo.Event.propertyChange, this.handleEditorWordWrap);
	this.controller.listen("selOrientText", Mojo.Event.propertyChange, this.handleOrientText);
};

SettingsAssistant.prototype.handleCommand = function(event){
	if(event.type == Mojo.Event.back ||
			(event.type == Mojo.Event.command && event.command == 'close')){
		event.stop();
		if(this.changed) {
			this.controller.stageController.popScene({action: "updatePrefs"});
		} else{
			this.controller.stageController.popScene();
		}
	}
};

SettingsAssistant.prototype.orientationChanged = function(orientation){
	if(this.orientation === orientation) {
		return;
   	}
   	if(this.appAssist.settings.explorer.orientation=="free") {
		this.orientation = orientation;
		this.controller.stageController.setWindowOrientation(this.orientation);
   	}
};

SettingsAssistant.prototype.handleHomeTap = function(event) {
	this.changed = true;
	this.controller.stageController.pushScene("folder-chooser", {action:"Select A Folder",
			name:"", path:"", isDir:false, filemgr:this.filemgr});
};

SettingsAssistant.prototype.handleTheme = function(event) {
	this.changed = true;
	this.appAssist.settings.explorer.theme = event.value;
	this.appAssist.saveSettings();
	var bodyEle = this.controller.document.getElementsByTagName("body")[0];
	var themes = ["palm-dark"];
	themes = themes.splice(themes.indexOf(this.appAssist.settings.explorer.theme), 1);
	for(var themeCounter=0; themeCounter<themes.length; themeCounter++) {
		if(bodyEle.className.indexOf(themes[themeCounter]) != -1) {
			bodyEle.removeClassName(themes[themeCounter]);
		}
	}
	if(this.appAssist.settings.explorer.theme!="palm-default") {
		bodyEle.addClassName(this.appAssist.settings.explorer.theme);
	}
};

SettingsAssistant.prototype.handleHidden = function(event) {
	this.changed = true;
	this.appAssist.settings.explorer.showHidden = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleFont = function(event) {
	this.changed = true;
	this.appAssist.settings.explorer.fontSize = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleWrapNames = function(event) {
	this.changed = true;
	this.appAssist.settings.explorer.wrapNames = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleSwipeDelete = function(event) {
	this.changed = true;
	this.appAssist.settings.explorer.swipeDelete = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleOrientFile = function(event) {
	this.changed = true;
	this.appAssist.settings.explorer.orientation = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleImageCard = function(event) {
	this.changed = true;
	this.appAssist.settings.images.newCard = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleImageFull = function(event) {
	this.changed = true;
	this.appAssist.settings.images.fullScreen = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleImageSwipe = function(event) {
	this.changed = true;
	this.appAssist.settings.images.swipeChange = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleOrientImage = function(event) {
	this.changed = true;
	this.appAssist.settings.images.orientation = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleEditorCard = function(event) {
	this.changed = true;
	this.appAssist.settings.editor.newCard = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleNewline = function(event) {
	this.changed = true;
	this.appAssist.settings.editor.newLine = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleEditorFont = function(event) {
	this.changed = true;
	this.appAssist.settings.editor.fontSize = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleEditorWordWrap = function(event) {
	this.changed = true;
	this.appAssist.settings.editor.wordWrap = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleOrientText = function(event) {
	this.changed = true;
	this.appAssist.settings.editor.orientation = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.handleIpkHandler = function(event) {
	this.changed = true;
	this.appAssist.settings.explorer.ipkHandler = event.value;
	this.appAssist.saveSettings();
};

SettingsAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("homeDir", Mojo.Event.tap, this.handleHomeTap);
	this.controller.stopListening("selTheme", Mojo.Event.propertyChange, this.handleTheme);
	this.controller.stopListening("toggleHidden", Mojo.Event.propertyChange, this.handleHidden);
	this.controller.stopListening("selFont", Mojo.Event.propertyChange, this.handleFont);
	this.controller.stopListening("toggleWrapNames", Mojo.Event.propertyChange, this.handleWrapNames);
	this.controller.stopListening("toggleSwipeDelete", Mojo.Event.propertyChange, this.handleSwipeDelete);
	this.controller.stopListening("selIpk", Mojo.Event.propertyChange, this.handleIpkHandler);
	this.controller.stopListening("selOrientFile", Mojo.Event.propertyChange, this.handleOrientFile);
	this.controller.stopListening("toggleImageCard", Mojo.Event.propertyChange, this.handleImageCard);
	this.controller.stopListening("toggleImageFull", Mojo.Event.propertyChange, this.handleImageFull);
	this.controller.stopListening("toggleImageSwipe", Mojo.Event.propertyChange, this.handleImageSwipe);
	this.controller.stopListening("selOrientImage", Mojo.Event.propertyChange, this.handleOrientImage);
	this.controller.stopListening("toggleEditorCard", Mojo.Event.propertyChange, this.handleEditorCard);
	this.controller.stopListening("selNewline", Mojo.Event.propertyChange, this.handleNewline);
	this.controller.stopListening("selEditorFont", Mojo.Event.propertyChange, this.handleEditorFont);
	this.controller.stopListening("toggleEditorWordWrap", Mojo.Event.propertyChange, this.handleEditorWordWrap);
	this.controller.stopListening("selOrientText", Mojo.Event.propertyChange, this.handleOrientText);
};

SettingsAssistant.prototype.cleanup = function(event) {
};
