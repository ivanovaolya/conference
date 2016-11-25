/* global FormData */
/* global angular */

class UserPhoto {
  constructor($http) {
    'ngInject';

    this.http = $http;
  }

  uploadPhoto(file) {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post('api/user/current/photo', formData, {
      transformRequest: angular.identity,
      headers: {
        'Cache-Control': 'no-cache, no-store',
        Pragma: 'no-cache',
        'Content-Type': undefined
      }
    });
  }

  // deleting photo
  deleteUserPhoto() {
    return this.http.delete('api/user/current/photo', {
      headers: {
        'Cache-Control': 'no-cache, no-store',
        Pragma: 'no-cache',
      }
    });
  }
}

export default UserPhoto;

