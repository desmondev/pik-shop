angular.module('AppNotificationsService', [
    ])

    .factory('AppNotificationsService', ['$rootScope', function ($rootScope) {
        return {
            loginConfirmed: function (data) {
                $rootScope.$broadcast('event:auth-loginConfirmed', data);
            },
            logoutConfirmed: function (data) {
                $rootScope.$broadcast('event:auth-logoutConfirmed', data);
            },
            loginRequired: function() {
                $rootScope.$broadcast('event:auth-loginRequired');
            }
        };
    }]);