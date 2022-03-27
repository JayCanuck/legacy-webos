enyo.kind({
	name: "ThumbConfirmDialog",
	kind: "ModalDialog",
	events: {
		onConfirm: "",
		onCancel: "",
	},
	width: "350px",
	components: [
		{content: "Important Notice", className: "enyo-dialog-prompt-title"},
		{className: "enyo-dialog-prompt-content", components: [
			{content:"This feature is considered experimental and uses a fair amount of memory and processing to generate the thumbnails. It performs best when folders contain less than 50 comics each.<br><br>Are you sure you want to enable this feature?", allowHtml:true, className: "enyo-dialog-prompt-message"},
			{height:"10px"},
			{kind: "Button", caption: "OK", onclick: "okClick"},
			{height:"5px"},
			{kind: "Button", caption: "Cancel", onclick: "cancelClick"}
		]}
	],
	okClick: function(inSender, inEvent) {
		this.close();
		this.doConfirm();
	},
	cancelClick: function(inSender, inEvent) {
		this.close();
		this.doCancel();
	}
});