<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Test</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
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
</head>
<body style="margin: 50px;">
    <h1>Test</h1>
    <br>
    <table class="table">
        <thead>
			<tr>
				<th>id_t</th>
				<th>data</th>
				<th>punctaj</th>
                <th>id_c</th>
			</tr>
		</thead>

        <tbody>
            <?php
            $servername = "localhost";
			$username = "root";
			$password = "";
			$database = "s08";
			$connection = new mysqli($servername, $username, $password, $database);
			if ($connection->connect_error) {
				die("Nu se poate conecta: " . $connection->connect_error);
			}
			$sql = "SELECT * FROM test";
			$result = $connection->query($sql);

            if (!$result) {
				die("Invalid " . $connection->error);
			}
			while($row = $result->fetch_assoc()) {
                echo "<tr>
                    <td>" . $row["id_t"] . "</td>
                    <td>" . $row["data"] . "</td>
                    <td>" . $row["punctaj"] . "</td>
                    <td>" . $row["id_c"] . "</td>
                </tr>";
            }

            $connection->close();
            ?>
        </tbody>
    </table>
</body>
</html>