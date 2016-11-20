/* global include_all_modules */
import currentUserService from './current/current';
import menusService from './menus/menus';
import talksService from './talk/talk';
import localStorage from './local-storage/local-storage';
import constants from './constants/constants';


export default (app) => {
  include_all_modules([
    localStorage,
    constants,
    currentUserService,
    menusService,
    talksService
  ], app);
};