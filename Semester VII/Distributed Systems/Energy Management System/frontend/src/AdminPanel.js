import React, { useState, useEffect } from 'react';

function CRUDDevicesModal({ onClose }) {
    const [devices, setDevices] = useState([]);
    const [newDevice, setNewDevice] = useState({ description: '', address: '', maxHourlyEnergyConsumption: null });
    const [deviceToEdit, setDeviceToEdit] = useState(null);

    useEffect(() => {
        fetchDevices();
    }, []);

    const fetchDevices = async () => {
        try {
            const token = localStorage.getItem('authToken'); // Get JWT token
            console.log(token);
            const response = await fetch('http://device.localhost/devices', {
                headers: {
                    Authorization: `Bearer ${token}`, // Add JWT to headers
                },
            });
            const data = await response.json();
            setDevices(data);
        } catch (error) {
            console.error('Error fetching devices:', error);
        }
    };

    const handleInsertOrUpdateDevice = async () => {
        if (deviceToEdit) {
            await handleUpdateDevice(deviceToEdit);
        } else {
            await handleInsertDevice();
        }
    };

    const handleInsertDevice = async () => {
        try {
            const token = localStorage.getItem('authToken'); // Get JWT token
            const response = await fetch('http://device.localhost/devices', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`, // Add JWT to headers
                },
                body: JSON.stringify(newDevice),
            });
            if (response.ok) {
                fetchDevices();
                setNewDevice({ description: '', address: '', maxHourlyEnergyConsumption: null }); // Reset input fields
            } else {
                console.error('Failed to insert device');
            }
        } catch (error) {
            console.error('Error inserting device:', error);
        }
    };

    const handleUpdateDevice = async (deviceId) => {
        try {
            const token = localStorage.getItem('authToken'); // Get JWT token
            const response = await fetch('http://device.localhost/devices', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`, // Add JWT to headers
                },
                body: JSON.stringify(newDevice),
            });
            if (response.ok) {
                fetchDevices();
                setNewDevice({ description: '', address: '', maxHourlyEnergyConsumption: null });
                setDeviceToEdit(null);
            } else {
                console.error('Failed to update device');
            }
        } catch (error) {
            console.error('Error updating device:', error);
        }
    };

    const handleEditClick = (device) => {
        setDeviceToEdit(device.id);
        setNewDevice({
            description: device.description,
            address: device.address,
            maxHourlyEnergyConsumption: device.maxHourlyEnergyConsumption,
        });
    };

    const handleDeleteDevice = async (deviceId) => {
        try {
            const token = localStorage.getItem('authToken');
            const response = await fetch(`http://device.localhost/devices/${deviceId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`, // Add JWT to headers
                },
            });
            if (response.ok) {
                fetchDevices();
            } else {
                console.error('Failed to delete device');
            }
        } catch (error) {
            console.error('Error deleting device:', error);
        }
    };

    return (
        <div className="modal">
            <div className="modal-content">
                <span className="close" onClick={onClose}>&times;</span>
                <h2>CRUD Devices</h2>
                <table>
                    <thead>
                    <tr>
                        <th>Description</th>
                        <th>Address</th>
                        <th>Max Consumption</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {devices.map((device) => (
                        <tr key={device.id}>
                            <td>{device.description}</td>
                            <td>{device.address}</td>
                            <td>{device.maxHourlyEnergyConsumption}</td>
                            <td>
                                <button onClick={() => handleEditClick(device)}>Update</button>
                                <button onClick={() => handleDeleteDevice(device.id)}>Delete</button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
                <h3>{deviceToEdit ? 'Edit Device' : 'Add New Device'}</h3>
                <input
                    type="text"
                    placeholder="Description"
                    value={newDevice.description}
                    onChange={(e) => setNewDevice({ ...newDevice, description: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="Address"
                    value={newDevice.address}
                    onChange={(e) => setNewDevice({ ...newDevice, address: e.target.value })}
                />
                <input
                    type="number"
                    placeholder="Max Hourly Consumption"
                    value={newDevice.maxHourlyEnergyConsumption || ''}
                    onChange={(e) => setNewDevice({ ...newDevice, maxHourlyEnergyConsumption: parseFloat(e.target.value) })}
                />
                <button onClick={handleInsertOrUpdateDevice}>
                    {deviceToEdit ? 'Update' : 'Insert'}
                </button>
            </div>
        </div>
    );
}

function MapDevicesModal({ onClose, onSave }) {
    const [users, setUsers] = useState([]);
    const [devices, setDevices] = useState([]);
    const [selectedUser, setSelectedUser] = useState('');
    const [selectedDevice, setSelectedDevice] = useState('');

    useEffect(() => {
        const token = localStorage.getItem('authToken'); // Get JWT token
        fetch('http://user.localhost/person', {
            headers: {
                Authorization: `Bearer ${token}`, // Add JWT to headers
            },
        })
            .then(response => response.json())
            .then(data => setUsers(data))
            .catch(error => console.error('Error fetching users:', error));

        fetch('http://device.localhost/devices', {
            headers: {
                Authorization: `Bearer ${token}`, // Add JWT to headers
            },
        })
            .then(response => response.json())
            .then(data => {
                setDevices(data);
                console.log("Devices fetched for combobox:", data);
            })
            .catch(error => console.error('Error fetching devices:', error));
    }, []);

    const handleDeviceSelect = (deviceId) => {
        setSelectedDevice(deviceId);
        console.log("Selected device ID:", deviceId);
    };

    return (
        <div className="modal">
            <div className="modal-content">
                <span className="close" onClick={onClose}>&times;</span>
                <h2>Map Devices</h2>
                <select value={selectedUser} onChange={e => setSelectedUser(e.target.value)}>
                    <option value="">Select User</option>
                    {users.map(user => (
                        <option key={user.id} value={user.id}>{user.name}</option>
                    ))}
                </select>
                <select value={selectedDevice} onChange={e => handleDeviceSelect(e.target.value)}>
                    <option value="">Select Device</option>
                    {devices.map(device => (
                        <option key={device.id} value={device.id}>{device.description}</option>
                    ))}
                </select>

                <button onClick={() => onSave(selectedUser, selectedDevice)}>Map</button>
            </div>
        </div>
    );
}

function CRUDUsersModal({ onClose }) {
    const [users, setUsers] = useState([]);
    const [newUser, setNewUser] = useState({ name: '', role: '', password: '' });
    const [userToEdit, setUserToEdit] = useState(null);

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            const token = localStorage.getItem('authToken'); // Get JWT token
            const response = await fetch('http://user.localhost/person', {
                headers: {
                    Authorization: `Bearer ${token}`, // Add JWT to headers
                },
            });
            const data = await response.json();
            setUsers(data);
        } catch (error) {
            console.error('Error fetching users:', error);
        }
    };

    const handleInsertOrUpdateUser = async () => {
        if (userToEdit) {
            await handleUpdateUser(userToEdit);
        } else {
            await handleInsertUser();
        }
    };

    const handleInsertUser = async () => {
        try {
            const token = localStorage.getItem('authToken'); // Get JWT token
            const response = await fetch('http://user.localhost/person', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}`, },
                body: JSON.stringify(newUser),

            });
            if (response.ok) {
                fetchUsers();
                setNewUser({ name: '', role: '', password: '' });
            } else {
                console.error('Failed to insert user');
            }
        } catch (error) {
            console.error('Error inserting user:', error);
        }
    };

    const handleUpdateUser = async (userId) => {
        try {
            const token = localStorage.getItem('authToken'); // Get JWT token
            const response = await fetch(`http://user.localhost/person/${userId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' , Authorization: `Bearer ${token}`,},
                body: JSON.stringify(newUser),
            });
            if (response.ok) {
                fetchUsers();
                setNewUser({ name: '', role: '', password: '' });
                setUserToEdit(null);
            } else {
                console.error('Failed to update user');
            }
        } catch (error) {
            console.error('Error updating user:', error);
        }
    };

    const handleEditClick = (user) => {
        setUserToEdit(user.id);
        setNewUser({ name: user.name, role: user.role, password: '' });
    };

    const handleDeleteUser = async (userId) => {
        try {
            const response = await fetch(`http://user.localhost/person/${userId}`, {
                method: 'DELETE',
            });
            if (response.ok) {
                fetchUsers();
            } else {
                console.error('Failed to delete user');
            }
        } catch (error) {
            console.error('Error deleting user:', error);
        }
    };

    return (
        <div className="modal">
            <div className="modal-content">
                <span className="close" onClick={onClose}>&times;</span>
                <h2>CRUD Users</h2>
                <table>
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Role</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {users.map((user) => (
                        <tr key={user.id}>
                            <td>{user.name}</td>
                            <td>{user.role}</td>
                            <td>
                                <button onClick={() => handleEditClick(user)}>Update</button>
                                <button onClick={() => handleDeleteUser(user.id)}>Delete</button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
                <h3>{userToEdit ? 'Edit User' : 'Add New User'}</h3>
                <input
                    type="text"
                    placeholder="Name"
                    value={newUser.name}
                    onChange={(e) => setNewUser({ ...newUser, name: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="Role"
                    value={newUser.role}
                    onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={newUser.password}
                    onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
                />
                <button onClick={handleInsertOrUpdateUser}>
                    {userToEdit ? 'Update' : 'Insert'}
                </button>
            </div>
        </div>
    );
}


function AdminPanel({ user }) {
    const [showMapModal, setShowMapModal] = useState(false);
    const [showCRUDUsersModal, setShowCRUDUsersModal] = useState(false);
    const [showCRUDDevicesModal, setShowCRUDDevicesModal] = useState(false);
    const openChat = () => {
        const adminName = user?.name || "Admin";
        window.open(`/chat-room?adminName=${encodeURIComponent(adminName)}`, "_blank");
    };

    return (
        <div className="admin-panel">
            <h1 className="admin-panel-title">Admin Panel</h1> {/* Stylish title */}
            <div className="button-container">
                <button className="large-button" onClick={() => setShowCRUDUsersModal(true)}>
                    CRUD Users
                </button>
                <button className="large-button" onClick={() => setShowCRUDDevicesModal(true)}>
                    CRUD Devices
                </button>
                <button className="large-button" onClick={() => setShowMapModal(true)}>
                    Map Devices
                </button>
            </div>

            {showCRUDUsersModal && (
                <CRUDUsersModal onClose={() => setShowCRUDUsersModal(false)} />
            )}

            {showCRUDDevicesModal && (
                <CRUDDevicesModal onClose={() => setShowCRUDDevicesModal(false)} />
            )}

            {showMapModal && (
                <MapDevicesModal
                    onClose={() => setShowMapModal(false)}
                    onSave={async (userId, deviceId) => {
                        try {
                            const response = await fetch(`http://user.localhost/person-device/map`, {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ userId, deviceId }),
                            });
                            if (response.ok) {
                                console.log(`Device ${deviceId} successfully mapped to user ${userId}`);
                            } else {
                                console.error('Failed to map device to user');
                            }
                        } catch (error) {
                            console.error('Error mapping device to user:', error);
                        }
                    }}
                />
            )}
            <button className="chat-button" onClick={openChat}>Chat</button>
        </div>
    );
}

export default AdminPanel;