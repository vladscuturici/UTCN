import React, { useState } from 'react';
import './style.css';
import DevicesContainer from './devices';
import AdminPanel from './AdminPanel';
import "./ChatRoom.css";

export const login = async (username, password) => {
    const API_URL = 'http://user.localhost/person/login';
    const response = await fetch(API_URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name: username, password }),
    });

    if (response.ok) {
        const data = await response.json();
        console.log(data.token);
        localStorage.setItem('authToken', data.token);
        return data.user;
    } else {
        throw new Error('Invalid username or password');
    }
};


function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [user, setUser] = useState(null);
    const [errorMessage, setErrorMessage] = useState('');

    const validateLogin = async () => {
        try {
            const loggedInUser = await login(username, password);
            setUser(loggedInUser);
            setErrorMessage('');
        } catch (error) {
            setErrorMessage(error.message);
        }
    };

    if (user && user.role === 'admin') {
        return <AdminPanel user={user} />;
    } else if (user) {
        return <DevicesContainer personId={user.id} />;
    }

    return (
        <div className="login-container">
            <h2>Login</h2>
            <div className="input-group">
                <label htmlFor="username">Username</label>
                <input
                    type="text"
                    id="username"
                    placeholder="Enter your username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
            </div>
            <div className="input-group">
                <label htmlFor="password">Password</label>
                <input
                    type="password"
                    id="password"
                    placeholder="Enter your password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </div>
            <button className="login-btn" onClick={validateLogin}>
                Login
            </button>
            {errorMessage && <div className="error-message">{errorMessage}</div>}
        </div>
    );
}

export default Login;
