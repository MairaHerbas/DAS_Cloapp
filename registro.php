<?php
$conexion = mysqli_connect("db", "admin", "test", "database");

if (!$conexion) {
    die(json_encode(array("estado" => "error", "mensaje" => "Fallo al conectar con BD")));
}

$user = $_POST['username'];
$pass = $_POST['password'];
$email = $_POST['email'];

$insertar = "INSERT INTO usuarios (username, password, email) VALUES ('$user', '$pass', '$email')";

if(mysqli_query($conexion, $insertar)) {
    echo json_encode(array("estado" => "ok", "mensaje" => "Usuario registrado"));
} else {
    echo json_encode(array("estado" => "error", "mensaje" => "Error o usuario ya existe"));
}
mysqli_close($conexion);
?>
