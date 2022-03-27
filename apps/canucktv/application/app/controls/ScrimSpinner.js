enyo.kind({
	name: "ScrimSpinner",
	kind: "Scrim",
	components: [
		{kind:"VFlexBox", flex:1, pack:"center", style:"height:100%; vertical-align:middle; width:100%;", components:[
			{name: "spinner", kind: "SpinnerLarge", style:"margin-left:auto; margin-right:auto;"}
		]}
	],
	create: function() {
		this.inherited(arguments);
		this.showAtZIndex(0);
	},
	show: function() {
		this.$.spinner.show();
		this.inherited(arguments);
	},
	hide: function() {
		this.inherited(arguments);
		this.$.spinner.hide();
	},
	start: function() {
		this.$.spinner.show();
	},
	stop: function() {
		this.$.spinner.hide();
	}
});