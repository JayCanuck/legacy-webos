enyo.kind({
	name: "UsageDialog",
	kind: "ModalDialog",
	scrim: true,
	dismissWithClick: true,
	width: "450px",
	components: [
		{content: "ComicShelf HD Usage Tips", style:"padding-left:35px", className:"enyo-dialog-prompt-title"},
		{className: "enyo-dialog-prompt-content", components: [
			{className: "enyo-dialog-prompt-message", flex:1, components:[
				{nodeTag:"ul", components:[
					{nodeTag:"li", content:"A single tap on comic page will toggle the top/botton toolbars."},
					{nodeTag:"li", content:"Pinch-zoom and well as double-tap will zoom in/out"},
					{nodeTag:"li", content:"Swiping will pan around the comic page and let you move between pages"},
					{nodeTag:"li", content:"There are many additional setting available on the setting screen; be sure to check it out"}
				]},
			]},
			{height:"5px"},
			{name: "okButton", kind: "Button", style:"width:75%; margin-left:auto; margin-right:auto;", caption: enyo._$L("OK"), onclick: "okClick"}
		]}
	],
	create: function() {
		this.inherited(arguments);
	},
	okClick: function(inSender, inEvent) {
		this.close();
	}
});