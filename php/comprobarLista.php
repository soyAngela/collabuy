<?php
include 'conexion_collabuy.php';

$usuario = $_POST["usuario"]
$clave = $_POST["clave"]

$resultado = mysqli_query($con, "SELECT * FROM Lista WHERE id = '$clave'");
$resultado2 = mysqli_query($con, "SELECT * FROM Participacion WHERE id_lista = '$clave' AND id_usuario = '$usuario'");

if ($resultado == true) {
    if ($resultado2 == true){
        echo '1';
    }
    else{
        echo '2';
    }
}
else{
        echo '3';
}

?>