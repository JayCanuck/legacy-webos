enyo.kind({
	name: "Other",
	kind: enyo.VFlexBox,
	style:"padding: 15px 15px 15px 15px; font-size:0.8em;",
	components: [
		{content:"Problem troubleshooter", className:"topic-title"},
		{kind:"Scroller", flex:1, autoVertical:true, components:[
			{kind:"HtmlContent", onLinkClick:"goToOther"}
		]}
	],
	create: function() {
		this.inherited(arguments);
		var html = "<b style=\"font-size:1.2em;\">Unfortunately, it seems you've encountered an atypical or unknown issue with WOSQI</b><br><br>In order to help me diagnose and fix this error, I'll need a bit of assistance from you. However, as the process below is somewhat complicated, it's completely optional. <br><br><b>Step 1)</b> Open up CommandPrompt/Terminal on your computer.<br><br><b>Step 2)</b> Navigate to where you have WOSQI saved.<br><br><b>Step 3)</b> Now we're going to load WOSQI in a special logging mode. Enter the following command:<br><div style=\"background:rgba(0,0,0,0.1); margin-top:10px; margin-bottom:20px; padding: 5px 1px 5px 1px; font-family:monospace; white-space:nowrap; font-size:smaller;\">java -jar WebOSQuickInstall.jar -log</div><b>Step 4)</b> Now recreate what lead you to the issue.<br><br><b>Step 5)</b> Close WOSQI. In the same folder as WOSQI, there will be a new file named wosqi.log. Do not re-run WOSQI again or you'll erase the log.<br><br><b>Step 6)</b> Go to http://bit.ly/wosqi-forums and look for a pinned WOSQI thread. Post a reply in it, with the wosqi.log file attached. (Do not copy and paste the log contents as that'll fill up the thread pages quickly.)<br><br>";
		this.$.htmlContent.setContent(html);
	}
});
