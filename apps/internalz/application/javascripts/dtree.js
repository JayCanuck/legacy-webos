/*--------------------------------------------------|
| dTree 2.05 | www.destroydrop.com/javascript/tree/ |
|---------------------------------------------------|
| Copyright (c) 2002-2003 Geir Landr�               |
|                                                   |
| This script can be used freely as long as all     |
| copyright messages are intact.                    |
|                                                   |
| Updated: 17.04.2003                               |
|---------------------------------------------------|
| Modified for webOS usage and recursive directory  |
| scanning by Jason Robitaille                      |
|--------------------------------------------------*/
	this._po = false;
	this.config = {
		filemgr				: null,
		showHidden			: true,
	}
	this.currNode = null;
};
};
	for(var i=0; i<this.aNodes.length; i++) {
		var s = t;
		if(s.length>1) {
			if(s.indexOf('/') == s.lastIndexOf('/')) {
				s = "/";
			} else {
				s = s.substring(0,s.lastIndexOf('/'));
			}
		}
		if(this.aNodes[i].title==s && this.aNodes[i].title.length==s.length) {
					"javascript:folder='" + t + "'", t, "_self", "images/folder.png",
					"images/baseFolder.png", false));
		}
	}

dTree.prototype.uniqueId = function() {
	var unique = -1;
	for(var i=0; i<this.aNodes.length; i++) {
		if(this.aNodes[i].id>=unique) {
			unique = this.aNodes[i].id + 1;
		}
	}
	return unique;
};
		if(node.id==-1)
		else
			str += '<img onclick="javascript: ' + this.obj + '.s(' + nodeId + ');folder=&quot;' + node.title + '&quot;;" style="width:40px;height:40px" id="i' + this.obj + nodeId + '" src="' +
				((node._io) ? node.iconOpen : node.icon) + '" alt="" />';

// Highlights the selected node
	if (this.currNode._po == false) { //remove "Loading..." placeholder
		for (var i = 0; i < this.aNodes.length; i++) {
			if (this.aNodes[i].pid == this.currNode.id
					&& this.aNodes[i].title == this.currNode.title + "/Loading...") {
				this.aNodes.splice(i, 1);
			}
		}
		this.currNode._po = true;
		this.getMore();
	}
	this.setScale();
};
	this.config.filemgr.listDirs(
		{
		},
			for(var i=0; i<response.items.length; i++) {
				if(response.items[i].link=="no") {
							response.items[i].name);
					if(response.items[i].hasChildren) {
						this.addByTitle(response.items[i].path + "/Loading...",
								$L("Loading..."));
					}
				}
			}
			this.controller.get("folderChooser").innerHTML = this.toString();
			this.setScale();


dTree.prototype.setScale = function() {
	this.controller.get("folderChooser").style.width = this.controller.get("tree")
			.offsetWidth +10 + "px";
};

dTree.prototype.cleanup = function(){
	for(var i=0; i<this.aNodes.length; i++) {
		delete this.aNodes[i];
	}
	delete this.root;
	delete this;
};