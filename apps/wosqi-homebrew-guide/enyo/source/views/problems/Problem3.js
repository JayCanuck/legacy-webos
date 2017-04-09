enyo.kind({
	name: "Problem3",
	kind: enyo.VFlexBox,
	events: {
		onChangePaneRequest:""
	},
	published: {
		system:"Windows"
	},
	style:"padding: 15px 15px 15px 15px; font-size:0.8em;",
	components: [
		{kind:"PalmService", service:"palm://com.palm.applicationManager", method:"launch"},
		{content:"Problem troubleshooter", className:"topic-title"},
		{kind:"Scroller", flex:1, autoVertical:true, components:[
			{name:"preface", kind:"HtmlContent", nodeTag:"span"},
			{kind:"Button", caption:"Tap Here To Open DevMode", style:"width:350px; margin-left:auto; margin-right:auto;", onclick:"launchDevMode"},
			{nodeTag:"br"},
			{name:"middle", kind:"HtmlContent", nodeTag:"span"},
			{name:"driverRestart", kind:"HtmlContent", nodeTag:"span"},
			{name:"ending", kind:"HtmlContent", nodeTag:"span"},
			{name:"otherLink", kind:"HtmlContent", onLinkClick:"goToOther", nodeTag:"span"}
		]}
	],
	create: function() {
		this.inherited(arguments);
		var html = "<table style=\"font-size:1.1em; margin-top:15px;\"><tr><td style=\"white-space:nowrap; vertical-align:text-top;\"><b>Problem:</b></td><td width=\"100%\">\"No Devices Found\" error</td></tr></table><img src=\"images/troubleshooter/1.5/error1.png\" class=\"computer-image\" style=\"margin-top:10px;\"/><br><br><b style=\"font-size:1.1em;\">Possible Solutions:</b><ul style=\"margin-top:6px;\"><li>Make sure DevMode is enabled.<br>";
		this.$.preface.setContent(html);
		html = "</li><li>Make sure your device is not in USB Mode. Remember, when you plug in the USB cord, you need to choose Just Charge/Close.<br><table style=\"margin-top:10px; margin-bottom:10px;\"><tbody><tr><td width=\"50%\"><img src=\"images/setup/1.5/device5.png\" class=\"device-image-half\"/></td><td width=\"50%\"><img src=\"images/setup/1.5/device6.png\" class=\"device-image-half\"/></td></tr></tbody></table></li><li>A powered computer USB port is required, so yo may need to try a different USB port on your computer to connect to.<br>&nbsp;</li><li>Choose the \"Reinstall Novacom\" option on the error and see if that fixes the connection issue<br>&nbsp;</li><li>Try restarting the novacom driver service.&nbsp;";
		this.$.middle.setContent(html);
		html = "</li><li>If this is a new device that has never been in USB Mode on this computer, switch into USB Mode, and once it finishes loading, eject it.<br>&nbsp;</li><li>Make sure you have the most recent version of WOSQI<br>&nbsp;</li><li>Restart your device with the USB cord still connected to the computer<br>&nbsp;</li><li>Restart your computer<br>&nbsp;</li></ul>";
		this.$.ending.setContent(html);
		html = "<div style=\"position:relative; top:-10px;\">Problem still not solved? <a href=\"#\" id=\"notSolved\">Click here.</a></div><br>";
		this.$.otherLink.setContent(html);
	},
	systemChanged: function() {
		if(this.system=="Windows") {
			var html = "StartMenu->Run then type \"services.msc\" and hit OK. Look for \"Palm Novacom\", right-click on it, and choose to restart (or start if that's the only option).<br><img src=\"images/troubleshooter/1.5/services-windows.png\" class=\"computer-image\" style=\"margin-top:10px; margin-bottom:20px;\"/>";
			this.$.driverRestart.setContent(html);
		} else if(this.system=="Mac") {
			var html = "Open up Terminal and enter the following commands:<div style=\"background:rgba(0,0,0,0.1); margin-top:10px; margin-left:-25px; margin-bottom:20px; padding: 5px 5px 5px 5px; font-family:monospace; white-space:nowrap; font-size:smaller;\">sudo /opt/nova/bin/stop-novacomd<br>sudo /opt/nova/bin/start-novacomd</div>";
			this.$.driverRestart.setContent(html);
		} else if(this.system=="Ubuntu") {
			var html = "Open up Terminal and enter the following commands:<div style=\"background:rgba(0,0,0,0.1); margin-top:10px; margin-bottom:20px; padding: 5px 5px 5px 5px; font-family:monospace; white-space:nowrap; font-size:smaller;\">sudo stop palm-novacomd<br>sudo killall -v -q novacomd<br>sudo start palm-novacomd</div>";
			this.$.driverRestart.setContent(html);
		}
	},
	goToOther: function(inSender, inEvent) {
		this.doChangePaneRequest("other");
	},
	launchDevMode: function(inSender, inEvent) {
		this.$.palmService.call({id:"com.palm.app.devmodeswitcher"});
	}
});
