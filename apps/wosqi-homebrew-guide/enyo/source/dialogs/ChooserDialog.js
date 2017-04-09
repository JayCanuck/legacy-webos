enyo.kind({
	name: "ChooserDialog",
	kind: "ModalDialog",
	scrim: true,
	events: {
		onChoose:""
	},
	width:"350px",
	components: [
		{name: "title", content: "What's your computer OS?", className: "enyo-dialog-prompt-title"},
		{className: "enyo-dialog-prompt-content", components: [
			{className: "enyo-dialog-prompt-message", components:[
				{kind: "RadioGroup", onChange:"radioButtonSelected", components: [
				    {caption: "Windows"},
				    {caption: "Mac"},
				    {caption: "Ubuntu"}
				]}
			]},
			{name: "okButton", kind: "Button", caption: enyo._$L("OK"), onclick: "okClick"}
		]}
	],
	radioButtonSelected: function(inSender, inEvent) {
		var value = inSender.getValue();
		if(value==0) {
			this.choice = "Windows";
		} else if(value==1) {
			this.choice = "Mac";
		} else {
			this.choice = "Ubuntu";
		}
	},
	okClick: function(inSender, inEvent) {
		this.doChoose(this.choice || "Windows")
		this.close();
	}
});