import React, { useState, useEffect } from "react";
import Chat from "./chat";
import { useSearchParams } from "react-router-dom";

const ChatRoom = () => {
    const [users, setUsers] = useState([]);
    const [activeChat, setActiveChat] = useState(null);
    const [activeChatName, setActiveChatName] = useState("");
    const [searchParams] = useSearchParams();
    const adminName = searchParams.get("adminName") || "Admin";

    const senderId = "dd50364c-490b-4749-895c-e52b8f7e28dc";

    useEffect(() => {
        const token = localStorage.getItem('authToken'); 

        fetch("http://user.localhost/person", {
            method: "GET", 
            headers: {
                "Content-Type": "application/json", 
                Authorization: `Bearer ${token}`
            }
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Failed to fetch users");
                }
                return response.json();
            })
            .then((data) => setUsers(data))
            .catch((error) => {
                console.error("Error fetching users:", error);
                setUsers([]);
            });
    }, []);


    const handleChatSelect = (id, name) => {
        setActiveChat(id);
        setActiveChatName(name);
    };

    return (
        <div className="chat-room">
            <div className="chat-header">
                <h1>Welcome, {adminName}</h1>
            </div>
            <div className="users-list">
                <h2>Users</h2>
                <ul>
                    {users.map((user) => (
                        <li key={user.id}>
                            <button onClick={() => handleChatSelect(user.id, user.name)}>
                                {user.name}
                            </button>
                        </li>
                    ))}
                </ul>
            </div>
            <div className="chat-window">
                {activeChat ? (
                    <Chat senderId={senderId} receiverId={activeChat} receiverName={activeChatName} />
                ) : (
                    <p>Select a user to start chatting</p>
                )}
            </div>
        </div>
    );
};

export default ChatRoom;