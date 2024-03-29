enyo.kind({
	name: "Homebrew",
	kind: enyo.VFlexBox,
	style:"padding: 15px 15px 15px 15px; font-size:0.8em;",
	components: [
		{content:"What is homebrew?", className:"topic-title"},
		{kind:"Scroller", flex:1, autoVertical:true, components:[
			{kind:"HtmlContent"}
		]}
	],
	create: function() {
		this.inherited(arguments);
		var html = "Homebrew is a classification given to webOS packages which are created and distributed outside of the official App Catalog on custom online feeds. They come in a variety of types:<ul><li><b>Applications</b> are basically the same thing you're familiar with in the App Catalog.</li><li><b>Services</b> power advanced homebrew applications and usually don't show up in the launcher.</li><li><b>Plugins, Linux Apps, and Linux Daemons</b> are low-level Linux binaries or scripts that also won't show up in the launcher, and sometimes even remove themselves.</li><li><b>Kernels</b> customized with the ability to overclock (via the app Govnah) and other low-level goodies, can be installed, replacing the default one. Only 1 custom kernel should be installed at a time.</li><li><b>Patches</b> will add, remove, or modify feature in webOS applications. One of the most popular forms of homebrew, patches let you customize your device to your liking.</li><li><b>Themes</b> replace some or all of the images used in the system. Everything from buttons and checkboxes, to backgrounds and icons, can be skinned with themes. Only 1 theme can be installed at a time, with the exception of Clock themes (of which multiple can be installed at once).</li></ul>WebOS Quick Install (WOSQI) is a desktop computer program that taps into these feeds and lets you install/uninstall homebrew packages. It follows the open homebrew standards, and as such, is fully compatible with other installers, including Preware.<br><br>As webOS homebrew doesn't face the security restrictions of the App Catalog, it's able to do riskier and lower-level things. While the homebrew feeds are a community venture and unstable/dangerous packages rarely make it onto the feeds, it's important to understand that installing homebrew packages are done at your own risk.<br><br>The homebrew ecosystem is separate from HP, though on several occations they have supported and touted the homebrew community as a positive force within the webOS community.";
		this.$.htmlContent.setContent(html);
	}
});
