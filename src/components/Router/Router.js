import React, { Component } from 'react';
import { Route, Switch } from 'react-router-dom'

import { Home, About, Documentation,Login, NotFound } from '../';

class Router extends Component {
  render() {
    return (
      <div>
        <Switch>
          <Route exact path='/' component={Home}/>
          <Route path='/documentation' component={Documentation}/>
          <Route path='/about' component={About}/>
          <Route path='/login' component={Login}/>
          <Route component={NotFound}/>
        </Switch>
      </div>
    );
  }
}

export default Router;