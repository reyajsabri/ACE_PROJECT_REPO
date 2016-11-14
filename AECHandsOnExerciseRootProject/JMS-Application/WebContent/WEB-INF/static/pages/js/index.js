$.noConflict();
function showHideTrigger() {
	jQuery("#topbar").toggle();
}
var outputRecords = [];
angular.module('JMS-Application', [ 'ngRoute' ]).config(
		[ '$routeProvider', function($routeProvider) {
			$routeProvider.when('/main', {
				controller : 'JMSMainPageController',
				templateUrl : 'static/pages/views/main.html'
			}).otherwise({
				redirectTo : 'index.html'
			});
		} ]).controller('indexController', function indexController($scope) {
	$scope.configView = function() {
		if ($scope.label == 'JMS Main-Page') {
			$scope.nextPage = "#/main";
			$scope.label = "Index";
			//showHideTrigger();
		} else {
			$scope.nextPage = "#/index";
			$scope.label = "JMS Main-Page";
		}
	};

}).controller('JMSMainPageController', function JMSMainPageController($scope) {
	$scope.doPublish = function() {
		$scope.outputRecords = outputRecords;
		var message_data = {
			orderId : "",
			status : null,
			itemId : $scope.v_itemID,
			itemDescription : $scope.v_description,
			price : $scope.v_price,
			quantity : $scope.v_quantity,
			errorClass : ""
		};
		
		var orderID = null;
		jQuery.ajax({
			async : false,
			url : "/JMS-Application/order",
			type : "POST",
			data : JSON.stringify(message_data),
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(response) {
				orderID = JSON.stringify(response);
			},
			error : function(response) {
				alert(JSON.stringify(response));
			}
		});

		var statusURL = "/JMS-Application/getOrderStatus/" + orderID;
		jQuery.ajax({
			async : false,
			url : statusURL,
			type : "GET",
			data : JSON.stringify(message_data),
			dataType : "json",
			contentType : "text/html; charset=utf-8",
			success : function(response) {
				$scope.outputRecords.push(response);
				resetData();
			},
			error : function(response) {
				alert("Error"+JSON.stringify(response));
			}
		});
		
	};

	$scope.doclearOutput = function() {
		$scope.outputRecords.splice(0);
	};
	$scope.resetData = function resetData() {
		$scope.v_itemID = "";
		$scope.v_description = "";
		$scope.v_quantity = "";
		$scope.v_price = "";

	}
	;

});