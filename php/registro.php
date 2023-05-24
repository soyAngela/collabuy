<?php

include 'conexion_collabuy.php';

$usuario = $_POST["usuario"];
$contrasena = $_POST["contrasena"];
$token = $_POST["token"];

$resultado = mysqli_query($con, "INSERT INTO Usuario (usuario, contrasena, token) VALUES ('$usuario','$contrasena','$token')");

if ($resultado==true) {
    echo '1';
}else{
    echo 'Ha habido un error: ' . mysqli_error($con);
}

?>