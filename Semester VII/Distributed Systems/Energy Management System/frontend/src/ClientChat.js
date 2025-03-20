import React from "react";
import { useSearchParams } from "react-router-dom";
import Chat from "./chat";

const ClientChat = () => {
    const [searchParams] = useSearchParams();
    const clientId = searchParams.get("personId");
    const clientName = searchParams.get("clientName") || "Client";
    const adminId = "dd50364c-490b-4749-895c-e52b8f7e28dc";
    const adminName = "admin";
    console.log(clientId);
    console.log(clientName);

    if (!clientId) {
        return <p>Loading client details...</p>;
    }

    return (
        <div className="chat-room">
            <div className="chat-header">
                <h1>Welcome, {clientName}</h1>
                <p>Chatting with {adminName}</p>
            </div>
            <div className="chat-window">
                {/* Initialize Chat component */}
                <Chat senderId={clientId} receiverId={adminId} receiverName={adminName} />
            </div>
        </div>
    );
};

export default ClientChat;