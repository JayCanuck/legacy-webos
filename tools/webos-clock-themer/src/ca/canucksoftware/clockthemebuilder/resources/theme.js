if(process.argv.length==5) {
	var THEME_FILE = "/usr/palm/applications/com.palm.app.clock/themes/themes.json";
	var action = process.argv[2];
	var id = process.argv[3];
	var name = process.argv[4];
	var fs = require("fs");
	fs.readFile(THEME_FILE, "utf8", function (err, data) {
		if(err) throw err;
		var json = JSON.parse(data);
		var found = false;
		for(var i=0; i<json.length; i++) {
			if(json[i].name==id) {
				if(action=="remove") {
					json.splice(i, 1);
				}
				found = true;
				break;
			}
				
		}
		if(!found && action=="add") {
			json.push({
				name:id,
				nicename:name,
				description:"",
				source:"themes/" + id + "/"
			});
		}
		var text = JSON.stringify(json, null, "\t");
		fs.writeFile(THEME_FILE, text + "\n", "utf8", function (err) {
			if(err) throw err;
			if(action=="add") {
				process.stdout.write(name + " added to themes.json\n");
			} else if(action=="remove") {
				process.stdout.write(name + " removed from themes.json\n");
			}
			
		});
	});
} else {
	process.stdout.write("Invalid js script arguments\n");
	process.exit(1);
}