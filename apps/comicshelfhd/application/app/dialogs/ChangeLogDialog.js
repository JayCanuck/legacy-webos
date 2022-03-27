enyo.kind({
	name: "ChangeLogDialog",
	kind: "ModalDialog",
	scrim: true,
	dismissWithClick: true,
	width: "550px",
	components: [
		{name:"prefs", kind: "Preferences"},
		{content: "Update Changelog", style:"padding-left:35px", className:"enyo-dialog-prompt-title"},
		{className: "enyo-dialog-prompt-content", components: [
			{className: "enyo-dialog-prompt-message", flex:1, components:[
				{kind:"Scroller", flex:1, style:"height:425px !important;", autoVertical:true, autoHorizontal:false, horizontal:false, components:[
					{kind:"Divider", caption:"v1.4.0"},
					{nodeTag:"ul", components:[
						{nodeTag:"li", content:"Added Auto-fit/Fit-by-width/Fit-by-height option"},
						{nodeTag:"li", content:"Maximum zoom ratio increased"},
						{nodeTag:"li", content:"Improved filetype fallback compatability"}
					]},
					{kind:"Divider", caption:"v1.3.2"},
					{nodeTag:"ul", components:[
						{nodeTag:"li", content:"Fixed the timeout issue with large comics"},
						{nodeTag:"li", content:"Fixed the Mark-All-Unread and Mark-All-Read options"},
						{nodeTag:"li", content:"Added the option to set current page as the thumbnail"},
						{nodeTag:"li", content:"Added a \"Cancel\" button to the comic loading scrim"}
					]},
					{kind:"Divider", caption:"v1.3.0"},
					{nodeTag:"ul", components:[
						{nodeTag:"li", content:"Improved handling of large comics"},
						{nodeTag:"li", content:"Optional read/unread management system"},
					]},
					{kind:"Divider", caption:"v1.2.2"},
					{nodeTag:"ul", components:[
						{nodeTag:"li", content:"Tapping left/right 20% to change page is now an option that can be disabled on the settings screen if you desire"},
						{nodeTag:"li", content:"Added the option to have the viewer's background be black rather than the neutral dark default"},
						{nodeTag:"li", content:"Improved scroller resetting"},
					]},
					{kind:"Divider", caption:"v1.2.1"},
					{nodeTag:"ul", components:[
						{nodeTag:"li", content:"Thumbnail shelf view is now an option that needs to be enabled on the Settings popup"},
						{nodeTag:"li", content:"Can no longer go outside the comics directory"},
						{nodeTag:"li", content:"Scroller resets to the top on directory change"},
					]},
					{kind:"Divider", caption:"v1.2.0"},
					{nodeTag:"ul", components:[
						{nodeTag:"li", content:"Redesigned file chooser with a new thumbnail view"},
						{nodeTag:"li", content:"Added a Settings popup with fullscreen and auto-resume options"},
						{nodeTag:"li", content:"Tapping left/right 20% of the screen will change page back/forward"},
						{nodeTag:"li", content:"Improved comic page extraction"},
						{nodeTag:"li", content:"Improved page order sorting"},
						{nodeTag:"li", content:"Fixed bug preventing comics from being detected"},
						{nodeTag:"li", content:"Many various small bugfixes"},
					]},
					{kind:"Divider", caption:"v1.0.1"},
					{nodeTag:"ul", components:[
						{nodeTag:"li", content:"Fixed a bug preventing some comic pages from loading"}
					]},
					{kind:"Divider", caption:"v1.0.0"},
					{nodeTag:"ul", components:[
						{nodeTag:"li", content:"Initial release"}
					]},
				]}
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