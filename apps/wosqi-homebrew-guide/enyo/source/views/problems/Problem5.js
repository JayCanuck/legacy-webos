enyo.kind({
	name: "Problem5",
	kind: enyo.VFlexBox,
	events: {
		onChangePaneRequest:""
	},
	published: {
		system:"Windows"
	},
	style:"padding: 15px 15px 15px 15px; font-size:0.8em;",
	components: [
		{content:"Problem troubleshooter", className:"topic-title"},
		{kind:"Scroller", flex:1, autoVertical:true, components:[
			{kind:"HtmlContent", onLinkClick:"goToOther"}
		]}
	],
	create: function() {
		this.inherited(arguments);
		var html = "<table style=\"font-size:1.1em; margin-top:15px;\"><tr><td style=\"white-space:nowrap; vertical-align:text-top;\"><b>Problem:</b></td><td width=\"100%\">Freezes on package install</td></tr></table><br><b style=\"font-size:1.1em;\">Possible Solutions:</b><ul style=\"margin-top:6px;\"><li>Make sure your device is not in USB Mode. Remember, when you plug in the USB cord, you need to choose Just Charge/Close.<br><table style=\"margin-top:10px; margin-bottom:10px;\"><tbody><tr><td width=\"50%\"><img src=\"images/setup/1.5/device5.png\" class=\"device-image-half\"/></td><td width=\"50%\"><img src=\"images/setup/1.5/device6.png\" class=\"device-image-half\"/></td></tr></tbody></table></li><li>Restart your device with the USB cord still connected to the computer<br>&nbsp;</li><li>Restart your computer<br>&nbsp;</li></ul><div style=\"position:relative; top:-10px;\">Problem still not solved? <a href=\"#\" id=\"notSolved\">Click here.</a></div><br>";
		this.$.htmlContent.setContent(html);
	},
	goToOther: function(inSender, inEvent) {
		this.doChangePaneRequest("other");
	}
});
