enyo.kind({
	name: "Installing",
	kind: enyo.VFlexBox,
	style:"padding: 15px 15px 15px 15px; font-size:0.8em;",
	components: [
		{content:"Installing homebrew packages", className:"topic-title"},
		{kind:"Scroller", flex:1, autoVertical:true, components:[
			{kind:"HtmlContent"}
		]}
	],
	create: function() {
		this.inherited(arguments);
		var html = "<b>Step 1)</b> If you haven't already, plug in the USB cord and choose Just Charge or Close.<br><br><table><tbody><tr><td width=\"50%\"><img src=\"images/setup/1.5/device5.png\" class=\"device-image-half\"/></td><td width=\"50%\"><img src=\"images/setup/1.5/device6.png\" class=\"device-image-half\"/></td></tr></tbody></table><br>And if WOSQI isn't already running, start it up.<br><br><b>Step 2)</b> Make sure the desired device is selected in the dropdown box in the upper right area, as WOSQI supports multiple devices connected at once.<br><br><img src=\"images/installing/1.5/wosqi1.png\" class=\"device-image\"/><br><br><b>Step 3)</b> Click the blue globe button. That'll load and open the online feed viewer (internet connection required).<br><br><img src=\"images/installing/1.5/wosqi2.png\" class=\"computer-image\"/><br><br>There are tabs at the top to see the different types of homebrew available. See the <i>What is homebrew?</i> section for more info on what each is.<br><br><img src=\"images/installing/1.5/wosqi3.png\" class=\"computer-image\"/><br><br><b>Step 4)</b> You can manually browse for the package you're looking for or use the search bar to find it.<br><br><img src=\"images/installing/1.5/wosqi4.png\" class=\"computer-image\"/><br><br><b>Step 6)</b> Now just hit the \"Install\" button and it'll install onto your device.<br><br><img src=\"images/installing/1.5/wosqi5.png\" class=\"computer-image\"/><br><br>";
		this.$.htmlContent.setContent(html);
	}
});
