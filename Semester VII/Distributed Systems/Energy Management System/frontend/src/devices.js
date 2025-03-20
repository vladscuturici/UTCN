import React, { useState, useEffect, useRef } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import './DevicesContainer.css';
import { useNavigate } from "react-router-dom";
import { DatePicker } from "antd";
import dayjs from "dayjs";


function DevicesContainer({ personId }) {
    const [devices, setDevices] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const [notifications, setNotifications] = useState([]);
    const stompClientRef = useRef(null);
    const [selectedDate, setSelectedDate] = useState(dayjs());
    const navigate = useNavigate();

    useEffect(() => {
        if (!personId) return;

        const client = new Client({
            webSocketFactory: () => new SockJS("http://monitoring.localhost/webs"),
            reconnectDelay: 5000,
            onConnect: () => {
                console.log("Connected to WebSocket");
                client.subscribe(`/topic/notifications/${personId}`, (message) => {
                    setNotifications((prevNotifications) => {
                        const newNotification = message.body;
                        if (prevNotifications.length === 0 || prevNotifications[prevNotifications.length - 1] !== newNotification) {
                            return [...prevNotifications, newNotification];
                        }
                        return prevNotifications;
                    });
                });
            },
            onStompError: (frame) => {
                console.error("STOMP Error:", frame);
            },
        });

        client.activate();
        stompClientRef.current = client;

        return () => {
            if (stompClientRef.current) {
                stompClientRef.current.deactivate();
            }
        };
    }, [personId]);


    const fetchDeviceDetails = async (deviceId) => {
        console.log('Fetching details for device ID:', deviceId);
        try {
            const token = localStorage.getItem('authToken');
            const response = await fetch(`http://device.localhost/devices/${deviceId}`, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                }
            });
            if (!response.ok) {
                throw new Error(`HTTP status ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.error('Error fetching device details:', error);
            throw error;
        }
    };

    const fetchDevices = async () => {
        setIsLoading(true);
        setError(null);
        try {
            const token = localStorage.getItem('authToken'); // Get JWT token
            const response = await fetch(`http://user.localhost/person-device/person/${personId}`, {
                headers: {
                    Authorization: `Bearer ${token}`, // Add JWT to headers
                },
            });
            if (!response.ok) {
                throw new Error('Network response was not ok for fetching devices by personId');
            }
            const deviceIds = await response.json();
            console.log('Device IDs received:', deviceIds);

            const detailedDevices = await Promise.all(deviceIds.map(device => fetchDeviceDetails(device.deviceId)));
            setDevices(detailedDevices);
        } catch (error) {
            setError(`Failed to fetch devices: ${error.message}`);
            console.error('Failed to fetch devices:', error);
        } finally {
            setIsLoading(false);
        }
    };

    const handleViewChart = (deviceId) => {
        const formattedDate = selectedDate.format("YYYY-MM-DD");
        navigate(`/device-chart/${deviceId}?date=${formattedDate}`);
    };

    const openChat = async () => {
        try {
            console.log(`Attempting to fetch user details for personId: ${personId}`);
            const token = localStorage.getItem('authToken'); // Get JWT token

            const response = await fetch(`http://user.localhost/person/${personId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`, // Add JWT to headers
                },
            });

            if (!response.ok) {
                throw new Error('Failed to fetch user details');
            }

            const user = await response.json();
            console.log('Fetched user details:', user);

            const clientName = user.name || "Client";

            window.location.href = `/client?clientName=${encodeURIComponent(clientName)}&personId=${encodeURIComponent(personId)}`;
        } catch (error) {
            console.error('Error opening chat:', error);
        }
    };

    useEffect(() => {
        if (personId) {
            fetchDevices();
        }
    }, [personId]);

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;

    return (
        <div className="device-list">
            <h2>Devices</h2>
            <div className="device-cards">
                {devices.map((device) => (
                    <div className="device-card" key={device.id}>
                        {Object.entries(device).map(([key, value]) => {
                            if (key !== 'id') {
                                return <p key={key}><strong>{key}:</strong> {value}</p>;
                            }
                            return null;
                        })}
                        <DatePicker
                            value={selectedDate}
                            onChange={(date) => setSelectedDate(date)}
                            format="YYYY-MM-DD"
                            style={{ marginBottom: "10px" }}
                        />

                        <button className="chart-button" onClick={() => handleViewChart(device.id)}>
                            View Chart
                        </button>
                    </div>
                ))}

            </div>

            <div className="notifications">
                <h3>Notifications</h3>
                {notifications.map((notification, index) => (
                    <div key={index} className="notification">
                        {notification}
                    </div>
                ))}
            </div>
        </div>
    );
}

export default DevicesContainer;