enyo.kind({
	name: "Topics",
	kind: enyo.VFlexBox,
	events: {
		onChangePaneRequest:"",
		onChooseOS:""
	},
	components: [
		{kind: "ChooserDialog", onChoose:"chooseOS"},
		{kind: "VirtualList", flex: 1, onSetupRow: "setupRow", components: [
			{kind: "RowItem", width:"100%", layoutKind: "HFlexLayout", tapHighlight: true, onclick:"rowClick", align:"center", components: [
				{name: "icon", kind: "Image", style:"margin-right:5px; margin-top:6px;"},
				{name: "label", align:"center", style:"margin-right:25px"}
			]}
		]}
	],
	listData: [
		{icon:"images/icons/info.png", label:"What is homebrew?", pane:"homebrew"},
		{icon:"images/icons/setup.png", label:"Setting up WOSQI", pane:"settingup"},
		{icon:"images/icons/installing.png", label:"Installing homebrew packages", pane:"installing"},
		{icon:"images/icons/local.png", label:"Installing .ipk and .patch files", pane:"local"},
		{icon:"images/icons/advanced.png", label:"Advanced features", pane:"advanced"},
		{icon:"images/icons/troubleshoot.png", label:"Problem troubleshooter", pane:"troubleshooter"},
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
        	this.$.icon.setSrc(item.icon);
        	this.$.label.setContent(item.label);
        	return true;
    	}
	},
	rowClick: function(inSender, inEvent) {
  		if(inEvent.rowIndex==this.listData.length-1) {
			this.$.chooserDialog.openAtCenter();
		} else {
			this.doChangePaneRequest(this.listData[inEvent.rowIndex].pane);
		}
	},
	chooseOS: function(inSender, inChoice) {
		this.doChooseOS(inChoice);
		this.doChangePaneRequest(this.listData[this.listData.length-1].pane);
	}
});
