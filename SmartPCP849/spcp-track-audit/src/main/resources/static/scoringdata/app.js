(function(angular){
	angular
	.module('trackingApp', ['ngMaterial', 'ngMessages', 'ngAnimate', 'md.data.table']);

	angular
	.module('trackingApp')
	.controller('WithTracingIdSearchCtrl', WithTracingIdSearchCtrl)
	.controller('WithMemberInfoSearchCtrl', WithMemberInfoSearchCtrl);

	WithTracingIdSearchCtrl.$inject = ['$scope', '$http', '$mdToast', '$filter', '$window'];
	WithMemberInfoSearchCtrl.$inject = ['$scope', '$http', '$mdToast', '$filter', '$window'];

	function WithTracingIdSearchCtrl($scope, $http, $mdToast, $filter, $window) {

		$scope.getToastPosition = getToastPosition;
		$scope.showSimpleToast = showSimpleToast;
		$scope.fetchPcpAssignmentData = fetchPcpAssignmentData;
		$scope.showPcpScoringData = showPcpScoringData;
		$scope.downloadPcpScoringData = downloadPcpScoringData;

		$scope.withTraceIdSearchForm = {};
		this.pcpAssignmentDate = new Date();
		this.maxDate=new Date();

		$scope.pcpAssignmentInfo=[];
		this.showTable = false;

		this.query = {
				order: '',
				limit: 5,
				page: 1
		};

		var last = {
				bottom: false,
				top: true,
				left: false,
				right: true
		};

		$scope.toastPosition = angular.extend({},last);

		function getToastPosition() {
			sanitizePosition();

			return Object.keys($scope.toastPosition)
			.filter(function(pos) { return $scope.toastPosition[pos]; })
			.join(' ');
		}

		function sanitizePosition() {
			var current = $scope.toastPosition;

			if ( current.bottom && last.top ) current.top = false;
			if ( current.top && last.bottom ) current.bottom = false;
			if ( current.right && last.left ) current.left = false;
			if ( current.left && last.right ) current.right = false;

			last = angular.extend({},current);
		}

		function showSimpleToast(message) {
			var pinTo = $scope.getToastPosition();
			$mdToast.show(
					$mdToast.simple()
					.textContent(message)
					.position(pinTo )
					.hideDelay(3000)
			);
		}

		function fetchPcpAssignmentData(traceId, pcpAssignmentDate) {
			var pcpAsgnDtStr = $filter('date')(pcpAssignmentDate.$viewValue, 'yyyy-MM-dd');

			$http.post('getAssignedPCPDtlsTraceId',{"traceId":traceId.$viewValue, "pcpAssignmentDate":pcpAsgnDtStr}).then(function (response) {

				var inputArr=response.data;
				if(null != inputArr && 0<inputArr.length){

					$scope.pcpAssignmentInfo = inputArr;
				}else{
					if(null!=inputArr && undefined!=inputArr.responseMessage){
						$scope.showSimpleToast(inputArr.responseMessage);
					}else{
						$scope.showSimpleToast('No records found !');
					}
				}
			},function (error) {
				$scope.showSimpleToast('Error while fetching pcp assignment data !');
			});

		}

		function showPcpScoringData(traceId){
			$window.open('getPCPScoreDataAsJson?traceId='+traceId,'_blank');
		}

		function downloadPcpScoringData(traceId) {
			$window.open('getPCPScoreDataAsXLSX?traceId='+traceId,'_blank');
		}
	}


	function WithMemberInfoSearchCtrl($scope, $http, $mdToast, $filter, $window){
		$scope.getToastPosition = getToastPosition;
		$scope.showSimpleToast = showSimpleToast;
		$scope.fetchPcpAssignmentData = fetchPcpAssignmentData;
		$scope.showPcpScoringData = showPcpScoringData;
		$scope.downloadPcpScoringData = downloadPcpScoringData;

		$scope.withMemberInfoSearchForm = {};
		this.pcpAssignmentDate = new Date();
		this.maxDate=new Date();

		$scope.pcpAssignmentInfo=[];
		$scope.showTable = false;

		$scope.query = {
				order: '',
				limit: 5,
				page: 1
		};

		var last = {
				bottom: false,
				top: true,
				left: false,
				right: true
		};

		$scope.toastPosition = angular.extend({},last);

		function getToastPosition() {
			sanitizePosition();

			return Object.keys($scope.toastPosition)
			.filter(function(pos) { return $scope.toastPosition[pos]; })
			.join(' ');
		}

		function sanitizePosition() {
			var current = $scope.toastPosition;

			if ( current.bottom && last.top ) current.top = false;
			if ( current.top && last.bottom ) current.bottom = false;
			if ( current.right && last.left ) current.left = false;
			if ( current.left && last.right ) current.right = false;

			last = angular.extend({},current);
		}

		function showSimpleToast(message) {
			var pinTo = $scope.getToastPosition();
			$mdToast.show(
					$mdToast.simple()
					.textContent(message)
					.position(pinTo )
					.hideDelay(3000)
			);
		}

		function fetchPcpAssignmentData(firstName, pcpId, pcpAssignmentDate) {

			var firstName = firstName.$modelValue;
			if((firstName == null || firstName == '') && (pcpId == null || pcpId == '')) {
				$scope.showSimpleToast('Please provide either First Name OR PCP Id !');
				return;
			}
			var pcpAsgnDtStr = $filter('date')(pcpAssignmentDate.$viewValue, 'yyyy-MM-dd');
			var pcpAsgnDt = new Date(pcpAsgnDtStr);
			pcpAsgnDt.setHours(0,0,0,0);

			$http.post('getAssignedPCPDtls',{"firstName":firstName,"pcpId":pcpId, "pcpAssignmentDate":pcpAsgnDtStr}).then(function (response) {
				var inputArr=response.data;
				if(null != inputArr && 0<inputArr.length){

					$scope.pcpAssignmentInfo = inputArr;
				}else{
					if(null!=inputArr && undefined!=inputArr.responseMessage){
						$scope.showSimpleToast(inputArr.responseMessage);
					}else{
						$scope.showSimpleToast('No records found !');
					}
				}
			},function (error) {
				$scope.showSimpleToast('Error while fetching pcp assignment data !');
			});
		}

		function showPcpScoringData(traceId){
			$window.open('getPCPScoreDataAsJson?traceId='+traceId,'_blank');
		}

		function downloadPcpScoringData(traceId) {
			$window.open('getPCPScoreDataAsXLSX?traceId='+traceId,'_blank');
		}
	}

})(angular)