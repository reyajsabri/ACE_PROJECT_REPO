$.noConflict();
function showHideTrigger() {
	jQuery("#topbar").toggle();
}
var outputRecords = [];
angular.module('Producer-ConsumerApp', [ 'ngRoute' ]).config(
		[ '$routeProvider', function($routeProvider) {
			$routeProvider.when('/main', {
				controller : 'ProducerConsumerController',
				templateUrl : 'static/pages/views/main.html'
			}).otherwise({
				redirectTo : 'index.html'
			});
		} ]).controller('indexController', function indexController($scope) {
	$scope.configView = function() {
		if ($scope.label == 'Proceed') {
			$scope.nextPage = "#/main";
			$scope.label = "Index";
			//showHideTrigger();
		} else {
			$scope.nextPage = "#/index";
			$scope.label = "Proceed";
		}
	};

}).controller('ProducerConsumerController', function ProducerConsumerController($scope) {
	$scope.outputRecords = outputRecords;
	$scope.doStart = function() {
		
		var producer_data = $scope.v_producerValue.split(",");
		jQuery.ajax({
			async : false,
			url : "/Producer-Consumer/start",
			type : "POST",
			data : JSON.stringify(producer_data),
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(response) {
				for(i = 0; i < response.length; i++){
					$scope.outputRecords.push(response[i]);
				}
				
			},
			error : function(response) {
				alert(JSON.stringify(response));
			}
		});

	};

	$scope.doclearOutput = function() {
		$scope.outputRecords.splice(0);
	};
	$scope.resetData = function resetData() {
		$scope.v_producerValue = "";

	}
	;

});