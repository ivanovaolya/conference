/* global include_all_modules */
import headerComponent from './header/header';
import homeComponent from './home/home';
import signInComponent from './sign-in/sign-in';
import accountPageComponent from './account-page/account-page';
import myInfoComponent from './my-info/my-info';
import tabsComponent from './tabs/tabs';
import signUpComponent from './sign-up/sign-up';
import myTalksComponent from './my-talks/my-talks';

export default (app) => {
  include_all_modules([
    headerComponent,
    homeComponent,
    signInComponent,
    accountPageComponent,
    myInfoComponent,
    tabsComponent,
    signUpComponent,
    myTalksComponent
  ], app);
};
