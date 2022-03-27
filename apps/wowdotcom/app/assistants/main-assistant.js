function MainAssistant(params) {
	this.url = params.url;
	this.history = [this.url];
	this.currPage = 0;
}

MainAssistant.prototype.setup = function() {
	this.services = new Services();

	this.rssRequest = new Ajax.Request("http://www.wow.com/rss.xml", {
		method: 'get',
		onSuccess: function(payload) {
			var firstItem = payload.responseXML.getElementsByTagName("item")[0];
			Preferences.set("lastReadGUID", Utils.mobilizeURL(firstItem
					.getElementsByTagName("guid")[0].textContent));
		}.bind(this),
		onComplete: function() {
			delete this.rssRequest;
		}.bind(this)
	});
	
	this.menuModel = {
        visible: true,
        items: [ 
            {label: "Settings", command: 'settings'},
            {label: "About", command: 'about' }
        ]
    };
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true},
			this.menuModel);
	
	this.reloadModel = {
 		label: 'Reload',
 		icon: 'refresh',
 		command: 'refresh'
 	};
  	this.stopModel = {
 		label: 'Stop',
 		icon: 'load-progress',
 		command: 'stop'
    };
	
	this.cmdMenuModel = {
		items: [
			{},
			{},
			{items: [
					{icon: "back", command:'back', disabled:true},
					{icon: "forward", command:'forward', disabled:true}
				]
			},
			{},
			this.reloadModel
		]
	};
	this.controller.setupWidget(Mojo.Menu.commandMenu, {menuClass:'no-fade'},
			this.cmdMenuModel);
	this.controller.setupWidget("webview", {url:this.url, interrogateClicks:true,
			cacheAdapter:true}, {});
};

MainAssistant.prototype.activate = function(event) {
	this.controller.get("body").style.backgroundColor = "white !important";
	this.handleUpdate = this.handleUpdate.bindAsEventListener(this);
	this.loadProgress = this.loadProgress.bindAsEventListener(this);
	this.loadStarted = this.loadStarted.bindAsEventListener(this);
	this.loadStopped = this.loadStopped.bindAsEventListener(this);
	this.controller.listen("webview", Mojo.Event.webViewLinkClicked, this.handleUpdate);
	this.controller.listen("webview",Mojo.Event.webViewLoadProgress, this.loadProgress);
	this.controller.listen("webview",Mojo.Event.webViewLoadStarted, this.loadStarted);
	this.controller.listen("webview",Mojo.Event.webViewLoadStopped, this.loadStopped);
	this.controller.listen("webview",Mojo.Event.webViewLoadFailed, this.loadStopped);
};

MainAssistant.prototype.handleCommand = function(event){
  	if (event.type == Mojo.Event.command) {
		switch (event.command) {
		case 'settings':
			this.controller.stageController.pushScene("settings");
			break;
		case 'about':
			this.msgbox("<center><div style=\"font-size:1.35em;" +
				"margin-bottom:10px\"><strong>" + Mojo.Controller.appInfo.title + 
				"</strong>&nbsp;&nbsp;&nbsp; " + Mojo.Controller.appInfo.version +
				"</div><div style=\"font-size:1.2em;margin-bottom:0px\"><em>" +
				"Unofficial</em></div><a href=\"http://canuck-software.ca\">" +
				"<img src=\"images/canuck.png\"/></a><div style=\"font-size:" +
				"0.8em\">&copy; Copyright 2010, Jason Robitaille</div>" +
				"</center>", ""
			);
			break;
		case 'back':
			this.currPage--;
			this.updateCmdMenu();
			this.controller.get("webview").mojo.openURL(this.history[this.currPage]);
			break;
		case 'forward':
			this.currPage++;
			this.updateCmdMenu();
			this.controller.get("webview").mojo.openURL(this.history[this.currPage]);
			break;
		case 'refresh':
			this.controller.get("webview").mojo.reloadPage();
			break;
		case 'stop':
			this.controller.get("webview").mojo.stopLoad();
			break;
		}
	} else if(event.type == Mojo.Event.back){
		this.currPage--;
		this.updateCmdMenu();
		event.stop();
		this.controller.get("webview").mojo.openURL(this.history[this.currPage]);
	} else if(event.type == Mojo.Event.forward){
		this.currPage++;
		this.updateCmdMenu();
		event.stop();
		this.controller.get("webview").mojo.openURL(this.history[this.currPage]);
	}
};

//  loadStarted - switch command button to stop icon & command
//
MainAssistant.prototype.loadStarted = function(event) {
 	this.cmdMenuModel.items.pop(this.reloadModel);
 	this.cmdMenuModel.items.push(this.stopModel);
 	this.controller.modelChanged(this.cmdMenuModel);
  	this.currLoadProgressImage = 0;
};

//  loadStopped - switch command button to reload icon & command
MainAssistant.prototype.loadStopped = function(event) {
 	this.cmdMenuModel.items.pop(this.stopModel);
 	this.cmdMenuModel.items.push(this.reloadModel);
 	this.controller.modelChanged(this.cmdMenuModel);
};

//  loadProgress - check for completion, then update progress
MainAssistant.prototype.loadProgress = function(event) {
  	var percent = event.progress;
 	try {
 		if (percent > 100) {
 			percent = 100;
 		}
 		else if (percent < 0) {
 			percent = 0;
 		}
  		// Update the percentage complete
 		this.currLoadProgressPercentage = percent;
  		// Convert the percentage complete to an image number
 		// Image must be from 0 to 23 (24 images available)
 		var image = Math.round(percent / 4.1);
 		if (image > 23) {
 			image = 23;
 		}
  		// Ignore this update if the percentage is lower than where we're showing
 		if (image < this.currLoadProgressImage) {
 			return;
 		}
  		// Has the progress changed?
 		if (this.currLoadProgressImage != image) {
 			// Cancel the existing animator if there is one
 			if (this.loadProgressAnimator) {
 				this.loadProgressAnimator.cancel();
 				delete this.loadProgressAnimator;
 			}
                          // Animate from the current value to the new value
 			var icon = this.controller.select('div.load-progress')[0];
 			if (icon) {
 				this.loadProgressAnimator = Mojo.Animation.animateValue(
						Mojo.Animation.queueForElement(icon),"linear",
						this._updateLoadProgress.bind(this), {
 							from: this.currLoadProgressImage,
 							to: image,
 							duration: 0.5
 						});
 			}
 		}
 	} catch(e) {
 		Mojo.Log.logException(e, e.description);
 	}
};
 
MainAssistant.prototype._updateLoadProgress = function(image) {
  	// Find the progress image
 	image = Math.round(image);
 	// Don't do anything if the progress is already displayed
 	if (this.currLoadProgressImage == image) {
 		return;
 	}
 	var icon = this.controller.select('div.load-progress');
 	if (icon && icon[0]) {
 		icon[0].setStyle({'background-position': "0px -" + (image * 48) + "px"});
 	}
 	this.currLoadProgressImage = image;
};

MainAssistant.prototype.msgbox = function(msg, title) {
	this.controller.showAlertDialog({
		onChoose: function(value){},
		title: title | "Information",
		message: msg,
		choices: [ {label: "OK",value: ""} ],
		allowHTMLMessage: true
	});
};

MainAssistant.prototype.handleUpdate = function(event) {
	var newURL = Utils.mobilizeURL(event.url);
	if(newURL.indexOf("http://i.wow.com")==0) {
		if(newURL!=="http://i.wow.com/#_articles") {
			var temp = [];
			for(var i=0; i<=this.currPage; i++) {
				temp.push(this.history[i]);
			}
			this.history = temp;
			this.currPage++;
			this.history.push(newURL);
			this.updateCmdMenu();
		}
		this.controller.get("webview").mojo.openURL(newURL);
	} else {
		if(newURL.indexOf("admob")<0 && newURL.indexOf("atwola")<0) {
			this.services.openExternalWebpage(newURL);
		}
	}
	event.stop();
};

MainAssistant.prototype.updateCmdMenu = function(){
	if(this.currPage==0) {
		this.cmdMenuModel.items[2].items[0].disabled = true;
	} else {
		this.cmdMenuModel.items[2].items[0].disabled = false;
	}
	if(this.currPage==this.history.length-1) {
		this.cmdMenuModel.items[2].items[1].disabled = true;
	} else {
		this.cmdMenuModel.items[2].items[1].disabled = false;
	}
	this.controller.modelChanged(this.cmdMenuModel);
};

MainAssistant.prototype.deactivate = function(event) {
	this.controller.get("body").style.backgroundColor = "";
	this.controller.stopListening("webview", Mojo.Event.webViewLinkClicked,
			this.handleUpdate);
};

MainAssistant.prototype.cleanup = function(event) {
	this.services.cleanup();
};
