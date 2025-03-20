import React from "react";
import { BrowserRouter as Router, Route, Routes, Navigate } from "react-router-dom";
import Login from "./login";
import ChatRoom from "./ChatRoom";
import AdminPanel from "./AdminPanel";
import ClientChat from "./ClientChat";
import DeviceChart from "./DeviceChart";


function App() {
    return (
        <Router>
            <div className="App">
                <header className="App-header">
                    <Routes>
                        {/* Default Route: Redirect to Login */}
                        <Route path="/" element={<Navigate to="/login" />} />

                        {/* Login Route */}
                        <Route path="/login" element={<Login />} />

                        {/* Admin Panel */}
                        <Route path="/admin-panel" element={<AdminPanel />} />

                        {/* Client Chat */}
                        <Route path="/client" element={<ClientChat />} />

                        {/* Chat Room */}
                        <Route path="/chat-room" element={
                            <div style={{ width: "100%", height: "100%" }}>
                                <ChatRoom />
                            </div>
                        } />
                        <Route path="/device-chart/:deviceId" element={<DeviceChart />} />

                    </Routes>
                </header>
            </div>
        </Router>
    );
}

export default App;
