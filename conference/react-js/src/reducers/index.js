import { combineReducers } from 'redux';
import user from './user';
import users from './users';
import userTalks from './userTalks';
import forgotPassword from './forgot-password';
import forgotPasswordErrorMessage from './forgot-password-error-message';
import talks from './talks';
import conference from './conference';
import contacts from './contacts';
import actionTypes from '../constants/actions-types';

const combinedReducer = combineReducers({
  user,
  forgotPassword,
  forgotPasswordErrorMessage,
  users,
  userTalks,
  talks,
  conference,
  contacts,
});

const rootReducers = (state, action) => (
  action.type === actionTypes.USER_LOGOUT ?
    combinedReducer(undefined, action) :
    combinedReducer(state, action)
);

export default rootReducers;
