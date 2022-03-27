enyo.kind({
	name: "SettingsDialog",
	kind: "ModalDialog",
	scrim: true,
	dismissWithClick: true,
	width: "425px",
	events: {
		onReloadList:"",
		onShelfChange:""
	},
	components: [
		{kind: "ComicService", method: "clearThumbCache", onSuccess:"deleteCacheSuccess"},
		{kind: "ThumbConfirmDialog", onConfirm:"thumbConfirm", onCancel:"thumbCancel"},
		{content: "Settings", style:"padding-left:20px", className: "enyo-dialog-prompt-title", align:"center", pack:"center"},
		{className: "enyo-dialog-prompt-content", components: [
			{className: "enyo-dialog-prompt-message", components:[
				{align:"center", tapHighlight:false, layoutKind:"HFlexLayout", components:[
					{content:"Comics in fullscreen", flex:1},
					{name:"fullscreenToggle", kind:"ToggleButton", onChange:"fullscreenChange"}
				]},
				{align:"center", tapHighlight:false, layoutKind:"HFlexLayout", components:[
					{content:"Tapping left/right 20% of viewer changes page", flex:1},
					{name:"edgetapToggle", kind:"ToggleButton", onChange:"edgetapChange"}
				]},
				{align:"center", tapHighlight:false, layoutKind:"HFlexLayout", components:[
					{content:"Black background in viewer", flex:1},
					{name:"blackbgToggle", kind:"ToggleButton", onChange:"blackbgChange"}
				]},
				{align:"center", tapHighlight:false, layoutKind:"HFlexLayout", style:"margin-top:1px; margin-bottom:1px;", components:[
					{content:"Viewing mode", flex:1},
					{name:"viewmodeSelector", kind:"ListSelector", onChange:"viewmodeChange", items: [
				        {caption: "Auto-fit", value: "auto"},
				        {caption: "Fit-by-width", value: "width"},
				        {caption: "Fit-by-height", value: "height"},
				    ]},
				]},
				/*{align:"center", tapHighlight:false, layoutKind:"HFlexLayout", components:[
					{content:"Retain zoom level between pages", flex:1},
					{name:"zoomlockToggle", kind:"ToggleButton", onChange:"zoomlockChange"}
				]},*/
				{align:"center", tapHighlight:false, layoutKind:"HFlexLayout", components:[
					{content:"Auto-resume when app is closed with comic left open", flex:1},
					{name:"autoresumeToggle", kind:"ToggleButton", onChange:"autoresumeChange"}
				]},
				{align:"center", tapHighlight:false, layoutKind:"HFlexLayout", components:[
					{content:"Unread/read system", flex:1},
					{name:"unreadToggle", kind:"ToggleButton", onChange:"unreadChange"}
				]},
				{align:"center", tapHighlight:false, layoutKind:"HFlexLayout", components:[
					{content:"Thumbnail shelf view", flex:1},
					{name:"shelfToggle", kind:"ToggleButton", onChange:"shelfChange"}
				]},
			]},
			{name:"thumbSpacer", height:"5px"},
			{name:"thumbButton", kind:"Button", caption:"Clear Thumbnail Cache", className:"enyo-button-negative", onclick:"deleteCache"},
			{height:"10px"},
			{kind:"Button", caption:"Done", className:"enyo-button-affirmative", onclick:"okClick"}
		]}
	],
	create: function() {
		this.inherited(arguments);
	},
	componentsReady: function() {
		this.inherited(arguments);
		this.$.fullscreenToggle.setState(enyo.application.prefs.get("fullScreen"));
		this.$.autoresumeToggle.setState(enyo.application.prefs.get("autoResume")!=undefined);
		this.$.edgetapToggle.setState(enyo.application.prefs.get("edgeTap"));
		this.$.blackbgToggle.setState(enyo.application.prefs.get("blackBackground"));
		this.$.viewmodeSelector.setValue(enyo.application.prefs.get("viewMode"));
		//this.$.zoomlockToggle.setState(enyo.application.prefs.get("zoomLock"));
		this.$.unreadToggle.setState(enyo.application.prefs.get("unreadReadSystem"));
		var shelf = enyo.application.prefs.get("shelfView");
		this.$.shelfToggle.setState(shelf);
		this.$.thumbSpacer.setShowing(shelf);
		this.$.thumbButton.setShowing(shelf);
	},
	fullscreenChange: function(inSender, inState) {
		enyo.application.prefs.set("fullScreen", inState);
	},
	autoresumeChange: function(inSender, inState) {
		if(inState) {
			enyo.application.prefs.set("autoResume", {});
		} else {
			enyo.application.prefs.set("autoResume", undefined);
		}
	},
	edgetapChange: function(inSender, inState) {
		enyo.application.prefs.set("edgeTap", inState);
	},
	blackbgChange: function(inSender, inState) {
		enyo.application.prefs.set("blackBackground", inState);
	},
	viewmodeChange: function(inSender, inEvent) {
		enyo.application.prefs.set("viewMode", this.$.viewmodeSelector.getValue());
	},
	/*zoomlockChange: function(inSender, inState) {
		enyo.application.prefs.set("zoomLock", inState);
	},*/
	unreadChange: function(inSender, inState) {
		if(!inState) {
			enyo.application.prefs.set("readComics", []);
		}
		enyo.application.prefs.set("unreadReadSystem", inState);
	},
	shelfChange: function(inSender, inState) {
		if(inState) {
			this.$.thumbConfirmDialog.openAtCenter();
		} else {
			this.$.thumbSpacer.setShowing(false);
			this.$.thumbButton.setShowing(false);
			enyo.application.prefs.set("shelfView", false);
			this.doShelfChange();
		}
	},
	thumbConfirm: function(inSender, inEvent) {
		this.$.thumbSpacer.setShowing(true);
		this.$.thumbButton.setShowing(true);
		enyo.application.prefs.set("shelfView", true);
		this.doShelfChange();
	},
	thumbCancel: function(inSender, inEvent) {
		this.$.shelfToggle.setState(false);
	},
	deleteCache: function(inSender, inEvent) {
		this.$.comicService.call({});
	},
	deleteCacheSuccess: function(inSender, inResponse) {
		enyo.application.prefs.set("thumbKeys", {});
		this.doReloadList();
	},
	okClick: function(inSender, inEvent) {
		this.close();
	}
});