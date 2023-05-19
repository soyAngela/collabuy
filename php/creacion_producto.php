<?php

include 'conexion_collabuy.php';

$nombre = $_POST["nombre"];
$descripcion = $_POST["descripcion"];
$imagen = $_POST["imagen"];
$usuario = $_POST["usuario"];


# Ejecutar la sentencia SQL de inserción de producto
$resultado = mysqli_query($con, "INSERT INTO Producto (nombre,descripcion,imagen,creador) VALUES('$nombre','$descripcion','$imagen','$usuario')");
# Comprobar si se ha ejecutado correctamente
if (!$resultado) {
    echo 'Ha ocurrido algún error: ' . mysqli_error($con);
}

mysqli_close($con);
?>