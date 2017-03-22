export default class {
  constructor($resource) {
    'ngInject';

    this.res = $resource('/api/topic');
  }

  query() {
    return this.res.query();
  }

  save(name, successCallback) {
    return this.res.save(
      { name },
      () => { successCallback(); });
  }
}