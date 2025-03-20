import React, { useState, useEffect, useRef } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import "./chat_styles.css";

const Chat = ({ senderId, receiverId, receiverName }) => {
  const [conversations, setConversations] = useState([]);
  const [messageText, setMessageText] = useState("");
  const [senderUsername, setSenderUsername] = useState(""); 
  const stompClientRef = useRef(null);

  useEffect(() => {
    const fetchSenderUsername = async () => {
      try {
        const token = localStorage.getItem('authToken');
        const response = await fetch(`http://user.localhost/person/username/${senderId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error("Failed to fetch sender's username");
        }
        const username = await response.text(); 
        setSenderUsername(username);
      } catch (error) {
        console.error("Error fetching sender's username:", error);
        setSenderUsername("Unknown");
      }
    };

    fetchSenderUsername();
  }, [senderId]);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8083/chat"),
      onConnect: () => {
        console.log("Connected to chat server");

        client.subscribe(`/message/${senderId}/${receiverId}`, (messageOutput) => {
          const message = JSON.parse(messageOutput.body);
          addMessageToConversations(message);
        });

        client.subscribe(`/message/${receiverId}/${senderId}`, (messageOutput) => {
          const message = JSON.parse(messageOutput.body);
          addMessageToConversations(message);
        });
      },
    });

    client.activate();
    stompClientRef.current = client;

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
      }
    };
  }, [senderId, receiverId]);

  const addMessageToConversations = (message) => {
    setConversations((prevConversations) => [...prevConversations, message]);
  };

  const sendMessage = () => {
    if (stompClientRef.current && messageText.trim()) {
      const message = {
        senderId,
        receiverId,
        senderUsername, 
        receiverUsername: receiverName, 
        messageText: messageText.trim(),
        timestamp: new Date().toISOString(),
      };

      stompClientRef.current.publish({
        destination: "/app/add",
        body: JSON.stringify(message),
      });

      setMessageText("");
    }
  };

  return (
      <div className="chat-container">
        <div className="chat-box">
          {conversations.map((msg, index) => (
              <div key={index}>
                <strong>{msg.senderUsername}:</strong> {msg.messageText}
              </div>
          ))}
        </div>
        <input
            type="text"
            value={messageText}
            onChange={(e) => setMessageText(e.target.value)}
            placeholder="Type a message"
        />
        <button onClick={sendMessage}>Send</button>
      </div>
  );
};

export default Chat;
