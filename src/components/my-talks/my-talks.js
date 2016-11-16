import myTalksComponent from './my-talks.component';

export default (app) => {
  app.config(($stateProvider) => {
    'ngInject';
    $stateProvider
      .state('header.tabs.myTalks', {
        url: '/my-talks',
        template: '<my-talks></my-talks>',
        resolve: {
          currentUser: Current => Current.current
        },
        controller: function (currentUser, $scope) {
          'ngInject';
          if (!currentUser || currentUser.roles.indexOf('s') === -1) {
            $scope.$emit('signInEvent');
          }
          this.currentUser = currentUser;
        }
      });
  }).component('myTalks', myTalksComponent);
};
