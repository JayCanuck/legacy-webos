enyo.kind({
	name: "ErrorDialog",
	kind: "SimpleDialog",
	create: function() {
		this.inherited(arguments);
		this.type = SimpleDialog.ERROR;
	}
});