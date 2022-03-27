function YoutubeAssistant() {
	this.appInfo = Mojo.Controller.appInfo;
	this.url = "http://gdata.youtube.com/feeds/api/playlists/9E4F100260CDAFBC?alt=json&v=2";
}

YoutubeAssistant.prototype.setup = function() {
	var menuAttr = {omitDefaultItems: true};
  	var menuModel = {
    	visible: true,
    	items: [
      		{label: $L("Back"), command: 'close'}
    	]
  	};
	this.controller.setupWidget(Mojo.Menu.appMenu, menuAttr, menuModel);
	this.controller.get("app-support-info-app-icon").style["background-image"] =
			"url(" + this.appInfo.smallicon + ")";
	this.controller.get("tipz-title").innerText = $L("Tutorial Videos");
	this.listAttrs = {
		listTemplate:'youtube/listContainer', 
		itemTemplate:'youtube/listItem',
		swipeToDelete:false,
		autoconfirmDelete:false,
		hasNoWidgets:true
	};
	this.listModel = {
		listTitle: $L("Internalz Tipz"),
		items: [
			{label: $L("Loading...")}
		]
	};
	this.controller.setupWidget("youtube-list", this.listAttrs, this.listModel);
	this.controller.get("copyright").innerHTML = $L(this.appInfo.copyright);
	this.request = new Ajax.Request(this.url, {
		method:"GET",
		evalJSON: "force",
		onSuccess: function(response) {
			var entries = response.responseJSON.feed.entry;
			for(var i=1; i<entries.length; i++) {
				var name = entries[i].media$group.media$title.$t
						.replace("Internalz Pro Tipz ", "");
				this.listModel.items[i-1] = {
					label: $L(name),
					thumb: entries[i].media$group.media$thumbnail[0].url,
					id: entries[i].media$group.yt$videoid.$t
				};
			}
			this.controller.modelChanged(this.listModel);
		}.bind(this)
	});
};

YoutubeAssistant.prototype.activate = function(event) {
	this.handleTap = this.handleTap.bindAsEventListener(this);
	this.controller.listen("youtube-list", Mojo.Event.listTap, this.handleTap);
};

YoutubeAssistant.prototype.handleTap = function(event) {
	this.controller.serviceRequest("palm://com.palm.applicationManager", {
		method: "open",
		parameters: {
			id: "com.palm.app.youtube",
			params: {
				target: "http://www.youtube.com/watch?v=" + event.item.id,
				direct: true
			}
		}
	});
};

YoutubeAssistant.prototype.handleCommand = function(event) {
	if(event.type == Mojo.Event.command) {
		if(event.command == "close") {
			this.controller.stageController.popScene();
		}
	}
};

YoutubeAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("youtube-list", Mojo.Event.listTap, this.handleTap);
};

YoutubeAssistant.prototype.cleanup = function(event) {
};
