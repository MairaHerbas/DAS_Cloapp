<?php
$conexion = mysqli_connect("db", "admin", "test", "database");

if (!$conexion) {
    die(json_encode(array("estado" => "error", "mensaje" => "Fallo al conectar con BD")));
}

$user = $_POST['username'];
$pass = $_POST['password'];

$consulta = "SELECT * FROM usuarios WHERE username='$user' AND password='$pass'";
$resultado = mysqli_query($conexion, $consulta);

if($fila = mysqli_fetch_assoc($resultado)) {
    echo json_encode(array("estado" => "ok", "mensaje" => "Login correcto", "id" => $fila['id']));
} else {
    echo json_encode(array("estado" => "error", "mensaje" => "Usuario o contraseña incorrectos"));
}
mysqli_close($conexion);
?>
