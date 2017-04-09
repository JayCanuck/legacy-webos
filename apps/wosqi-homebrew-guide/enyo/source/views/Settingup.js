enyo.kind({
	name: "Settingup",
	kind: enyo.VFlexBox,
	style:"padding: 15px 15px 15px 15px; font-size:0.8em;",
	components: [
		{kind:"PalmService", service:"palm://com.palm.applicationManager", method:"launch"},
		{content:"Setting up WOSQI", className:"topic-title"},
		{kind:"Scroller", flex:1, autoVertical:true, components:[
			{name:"preface", kind:"HtmlContent"},
			{nodeTag:"br"},
			{kind:"DividerDrawer", caption:"ON YOUR DEVICE", components:[
				{name:"deviceStep1", kind:"HtmlContent", nodeTag:"span"},
				{kind:"Button", caption:"Tap Here To Open DevMode", style:"width:350px; margin-left:auto; margin-right:auto;", onclick:"launchDevMode"},
				{nodeTag:"br"},
				{name:"deviceStep2To5", kind:"HtmlContent", nodeTag:"span"},
			]},
			{nodeTag:"br"},
			{kind:"DividerDrawer", caption:"ON YOUR COMPUTER", components:[
				{name:"computer", kind:"HtmlContent", nodeTag:"span"},
			]}
		]}
	],
	create: function() {
		this.inherited(arguments);
		var html = "<b>Preface:</b> Everybody's computer is different, and as such, there are a number of possible issues that could arise during setup. That's why a troubleshooter is included in this app.";
		this.$.preface.setContent(html);
		html = "<b>Step 1)</b> The first thing you need to do is open up the DevMode app that's hidden in every webOS device.<br><br>Normally, this can be done by typing \"webos20090606\" in Universal Search/Just Type.<br><br><img src=\"images/setup/1.5/device1.png\" class=\"device-image\"/><br><br>However, as you're using this app, all you need to do is...<br>";
		this.$.deviceStep1.setContent(html);
		html = "<b>Step 2)</b> Turn the toggle on. This will enable DevMode and allow WOSQI to connect to your device.<br><br><img src=\"images/setup/1.5/device2.png\" class=\"device-image\"/><br><br><b>Step 3)</b> If you get prompted for a DevMode password, enter it. If you have never set a password yet, then don't type anything and hit submit.<br><br><img src=\"images/setup/1.5/device3.png\" class=\"device-image\"/><br><br><b>Step 4)</b> If you get prompted to reset, then go ahead and do so.<br><br><img src=\"images/setup/1.5/device4.png\" class=\"device-image\"/><br><br><b>Step 5)</b> Plug in your USB cable to your computer and your device. Depending on your version of webOS, you'll want to choose Just Charge or Close (not USB Mode).<br><br><table><tbody><tr><td width=\"50%\"><img src=\"images/setup/1.5/device5.png\" class=\"device-image-half\"/></td><td width=\"50%\"><img src=\"images/setup/1.5/device6.png\" class=\"device-image-half\"/></td></tr></tbody></table>";
		this.$.deviceStep2To5.setContent(html);
		html = "<b>Step 1)</b> Go to this webpage: http://bit.ly/wosqi-java<br><br>That page will show you what version you have installed. You will need Java SE 6 or later.<br><br><b>Step 2)</b> Download the current version of WOSQI to your computer (save it wherever you want) from: http://bit.ly/wosqi-download<br><br><b>Step 3)</b> Run WebOSQuickInstall.jar by double-clicking it.<br><br><b>Step 4)</b> If your computer doesn't have the Novacom drivers already installed, you'll be prompted.<br><br><img src=\"images/setup/1.5/computer1.png\" class=\"computer-image\"/><br><br>Choose the \"yes\" option. The driver will then download.<br><br><img src=\"images/setup/1.5/computer2.png\" class=\"computer-image\"/><br><br>After the download completes, it'll install the driver. Installation will be different depending on your OS, but it should be relatively straightforward.<br><br><img src=\"images/setup/1.5/computer3.png\" class=\"computer-image\"/><br><br><b>Step 5)</b> If all went well, WOSQI should be loaded successfully, with your device selected in the dropdown box in the upper right area.<br><br><img src=\"images/setup/1.5/computer4.png\" class=\"computer-image\"/><br><br>You are now able to use WOSQI with your device. And anytime you want to use WOSQI in the future, all you need to do if choose Just Change/Close on your device and you'll be good to go (assuming you leave DevMode on).";
		this.$.computer.setContent(html);
	},
	launchDevMode: function(inSender, inEvent) {
		this.$.palmService.call({id:"com.palm.app.devmodeswitcher"});
	}
});
