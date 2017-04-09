enyo.kind({
	name: "Problem1",
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
	},
	systemChanged: function() {
		if(this.system=="Windows") {
			var html = "<table style=\"font-size:1.1em; margin-top:15px;\"><tr><td style=\"white-space:nowrap; vertical-align:text-top;\"><b>Problem:</b></td><td width=\"100%\">WOSQI won't start up</td></tr></table><br><b style=\"font-size1.1em;\">Possible Solutions:</b><ul style=\"margin-top:6px;\"><li>Read and review http://bit.ly/wosqi-howto-jar as it will almost certainly be of assistance.<br>&nbsp;</li><li>Make sure that you are not trying to open the jar file with an archive manager.<br>&nbsp;</li><li>If your computer is 64-bit, you may need to install the 64-bit Java Runtime Environment, even if you already have the 32-bit version installed. You can download it at http://bit.ly/wosqi-jvm<br>&nbsp;</li></ul><div style=\"position:relative; top:-10px;\">Problem still not solved? <a href=\"#\" id=\"notSolved\">Click here.</a></div><br>";
			this.$.htmlContent.setContent(html);
		} else if(this.system=="Mac") {
			var html = "<table style=\"font-size:1.1em; margin-top:15px;\"><tr><td style=\"white-space:nowrap; vertical-align:text-top;\"><b>Problem:</b></td><td width=\"100%\">WOSQI won't start up</td></tr></table><br><b style=\"font-size1.1em;\">Possible Solutions:</b><ul style=\"margin-top:6px;\"><li>Run /Application/Utilities/Java/Java Preferences. Then click and drag Jave SE 6 to top of both list, and exit the program. This will set Java 1.6 as the default version to use.<br><img src=\"images/troubleshooter/1.5/mac-java-prefs.png\" class=\"computer-image\" style=\"margin-top:10px;\"/><br>&nbsp;</li><li>Mac OS X 10.4 is unable to run the required Java SE 6. Although not officially supported, you may wish to investigate http://bit.ly/wosqi-soylatte as a possible option for Mac OS X 10.4.<br>&nbsp;</li><li>Read and review http://bit.ly/wosqi-howto-jar as it will almost certainly be of assistance.<br>&nbsp;</li><li>Make sure that you are not trying to open the jar file with an archive manager.<br>&nbsp;</li></ul><div style=\"position:relative; top:-10px;\">Problem still not solved? <a href=\"#\" id=\"notSolved\">Click here.</a></div><br>";
			this.$.htmlContent.setContent(html);
		} else if(this.system=="Ubuntu") {
			var html = "<table style=\"font-size:1.1em; margin-top:15px;\"><tr><td style=\"white-space:nowrap; vertical-align:text-top;\"><b>Problem:</b></td><td width=\"100%\">WOSQI won't start up</td></tr></table><br><b style=\"font-size1.1em;\">Possible Solutions:</b><ul style=\"margin-top:6px;\"><li>If you have problems with the Sun Java Runtime Environment, consider installing OpenJDK, which WOSQI can also run on.<br>&nbsp;</li><li>Read and review http://bit.ly/wosqi-howto-jar as it will almost certainly be of assistance.<br>&nbsp;</li><li>Make sure that you are not trying to open the jar file with an archive manager.<br>&nbsp;</li></ul><div style=\"position:relative; top:-10px;\">Problem still not solved? <a href=\"#\" id=\"notSolved\">Click here.</a></div><br>";
			this.$.htmlContent.setContent(html);
		}
	},
	goToOther: function(inSender, inEvent) {
		this.doChangePaneRequest("other");
	}
});
