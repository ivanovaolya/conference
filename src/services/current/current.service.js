/* global angular */
/* global FormData */

function Current($resource, $window, $q, $rootScope, $http) {
  "ngInject";
  function getToken() {
    let info = $window.localStorage.userInfo;
    let token;

    if (info) {
      info = JSON.parse(info);
      token = info.token;
    } else {
      token = '';
    }

    return token;
  }

  const users = $resource('/api/users/current', {}, {
    getCurrentUser: {
      method: 'GET',
      headers: {
        token: getToken,
        'Cache-Control': 'no-cache, no-store',
        Pragma: 'no-cache'
      }
    },
    updateCurrentUser: {
      method: 'POST',
      headers: {
        token: getToken,
        'Cache-Control': 'no-cache, no-store',
        Pragma: 'no-cache'
      }
    },
    addTalk: {
      method: 'POST',
      url: '/api/users/current/talks',
      headers: {
        token: getToken,
        'Cache-Control': 'no-cache, no-store',
        Pragma: 'no-cache'
      }
    }
  });

  function getInfo() {
    const current = $q.defer();
    users.getCurrentUser({}, (data) => {
      current.resolve(data);
    },
    () => {
      current.resolve(null);
    });

    this.current = current.promise;
  }

  function updateInfo(userInfo) {
    users.updateCurrentUser(userInfo, () => {
    },
    () => {
      $rootScope.$broadcast('signInEvent');
    });
  }

  function uploadPhoto(file) {
    const formData = new FormData();
    formData.append('file', file);
    return $http.post('api/users/current/photo', formData, {
      transformRequest: angular.identity,
      headers: {
        token: getToken,
        'Cache-Control': 'no-cache, no-store',
        Pragma: 'no-cache',
        'Content-Type': undefined
      }
    });
  }

  // deleting photo
  function deleteUserPhoto() {
    return $http.delete('api/users/current/photo', {
      headers: {
        token: getToken,
        'Cache-Control': 'no-cache, no-store',
        Pragma: 'no-cache',
      }
    });
  }

  return {
    getInfo,
    updateInfo,
    uploadPhoto,
    deleteUserPhoto
  };
}

export default Current;