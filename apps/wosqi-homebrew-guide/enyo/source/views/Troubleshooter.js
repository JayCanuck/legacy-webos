enyo.kind({
	name: "Troubleshooter",
	kind: enyo.VFlexBox,
	events: {
		onChangePaneRequest:""
	},
	style:"padding: 15px 15px 15px 15px; font-size:0.8em;",
	components: [
		{content:"Problem troubleshooter", className:"topic-title"},
		{content:"Tap the row below that best describes your problem. If none of them apply, select the last row."},
		{nodeTag:"br"},
		{kind:"Scroller", flex:1, autoVertical:true, components:[
			{kind: "VirtualList", onSetupRow:"setupRow", components: [
				{kind: "RowItem", tapHighlight: true, onclick:"rowClick", components: [
					{name:"label", style:"padding-left:50px; font-style:italic; font-size:1.0em;", kind:"HtmlContent"}
				]}
			]}
		]}
	],
	listData: [
		{label:"WOSQI won't start up", pane:"problem1"},
		{label:"Driver installation failed", pane:"problem2"},
		{label:"&quotNo Devices Found&quot error", pane:"problem3"},
		{label:"Feed viewer won't load", pane:"problem4"},
		{label:"Freezes on package install", pane:"problem5"},
		{label:"Other problem...", pane:"other"}
	],
	create: function() {
		this.inherited(arguments);
	},
	rendered: function() {
		this.inherited(arguments);
		this.$.virtualList.$.scroller.setVertical(false);
	},
	setupRow: function(inSender, inIndex) {
		var item = this.listData[inIndex];
    	if(item) {
        	this.$.label.setContent(item.label);
        	return true;
    	}
	},
	rowClick: function(inSender, inEvent) {
		this.doChangePaneRequest(this.listData[inEvent.rowIndex].pane);
	},
});
