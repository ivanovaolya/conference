export default class TalksController {
  constructor(Current, Talks, Menus) {
    'ngInject';

    this.current = Current.current;
    this.talks = Talks.getAll();
    this.filter = {};
    this.menuService = Menus;
    this.showFilters = true;
  }
  showSettings() {
    this.showFilters = !this.showFilters;
  }
}
