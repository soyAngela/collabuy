<?php

include 'conexion_collabuy.php';

$id = $_POST["id"];

$resultado = mysqli_query($con, "SELECT imagen FROM Producto INNER JOIN Contiene ON id_producto = id WHERE id_producto = '$id';");

if (!$resultado) {
    echo 'Ha habido un error: ' . mysqli_error($con);
}

$fila=mysqli_fetch_assoc($resultado);

echo $fila['imagen'];

mysqli_close($con);

?>