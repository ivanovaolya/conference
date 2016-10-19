import forgotPasswordComponent from './forgot-password.component';

export default (app) => {
  app.config(($stateProvider) => {
    $stateProvider
      .state('forgotPassword', {
        url: '/forgot-password',
        template: '<forgot-password></forgot-password>'
      });
  }).component('forgotPassword', forgotPasswordComponent);
};
