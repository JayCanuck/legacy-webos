enyo.kind({
	name: "SimpleDialog",
	kind: "ModalDialog",
	statics: {
		INFO: $L("Information"),
		ERROR: $L("Error")
	},
	published: {
		type: ""
	},
	components: [
		{name: "title", content: "", className: "enyo-dialog-prompt-title"},
		{className: "enyo-dialog-prompt-content", components: [
			{name: "message", content:"", className: "enyo-dialog-prompt-message"},
			{name: "okButton", kind: "Button", caption: enyo._$L("OK"), onclick: "okClick"}
		]}
	],
	create: function() {
		this.inherited(arguments);
		this.validateComponents();
	},
	openAtCenter: function(message, title) {
		this.inherited(arguments);
		this.$.message.setContent(message);
		if(title) {
			this.$.title.setContent(title);
		} else {
			if(this.type=="") {
				this.type = SimpleDialog.INFO
			}
			this.$.title.setContent(this.type);
		}
	},
	okClick: function(inSender, inEvent) {
		this.close();
	}
});