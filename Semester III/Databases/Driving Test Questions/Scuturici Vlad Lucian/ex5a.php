<style>
        .button {
        background-color: blue;
        color: white;
        font-size: 20px;
        padding: 15px 30px;
        border-radius: 50px;
        border: 2px solid white;
        cursor: pointer;
        text-align: center;
        box-shadow: 0px 5px 15px rgba(0,0,0,0.3);
        transition: all 0.2s ease-in-out;
    }
    .button:hover {
        background-color: darkblue;
        border-color: darkblue;
        transform: translateY(-3px);
        box-shadow: 0px 8px 20px rgba(0,0,0,0.5);
    }
    .button:active {
        transform: translateY(2px);
        box-shadow: 0px 3px 10px rgba(0,0,0,0.3);
    }

    p {
    font-family: "Times New Roman", Times, serif;
    font-size: 30px;
    text-shadow: 1px 1px rgb(187, 186, 186);
    letter-spacing: 2px;
}
body {
    background-color: whitesmoke;
    background: linear-gradient(to right, #0df6ea, #2798ac);
    background-color: rgba(255, 0, 0, 0.5);
}
table {
    text-align:center;
    margin: auto;
    border: solid 3px;
    border-color: black;
    border-radius: 10px;
    box-shadow: 0px 0px 10px #333;
    animation: fade-in 1s;
    background: linear-gradient(rgba(255, 255, 255, 0.5), rgba(255, 255, 255, 0.5));
}

@keyframes fade-in {
    from { opacity: 0; }
    to { opacity: 1; }
}

th {
    background-color: #f44336;
    color: black;
    border: 3px;
    border-radius: 10px;
    padding: 10px;
    font-size: 20px;
}
td{
    border: solid 1px;
    border-color: black;
    width: auto;
    margin: 5px;
    padding: 10px;
    font-size: 18px;
    border-radius: 5px;
}

td:hover {
    background-color: #7D7DB8;
}

tr{
    text-align: center;
    margin: 5px;
    border-radius: 10px;
}

tr:hover {
    background-color: #B5B4FC;
}

tr:nth-child(even) {
    background-color: #3DFFE5;
}

tr:nth-child(odd) {
    background-color: #35E3CC;
}
    </style>
<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "s08";
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

echo "<h1>Exercitiul 5.a</h1>";
echo "<title>Exercitiul 5.a</title>";
echo "<p>a) Să se găsească răspunsurile invalide la testul cu identificatorul 1.</p>";
$conn->close();
?>