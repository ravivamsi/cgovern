import React, { Component } from 'react';
import { Route, Switch } from 'react-router-dom'

import Home from './Home';
import Login from './Login';

class Router extends Component {
  render() {
    return (
      <div>
        <Switch>
          <Route exact path='/' component={Home}/>
         
          <Route path='/login' component={Login}/>
          
        </Switch>
      </div>
    );
  }
}

export default Router;