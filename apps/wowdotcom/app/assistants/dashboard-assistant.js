function DashboardAssistant(params) {
	this.urls = params.urls;
	this.titles = params.titles;
	this.currIndex = 0;
	this.baseURL = "http://i.wow.com";
}

DashboardAssistant.prototype.setup = function() {
	this.services = new Services();
	this.load();
};

DashboardAssistant.prototype.activate = function(event) {
	this.handleIcon = this.handleIcon.bindAsEventListener(this);
	this.handleTitle = this.handleTitle.bindAsEventListener(this);
	this.controller.listen("iconbox", Mojo.Event.tap, this.handleIcon);
	this.controller.listen("titlebox", Mojo.Event.tap, this.handleTitle);
};

DashboardAssistant.prototype.load = function() {
	if(this.titles.length==1) {
		this.controller.get("newCircle").style.visibility="hidden";
	} else {
		this.controller.get("newCircle").style.visibility="visible";
		this.controller.get("articleCount").innerText = this.titles.length;
	}
	this.controller.get("currTitle").innerText = this.titles[0];
};

DashboardAssistant.prototype.updateDashboard = function(urls, titles) {
	this.urls = urls;
	this.titles = titles;
	this.load();
};

/*DashboardAssistant.prototype.handleNext = function(event){
	this.currIndex++;
	if(this.currIndex==this.titles.length) {
		this.currIndex = 0;
	}
	this.controller.get("currTitle").innerText = this.titles[this.currIndex];
};*/

DashboardAssistant.prototype.handleIcon = function(urls, titles) {
	Preferences.set("lastReadGUID", this.urls[0]);
	this.controller.window.close();
	this.services.openWebpage(this.baseURL);
};

DashboardAssistant.prototype.handleTitle = function(urls, titles) {
	Preferences.set("lastReadGUID", this.urls[0]);
	this.controller.window.close();
	this.services.openWebpage(this.urls[this.currIndex]);
};

DashboardAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("iconbox", Mojo.Event.tap, this.handleIcon);
	this.controller.stopListening("titlebox", Mojo.Event.tap, this.handleTitle);
};

DashboardAssistant.prototype.cleanup = function(event) {
	Preferences.set("lastReadGUID", this.urls[0]);
	this.services.cleanup();
};
