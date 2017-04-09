enyo.kind({
	name: "AboutDialog",
	kind: "ModalDialog",
	scrim: true,
	width: "375px",
	components: [
		{kind: "PalmService", service:"palm://com.palm.applicationManager"},
		{className: "enyo-dialog-prompt-title", components: [
			{kind:"HFlexBox", components: [
				{name:"icon", kind:"Image"},
				{name:"title", style:"padding-left:10px"}
			]}
		]},
		{className: "enyo-dialog-prompt-content", components: [
			{className: "enyo-dialog-prompt-message", components:[
				{name: "versionAndVendor", style:"padding-bottom:10px;"},
				{kind: "RowGroup", caption: "Support", components: [
				    {kind: "Item", tapHighlight:true, components: [
				        {content: "Discussion Forums", flex: 1, onclick: "openForums"}
				    ]},
					{kind: "Item", tapHighlight:true, components: [
				        {content: "PreCentral", flex: 1, onclick: "openPC"}
				    ]},
					{kind: "Item", tapHighlight:true, components: [
				        {content: "webOSroundup", flex: 1, onclick: "openWOR"}
				    ]},
					{kind: "Item", tapHighlight:true, components: [
				        {content: "Donate", flex: 1, onclick: "openDonate"}
				    ]}
				]},
				{name: "copyright", style: "font-size:smaller;", allowHtml:true},
			]},
			{name: "okButton", kind: "Button", caption: enyo._$L("OK"), onclick: "okClick"}
		]}
	],
	create: function() {
		this.appInfo = enyo.fetchAppInfo();
		this.inherited(arguments);
		this.validateComponents();
	},
	openAtCenter: function() {
		this.inherited(arguments);
		this.$.icon.setSrc(this.appInfo.smallicon);
		this.$.title.setContent(this.appInfo.title);
		this.$.versionAndVendor.setContent(this.appInfo.version + " by " + this.appInfo.vendor);
		this.$.copyright.setContent(this.appInfo.copyright);
	},
	openForums: function(inSender, inEvent) {
		this.$.palmService.call({
			target: this.appInfo.support.resources[0].url
		}, {
			method: "open"
		});
	},
	openPC: function(inSender, inEvent) {
		this.$.palmService.call({
			target: this.appInfo.support.resources[1].url
		}, {
			method: "open"
		});
	},
	openWOR: function(inSender, inEvent) {
		this.$.palmService.call({
			target: this.appInfo.support.resources[2].url
		}, {
			method: "open"
		});
	},
	openDonate: function(inSender, inEvent) {
		this.$.palmService.call({
			target: this.appInfo.support.resources[3].url
		}, {
			method: "open"
		});
	},
	okClick: function(inSender, inEvent) {
		this.close();
	}
});