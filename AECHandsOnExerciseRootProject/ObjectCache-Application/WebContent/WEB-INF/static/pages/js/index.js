$.noConflict();

function showHideTrigger() {
	jQuery("#topbar").toggle();
}

function showCreateCacheButtons() {
	jQuery("#createCacheButtons").show();
	jQuery("#inputObjectform").hide();
}
function hideCreatePersonForms() {
	jQuery("#cacheSizeLabel").hide();
	jQuery("#timeToLiveLabel").hide();
	jQuery("#cacheSize").hide();
	jQuery("#timeToLive").hide();
	jQuery("#createCacheButtons").hide();
	jQuery("#inputObjectform").show();
}

var repository = [];
var cacheRecords = [];
var consoleLines = [];
angular.module('ObjectCache-Application', [ 'ngRoute' ]).config(
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
			showCreateCacheButtons();
		} else {
			$scope.nextPage = "#/index";
			$scope.label = "Proceed";
		}
	};

}).controller('DownloadController', function DownloadController($scope, $timeout) {
	$scope.repository = repository;
	$scope.cacheRecords = cacheRecords;
	$scope.consoleLines = consoleLines;
	$scope.createCache = function() {
		jQuery.ajax({
			async : false,
			url : "/ObjectCache-Application/createCache/"+$scope.v_cacheSize+"/"+$scope.v_timeToLive,
			type : "GET",
			data : "",
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(response) {
				if("true" == JSON.stringify(response)){
					hideCreatePersonForms();
				}else{
					alert("Can not configure Cache");
				}
							
			},
			error : function(response) {
				alert("Error:"+JSON.stringify(response));
			}
		});
		
	};
	
	$scope.createPerson = function(){
		jQuery.ajax({
			async : false,
			url : "/ObjectCache-Application/createPerson/"+$scope.v_firstName+"/"+$scope.v_lastName,
			type : "GET",
			data : "",
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(response) {
				repository.push(response);
			},
			error : function(response) {
				alert("Error:"+JSON.stringify(response));
			}
		});
	};
	
	$scope.createPerson = function(){
		jQuery.ajax({
			async : false,
			url : "/ObjectCache-Application/createPerson/"+$scope.v_firstName+"/"+$scope.v_lastName,
			type : "GET",
			data : "",
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(response) {
				repository.push(response);
			},
			error : function(response) {
				alert("Error:"+JSON.stringify(response));
			}
		});
	};
	
	$scope.findPerson = function(){
		jQuery.ajax({
			async : false,
			url : "/ObjectCache-Application/findPersonAndReport",
			type : "POST",
			data : $scope.v_findPerson,
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(response) {
				for (var key in response){
					downloadKey = key;
					if(key == "repository"){
						$scope.repository.splice(0);
						for(var i = 0; i < response[key].length; i++){
							
							$scope.repository.push(response[key][i]);
						}
					}else if(key == "cached"){
						$scope.cacheRecords.splice(0);
						for(var i = 0; i < response[key].length; i++){
							
							$scope.cacheRecords.push(response[key][i]);
						}
					}else if(key == "outputConsole"){
						$scope.consoleLines.splice(0);
						for(var i = 0; i < response[key].length; i++){
							var lineItem = {"Line":i,"description":response[key][i]};
							$scope.consoleLines.push(lineItem);
						}
						
					};
					
				}
				
			},
			error : function(response) {
				alert("Error:"+JSON.stringify(response));
			}
		});
	};

});