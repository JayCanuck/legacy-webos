enyo.kind({
	name: "AppLauncher",
	kind: enyo.VFlexBox,
	components: [
		{kind: enyo.ApplicationEvents, onApplicationRelaunch: "relaunchHandler", onOpenAppMenu: "openAppMenu", onCloseAppMenu: "closeAppMenu", onUnload: "windowClosing"},
		{kind: "ComicService"},
		{kind: "Pane", flex: 1, components: [
			{name: "browser", kind: "Browser", onOpenComic:"openComic"},
			{name: "viewer", kind: "Viewer", onComicLoaded:"comicLoaded", onCloseComic:"closeComic", onSetThumb:"setThumbImage"}
		]}
	],
	create: function() {
		enyo.application.prefs = new Preferences();
		
		this.inherited(arguments);
		
		var params = enyo.clone(enyo.windowParams);
		enyo.windows.setWindowParams(window, {});
		if(params.target && params.target.trim().length>0) {
			var ext = getFileExt(params.target);
			if(!params.target.startsWith("http") && (ext == "cbr" || ext == "cbz")) {
				params.target = params.target.replace("/var/luna/data/file:", "");
				params.target = params.target.replace("file://", "");
				if(params.target.charAt(0) != "/") {
					params.target = "/" + params.target;
				}
				this.$.viewer.loadComic(params.target);
				this.$.pane.selectView(this.$.viewer);
			}
		} else { //no external handling, so check for auto-resume
			var autoResume = enyo.application.prefs.get("autoResume");
			if(autoResume && autoResume.file) {
				this.$.viewer.loadComic(autoResume.file);
				this.$.pane.selectView(this.$.viewer);
			} else {
				this.$.pane.selectView(this.$.browser);
			}
		}
	},
	relaunchHandler: function(inSender, inEvent) {
		var params = enyo.clone(enyo.windowParams);
		enyo.windows.setWindowParams(window, {});
		if(params.target && params.target.trim().length>0) {
			var ext = getFileExt(params.target);
			if(!params.target.startsWith("http") && (ext == "cbr" || ext == "cbz" || ext == "rar" || ext == "zip")) {
				params.target = params.target.replace("/var/luna/data/file:", "");
				params.target = params.target.replace("file://", "");
				if(params.target.charAt(0) != "/") {
					params.target = "/" + params.target;
				}
				if(this.$.viewer.filepath != params.target) {
					this.$.viewer.close();
					this.$.viewer.loadComic(params.target);
					this.$.pane.selectViewImmediate(this.$.viewer);
				}
				
			}
		}
		enyo.windows.activateWindow(enyo.windows.getActiveWindow());
		/*var name = "ComicShelfHD";
		var params = enyo.clone(enyo.windowParams);
		if(params.target) {
			var ext = getFileExt(params.target);
			if(!params.target.startsWith("http") && (ext=="cbr" || ext=="cbz")) {
				params.target = params.target.replace("/var/luna/data/file:", "");
				params.target = params.target.replace("file://", "");
				if(params.target.charAt(0) != "/") {
					params.target = "/" + params.target;
				}
				name = params.target;
			}
		}
		enyo.windows.activate("index.html", name, params);*/
		return true;
	},
	openAppMenu: function() {
		this.$.pane.getView().getAppMenu().open();
	},
	closeAppMenu: function() {
		this.$.pane.getView().getAppMenu().close();
	},
	openComic: function(inSender, inFile) {
		this.$.viewer.loadComic(inFile);
		this.$.pane.selectView(this.$.viewer);
	},
	setThumbImage: function(inSender, inEvent) {
		this.$.browser.setCoverEvent(inSender, inEvent);
	},
	comicLoaded: function(inSender, inEvent) {
		this.$.browser.comicLoaded(inSender, inEvent);
	},
	closeComic: function(inSender, inTimestamp) {
		this.$.comicService.call({timestamp: inTimestamp}, {method:"unload"});
		if(!this.$.browser.isLoaded) {
			this.$.browser.loadList();
		}
		this.$.pane.selectView(this.$.browser);
		if(this.$.viewer.closeAsUnread) {
			this.$.viewer.closeAsUnread = undefined;
			this.$.browser.closeAsUnread(this.$.viewer.filepath);
		}
	},
	windowClosing: function(inSender, inEvent) {
		var win = enyo.windows.getWindows();
		var winCount = 0;
		for(var currWin in win) {
			winCount++;
		}
		if(winCount<=1) {
			if(enyo.application.prefs.get("autoResume")!=undefined) {
				if(this.$.pane.getViewName()=="viewer") {
					enyo.application.prefs.set("autoResume", this.$.viewer.getCurrentComic());
				} else {
					enyo.application.prefs.set("autoResume", {});
				}
			}
			this.$.viewer.$.extractor.cancel();
			this.$.browser.$.coverService.cancel();
			this.$.comicService.call({}, {method:"unloadAll"});
		}
	}
});