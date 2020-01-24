import React from 'react';
import ReactDOM from 'react-dom';
import { Route, BrowserRouter as Router } from 'react-router-dom'
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import Admin from './components/Admin/Admin'
import Profile from './components/Profile/Profile'
import Projects from './components/Projects/Projects'
import Settings from './components/Settings/Settings'



const routing = (
    <Router>
      <div>
        <Route exact path="/" component={App} />
        <Route path="/admin" component={Admin} />
        <Route path="/profile" component={Profile} />
        <Route path="/projects" component={Projects} />
        <Route path="/settings" component={Settings} />
      </div>
    </Router>
  )

ReactDOM.render(routing , document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
