enyo.kind({
	name: "Guide",
	kind: enyo.VFlexBox,
	components: [
		{kind: "AppMenu", components: [
		  	{caption: "About", onclick: "showAbout"}
		]},
		{kind:"AboutDialog"},
		{kind: "PageHeader", pack:"center", align:"center", width:"100%", components: [
			{flex:1},
			{kind: "HFlexBox", pack:"center", align:"center", style:"margin-top:-20px; margin-bottom:-20px; font-size:1.1em", components:[
				{kind:"Image", src:"images/icons/icon.png", style:"margin-top:6px; margin-right:5px; margin-left:30px"},
				{name:"title", content:" WOSQI Homebrew Guide", style:"font-weight:normal;"},
			]},
			{flex:1},
			{kind: "Button", caption: "Back", onclick:"backHandler"}
		]},
		{kind: "SlidingPane", flex: 1, components: [
			{name: "leftView", width: "260px", fixedWidth:true, dragAnywhere:false, edgeDragging:false, components: [
				{kind: "Topics", flex:1, onChangePaneRequest:"changePane", onChooseOS:"updateOSChoice"},
				{kind: "Toolbar", className: "enyo-toolbar-light"}
			]},
			{name:"mainView", flex:1, dragAnywhere:false, edgeDragging:true, components:[
				{name:"innerPane", kind: "Pane", flex: 1, components: [
					{name:"empty", kind:"Empty", flex:1, onChangePaneRequest:"changePane"},
					{kind:"Homebrew", flex:1},
					{kind:"Settingup", flex:1},
					{kind:"Installing", flex:1},
					{kind:"Local", flex:1},
					{kind:"Advanced", flex:1},
					{kind:"Troubleshooter", flex:1, onChangePaneRequest:"changePane"},
					{kind:"Problem1", flex:1, onChangePaneRequest:"changePane"},
					{kind:"Problem2", flex:1, onChangePaneRequest:"changePane"},
					{kind:"Problem3", flex:1, onChangePaneRequest:"changePane"},
					{kind:"Problem4", flex:1, onChangePaneRequest:"changePane"},
					{kind:"Problem5", flex:1, onChangePaneRequest:"changePane"},
					{kind:"Other", flex:1}
				]},
				{kind: "Toolbar", className: "enyo-toolbar-light", components:[
					{kind:"GrabButton", onclick:"toggleInnerPane"}
				]}
			]}
		]}
	],
	create: function() {
		this.inherited(arguments);
		this.os = "Windows";
		this.$.innerPane.selectView(this.$.empty);
		this.$.slidingPane.selectViewImmediate(this.$.leftView);
		this.$.button.applyStyle("visibility", "hidden");
	},
	showAbout: function(inSender, inEvent) {
		this.$.aboutDialog.openAtCenter();
	},
	backHandler: function(inSender, inEvent) {
		if(this.$.innerPane.getViewName()!="empty") {
			this.$.innerPane.back(inEvent);
			if(this.$.innerPane.getViewName()=="empty") {
				this.$.button.applyStyle("visibility", "hidden");
			}
		}
	},
	updateOSChoice: function(inSender, inChoice) {
		this.os = inChoice;
	},
	changePane: function(inSender, inName) {
		this.$.button.applyStyle("visibility", "visible");
		this.$.innerPane.selectView(this.$[inName]);
		if(inName.startsWith("problem")) {
			this.$[inName].setSystem(this.os);
		}
	},
	toggleInnerPane: function(inSender, inEvent) {
		if(this.$.slidingPane.getViewName()=="leftView") { //collapse left
			this.$.slidingPane.selectViewByName("mainView");
		} else { //expand left
			this.$.slidingPane.selectViewByName("leftView");
		}
	}
});
