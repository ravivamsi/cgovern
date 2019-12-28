import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import './NavBar.css';

class TopBar extends Component {
    render() {
        return (
            <ul className="NavList">
                <li>
                    <Link exact to="/">Home</Link>
                </li>
                <li>
                    <Link to="/documentation">Documentation</Link>
                </li>
                <li>
                    <Link to="/about">About</Link>
                </li>
                <li>
                    <Link to="/login">Login</Link>
                </li>
            </ul>
        );
    }
}

export default TopBar;