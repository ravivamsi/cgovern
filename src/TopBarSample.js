import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import './TopBar.css';

class TopBarSample extends Component {
    render() {
        return (
            <ul className="NavList">
                <li>
                    <Link exact to="/">Home</Link>
                </li>
                
                <li>
                    <Link to="/login">Login</Link>
                </li>
            </ul>
        );
    }
}

export default TopBarSample;