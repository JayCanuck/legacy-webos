enyo.kind({
	name: "Viewer",
	kind: enyo.VFlexBox,
	published: {
		params: {}
	},
	events: {
		onComicLoaded: "",
		onCloseComic: "",
		onSetThumb: ""
	},
	components: [
		{name:"appMenu", kind: "AppMenu", automatic:false, components: [
		  	{caption: "Email Comic", onclick: "emailComic"},
			{caption: "Page Options", components:[
				{caption: "Email Page", onclick: "emailPage"},
				{caption: "Save Page", onclick: "savePage"},
				{caption: "Set Wallpaper", onclick: "setWallpaper"},
			]},
			{caption: "Print", onclick: "print"}
		]},
		{name:"appMenuThumb", kind: "AppMenu", automatic:false, components: [
		  	{caption: "Email Comic", onclick: "emailComic"},
			{caption: "Page Options", components:[
				{caption: "Email Page", onclick: "emailPage"},
				{caption: "Save Page", onclick: "savePage"},
				{caption: "Set Wallpaper", onclick: "setWallpaper"},
				{caption: "Set As Thumbnail", onclick: "setThumbnail"},
			]},
			{caption: "Print", onclick: "print"}
		]},
		{name:"appMenuExtended", kind: "AppMenu", automatic:false, components: [
		  	{caption: "Email Comic", onclick: "emailComic"},
			{caption: "Page Options", components:[
				{caption: "Email Page", onclick: "emailPage"},
				{caption: "Save Page", onclick: "savePage"},
				{caption: "Set Wallpaper", onclick: "setWallpaper"},
			]},
			{caption: "Print", onclick: "print"},
			{caption: "Close & Mark Unread", onclick: "closeUnread"}
		]},
		{name:"appMenuExtendedThumb", kind: "AppMenu", automatic:false, components: [
		  	{caption: "Email Comic", onclick: "emailComic"},
			{caption: "Page Options", components:[
				{caption: "Email Page", onclick: "emailPage"},
				{caption: "Save Page", onclick: "savePage"},
				{caption: "Set Wallpaper", onclick: "setWallpaper"},
				{caption: "Set As Thumbnail", onclick: "setThumbnail"},
			]},
			{caption: "Print", onclick: "print"},
			{caption: "Close & Mark Unread", onclick: "closeUnread"}
		]},
		{name:"prefs", kind: "Preferences"},
		{kind: "ScrimSpinner", onCancel:"cancelExtraction"},
		{kind: "ComicService"},
		{name: "extractor", kind: "ComicService", subscribe:true, resubscribe:true},
		{kind: "PalmService"},
		{kind: "SafePrintDialog", appName: "ComicShelf", mediaTypeOption:true, mediaSizeOption:true, qualityOption:true},
		{kind: "SimpleDialog"},
		{kind: "ErrorDialog"},
		{kind: "PageHeader", className:"enyo-header-dark", pack:"center"},
		{name: "imageView", kind: "ComicPageViewer", flex: 1, style:"width:100%; height:100%; background-color:#393e43;", onGetLeft: "getLeft", onGetRight: "getRight", onclick: "imageTap"},//
		{name: "bottomBar", className:"toolbarContainer", components:[
			{kind: "Toolbar", className:"enyo-toolbar-dark", components: [
				{kind:"HFlexBox", flex:1, components:[
					{name:"closeButton", kind: "ToolButton", icon: "images/close.png", className:"toolbarButton", caption:"Close", onclick: "close"}
				]},
				{kind:"HFlexBox", flex:1, pack:"center", components:[
					{className:"controlContainer", components:[
						{kind: "ToolButtonGroup", className:"controlGroup", components: [
							{name:"backButton", icon: "images/back.png", style:"padding-top:15px;", onclick: "goBack"},
							{name:"pageButton", kind: "GroupedToolButton", components:[
								{name:"pagePopup", kind: "CustomListSelector", className:"pagePopup", value:1, label: " /??", onChange: "changePage", items:[
									{caption:"Page 1", value:1}
								]}
							]},
							{name:"forwardButton", icon: "images/forward.png", style:"padding-top:15px;", onclick: "goForward"}
			  			]},
					]},
				]},
				{kind:"HFlexBox", flex:1, pack:"end", components:[
					{kind: "ToolButton", icon: "images/email.png", className:"toolbarButton",  caption:"Email", onclick: "emailPage"},
			      	{kind: "ToolButton", icon: "images/wallpaper.png", className:"toolbarButton",  caption:"Wallpaper", onclick: "setWallpaper"},
				]}
			]}
		]}
	],
	create: function() {
		this.inherited(arguments);
		this.controlsHidden = false;
		this.$.scrimSpinner.hide();
	},
	loadComic: function(file) {
		enyo.ImageView.initZoom = undefined;
		if(enyo.application.prefs.get("fullScreen")) {
			enyo.setFullScreen(true);
		}
		if(enyo.application.prefs.get("blackBackground")) {
			this.$.imageView.applyStyle("background-color", "black");
		} else {
			this.$.imageView.applyStyle("background-color", "#393e43");
		}
		this.$.scrimSpinner.showWithCancel();
		this.filepath = file;
		this.filename = getFileName(this.filepath);
		this.filename = this.filename.substring(0, this.filename.lastIndexOf("."))
		this.$.pageHeader.setContent(this.filename);
		var autoResume = this.$.prefs.get("autoResume");
		this.index = 0;
		if(autoResume && autoResume.file) {
			if(autoResume.file==this.filepath) {
				this.index = autoResume.index;
			} else {
				this.$.prefs.set("autoResume", {});
			}
		}
		this.$.comicService.call({file:this.filepath}, {
			method: "query",
			onSuccess: "querySuccess",
			onFailure: "queryFailure"
		});
	},
	querySuccess: function(inSender, inResponse) {
		this.images = inResponse.entries;
		this.imageFiles = [];
		var pageItems = [];
		for(var i=0; i<this.images.length; i++) {
			pageItems.push({caption: "Page " + (i+1), value:(i+1)});
		}
		this.$.pagePopup.setItems(pageItems);
		this.$.pagePopup.setValue(this.index+1);
		this.$.pagePopup.setLabel(" /" + this.images.length);

		this.$.extractor.call({file:this.filepath, subscribe:true}, {
			method: "extract",
			onSuccess: "extractSuccess",
			onFailure: "extractFailure"
		});
	},
	queryFailure: function(inSender, inResponse) {
		enyo.error("**Query failure: " + enyo.json.stringify(inResponse) + "**");
		this.$.scrimSpinner.hide();
		this.$.errorDialog.openAtCenter(inResponse.errorText);
	},
	extractSuccess: function(inSender, inResponse) {
		//enyo.log("**Extract progress: " + enyo.json.stringify(inResponse) + "**");
		if(inResponse.timestamp) {
			this.timestamp = inResponse.timestamp;
			for(var i=0; i<this.images.length; i++) {
				//this.images[i] = "/var/luna/data/extractfs" + inResponse.destination + this.images[i] + ":0:0:1200:1200:3";
				this.imageFiles[i] = inResponse.destination + this.images[i];
				this.images[i] = inResponse.destination + encodeURIComponent(this.images[i]);
			}
			this.$.scrimSpinner.hide();
			this.doComicLoaded(this.filepath);
			this.$.imageView.setCenterSrc(this.images[this.index]);
		}
	},
	extractFailure: function(inSender, inResponse) {
		//enyo.error("**Extract progress: " + enyo.json.stringify(inResponse) + "**");
		this.$.scrimSpinner.hide();
		this.$.errorDialog.openAtCenter(inResponse.errorText);
	},
	cancelExtraction: function(inSender, inEvent) {
		this.$.extractor.cancel();
		this.close();
	},
	getLeft: function(inSender, inSnap) {
		inSnap && this.index--;
		this.$.pagePopup.setValue(this.index+1);
		this.updateControls();
		return this.images[this.index-1];
	},
	getRight: function(inSender, inSnap) {
		inSnap && this.index++;
		this.$.pagePopup.setValue(this.index+1);
		this.updateControls();
		return this.images[this.index+1];
	},
	updateControls: function() {
		if(this.images.length==1) {
			this.$.backButton.setDisabled(true);
			this.$.forwardButton.setDisabled(true);
		} else if(this.index==0) {
			this.$.backButton.setDisabled(true);
			this.$.forwardButton.setDisabled(false);
		} else if(this.index==this.images.length-1) {
			this.$.backButton.setDisabled(false);
			this.$.forwardButton.setDisabled(true);
		} else {
			this.$.backButton.setDisabled(false);
			this.$.forwardButton.setDisabled(false);
		}
	},
	goBack: function(inSender, inEvent) {
		this.index--;
		this.$.imageView.setCenterSrc(this.images[this.index]);
		this.updateControls();
	},
	changePage: function(inSender, inValue, inOldValue) {
		this.index = inValue-1;
		this.$.imageView.setCenterSrc(this.images[this.index]);
		this.updateControls();
	},
	goForward: function(inSender, inEvent) {
		this.index++;
		this.$.imageView.setCenterSrc(this.images[this.index]);
		this.updateControls();
	},
	imageTap: function(inSender, inEvent) {
		var edgeTapEnabled = enyo.application.prefs.get("edgeTap");
		var twentyPercent = Math.round(window.innerWidth*0.20);
		if(edgeTapEnabled && (inEvent.x >= 0) && (inEvent.x <= twentyPercent)) {
			if(this.index>0) {
				this.goBack();
			}
		} else if(edgeTapEnabled && (inEvent.x <= window.innerWidth) && (inEvent.x >= (window.innerWidth-twentyPercent))) {
			if(this.index<this.images.length-1) {
				this.goForward();
			}
		} else {
			if(this.controlsHidden) {
				this.$.pageHeader.show();
				this.$.bottomBar.show();
			} else {
				this.$.pageHeader.hide();
				this.$.bottomBar.hide();
			}
			this.controlsHidden = !this.controlsHidden;
		}
	},
	emailComic: function(inSender, inEvent) {
		this.$.palmService.call({
			id: "com.palm.app.email",
			params: {
				attachments: [
					{fullPath: this.filepath}
				]
			}
		}, {
			service: "palm://com.palm.applicationManager",
			method: "open"
		});
	},
	emailPage: function(inSender, inEvent) {
		this.$.palmService.call({
			id: "com.palm.app.email",
			params: {
				attachments: [
					{fullPath: this.imageFiles[this.index]}
				]
			}
		}, {
			service: "palm://com.palm.applicationManager",
			method: "open"
		});
	},
	savePage: function(inSender, inEvent) {
		this.tempPath = "/media/internal/" + this.filename + " - Page " + (this.index+1) + "." +
				getFileExt(this.imageFiles[this.index]);
		this.$.scrimSpinner.show();		
		this.$.comicService.call({from:this.imageFiles[this.index], to:this.tempPath}, {
			method: "save",
			onSuccess: "savePageSuccess",
			onFailure: "savePageFailure"
		});
	},
	savePageSuccess: function(inSender, inResponse) {
		this.$.scrimSpinner.hide();
		this.$.simpleDialog.openAtCenter("Image saved successfully.");
	},
	savePageFailure: function(inSender, inResponse) {
		this.$.scrimSpinner.hide();
		this.$.errorDialog.openAtCenter(inResponse.errorText);
	},
	print: function(inSender, inEvent) {
		this.$.safePrintDialog.setImagesToPrint(this.imageFiles);
		this.$.safePrintDialog.setPageRange({min: 1, max: this.imageFiles.length});
		this.$.safePrintDialog.openAtCenter();
	},
	setWallpaper: function(inSender, inEvent) {
		this.tempPath = "/media/internal/comics/" + new Date().getTime() + "." +
				getFileExt(this.imageFiles[this.index]);
		this.$.scrimSpinner.show();
		this.$.comicService.call({from:this.imageFiles[this.index], to:this.tempPath}, {
			method: "save",
			onSuccess: "saveWallpaperSuccess",
			onFailure: "saveWallpaperFailure"
		});
	},
	saveWallpaperSuccess: function(inSender, inResponse) {
		this.$.palmService.call({target: "file://" + this.tempPath}, {
			service: "palm://com.palm.systemservice/wallpaper",
			method: "importWallpaper",
			onSuccess: "importSuccess",
			onFailure: "importFailure"
		});
	},
	saveWallpaperFailure: function(inSender, inResponse) {
		this.$.scrimSpinner.hide();
		this.$.errorDialog.openAtCenter(inResponse.errorText);
	},
	importSuccess: function(inSender, inResponse) {
		this.$.palmService.call({wallpaper: inResponse.wallpaper}, {
			service: "palm://com.palm.systemservice",
			method: "setPreferences",
			onSuccess: "wallpaperSetSuccess",
			onFailure: "wallpaperSetFailure"
		});
	},
	importFailure: function(inSender, inResponse) {
		this.$.comicService.call({file:this.tempPath}, {
			method: "delete"
		});
		this.$.scrimSpinner.hide();
		this.$.errorDialog.openAtCenter(inResponse.errorText);
	},
	wallpaperSetSuccess: function(inSender, inResponse) {
		this.$.comicService.call({file:this.tempPath}, {
			method: "delete"
		});
		this.$.scrimSpinner.hide();
		this.$.simpleDialog.openAtCenter("Wallpaper set successfully.");
	},
	wallpaperSetFailure: function(inSender, inResponse) {
		this.$.comicService.call({file:this.tempPath}, {
			method: "delete"
		});
		this.$.scrimSpinner.hide();
		this.$.errorDialog.openAtCenter(inResponse.errorText);
	},
	setThumbnail: function(inSender, inEvent) {
		this.doSetThumb({thumbnail:this.imageFiles[this.index], file:this.filepath});
	},
	close: function(inSender, inEvent) {
		enyo.ImageView.initZoom = undefined;
		if(enyo.application.prefs.get("fullScreen")) {
			enyo.setFullScreen(false);
		}
		this.doCloseComic(this.timestamp);
		this.$.pagePopup.setItems([{caption: "Page 1", value:1}]);
		this.$.pagePopup.setValue(1);
		this.$.pagePopup.setLabel(" /??");
		this.index = 0;
		this.images = ["images/blank.png"];
		this.imageFiles = ["images/blank.png"];
		this.$.imageView.setCenterSrc(this.images[0]);
		if(this.controlsHidden) {
			this.controlsHidden = false;
			this.$.pageHeader.show();
			this.$.bottomBar.show();
		}
	},
	closeUnread: function(inSender, inEvent) {
		this.closeAsUnread = true;
		this.close();
	},
	getCurrentComic: function() {
		return {file:this.filepath, index:this.index};
	},
	getAppMenu: function() {
		var result;
		if(enyo.application.prefs.get("unreadReadSystem")) {
			if(enyo.application.prefs.get("shelfView")) {
				result =  this.$.appMenuExtendedThumb;
			} else {
				result =  this.$.appMenuExtended;
			}
		} else {
			if(enyo.application.prefs.get("shelfView")) {
				result =  this.$.appMenuThumb;
			} else {
				result =  this.$.appMenu;
			}
		}
		return result;
	}
});