enyo.kind({
	name: "ScrimSpinner",
	kind: "Scrim",
	events: {
		onCancel:""
	},
	components: [
		{kind:"VFlexBox", flex:1, pack:"center", align:"center", style:"height:100%; vertical-align:middle; width:100%;", components:[
			{name: "spinner", kind: "SpinnerLarge", style:"margin-left:auto; margin-right:auto;"},
			{name: "cancelOption", kind:"VFlexBox", width:"150px",  style:"opacity:0.7", showing:false, components:[
				{height:"100px"},
				{kind: "Button", caption: "Cancel", className: "enyo-button-dark", onclick:"closeClick"}
			]}
			
		]}
	],
	create: function() {
		this.inherited(arguments);
		this.showAtZIndex(1000);
	},
	show: function() {
		this.$.spinner.show();
		this.inherited(arguments);
	},
	showWithCancel: function() {
		this.show();
		this.$.cancelOption.setShowing(true);
	},
	hide: function() {
		this.inherited(arguments);
		this.$.spinner.hide();
		this.$.cancelOption.hide();
	},
	closeClick: function(inSender, inEvent) {
		this.doCancel();
	}
});