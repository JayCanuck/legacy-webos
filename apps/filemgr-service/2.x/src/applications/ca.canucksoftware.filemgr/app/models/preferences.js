function Preferences() {
}

Preferences.get = function(key, defaultVal) {
	var cookie = new Mojo.Model.Cookie(key);
	var val = cookie.get();
	if(val==undefined) {
		val = defaultVal;
		cookie.put(defaultVal);
	}
	delete cookie;
	return val;
};

Preferences.set = function(key, val) {
	var cookie = new Mojo.Model.Cookie(key);
	cookie.put(val);
	delete cookie;
};
