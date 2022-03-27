function YoutubeAssistant() {
	this.appInfo = Mojo.Controller.appInfo;
	this.url = "http://gdata.youtube.com/feeds/api/playlists/546CD4E59BAFF09B?alt=json&v=2";
}

YoutubeAssistant.prototype.setup = function() {
	this.controller.setupWidget(Mojo.Menu.appMenu, {omitDefaultItems: true}, {
			visible: true,
			items: [ Mojo.Menu.editItem ]
		}
	);
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
	this.controller.get("copyright").innerHTML = this.appInfo.copyright;
	this.request = new Ajax.Request(this.url, {
		method:"GET",
		evalJSON: "force",
		onSuccess: function(response) {
			var entries = response.responseJSON.feed.entry;
			for(var i=0; i<entries.length; i++) {
				var name = entries[i].media$group.media$title.$t
						.replace("Internalz Tipz ", "");
				this.listModel.items[i] = {
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

YoutubeAssistant.prototype.deactivate = function(event) {
	this.controller.stopListening("youtube-list", Mojo.Event.listTap, this.handleTap);
};

YoutubeAssistant.prototype.cleanup = function(event) {
};
