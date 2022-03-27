enyo.kind({
	name: "SafePrintDialog",
	kind: "PrintDialog",
	close: function() {
		try {
			this.inherited(arguments);
		} catch(e) {}
		
	}
});