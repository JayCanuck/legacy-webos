function TexteditorAssistant(params) {
	this.filepath = params.path;
	this.isReadOnly = true;
	this.appAssist = Mojo.Controller.getAppController().assistant;
	this.WRITABLE = new Array("/media/cryptofs/apps/", "/media/internal/", "/var/");
	this.isOnline = false;
	if(this.filepath.startsWith("view-source://")) {
		this.filepath = this.filepath.replace("view-source://", "");
		if(!this.filepath.startsWith("http://") && !this.filepath.startsWith("https://")) {
			this.filepath = "http://" + this.filepath;
		}
	}
	if(this.filepath.startsWith("view-source:")) {
		this.filepath = this.filepath.replace("view-source:", "");
		if(!this.filepath.startsWith("http://") && !this.filepath.startsWith("https://")) {
			this.filepath = "http://" + this.filepath;
		}
	}
	if(this.filepath.startsWith("http")) {
		this.isOnline = true;
		this.isReadOnly = true;
	} else if(this.appAssist.settings.explorer.masterMode ||
			(this.filepath.startsWith(this.WRITABLE[0]) ||
			this.filepath.startsWith(this.WRITABLE[1]) ||
			this.filepath.startsWith(this.WRITABLE[2]))) {
		this.isReadOnly = false;
	}
	this.filemgr = params.filemgr || new FileMgrService();
	this.isModified = false;
	this.loaded = false;
	this.text = "";
}

TexteditorAssistant.prototype.setup = function() {
	var menuAttr = {omitDefaultItems: true};
  	this.menuModel;
	if(this.isReadOnly) {
		this.menuModel = {
	    	visible: true,
	    	items: [
				{label: $L("Toggle Word Wrap"), command: 'wordwrap'},
	      		{label: $L("Close"), command: 'close'}
	    	]
	  	};
	} else {
		this.menuModel = {
	    	visible: true,
	    	items: [
				{label: $L("Toggle Word Wrap"), command: 'wordwrap'},
				Mojo.Menu.editItem,
				{label: $L("Save File"), command: 'save'},
	      		{label: $L("Close"), command: 'close'}
	    	]
	  	};
	}
	if(!this.filepath.startsWith("/media/internal/") && !this.filepath.startsWith("http")) {
		this.menuModel.items.unshift({label: $L("Copy To User Storage"), command: 'copy'});
	}
	this.controller.setupWidget(Mojo.Menu.appMenu, menuAttr, this.menuModel);
	
	var bodyEle = this.controller.document.getElementsByTagName("body")[0];
	if(this.appAssist.settings.explorer.darkTheme
			&& bodyEle.className.indexOf("palm-dark") == -1) {
		bodyEle.addClassName("palm-dark");
	} else if(!this.appAssist.settings.explorer.darkTheme
			&& bodyEle.className.indexOf("palm-dark") != -1) {
		bodyEle.removeClassName("palm-dark");
	}
	
	if(Mojo.Environment.DeviceInfo.platformVersionMajor != 1) {
		this.chunkMax = 500000;
	} else {
		this.chunkMax = 200000;
	}
	
	this.viewW = this.controller.window.innerWidth-16;
	this.viewH = this.controller.window.innerHeight-70;
	this.width = this.viewW;
	this.height = this.viewH;
	
	this.controller.setInitialFocusedElement(null);
	this.controller.get("text").hide();
	this.wordWrap = this.appAssist.settings.editor.wordWrap;
	this.orientation = this.appAssist.settings.editor.orientation;
	if(this.orientation!='free') {
		this.controller.stageController.setWindowOrientation(this.orientation);
	} else {
		this.orientation = this.controller.stageController.getWindowOrientation();
		this.controller.stageController.setWindowOrientation(this.orientation);
	}
	
	this.fontSize = this.appAssist.settings.editor.fontSize;
	this.controller.get("text").style.fontSize = this.fontSize + "px";

	if(this.wordWrap) {
		this.controller.get("text").setStyle({
			"white-space": "pre-wrap",
			"word-wrap": "break-word",
			"text-overflow": "clip",
			"overflow-x": "hidden"
		});
	}

	this.controller.setupWidget("spinLoading", {spinnerSize: 'large'}, {spinning:true});

	if(this.isOnline) 
		this.name = this.filepath;
	else
		this.name = getFileName(this.filepath);
	this.controller.get("textTitle").innerText = this.name;
	
	this.controller.setupWidget("textScroller", {mode: 'horizontal'},{});
	
	if(this.isOnline) {
		this.ajaxRequest = new Ajax.Request(this.filepath, {
			method: "GET",
			onSuccess: function(response) {
				this.text = response.responseText;
				this.initText();
			}.bind(this),
			onFailure: this.errText.bind(this)
		});
	} else {
		this.filemgr.getSize({file: this.filepath, formatted: false},
				this.doRead.bind(this), this.doRead.bind(this));
	}
};

TexteditorAssistant.prototype.doRead = function(response) {
	if(response && response.size) {
		this.fileSize = response.size;
	}
	this.readRequest = this.filemgr.read({file: this.filepath, subscribe:true},
		function(response2){
			this.text += response2.data;
			if(this.fileSize) {
				this.controller.get("loadingText").innerText = $L("Loading...") + " "
						+ this.loadingPercent(this.text.length, this.fileSize) + "%";
			}
			if(response2.completed) {
				this.initText();
			}
		}.bind(this),
		this.errText.bind(this)
	);
}

TexteditorAssistant.prototype.initText = function() {
	this.controller.get("loadingText").innerText = $L("Loading...") + " 100%";
	this.text = this.text.replace(/\r\n/g, "\n");
	this.text = this.text.replace(/\r/g, "\n");
	this.controller.get('spinLoading').mojo.stop();
	this.controller.get('loadingScrim').hide();
	this.controller.get("text").show();
	this.controller.get("text").innerText = this.text;
	if(!this.isReadOnly) {
		this.controller.get("text").setStyle({"-webkit-user-modify":"read-write"});
	}
	this.loadText();
	this.controller.get("text").focus();
	this.controller.window.getSelection().collapseToStart();
	var vertical = this.controller.getSceneScroller().mojo.getState();
	vertical.top = 0;
	this.controller.getSceneScroller().mojo.setState(vertical, false);
	this.text = "";
};

TexteditorAssistant.prototype.errText = function(err) {
	this.controller.get('spinLoading').mojo.stop();
	this.controller.get('loadingScrim').hide();
	Error($L(err.errorText));
};

TexteditorAssistant.prototype.loadingPercent = function(amount, total) {
	var value = (amount/total)*100;
	value = Math.round(value);
	return value;
}

TexteditorAssistant.prototype.activate = function(event) {
	if(event && event.action && event.action=="copy") {
		this.copiedPath = event.to;
		this.filemgr.copy(event.from, event.to,
			function(response) {
				this.reloadDialog();
			}.bind(this),
			function(err) {
				Error($L(err.errorText));
			}.bind(this)
		);
	}
	
	if(this.loaded) {
		this.orientationChanged(this.orientation);
	}
	this.handleResize = this.handleResize.bindAsEventListener(this);
	this.handleKeyup = this.handleKeyup.bindAsEventListener(this);
	this.handleTap = this.handleTap.bindAsEventListener(this);
	this.gestureChange = this.gestureChange.bindAsEventListener(this);
	this.gestureEnd = this.gestureEnd.bindAsEventListener(this);
	this._addAsScrollListener = this._addAsScrollListener.bind(this);
	this.controller.listen(this.controller.window, 'resize', this.handleResize);
	this.controller.listen("text", "keyup", this.handleKeyup);
	this.controller.listen(this.controller.window, Mojo.Event.tap, this.handleTap);
	this.controller.listen(this.controller.stageController.document, "gesturechange",
			this.gestureChange);
	this.controller.listen(this.controller.stageController.document, "gestureend",
			this.gestureEnd);
	this.controller.listen(this.controller.getSceneScroller(), Mojo.Event.scrollStarting,
			this._addAsScrollListener);
	this.controller.listen("textScroller", Mojo.Event.scrollStarting,
			this._addAsScrollListener);
};

TexteditorAssistant.prototype._addAsScrollListener = function(event) {
	if(event.target === this.controller.getSceneScroller()) {
		event.scroller.addListener(this);
	} else if(event.target === this.controller.get("textScroller")) {
		event.scroller.addListener(this);
	}
};

TexteditorAssistant.prototype.reloadDialog = function(){
	this.controller.showAlertDialog({
		onChoose: function(value){
			if(value=="yes") {
				if(this.controller.stageController.window.name != "Internalz") {
					this.controller.stageController.window.name = this.copiedPath;
				}
				this.controller.stageController.swapScene(
					{name: "texteditor", transition: Mojo.Transition.crossFade},
					{filemgr: this.filemgr, path: this.copiedPath}
				);
				
			}
		}.bind(this),
		title: $L("Reload File?"),
		message: $L("Copy completed") + ".<br/><br/>"
				+ $L("Would you like to reload to the copied file?"),
		choices: [
			{label: $L("Yes"),value: "yes", type:"affirmative"},
			{label: $L("No"),value: "no", type:"negative"}
		],
		allowHTMLMessage: true
	});
};

TexteditorAssistant.prototype.handleCommand = function(event){
	if(event.type == Mojo.Event.command) {
		if (event.command == 'close') {
			this.handleClose();
		} else if (event.command == 'save') {
			this.saveText();
			this.isModified = false;
		} else if (event.command == 'wordwrap') {
			if(this.wordWrap) { //disable word wrap
				this.controller.get("text").setStyle({
					"white-space": "pre",
					"word-wrap": "",
					"text-overflow": "",
					"overflow-x": ""
				});
			} else { //enable word wrap
				this.controller.get("text").setStyle({
					"white-space": "pre-wrap",
					"word-wrap": "break-word",
					"text-overflow": "clip",
					"overflow-x": "hidden"
				});
				var horizontal = this.controller.get("textScroller").mojo.getState();
				horizontal.left = 0;
				this.controller.get("textScroller").mojo.setState(horizontal, false);
			}
			this.wordWrap = !this.wordWrap;
			this.appAssist.settings.editor.wordWrap = this.wordWrap;
			this.appAssist.saveSettings();
			this.loaded = false;
			this.loadText();
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
		}
	} else if(event.type == Mojo.Event.back){
			event.stop();
			this.handleClose();
	}
};

TexteditorAssistant.prototype.handleClose = function(){
	if(this.isModified){
		//if(this.appAssist.settings.editor.saveOnClose) {
		//	this.saveText();
		//	this.closeEditor();
		//} else {
			this.controller.showAlertDialog({
				onChoose: function(value) {
					if(value=='yes') {
						this.closeAfterSave = true;
						this.saveText();
					} else {
						this.closeEditor();
					}
				},
				title: $L("Save Changes?"),
				message: $L("This file has been modified. Would you like to save " +
						"before closing?"),
				choices:[
					{label:$L('Yes'), value:"yes", type:'affirmative'},  
					{label:$L('No'), value:"no", type:'negative'} 
				]
			});
		//}
	} else{
		this.closeEditor();
	}
};

TexteditorAssistant.prototype.closeEditor = function(){
	this.name = this.controller.stageController.window.name;
	if(this.name==="Internalz") {
		if(this.controller.stageController.getScenes().length == 1) {
			this.controller.stageController.swapScene("explorer", {
				filemgr:this.filemgr,
				path:this.appAssist.settings.explorer.startDir
			});
		} else {
			this.controller.stageController.popScene({action:"updateSize",
				path:this.filepath});
		}
		
	} else {
		if(this.controller.stageController.getScenes().length == 1) {
			var stage = Mojo.Controller.getAppController().getStageController("Internalz");
			if(stage) {
				stage.delegateToSceneAssistant("updateSize", this.filepath);
				stage.delegateToSceneAssistant("gracefulTransition", this.name);
				this.controller.window.close();
			} else {
				this.controller.window.close();
			}
		} else {
			this.controller.stageController.popScene();
		}
	}
};

TexteditorAssistant.prototype.loadText = function() {
	var offsetHeight = this.controller.get("text").offsetHeight;
	if(offsetHeight <= this.viewH) {
		this.controller.get("textContainer").style.height = this.viewH + "px";
	} else {
		this.controller.get("textContainer").style.height = offsetHeight + "px";
	}
	var offsetWidth = this.controller.get("text").offsetWidth;
	
	if(this.wordWrap) {
		this.controller.get("textContainer").style.width = this.viewW + "px";
		this.controller.get("text").style.width = "100%";
	} else {
		if(offsetWidth<=0) {
			this.controller.get("text").style.width = "40px";
		} else {
			this.controller.get("text").style.width = "";
		}
		if(offsetWidth < this.viewW) {
			offsetWidth = this.viewW;
		}
		this.controller.get("textContainer").style.width = offsetWidth + "px";
	}
	
	if(!this.loaded) {
		this.controller.window.setTimeout(this.loadText.bind(this), 500);
	}
	this.loaded = true;
};

TexteditorAssistant.prototype.saveText = function() {
	this.controller.get('spinLoading').mojo.start();
	this.controller.get("text").hide();
	this.controller.get("loadingText").innerText = $L("Saving...") + " 0%";
	this.controller.get('loadingScrim').show();
	
	if(!this.isReadOnly) {
		this.controller.get("text").setStyle({"-webkit-user-modify":"read-only"});
	}
	
	this.isModified = false;
	this.text = this.controller.get("text").innerText;
	if(this.appAssist.settings.editor.newLine == 1) {
		this.text = this.text.replace(/\n/g, "\r\n");
	} else if(this.appAssist.settings.editor.newLine == 3) {
		this.text = this.text.replace(/\n/g, "\r");
	}
	this.append = false;
	this.fileSize = this.text.length;
	this.controller.get("loadingText").innerText = $L("Saving...") + " "
			+ this.loadingPercent(this.fileSize - this.text.length, this.fileSize)
			+ "%";
	if(this.text.length > this.chunkMax) {
		var data = this.text.substring(0, this.chunkMax - 1);
		this.text = this.text.substring(this.chunkMax - 1);
		this.writeText(data);
	} else {
		var data = this.text.substring(0);
		this.text = "";
		this.writeText(data);
	}
};

TexteditorAssistant.prototype.writeText = function(data) {
	this.filemgr.write({file: this.filepath, str: data, append:this.append},
		function(response) {
			this.append = true;
			this.controller.get("loadingText").innerText = $L("Saving...") + " "
					+ this.loadingPercent(this.fileSize - this.text.length, this.fileSize)
					+ "%";
			if(this.text.length > this.chunkMax) {
				var data = this.text.substring(0, this.chunkMax - 1);
				this.text = this.text.substring(this.chunkMax - 1);
				this.writeText.bind(this).delay(500, data);
			} else if(this.text.length != 0) {
				var data = this.text.substring(0);
				this.text = "";
				this.writeText.bind(this).delay(500, data);
			} else {
				this.controller.get('spinLoading').mojo.stop();
				this.controller.get('loadingScrim').hide();
				this.controller.get("text").show();
				if(!this.isReadOnly) {
					this.controller.get("text")
							.setStyle({"-webkit-user-modify":"read-write"});
				}
				if(this.closeAfterSave) {
					this.closeEditor();
				}
			}
		}.bind(this),
		function(err) {
			Error($L(err.errorText));
		}.bind(this)
	);
};

TexteditorAssistant.prototype.handleKeyup = function(event){
	if(Mojo.Char.isValidWrittenChar(event.keyCode) || event.keyCode==8
			|| event.keyCode==13 || event.keyCode==46) {
		this.isModified = true;
		this.loadText();
	}
};

TexteditorAssistant.prototype.orientationChanged = function(orientation){
	if(this.orientation === orientation) {
		return;
   	}
   	if(this.appAssist.settings.editor.orientation=="free") {
		this.orientation = orientation;
		this.controller.stageController.setWindowOrientation(this.orientation);
		this.viewW = this.controller.window.innerWidth-16;
		this.viewH = this.controller.window.innerHeight-70;
		this.loadText();
   	}
};

TexteditorAssistant.prototype.moved = function(orientation){
	var posV = this.controller.getSceneScroller().mojo.getScrollPosition();
	var posH = this.controller.get("textScroller").mojo.getScrollPosition();
	var mHeight = this.controller.getSceneScroller().scrollHeight - this.controller
			.window.innerHeight;
	var mWidth = this.controller.get("textScroller").scrollWidth - this.controller
			.window.innerWidth;
	var curHeight = posV.top;
	var curWidth = posH.left;
	if(this.scrollbarTimer) {
		this.controller.stageController.window.clearTimeout(this.scrollbarTimer);
	}
	var scrollerHide = function(){
		this.controller.get('scrollbarbg').setOpacity(0.0);
		this.controller.get('scrollbarhbg').setOpacity(0.0);
	};
	this.scrollbarTimer = this.controller.stageController.window
			.setTimeout(scrollerHide.bind(this), 1000);
	this.controller.get('scrollbarbg').setOpacity(0.7);
	if(!this.wordWrap) {
		this.controller.get('scrollbarhbg').setOpacity(0.7);
	}
	var percent = Math.round((curHeight / mHeight) * 90)*-1;
	var percenth = Math.round((curWidth / mWidth) * 90)*-1;
	if(percent<0) {
		percent = 0;
	}
	if(percent>90) {
		percent = 90;
	}
	if(percenth<0) {
		percenth = 0;
	}
	if(percenth>90) {
		percenth = 90;
	}
	this.controller.get('scrollbar').style.top = percent + '%';
	this.controller.get('scrollbarh').style.left = percenth + '%';
};

TexteditorAssistant.prototype.handleTap = function(event) {
	if(this.controller.get("text").offsetWidth < this.viewW &&
			this.controller.get("text").offsetHeight < this.viewH) {
		this.controller.get("text").focus();
		this.controller.window.getSelection().collapseToStart();
		var vertical = this.controller.getSceneScroller().mojo.getState();
		vertical.top = 0;
		this.controller.getSceneScroller().mojo.setState(vertical, false);
	}
};

TexteditorAssistant.prototype.handleResize = function(event) {
	this.viewW = this.controller.window.innerWidth-16;
	this.viewH = this.controller.window.innerHeight-70;
	this.loadText();
};

TexteditorAssistant.prototype.gestureChange = function(event) {
	var size = this.fontSize + this.fontSize*((event.scale-1)*0.5);
	size = Math.round(size);
	if(size>18) {
		size = 18;
	} else if(size<10) {
		size = 10;
	}
	this.controller.get("text").style.fontSize = size + "px";
	this.loadText();
};

TexteditorAssistant.prototype.gestureEnd = function(event) {
	this.fontSize = parseInt(this.controller.get("text").style.fontSize
			.replace("px", ""));
	this.appAssist.settings.editor.fontSize = this.fontSize;
	this.appAssist.saveSettings();
};

TexteditorAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening(this.controller.window, 'resize', this.handleResize);
	this.controller.stopListening(this.controller.window, Mojo.Event.tap, this.handleTap);
	this.controller.stopListening("text", "keyup", this.handleKeyup);
	this.controller.stopListening(this.controller.stageController.document,
			"gesturechange", this.gestureChange);
	this.controller.stopListening(this.controller.stageController.document,
			"gestureend", this.gestureEnd);
	this.controller.stopListening(this.controller.getSceneScroller(),
			Mojo.Event.scrollStarting, this._addAsScrollListener);
	this.controller.stopListening("textScroller", Mojo.Event.scrollStarting,
			this._addAsScrollListener);
};

TexteditorAssistant.prototype.cleanup = function(event) {
	var name = this.controller.stageController.window.name;
	if(name!="Internalz" && this.isModified) {
		if(this.appAssist.settings.editor.saveOnClose) {
			//this.saveText();
		}
		var stage = Mojo.Controller.getAppController().getStageController("Internalz");
		stage.delegateToSceneAssistant("updateSize", this.filepath);
	}
	delete this.WRITABLE;
	//this.filemgr.gc();
};
