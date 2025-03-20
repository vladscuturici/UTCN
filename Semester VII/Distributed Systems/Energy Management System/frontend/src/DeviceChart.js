import React, { useEffect, useState } from "react";
import { useParams, useLocation } from "react-router-dom";
import { Line } from "react-chartjs-2";
import { Chart, registerables } from "chart.js";

Chart.register(...registerables);

const DeviceChart = () => {
    const { deviceId } = useParams();
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const selectedDate = queryParams.get("date");

    const [chartData, setChartData] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await fetch(`http://monitoring.localhost/monitoring/${selectedDate}/${deviceId}`);
                if (!response.ok) throw new Error("Failed to fetch data");

                const data = await response.json();
                setChartData({
                    labels: Array.from({ length: 24 }, (_, i) => `${i}:00`),
                    datasets: [
                        {
                            label: "Energy Consumption (kWh)",
                            data: data,
                            borderColor: "blue",
                            backgroundColor: "rgba(0, 0, 255, 0.2)",
                        },
                    ],
                });
            } catch (error) {
                console.error("Error fetching chart data:", error);
            }
        };

        fetchData();
    }, [deviceId, selectedDate]);

    return (
        <div style={{ width: "80%", margin: "auto", textAlign: "center" }}>
            <h2>Energy Consumption for {selectedDate}</h2>
            {chartData ? <Line data={chartData} /> : <p>Loading chart...</p>}
        </div>
    );
};

export default DeviceChart;
