import React, { PureComponent } from 'react';
import classNames from 'classnames';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import { bindActionCreators } from 'redux';

import { baseUrl } from '../../constants/route-url';
import SignInForm from '../../containers/SignInForm';
import UserMenuFilter from '../User-menu-filter';
import logout from '../../actions/logout';

class Header extends PureComponent {
  constructor() {
    super();
    this.state = {
      dropdown: false,
    };
  }

  componentWillUnmount() {
    document.removeEventListener('click', this.closeSignIn);
  }

  onButtonAccountClick = () => {
    document.removeEventListener('click', this.closeSignIn);
    if (!this.state.dropdown) {
      document.addEventListener('click', this.closeSignIn);
    }

    this.setState(prevState => ({
      dropdown: !prevState.dropdown,
    }));
  };

  closeSignIn = (event) => {
    const formContainer = document.querySelector('.menu-container__content');
    if (!this.isDescendant(formContainer, event.target)) {
      this.setState({
        dropdown: false,
      });
      document.removeEventListener('click', this.closeSignIn);
    }
  };

  closeDropDown = () => {
    this.setState({
      dropdown: false,
    });
  }

  isDescendant = (parent, child) => {
    let node = child.parentNode;
    while (node != null) {
      if (node === parent) {
        return true;
      }
      node = node.parentNode;
    }
    return false;
  };

  render() {
    const { user: { roles, fname }, dispatch } = this.props;
    const logoutAction = bindActionCreators(logout, dispatch);

    return (
      <header className="header">
        <div className="header__title">
          <Link
            className="link_header"
            to={baseUrl}
          >
            conference management
          </Link>
        </div>
        <div className="menu-container">
          <button
            className="menu-container__button js-dropdown"
            onClick={this.onButtonAccountClick}
          >{ fname ? `${fname}'s` : 'Your'} Account
          </button>
          <div className={classNames({
            'menu-container__content': true,
            none: !this.state.dropdown,
          })}
          >
            {
              roles.length > 0 ?
                <UserMenuFilter
                  close={this.closeDropDown}
                  roles={roles}
                  logout={logoutAction}
                /> :
                <SignInForm
                  close={this.closeDropDown}
                />
            }

          </div>
        </div>
      </header>
    );
  }
}

Header.propTypes = {
  dispatch: PropTypes.func.isRequired,
  user: PropTypes.shape({
    id: PropTypes.number,
    roles: PropTypes.array,
    mail: PropTypes.string,
    fname: PropTypes.string,
    lname: PropTypes.string,
    bio: PropTypes.string,
    job: PropTypes.string,
    company: PropTypes.string,
    past: PropTypes.string,
    photo: PropTypes.string,
    linkedin: PropTypes.string,
    twitter: PropTypes.string,
    facebook: PropTypes.string,
    blog: PropTypes.string,
    info: PropTypes.string,
  }).isRequired,
};

const mapStateToProps = state => ({
  user: state.user,
});

export default connect(mapStateToProps)(Header);
