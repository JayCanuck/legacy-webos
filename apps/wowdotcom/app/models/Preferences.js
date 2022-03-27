function Preferences() {
}

Preferences.get = function(key, defaultVal) {
	var cookie = new Mojo.Model.Cookie(key);
	var entry = cookie.get();
	if(!entry) {
		entry = {value:defaultVal};
		cookie.put(entry);
	}
	delete cookie;
	return entry.value;
}

Preferences.set = function(key, val) {
	var cookie = new Mojo.Model.Cookie(key);
	cookie.put({value:val});
	delete cookie;
}