import React, { PureComponent } from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import editUser from '../../actions/edit-user';

import SlideBlock from '../../components/SlideBlock';
import NameBrief from '../../components/NameBrief';
import EmailBrief from '../../components/EmailBrief';
import PasswordBrief from '../../components/PasswordBrief';
import NameChangeForm from '../../components/NameChangeForm';
import EmailChangeForm from '../../components/EmailChangeForm';
import PasswordChangeForm from '../../components/PasswordChangeForm';

class SettingsContainer extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      fname: '',
      lname: '',
      oldMail: '',
      mail: '',
      currentPassword: '',
      newPassword: '',
      confirmNewPassword: '',
      current: null,
    };
  }

  componentDidMount() {
    this.setDefaultValues(this.props);
  }

  componentWillReceiveProps(nextProps) {
    this.setDefaultValues(nextProps);
  }

  setDefaultValues = ({ fname, lname, mail }) => {
    this.setState({
      fname,
      lname,
      oldMail: mail,
      mail: '',
    });
  }

  show = (id) => {
    this.setState({ current: id });
    this.props.reset();
  }

  change = ({ target: { name, value } }) => {
    this.setState({
      [name]: value,
    });
  }

  submitEmail = (e) => {
    e.preventDefault();
    const { mail } = this.state;
    const { setMessage, edit } = this.props;

    edit({ mail });
    setMessage('Your email changed!');
    this.cancel();
  };

  submitName = (e) => {
    e.preventDefault();
    const { fname, lname } = this.state;
    const { edit, setMessage } = this.props;

    edit({ fname, lname });
    setMessage('Your name changed!');
    this.cancel();
  }

  submitPassword = (e) => {
    e.preventDefault();
    const { newPassword,
      confirmNewPassword } = this.state;

    if (newPassword === confirmNewPassword) {
      this.props.setMessage('Your password changed!');
      this.cancel();
    } else {
      this.props.setError('Passwords do not match');
    }
  }

  cancel = () => {
    this.setDefaultValues(this.props);
    this.setState({ current: -1 });
  }

  render() {
    const {
      fname,
      lname,
      mail,
      oldMail,
      currentPassword,
      newPassword,
      confirmNewPassword,
      current,
    } = this.state;

    return (
      <div>
        <SlideBlock isOpened={current === 0}>
          <NameBrief
            show={() => this.show(0)}
            fname={fname}
            lname={lname}
          />
          <NameChangeForm
            cancel={this.cancel}
            submit={this.submitName}
            change={this.change}
            fname={fname}
            lname={lname}
          />
        </SlideBlock>
        <SlideBlock isOpened={current === 1}>
          <EmailBrief
            show={() => this.show(1)}
            mail={oldMail}
          />
          <EmailChangeForm
            cancel={this.cancel}
            submit={this.submitEmail}
            change={this.change}
            mail={mail}
            oldMail={oldMail}
          />
        </SlideBlock>
        <SlideBlock isOpened={current === 2}>
          <PasswordBrief show={() => this.show(2)} />
          <PasswordChangeForm
            cancel={this.cancel}
            submit={this.submitPassword}
            change={this.change}
            currentPassword={currentPassword}
            newPassword={newPassword}
            confirmNewPassword={confirmNewPassword}
          />
        </SlideBlock>
      </div>
    );
  }
}

const mapStateToProps = ({
  user: { fname, lname, mail },
}) => ({
  fname, lname, mail,
});

SettingsContainer.propTypes = {
  edit: PropTypes.func.isRequired,
  setMessage: PropTypes.func.isRequired,
  setError: PropTypes.func.isRequired,
  reset: PropTypes.func.isRequired,
};

export default connect(
  mapStateToProps,
  { edit: editUser },
)(SettingsContainer);
