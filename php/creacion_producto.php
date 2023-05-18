<?php

include 'conexion.php';

$nombre = $_POST["nombre"];
$descripcion = $_POST["descripcion"];
$imagen = $_POST["imagen"];
$usuario = $_["usuario"];

if($metodo == 0){
    # Ejecutar la sentencia SQL de inserción de producto
    $resultado = mysqli_query($con, "INSERT INTO Producto (nombre,descripcion,imagen,creador) VALUES('$nombre','$descripcion','$imagen','$usuario')");
    # Comprobar si se ha ejecutado correctamente
    if (!$resultado) {
        echo 'Ha ocurrido algún error: ' . mysqli_error($con);
    }
}

mysqli_close($con);
?>