enyo.kind({
	name: "Problem4",
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
		var html = "<table style=\"font-size:1.1em; margin-top:15px;\"><tr><td style=\"white-space:nowrap; vertical-align:text-top;\"><b>Problem:</b></td><td width=\"100%\">Feed viewer won't load</td></tr></table><br><b style=\"font-size:1.1em;\">Possible Solutions:</b><ul style=\"margin-top:6px;\"><li>Wait for your device to be loaded into the dropdown box in the upper-right area. If you click the blue globe button too quick before it's loaded, there's a slim chance it'll cause the feed viewer to fail to load.<br>&nbsp;</li><li>Connect to the internet. This problem typically only happens when internet access is unavailable for WOSQI.<br>&nbsp;</li><li>If you're unable to connect to the internet due to a proxy, information on how to use WOSQI with a proxy can be found in the <i>Advanced features</i> section of this app.<br>&nbsp;</li></ul><div style=\"position:relative; top:-10px;\">Problem still not solved? <a href=\"#\" id=\"notSolved\">Click here.</a></div><br>";
		this.$.htmlContent.setContent(html);
	},
	goToOther: function(inSender, inEvent) {
		this.doChangePaneRequest("other");
	}
});
