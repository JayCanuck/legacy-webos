enyo.kind({
	name: "InputDialog",
	kind: "ModalDialog",
	events: {
		onSubmit:""
	},
	components: [
		{name: "title", content: " ", className: "enyo-dialog-prompt-title"},
		{className: "enyo-dialog-prompt-content", components: [
			{name: "message", content:" ", className: "enyo-dialog-prompt-message", style:"margin-left:-8px; margin-right:-8px;"},
			{kind:"Input", onfocus:"focused", onblur:"unfocused", alwaysLooksFocused:true, spellcheck:false, autocorrect:false, autoWordComplete:false, autoCapitalize:"lowercase"},
			{height:"20px"},
			{kind:"HFlexBox", flex:1, style:"margin-bottom:-5px;", components:[
				{name: "okButton", flex:1, kind: "Button", caption: enyo._$L("OK"), onclick: "okClick"},
				{name: "cancelButton", flex:1, kind: "Button", caption: enyo._$L("Cancel"), onclick: "cancelClick"}
			]}
			
		]}
	],
	componentsReady: function() {
		this.inherited(arguments);
		this.inFocus = false;
	},
	openAtCenter: function(message, title) {
		this.inherited(arguments);
		this.$.title.setContent(" " + title);
		this.$.message.setContent(message);
	},
	focused: function() {
		this.inFocus = true;
	},
	keypressHandler: function(inSender, inEvent) {
		if(this.inFocus) {
			if(inEvent.keyCode==13) { //enter key
				enyo.stopEvent(inEvent);
				if(this.validate()) {
					this.doSubmit(this.$.input.getValue().trim());
					this.close();
				}
			} else {
				if(!this.filter(inEvent.keyCode)) {
					enyo.stopEvent(inEvent);
				}
			}
		}
	},
	unfocused: function() {
		this.inFocus = false;
	},
	filter: function(keycode) {
		return (keycode>=32 && keycode<=255 && keycode!=92 && keycode!=47  && keycode!=58
			&& keycode!=42 && keycode!=63 && keycode!=34 && keycode!=60  && keycode!=62
			&& keycode!=124);
	},
	validate: function() {
		return !(this.$.input.getValue().trim().length==0);
	},
	okClick: function(inSender, inEvent) {
		if(this.validate()) {
			this.doSubmit(this.$.input.getValue().trim());
			this.close();
		}
	},
	cancelClick: function(inSender, inEvent) {
		this.close();
	}
});