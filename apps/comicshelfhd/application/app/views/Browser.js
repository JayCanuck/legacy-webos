enyo.kind({
	name: "Browser",
	kind: enyo.VFlexBox,
	style:"background-color:black;",//#393e43;
	published: {
		params: {}
	},
	events: {
		onOpenComic: "",
	},
	components: [
		{name:"appMenuExtended", kind: "AppMenu", automatic:false, components: [
			{name:"markRead", caption: "Mark All Read", onclick: "markAllRead"},
			{name:"markUnread", caption: "Mark All Unread", onclick: "markAllUnread"},
			{caption: "Settings", onclick: "showSettings"},
		  	{caption: "About", onclick: "showAbout"}
		]},
		{name:"appMenu", kind: "AppMenu", automatic:false, components: [
			{caption: "Settings", onclick: "showSettings"},
		  	{caption: "About", onclick: "showAbout"}
		]},
		{name:"thumbSetter", kind: "ComicService", method:"setThumb", onSuccess:"setThumbSuccess", onFailure:"setThumbFailure"},
		{name:"listService", kind: "ComicService", method: "list", onSuccess: "listSuccess", onFailure: "listFailure"},
		{name:"thumbRemoveService", kind: "ComicService", method: "removeThumbs"},
		{name:"coverService", kind: "ComicService", method: "fetchCovers", onSuccess: "coverSuccess", onFailure: "coverFailure", subscribe:true, resubscribe:true},
		{kind:"UsageDialog"},
		{kind:"ChangeLogDialog"},
		{kind:"SettingsDialog", onReloadList:"reloadList", onShelfChange:"shelfPrefChange"},
		{kind:"AboutDialog"},
		{kind:"SimpleDialog"},
		{kind:"ErrorDialog"},
		{kind: enyo.ApplicationEvents, onUnload: "windowClosing"},
		{kind: "PageHeader", className: "enyo-header-dark", style:"position:static !important;nackground-color:black", pack:"center", align:"center", components:[
			{kind: "Image", src: "images/header.png"},
			{kind: "RadioGroup", onChange:"changeView", value:0,  style:"position:absolute; right:35px; top:15px;", components: [
				{kind: "RadioButton", icon: "images/grid.png", value: 1, className:"enyo-radiobutton-dark"},
				{kind: "RadioButton", icon: "images/list.png", value: 2, className:"enyo-radiobutton-dark"},
			]}
		]},
		{name:"mainScroller", kind:"Scroller", flex:1, style:"width:100%; height:100%; background-color:black;", autoVertical:true, horizontal:false, autoHorizontal:false, components:[
			{name:"mainContainer", flex:1, style:"width:100%; height:100%; background-repeat:repeat-y; background-size: 100%", components:[
				{name:"mainSpinner", kind:"ScrimSpinner"},
				{name:"noneFound", components:[
					{style:"width:100%; height:29px; background:url(images/shelf/shadow.png) repeat-x"},
					{style:"color:lightgray; width:100%; text-align:center; margin-top:100px;", content:"Nothing found in the comics directory"}
				]},
				{kind:"Pane", flex:1,components:[
					{name:"grid", flex:1, components:[
						{name: "gridRepeater", kind: "VirtualRepeater", onSetupRow: "gridSetupRow", components: [
			        		{name:"gridRowItem", kind:"HFlexBox", style:"padding:0 0 0 0; margin:0 0 0 0;", components: [
								{kind:"VFlexBox", className:"shelf-left", components:[
									{className:"shelf-top-left"},
									{flex:1},
									{className:"shelf-bottom-left"}
								]},
								{kind:"VFlexBox", flex:1, components:[
									{className:"shelf-top"},
									{kind:"HFlexBox", className:"shelf-center", components:[
										{flex:1},
										{name:"container-1", kind:"ThumbFlexBox", align:"center", pack:"center", style:"width:100px; height:120px;", onclick:"gridItemTap", components:[
											{flex:1},
											{name:"thumbnail-1", kind:"Image", src:"images/shelf/loading-thumb.png", className:"thumbImage", style:"margin: auto auto auto auto;"},
											{flex:1},
											//{name:"label-1", className:"thumbnail-label", content:""}
										]},
										{flex:1},
										{name:"container-2", kind:"ThumbFlexBox", align:"center", pack:"center", style:"width:100px; height:120px;", onclick:"gridItemTap", components:[
											{flex:1},
											{name:"thumbnail-2", kind:"Image", src:"images/shelf/loading-thumb.png", className:"thumbImage", style:"margin: auto auto auto auto;"},
											{flex:1},
											//{name:"label-2", className:"thumbnail-label", content:""}
										]},
										{flex:1},
										{name:"container-3", kind:"ThumbFlexBox", align:"center", pack:"center", style:"width:100px; height:120px;", onclick:"gridItemTap", components:[
											{flex:1},
											{name:"thumbnail-3", kind:"Image", src:"images/shelf/loading-thumb.png", className:"thumbImage", style:"margin: auto auto auto auto;"},
											{flex:1},
											//{name:"label-3", className:"thumbnail-label", content:""}
										]},
										{flex:1},
										{name:"container-4", kind:"ThumbFlexBox", align:"center", pack:"center", style:"width:100px; height:120px;", onclick:"gridItemTap", components:[
											{flex:1},
											{name:"thumbnail-4", kind:"Image", src:"images/shelf/loading-thumb.png", className:"thumbImage", style:"margin: auto auto auto auto;"},
											{flex:1},
											//{name:"label-4", className:"thumbnail-label", content:""}
										]},
										{flex:1},
										{name:"container-5", kind:"ThumbFlexBox", align:"center", pack:"center", style:"width:100px; height:120px;", onclick:"gridItemTap", components:[
											{flex:1},
											{name:"thumbnail-5", kind:"Image", src:"images/shelf/loading-thumb.png", className:"thumbImage", style:"margin: auto auto auto auto;"},
											{flex:1},
											//{name:"label-5", className:"thumbnail-label", content:""}
										]},
										{flex:1},									
									]},
									{kind:"HFlexBox", className:"shelf-bottom", components:[
										{flex:1},
										{name:"labelbox-1", kind:"ThumbFlexBox", align:"center", pack:"start", style:"width:100px;", onclick:"gridItemTap", components:[
											{name:"label-1", className:"thumbnail-label", content:""}
										]},
										{flex:1},
										{name:"labelbox-2",kind:"ThumbFlexBox", align:"center", pack:"start", style:"width:100px;", onclick:"gridItemTap", components:[
											{name:"label-2", className:"thumbnail-label", content:""}
										]},
										{flex:1},
										{name:"labelbox-3",kind:"ThumbFlexBox", align:"center", pack:"start", style:"width:100px;", onclick:"gridItemTap", components:[
											{name:"label-3", className:"thumbnail-label", content:""}
										]},
										{flex:1},
										{name:"labelbox-4",kind:"ThumbFlexBox", align:"center", pack:"start", style:"width:100px;", onclick:"gridItemTap", components:[
											{name:"label-4", className:"thumbnail-label", content:""}
										]},
										{flex:1},
										{name:"labelbox-5",kind:"ThumbFlexBox", align:"center", pack:"start", style:"width:100px;", onclick:"gridItemTap", components:[
											{name:"label-5", className:"thumbnail-label", content:""}
										]},
										{flex:1}
									]},
								]},
								{kind:"VFlexBox", className:"shelf-right", components:[
									{className:"shelf-top-right"},
									{flex:1},
									{className:"shelf-bottom-right"}
								]}
							]}
						]},
						{style:"width:100%; height:29px; background:url(images/shelf/shadow.png) repeat-x"}
					]},
					{name:"list", flex:1, components:[
						{style:"width:100%; height:29px; background:url(images/shelf/footer2.png) repeat-x"},
						{name:"listScroller", kind:"Scroller", autoVertical:true, autoHorizontal: false, horizontal:false, components:[
							{name: "listRepeater", style:"width:70%; margin-left:auto; margin-right:auto;", kind: "VirtualRepeater", flex: 1, onSetupRow: "listSetupRow", components: [
				        		{kind: "Item", onclick: "listItemTap", layoutKind: "HFlexLayout", style:"padding:0 0 0 0; margin:0 0 0 0;", className:"fileItem", tapHighlight: true, components: [
									{name:"icon", layoutKind: "VFlexLayout", style:"width:48px; height:48px; margin-left:10px; margin-right:10px;"},
									{name:"name", layoutKind: "VFlexLayout", className:"nameLabel"}
								]}
			    			]}
						]}
					]}
				]}
			]}
		]}
	],
	create: function() {
		this.inherited(arguments);
		this.isFirstUse = (localStorage.getItem("comicDir")==undefined);
		this.path = enyo.application.prefs.get("comicDir");
		this.$.noneFound.hide();
		this.startPane = enyo.application.prefs.get("startView");
		this.thumbKeys = enyo.application.prefs.get("thumbKeys");
		if(this.startPane == "grid" && enyo.application.prefs.get("shelfView")) {
			this.$.pane.selectViewByName("grid");
			this.$.radioGroup.setValue(1);
		} else {
			this.$.pane.selectViewByName("list");
			this.$.radioGroup.setValue(2);
		}
		this.$.radioGroup.setShowing(enyo.application.prefs.get("shelfView"));
		this.thumbWidth = 90;
		this.thumbHeight = 120;
		this.thumbsPerRow = 5;
		this.readComics = enyo.application.prefs.get("readComics");
		this.items = [];
		this.removedCovers = [];
		this.rowCount = 0;
		this.resizeHandler();
		this.$.mainSpinner.show();
		this.loadList();
	},
	rendered: function() {
		this.inherited(arguments);
		if(!this.doneCheck) {
			this.doneCheck = true;
			var version = enyo.fetchAppInfo().version;
			var verStored = enyo.application.prefs.get("version");
			if(this.isFirstUse) {
				this.$.usageDialog.openAtCenter();
			} else if(!this.isFirstUse && verStored!=version) {
				enyo.application.prefs.set("version", version);
				this.$.changeLogDialog.openAtCenter();
			}
		}
	},
	resizeHandler: function() {
		if(window.innerWidth==768) { //portrait
			this.$.mainContainer.applyStyle("background-image", "url(images/shelf/bg-768.jpg)");
		} else if(window.innerWidth==1024) { //portrait
			this.$.mainContainer.applyStyle("background-image", "url(images/shelf/bg-1024.jpg)");
		} else {
			this.$.mainContainer.applyStyle("background-image", null);
		}
		this.$.listScroller.applyStyle("height", (window.innerHeight-115) + "px");
		
	},
	windowClosing: function() {
		this.$.coverService.cancelCall("coverFetchRequest");
	},
	shelfPrefChange: function(inSender, inEvent) {
		this.$.radioGroup.setShowing(enyo.application.prefs.get("shelfView"));
		var paneName = enyo.application.prefs.get("startView");
		if(paneName == "grid" && enyo.application.prefs.get("shelfView")) {
			this.$.pane.selectViewByName("grid");
			this.$.radioGroup.setValue(1);
		} else {
			this.$.pane.selectViewByName("list");
			this.$.radioGroup.setValue(2);
		}
		this.loadList();
	},
	changeView: function(inSender, inEvent) {
		var value = inSender.getValue();
		if(value == 1) {
			enyo.application.prefs.set("startView", "grid");
			this.$.pane.selectViewByName("grid");
			this.$.gridRepeater.render();
		} else if(value == 2) {
			enyo.application.prefs.set("startView", "list");
			this.$.pane.selectViewByName("list");
			this.$.listRepeater.render();
		}
		this.$.mainScroller.scrollIntoView(0, 0);
		this.$.listScroller.scrollIntoView(0, 0);
	},
	markAllRead: function() {
		//enyo.error(enyo.json.stringify(this.items));
		for(var i=0; i<this.items.length; i++) {
			this.setRead(i, true);
		}
		this.rerenderRepeaters();
	},
	markAllUnread: function() {
		//enyo.error(enyo.json.stringify(this.items));
		for(var i=0; i<this.items.length; i++) {
			this.setRead(i, false);
		}
		this.rerenderRepeaters();
	},
	loadList: function() {
		this.isLoaded = true;
		this.$.coverService.cancelCall("coverFetchRequest");
		this.$.listService.call({path:this.path});
	},	
	listSuccess: function(inSender, inResponse) {
		this.items = [];
		if(!this.path.endsWith("/")) {
			this.path += "/";
		}
		if(this.path!="/media/internal/comics/") {
			this.items.push({name:"..", type:"upDir"});
		}
		if(enyo.application.prefs.get("unreadReadSystem")) {
			this.readComics = enyo.application.prefs.get("readComics");
			this.updateRead(inResponse.files);
		}
		this.items = this.items.concat(inResponse.dirs, inResponse.files);
		this.$.mainSpinner.hide();
		if(this.items.length==0) {
			this.$.pane.hide();
			this.$.noneFound.show();
		}
		this.rowCount = Math.ceil(this.items.length / this.thumbsPerRow);
		if(enyo.application.prefs.get("shelfView")) {
			this.currThumbKey = this.getDirThumbKey(this.path);
			this.missingCovers = [];
			enyo.application.prefs.set("thumbKeys", this.thumbKeys);
			this.updateDirThumbKey();
			this.$.thumbRemoveService.call({paths:this.removedCovers});
			this.$.gridRepeater.render();
		}
		this.$.listRepeater.render();
		this.$.mainScroller.scrollIntoView(0, 0);
		this.$.listScroller.scrollIntoView(0, 0);
		enyo.application.prefs.set("thumbKeys", this.thumbKeys);
		if(enyo.application.prefs.get("shelfView")) {
			this.$.coverService.call({comics:this.missingCovers, subscribe:true}, {name:"coverFetchRequest"});
		}
	},
	listFailure: function(inSender, inResponse) {
		this.$.mainSpinner.hide();
		if(this.path!="/media/internal/comics/") {
			enyo.application.prefs.set("thumbs-" + this.path);
			this.path = "/media/internal/comics/";
			this.loadList();
		} else {
			this.$.errorDialog.openAtCenter(inResponse.errorText);
		}
	},
	getDirThumbKey: function(path) {
		var tokens = path.replace("/media/internal/comics", "").split("/");
		var currNode = this.thumbKeys;
		for(var i=0; i<tokens.length; i++) {
			if(tokens[i].length>0) {
				if(!currNode[tokens[i] + "/"]) {
					currNode[tokens[i] + "/"] = {};
				}
				currNode = currNode[tokens[i] + "/"];
			}
		}
		return currNode;
	},
	updateDirThumbKey: function() {
		var currItems = [];
		for(var i=0; i<this.items.length; i++) {
			if(this.items[i].name!="..") {
				var name = this.items[i].filename;
				if(!this.currThumbKey[name]) {
					if(this.items[i].type=="dir") {
						this.currThumbKey[name] = {};
					} else {
						this.missingCovers.push(this.items[i].path);
					}
				} else {
					if(this.items[i].type!="dir") {
						if(this.items[i].size!=this.currThumbKey[name].size) {
							this.missingCovers.push(this.items[i].path);
							delete this.currThumbKey[name];
						}
					}
				}
				currItems.push(name);
			}
		}
		for(var x in this.currThumbKey) {
			if(currItems.indexOf(x)==-1) {
				if(x.endsWith("/")) {
					this.removeThumbSubkeys(this.currThumbKey[x]);
					delete this.currThumbKey[x];
				} else {
					this.removedCovers.push(this.currThumbKey[x].path);
					delete this.currThumbKey[x];
				}
				
			}
		}
	},
	removeThumbSubkeys: function(currNode) {
		for(var x in currNode) {
			if(x.endsWith("/")) {
				this.removeThumbSubkeys(currNode[x]);
				delete currNode[x];
			} else {
				this.removedCovers.push(currNode[x].path);
				delete currNode[x];
			}
		}
	},
	setCoverEvent: function(inSender, inEvent) {
		var params = {file:inEvent.thumbnail, comic:inEvent.file};
		var dir = inEvent.file.substring(0, inEvent.file.lastIndexOf("/")+1);
		var name = inEvent.file.substring(inEvent.file.lastIndexOf("/")+1);
		var node = this.getDirThumbKey(dir);
		if(node[name]) {
			params.oldFile = node[name].path;
		}
		this.$.thumbSetter.call(params);
	},
	setThumbSuccess: function(inSender, inResponse) {
		enyo.error(enyo.json.stringify(inResponse));
		var dir = inResponse.comic.substring(0, inResponse.comic.lastIndexOf("/")+1);
		var name = inResponse.comic.substring(inResponse.comic.lastIndexOf("/")+1);
		var node = this.getDirThumbKey(dir);
		node[name] = {path:inResponse.destination, size:inResponse.size};
		enyo.application.prefs.set("thumbKeys", this.thumbKeys);
		this.$.gridRepeater.render();
		this.$.simpleDialog.openAtCenter("Thumbnail set successfully.");
	},
	setThumbFailure: function(inSender, inResponse) {
		enyo.error("Unable to set thumbnail: " + inResponse.errorText);
		this.$.errorDialog.openAtCenter("Unable to set thumbnail");
	},
	coverSuccess: function(inSender, inResponse) {
		if(inResponse.cover) {
			var node = this.getDirThumbKey(inResponse.comic.directory);
			node[inResponse.comic.name] = inResponse.cover;
			var index = this.missingCovers.indexOf(inResponse.comic.directory + inResponse.comic.name);
			if(index>=0) {
				this.missingCovers.splice(index, 1);
			}
			if(this.missingCovers.length==0) {
				this.$.coverService.cancelCall("coverFetchRequest");
			}
			enyo.application.prefs.set("thumbKeys", this.thumbKeys);
			this.$.gridRepeater.render();
		}
		if(inResponse.complete) {
			this.$.coverService.cancelCall("coverFetchRequest");
		}
	},
	coverFailure: function(inSender, inResponse) {
		enyo.error(JSON.stringify(inResponse));
		this.$.gridRepeater.render();
	},
	showSettings: function(inSender, inEvent) {
		this.$.settingsDialog.openAtCenter();
	},
	reloadList: function(inSender, inEvent) {
		this.thumbKeys = enyo.application.prefs.get("thumbKeys");
		this.loadList();
	},
	showAbout: function(inSender, inEvent) {
		this.$.aboutDialog.openAtCenter();
	},
	gridSetupRow: function(inSender, inIndex) {
		if(inIndex>=0 && inIndex<this.rowCount) {
			var row = [{flex:1}];
			for(var i=0; i<this.thumbsPerRow; i++) {
				var currIndex = (inIndex*this.thumbsPerRow) + i;
				var currItem = this.items[currIndex];
				if(currItem) {
					this.$["thumbnail-" + (i+1)].show();
					this.$["label-" + (i+1)].show();
					this.$["container-" + (i+1)].setIndex(currIndex);
					this.$["labelbox-" + (i+1)].setIndex(currIndex);
					if(currItem.name=="..") {
						this.$["label-" + (i+1)].setContent("..");
						this.$["thumbnail-" + (i+1)].setSrc("images/shelf/up-thumb.png");
					} else if(currItem.type=="dir") {
						this.$["label-" + (i+1)].setContent(currItem.name);
						this.$["thumbnail-" + (i+1)].setSrc("images/shelf/dir-thumb.png");
					} else {
						this.$["label-" + (i+1)].setContent(currItem.name);
						if(this.currThumbKey[currItem.filename]) {
							this.$["thumbnail-" + (i+1)].setSrc("/var/luna/data/extractfs" + this.currThumbKey[currItem.filename].path + ":0:0:90:120:3");
						} else {
							this.$["thumbnail-" + (i+1)].setSrc("images/shelf/loading-thumb.png");
						}
						if(enyo.application.prefs.get("unreadReadSystem") && this.isRead(currItem)) {
							this.$["thumbnail-" + (i+1)].applyStyle("opacity", "0.5");
							this.$["label-" + (i+1)].applyStyle("opacity", "0.5");
						} else {
							this.$["thumbnail-" + (i+1)].applyStyle("opacity", null);
							this.$["label-" + (i+1)].applyStyle("opacity", null);
						}
					}
				} else {
					this.$["container-" + (i+1)].setIndex(-1);
					this.$["thumbnail-" + (i+1)].hide();
					this.$["label-" + (i+1)].hide();
				}
			}
			return true;
		}
	},
	listSetupRow: function(inSender, inIndex) {
		if(inIndex>=0 && inIndex<this.items.length) {
			this.$.icon.setClassName(this.items[inIndex].type);
			this.$.name.setContent(this.items[inIndex].name);
			if(enyo.application.prefs.get("unreadReadSystem") && this.isRead(this.items[inIndex])) {
				this.$.icon.applyStyle("opacity", "0.5");
				this.$.name.applyStyle("opacity", "0.5");
			} else {
				this.$.icon.applyStyle("opacity", null);
				this.$.name.applyStyle("opacity", null);
			}
			return true;
		}
	},
	updateRead: function(filesInfolder) {
		if(filesInfolder.length>0) {
			for(var i=0; i<this.readComics.length; i++) {
				if(this.readComics[i].file.startsWith(this.path) &&
						(this.readComics[i].file.replace(this.path, "").lastIndexOf("/")<=0)) {
					var found = false;
					for(var j=0; j<filesInfolder.length; j++) {
						if(filesInfolder[j].path == this.readComics[i].file) {
							found = true;
							if(filesInfolder[j].size != this.readComics[i].size) {
								found = false;
							}
						}
					}
					if(!found) {
						this.readComics.splice(i, 1);
						i--;
					}
				}
			}
			enyo.application.prefs.set("readComics", this.readComics);
		}
	},
	isRead: function(item) {
		var found = false;
		for(var i=0; i<this.readComics.length; i++) {
			if((this.readComics[i].file == item.path) && (this.readComics[i].size == item.size)) {
				found = true;
				break;
			}
		}
		return found;
	},
	setRead: function(index, value) {
		var item = this.items[index];
		//var rowIndex = Math.ceil(index / this.thumbsPerRow);
		//var thumbIndex = index % this.thumbsPerRow;
		//enyo.error("**setRead(" + index + ", " + value + ")**");
		if(item.type!="upDir" && item.type!="dir") {
			if(this.isRead(item)!=value) {
				if(value) {
					this.readComics.push({file:item.path, size:item.size});
				} else {
					//enyo.error("    Removing from read list");
					for(var i=0; i<this.readComics.length; i++) {
						if((this.readComics[i].file == item.path) && (this.readComics[i].size == item.size)) {
							this.readComics.splice(i, 1);
							i--;
						}
					}
				}
				enyo.application.prefs.set("readComics", this.readComics);
			}
		}
		
	},
	rerenderRepeaters: function() {
		if(enyo.application.prefs.get("shelfView") && this.$.pane.getViewName()=="grid") {
			this.$.gridRepeater.render();
		} else {
			this.$.listRepeater.render();
		}
	},
	gridItemTap: function(inSender, inEvent) {
		if(inSender.getIndex()>-1) {
			this.listItemTap(inSender,{rowIndex:inSender.getIndex()});
		}
	},
	listItemTap: function(inSender, inEvent) {
		var tapped = this.items[inEvent.rowIndex];
		if(tapped.name=="..") { //go up a directory level
			if(this.path.endsWith("/")) {
				this.path = this.path.substring(0, this.path.length-1);
			}
			this.path = getFileDir(this.path) + "/";
			if(!this.path.startsWith("/media/internal/comics")) {
				this.path = "/media/internal/comics/";
			}
			enyo.application.prefs.set("comicDir", this.path);
			this.loadList();
		} else if(tapped.type=="dir") { //go into a directory
			if(!this.path.endsWith("/")) {
				this.path += "/";
			}
			this.path += tapped.name + "/";
			enyo.application.prefs.set("comicDir", this.path);
			this.loadList();
		} else { //open file
			this.doOpenComic(tapped.path);
		}
	},
	comicLoaded: function(inSender, inPath) {
		if(enyo.application.prefs.get("unreadReadSystem")) {
			for(var i=0; i<this.items.length; i++) {
				if(this.items[i].path == inPath) {
					this.setRead(i, true);
					this.rerenderRepeaters();
					break;
				}
			}
		}
	},
	closeAsUnread: function(file) {
		for(var i=0; i<this.items.length; i++) {
			if(this.items[i].path == file) {
				this.setRead(i, false);
				this.rerenderRepeaters();
				break;
			}
		}
	},
	getAppMenu: function() {
		var result;
		if(enyo.application.prefs.get("unreadReadSystem")) {
			result =  this.$.appMenuExtended;
		} else {
			result =  this.$.appMenu;
		}
		return result;
	}
});