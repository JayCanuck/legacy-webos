function TorchService() {
};

TorchService.getState = function(callback) {
	var request = new Mojo.Service.Request(TorchService.TorchId, {
		method: 'flashState',
		onSuccess: callback || Mojo.doNothing,
		onFailure: TorchService.genericErrorCallback
	});
	return request;
};

TorchService.setState = function(params, callback) {
	var request = new Mojo.Service.Request(TorchService.TorchId, {
		method: 'setFlash',
		parameters: params,
		onSuccess: callback || Mojo.doNothing,
		onFailure: TorchService.genericErrorCallback
	});
	return request;
};

TorchService.genericErrorCallback = function(err) {
	Mojo.Controller.errorDialog(err.errorText)
};

TorchService.TorchId = 'palm://ca.canucksoftware.systoolsmgr';
