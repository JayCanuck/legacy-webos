enyo.kind({
	name: "CanuckTV",
	kind: enyo.VFlexBox,
	components: [
		{kind: "AppMenu", components: [
		  	{caption: "About", onclick: "showAbout"}
		]},
		{kind:"AboutDialog"},
		{name: "slidingPane", kind: "SlidingPane", flex: 1, components: [
			{name: "left", width: "225px", components: [
				{kind: "PageHeader"},//, content: "Canuck TV"
				{name: "list", flex: 1, kind: "VirtualRepeater", onSetupRow: "getListItem", components: [
					{kind: "Item", layoutKind: "VFlexLayout", tapHighlight: true, components: [
						{name: "name"}
					], onclick: "listItemClick"}
				]},
				{kind: "Toolbar"}
			]},
			{name: "main", flex: 1, onResize: "leftHide", components: [
				{name: "backdrop", kind: "Image", className:"mainPaneBackdrop", src:"images/bg.jpg"},
				{name: "spinner", kind:"ScrimSpinner"},
				{kind: "WebView", flex: 1, style: "width:100%; height:100%; vertical-align:middle", onLoadStarted: "loadStarted", onLoadProgress:"loadProgress", onLoadStopped:"loadCompleted", onLoadComplete: "loadCompleted", onError:"loadCompleted"},
				{kind: "Toolbar", components: [
					{kind: "GrabButton"},
					{name: "controlContainer", kind:"HFlexBox", flex: 1, pack:"center", components:[
						{kind: "ToolButtonGroup", className:"controlGroup", components: [
							{name:"backButton", kind:"GroupedToolButton", icon: "images/back.png", style:"padding-top:10px;", onclick: "goBack"},
							{name:"forwardButton", kind:"GroupedToolButton", icon: "images/forward.png", style:"padding-top:10px;", onclick: "goForward"},
							{name:"refreshStopButton", kind:"GroupedToolButton", icon: "images/refresh.png", style:"padding-top:10px;", onclick: "refreshStop"}
			  			]},
					]},
				]}
			]}
		]}
	],
	create: function() {
		this.inherited(arguments);
		this.websites = [
			{name: "CTV", url:"http://watch.ctv.ca"},
			{name: "Space", url:"http://watch.spacecast.com"},
			{name: "Comedy Network", url:"http://watch.thecomedynetwork.ca"},
			{name: "TSN", url:"http://watch.tsn.ca"},
			{name: "MuchMusic", url:"http://watch.muchmusic.com"},
			{name: "Discovery Channel", url:"http://watch.discoverychannel.ca"},
			{name: "Bravo! Canada", url:"http://watch.bravo.ca"},
			{name: "Business News Network", url:"http://watch.bnn.ca"}
		];
		this.isLoading = false;
		this.$.controlContainer.hide();
		this.$.spinner.hide();
		this.$.list.render();
	},
	showAbout: function(inSender, inEvent) {
		this.$.aboutDialog.openAtCenter();
	},
	getListItem: function(inSender, inIndex) {
		var r = this.websites[inIndex];
		if(r) {
			this.$.name.setContent(r.name);
			return true;
		}
	},
	listItemClick: function(inSender, inEvent) {
		//this.$.backdrop.hide();
		this.$.controlContainer.show();
		this.$.webView.setUrl(this.websites[inEvent.rowIndex].url);
		this.loadStarted();
	},
	loadStarted: function(inSender, inEvent) {
		this.isLoading = true;
		this.$.spinner.show();
		this.$.refreshStopButton.setIcon("images/stop.png");
	},
	loadProgress: function(inSender, inEvent) {
		if(inEvent.progress && inEvent.progress==100) {
			this.loadCompleted();
		}
	},
	loadCompleted: function(inSender, inEvent) {
		this.isLoading = false;
		this.$.spinner.hide();
		this.$.refreshStopButton.setIcon("images/refresh.png");
	},
	goBack: function(inSender, inEvent) {
		this.$.webView.goBack();
	},
	goForward: function(inSender, inEvent) {
		this.$.webView.goForward();
	},
	refreshStop: function(inSender, inEvent) {
		if(this.isLoading) {
			this.$.webView.stopLoad();
			this.loadCompleted();
		} else {
			this.$.webView.reloadPage();
			this.loadStarted();
		}
	}
});