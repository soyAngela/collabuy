<?php

include 'conexion_collabuy.php';

$usuario = $_POST["usuario"];
$token = $_POST["token"];
# Ejecutar la sentencia SQL de actualizar token
$res = mysqli_query($con, "UPDATE Usuario SET token='$token' WHERE usuario='$usuario'");
# Comprobar si se ha ejecutado correctamente
if ($res) {
    echo '1';
}else{
    echo 'Ha habido un error: ' . mysqli_error($con);
}

mysqli_close($con);
?>