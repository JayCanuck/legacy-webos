function AppAssistant(appController) {
	this.controller = appController;
}

AppAssistant.prototype.handleLaunch = function(launchParams) {
	this.url = "http://i.wow.com";
	this.feed = "http://www.wow.com/rss.xml";
	if(launchParams.action=="checkWebsite") {
		this.services = new Services();
		this.services.setBackgroundActionIn("checkWebsite",
				Preferences.get("checkTime", "00:30:00"));
		this.services.cleanup();
		this.rssRequest = new Ajax.Request(this.feed, {
			method: 'get',
			onSuccess: this.checkWebsite.bind(this),
			onComplete: function() {
				delete this.rssRequest;
			}.bind(this)
		});
	} else {
		if(launchParams && launchParams.url) {
			this.url = launchParams.url;
		}
		this.stageName = "WoW.com";
		var stageController = this.controller.getStageController(this.stageName);
		if(stageController) {
			stageController.activate();
			return;
		}
		this.controller.createStageWithCallback(this.stageName, this.pushScene.bind(this));
	}
};

AppAssistant.prototype.pushScene = function(stageController) {
	stageController.pushScene('main', {url:this.url});
};

AppAssistant.prototype.checkWebsite = function(payload) {
	var rssItems = payload.responseXML.getElementsByTagName("item");
	if(this.hasNewArticles(rssItems)) {
		this.urls = [];
		this.titles = [];
		for(var i=0; i<rssItems.length; i++) {
			var currGUID = rssItems[i].getElementsByTagName("guid")[0].textContent;
			currGUID = Utils.mobilizeURL(currGUID);
			if(currGUID == this.lastGUID) {
				break;
			} else {
				this.urls.push(currGUID);
				this.titles.push(rssItems[i].getElementsByTagName("title")[0].textContent);
			}
		}
		if(Preferences.get("type", "dashboard")=="banner") {
			var msg = this.titles[0];
			if(this.titles.length>1) {
				msg = this.titles.length + " new articles";
			}
			this.controller.showBanner(msg, {url:this.urls[0]});
			Preferences.set("lastReadGUID", this.urls[0]);
		} else {
			this.dashboardName = "WoW.com-Dashboard";
			var dashboardStage = this.controller.getStageProxy(this.dashboardName);
			if(dashboardStage) {
  				dashboardStage.delegateToSceneAssistant("updateDashboard", this.urls,
						this.titles);
			} else {
				this.controller.createStageWithCallback(this.dashboardName,
						this.pushDashboard.bind(this), Mojo.Controller.StageType.dashboard);
			}
		}
	}
};

AppAssistant.prototype.hasNewArticles = function(rssItems) {
	var firstItem = rssItems[0];
	var guid = firstItem.getElementsByTagName("guid")[0].textContent;
	guid = Utils.mobilizeURL(guid);
	this.lastGUID = Preferences.get("lastReadGUID", guid);
	return (guid != this.lastGUID);
};

AppAssistant.prototype.pushDashboard = function(stageController) {
	stageController.setWindowOrientation('free');
	stageController.pushScene('dashboard', {urls:this.urls, titles:this.titles});
};
