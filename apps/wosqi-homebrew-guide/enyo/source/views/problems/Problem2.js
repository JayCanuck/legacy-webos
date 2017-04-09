enyo.kind({
	name: "Problem2",
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
			var html = "<table style=\"font-size:1.1em; margin-top:15px;\"><tr><td style=\"white-space:nowrap; vertical-align:text-top;\"><b>Problem:</b></td><td width=\"100%\">Driver installation failed</td></tr></table><br><b style=\"font-size:1.1em;\">Possible Solutions:</b><ul style=\"margin-top:6px;\"><li>If an older version of novacom is already installed, uninstall it.<br>&nbsp;</li><li>Restart your computer, then try letting WOSQI install the drivers again<br>&nbsp;</li><li>Try manually installing the driver. You can download it from http://bit.ly/wosqi-driver-win32 for 32-bit computers or http://bit.ly/wosqi-driver-win64 for 64-bit computers.</li></ul><br>";
			this.$.htmlContent.setContent(html);
		} else if(this.system=="Mac") {
			var html = "<table style=\"font-size:1.1em; margin-top:15px;\"><tr><td style=\"white-space:nowrap; vertical-align:text-top;\"><b>Problem:</b></td><td width=\"100%\">Driver installation failed</td></tr></table><br><b style=\"font-size:1.1em;\">Possible Solutions:</b><ul style=\"margin-top:6px;\"><li>If an older version of novacom is already installed, uninstall it.<br>&nbsp;</li><li>Restart your computer, then try letting WOSQI install the drivers again<br>&nbsp;</li><li>Try manually installing the driver. You can download it from http://bit.ly/wosqi-driver-mac</li></ul><br>";
			this.$.htmlContent.setContent(html);
		} else if(this.system=="Ubuntu") {
			var html = "<table style=\"font-size:1.1em; margin-top:15px;\"><tr><td style=\"white-space:nowrap; vertical-align:text-top;\"><b>Problem:</b></td><td width=\"100%\">Driver installation failed</td></tr></table><br><b style=\"font-size:1.1em;\">Possible Solutions:</b><ul style=\"margin-top:6px;\"><li>If an older version of novacom is already installed, uninstall it.<br>&nbsp;</li><li>Restart your computer, then try letting WOSQI install the drivers again<br>&nbsp;</li><li>Try manually installing the driver. You can download it from http://bit.ly/wosqi-driver-linux32 for 32-bit computers or http://bit.ly/wosqi-driver-linux64 for 64-bit computers.</li></ul><br>";
			this.$.htmlContent.setContent(html);
		}
	},
	goToOther: function(inSender, inEvent) {
		this.doChangePaneRequest("other");
	}
});
