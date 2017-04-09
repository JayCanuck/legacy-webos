enyo.kind({
	name: "Advanced",
	kind: enyo.VFlexBox,
	style:"padding: 15px 15px 15px 15px; font-size:0.8em;",
	components: [
		{content:"Advanced features", className:"topic-title"},
		{kind:"Scroller", flex:1, autoVertical:true, components:[
			{name:"preface", kind:"HtmlContent"},
			{nodeTag:"br"},
			{kind:"DividerDrawer", caption:"TRANSFER FILES", open:false, components:[
				{name:"transfer", kind:"HtmlContent", nodeTag:"span"},
			]},
			{nodeTag:"br"},
			{kind:"DividerDrawer", caption:"USING WITH A PROXY", open:false, components:[
				{name:"proxy1To4", kind:"HtmlContent", nodeTag:"span"},
				{kind:"Scroller", width:"100%", height:"45px", autoVertical:true, vertical:false, style:"background:rgba(0,0,0,0.1);", components:[
					{name:"code1", style:"padding: 10px 10px 10px 10px; font-family:monospace; white-space:nowrap; width:100%;", kind:"HtmlContent", content:"java -Dhttp.proxyHost=yourproxyhost -Dhttp.proxyPort=yourproxyport -jar WebOSQuickInstall.jar"},
				]},
				{nodeTag:"br"},
				{name:"proxy4", kind:"HtmlContent", nodeTag:"span"},
				{kind:"Scroller", width:"100%", height:"45px", autoVertical:true, vertical:false, style:"background:rgba(0,0,0,0.1);", components:[
					{name:"code2", style:"padding: 10px 10px 10px 10px; font-family:monospace; white-space:nowrap; width:100%;", kind:"HtmlContent", content:"java -Dhttp.proxyHost=myproxy.com -Dhttp.proxyPort=80 -jar WebOSQuickInstall.jar"}
				]},
				{nodeTag:"br"},
				{name:"proxyNote", kind:"HtmlContent", nodeTag:"span"},
			]},
			{nodeTag:"br"},
			{kind:"DividerDrawer", caption:"LINUX COMMANDLINE", open:false, components:[
				{name:"linux", kind:"HtmlContent", nodeTag:"span"},
			]}
		]}
	],
	create: function() {
		this.inherited(arguments);
		var html = "Below are a few advanced features contained within WOSQI. Tap a topic header to view the details. More will be added in future updates.";
		this.$.preface.setContent(html);
		html = "<b>Step 1)</b> If you haven't already, plug in the USB cord and choose Just Charge or Close.<br><br><table><tbody><tr><td width=\"50%\"><img src=\"images/setup/1.5/device5.png\" class=\"device-image-half\"/></td><td width=\"50%\"><img src=\"images/setup/1.5/device6.png\" class=\"device-image-half\"/></td></tr></tbody></table><br>And if WOSQI isn't already running, start it up.<br><br><b>Step 2)</b> Make sure the desired device is selected in the dropdown box in the upper right area, as WOSQI supports multiple devices connected at once.<br><br><img src=\"images/installing/1.5/wosqi1.png\" class=\"device-image\"/><br><br><b>Step 3)</b> Click to open the Tools menu.<br><br><img src=\"images/advanced/1.5/transfer1.png\" class=\"device-image\"/><br><br><b>Step 4)</b> You can choose either the Send File or Receive File option. Both are pretty straightforward and will allow you to send or receive a file to or from your device. That includes hidden system areas that USB Mode doesn't allow.<br><br><img src=\"images/advanced/1.5/transfer2.png\" class=\"computer-image\"/><br><img src=\"images/advanced/1.5/transfer3.png\" class=\"computer-image\"/>";
		this.$.transfer.setContent(html);
		html = "<b>Step 1)</b> If you haven't already, plug in the USB cord and choose Just Charge or Close.<br><br><table><tbody><tr><td width=\"50%\"><img src=\"images/setup/1.5/device5.png\" class=\"device-image-half\"/></td><td width=\"50%\"><img src=\"images/setup/1.5/device6.png\" class=\"device-image-half\"/></td></tr></tbody></table><br><b>Step 2)</b> Open CommandPrompt/Terminal on your computer.<br><br><b>Step 3)</b> Use CommandPrompt/Terminal to navigate to the folder where you have WOSQI saved.<br><br><b>Step 4)</b> To launch WOSQI through a proxy, type the following:<br><br>";
		this.$.proxy1To4.setContent(html);
		html = "Replace yourproxyhost with your proxy host, and yourproxyport with the port number for your proxy. For example:<br><br>";
		this.$.proxy4.setContent(html);
		html = "Note: if your WOSQI is named something other than WebOSQuickinstall.jar, adjust the above command as needed.";
		this.$.proxyNote.setContent(html);
		html = "<b>Step 1)</b> If you haven't already, plug in the USB cord and choose Just Charge or Close.<br><br><table><tbody><tr><td width=\"50%\"><img src=\"images/setup/1.5/device5.png\" class=\"device-image-half\"/></td><td width=\"50%\"><img src=\"images/setup/1.5/device6.png\" class=\"device-image-half\"/></td></tr></tbody></table><br>And if WOSQI isn't already running, start it up.<br><br><b>Step 2)</b> Make sure the desired device is selected in the dropdown box in the upper right area, as WOSQI supports multiple devices connected at once.<br><br><img src=\"images/installing/1.5/wosqi1.png\" class=\"device-image\"/><br><br><b>Step 3)</b> Click to open the Tools menu.<br><br><img src=\"images/advanced/1.5/transfer1.png\" class=\"device-image\"/><br><br><b>Step 4)</b> Click the \"Linux Commandline\" option. A new window will open, giving you full root access to your device's commandline.<br><br><img src=\"images/advanced/1.5/commandline.png\" class=\"computer-image\"/><br><br>";
		this.$.linux.setContent(html);
	}
});
