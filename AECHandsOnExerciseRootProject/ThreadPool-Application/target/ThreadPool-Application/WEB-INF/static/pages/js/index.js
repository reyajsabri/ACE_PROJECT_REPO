$.noConflict();

function showHideTrigger() {
	jQuery("#topbar").toggle();
}

function showPrepareButtons() {
	jQuery("#prepareDownloadButtons").show();
	jQuery("#downloadButtons").hide();
}
function showdownloadButtons() {
	jQuery("#prepareDownloadButtons").hide();
	jQuery("#downloadButtons").show();
}

var downLoadUrls = [];
var downLoadTasks = [];
var downLoadTmpTasks = [];
var downloadKey;
var downloadCompleted = false;
var trackCompletion = false;
var statusUpdate;
angular.module('ThreadPoolDownloadApp', [ 'ngRoute' ]).config(
		[ '$routeProvider', function($routeProvider) {
			$routeProvider.when('/main', {
				controller : 'DownloadController',
				templateUrl : 'static/pages/views/main.html'
			}).otherwise({
				redirectTo : 'index.html'
			});
		} ]).controller('indexController', function indexController($scope) {
	$scope.configView = function() {
		if ($scope.label == 'Proceed') {
			$scope.nextPage = "#/main";
			$scope.label = "Index";
			showPrepareButtons();
		} else {
			$scope.nextPage = "#/index";
			$scope.label = "Proceed";
		}
	};

}).controller('DownloadController', function DownloadController($scope, $timeout) {
	$scope.downLoadTasks = downLoadTasks;
	$scope.downLoadTmpTasks = downLoadTmpTasks;
	$scope.doSubmit = function() {
		prepareDownloadTask();
	};
	$scope.stratDownload = function() {
//		downloadCompleted = false;
//		isAllTasksCompleted();
//		if(downloadCompleted){
//			alert("Cannot Start download! Thread Pool is shutdown");
//		}
		if($scope.downLoadTasks.length == 0){
			alert("Please Submit URL to download");
			return;
		}
		jQuery.ajax({
			async : false,
			url : "/ThreadPool-Application/startDownload/"+downloadKey,
			type : "GET",
			data : JSON.stringify($scope.downLoadTasks),
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(response) {
				if("true" == JSON.stringify(response)){
					getProgressStatus();
				}else{
					alert("Can not start download");
				}
							
			},
			error : function(response) {
				alert("Error:"+JSON.stringify(response));
			}
		});
		
		
	};
	
	function isAllTasksCompleted(){
		jQuery.ajax({
			async : false,
			url : "/ThreadPool-Application/isTasksCompleted",
			type : "GET",
			data : "",
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(response) {
				if("true" == JSON.stringify(response)){
					downloadCompleted = true;
				}else{
					downloadCompleted = false;
				}
							
			},
			error : function(response) {
				alert("Error:"+JSON.stringify(response));
			}
		});
	};
	function getProgressStatus(){

		jQuery.ajax({
			async : false,
			url : "/ThreadPool-Application/getTasksProgress/"+downloadKey,
			type : "GET",
			data : JSON.stringify($scope.downLoadTasks),
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(response) {
				//alert(JSON.stringify(response))
				var firstUrl = $scope.downLoadTasks[0].progress;
				$scope.downLoadTasks.splice(0);
				trackCompletion = true;
				for(var i = 0; i < response.length; i++){
					$scope.downLoadTasks.push(response[i]);
					if(response[i].progress != "100%"){
						trackCompletion = false;
					}
					//$scope.downLoadTasks.push(response[i]);
				}
				
				
				if(trackCompletion){
					isAllTasksCompleted();
				}
				if(downloadCompleted){
					$timeout.cancel(statusUpdate);
					return;
				}else{
					statusUpdate = $timeout(getProgressStatus, 100);
				}
				
			},
			error : function(response) {
				alert("Error:"+JSON.stringify(response));
			}
		});
	}
	
	function prepareDownloadTask() {
		if(downLoadUrls.length == 0){
			alert("Please Enter URL to download");
			return;
		}
		jQuery.ajax({
			async : false,
			url : "/ThreadPool-Application/prepareDownloadTask",
			type : "POST",
			data : JSON.stringify(downLoadUrls),
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(response) {
				
				$scope.downLoadTasks.splice(0);
				$scope.downLoadTmpTasks.splice(0);
				downLoadUrls.splice(0);
				for (var key in response){
					downloadKey = key;
					
					for(var i = 0; i < response[key].length; i++){
						
						$scope.downLoadTasks.push(response[key][i]);
					}
				}
				showdownloadButtons();
				
			},
			error : function(response) {
				alert(JSON.stringify(response));
			}
		});

	};

	$scope.doclearOutput = function() {
		$scope.downLoadTasks.splice(0);
		showPrepareButtons();
	};
	$scope.addDownload = function() {
			if($scope.v_urlValue.length == 0){
				alert("Please Enter URL to download");
				return;
			}
			var urlObj = {"url":$scope.v_urlValue};
			$scope.downLoadTmpTasks.push(urlObj);
			downLoadUrls.push(urlObj.url);
	};
	
	$scope.saveFile = function(index){
		var fileUrl = $scope.downLoadTasks[index].url; 
		var fileUrlSplit = fileUrl.split('/');
		var fileName = fileUrlSplit[fileUrlSplit.length-1];
		alert("FileName:"+fileName);
		
		var xhr = new XMLHttpRequest();
		xhr.open('POST', '/ThreadPool-Application/getDownloadData/'+downloadKey+'/'+$scope.downLoadTasks[index].id, true);
		xhr.responseType = 'blob';
		xhr.setRequestHeader('Content-Type', 'application/json')
		xhr.overrideMimeType('application/octet-stream; charset=x-user-defined');
		xhr.onload = function(e) {
		  //var arrayBuffer = xhr.response;
		  //var byteArray = new Uint8Array(arrayBuffer);
		  //var blob = new Blob(byteArray, {type: 'application/octet-stream; charset=x-user-defined'});
		  blob = xhr.response;
		  var dataURL = window.URL.createObjectURL(blob);
		  var a = document.createElement("a");
          document.body.appendChild(a);
          a.style = "display: none";
          a.href = dataURL;
          a.download = fileName;
          a.click();
		};
		xhr.send();
//		jQuery.ajaxTransport( 'binary', function( options, originalOptions, jqXHR ) {
//			  if( true ) {
//			    return {
//			      send: function( headers, completeCallback ) {
//			        // Send code
//			      },
//			      abort: function() {
//			        // Abort code
//			      }
//			    };
//			  }
//			});
//		
//		jQuery.ajax({
//			async : false,
//			url : "/ThreadPool-Application/getDownloadData/"+downloadKey+"/"+$scope.downLoadTasks[index].id,
//			type : "POST",
//			dataType : "binary",
//			headers:{'Content-Type':'image/png','X-Requested-With':'XMLHttpRequest'},
//			processData: false,
//			contentType : "application/json; charset=x-user-defined'",
//			responseType : 'arrayBuffer',
//			success : function(response) {
//				alert("Save Success"+JSON.stringify(response));
//				data = response;
//				
//			},
//			error : function(response) {
//				if(response.status == 200 && response.statusText == "OK"){
//					alert("Save Error:"+response.responseText);
//					var blob = new Blob([response.responseText], {type: 'application/octet-stream;'});
//					var dataURL = window.URL.createObjectURL(blob);
//					var a = document.createElement("a");
//		            document.body.appendChild(a);
//		            a.style = "display: none";
//	                a.href = dataURL;
//	                a.download = fileName;
//	                a.click();
//
//				}
//			}
//		});
	};

});