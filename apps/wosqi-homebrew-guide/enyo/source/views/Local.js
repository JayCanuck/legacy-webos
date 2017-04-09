enyo.kind({
	name: "Local",
	kind: enyo.VFlexBox,
	style:"padding: 15px 15px 15px 15px; font-size:0.8em;",
	components: [
		{content:"Installing .ipk and .patch files", className:"topic-title"},
		{kind:"Scroller", flex:1, autoVertical:true, components:[
			{kind:"HtmlContent"}
		]}
	],
	create: function() {
		this.inherited(arguments);
		var html = "<b>Preface:</b> Aside from the online packages, WOSQI can install off-feed .ipk and .patch files. They install following the same homebrew standard as on-feed apps and patches.<br><br><b>Step 1)</b> If you haven't already, plug in the USB cord and choose Just Charge or Close.<br><br><table><tbody><tr><td width=\"50%\"><img src=\"images/setup/1.5/device5.png\" class=\"device-image-half\"/></td><td width=\"50%\"><img src=\"images/setup/1.5/device6.png\" class=\"device-image-half\"/></td></tr></tbody></table><br>And if WOSQI isn't already running, start it up.<br><br><b>Step 2)</b> Make sure the desired device is selected in the dropdown box in the upper right area, as WOSQI supports multiple devices connected at once.<br><br><img src=\"images/installing/1.5/wosqi1.png\" class=\"device-image\"/><br><br><b>Step 3)</b> You can use the + button to add files to the list, and the - button to remove them from the list. You can also drag-and-drop files onto the window.<br><br><img src=\"images/local/1.5/wosqi1.png\" class=\"computer-image\"/><br><br><b>Step 4)</b> Now you just need to click the \"Install\" button and all listed files will be installed.<br><br><img src=\"images/local/1.5/wosqi2.png\" class=\"computer-image\"/><br><br>";
		this.$.htmlContent.setContent(html);
	}
});
