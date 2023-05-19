<?php

include 'conexion_collabuy.php';

$usuario = $_POST["usuario"];
$contrasena = $_POST["contrasena"];

$resultado = mysqli_query($con, "INSERT INTO Usuario (usuario, contrasena) VALUES ('$usuario','$contrasena')");

if ($resultado==true) {
    echo '1';
}else{
    echo 'Ha habido un error: ' . mysqli_error($con);
}

?>