<?php

include 'conexion_collabuy.php';

$id = $_POST["id"];

$resultado = mysqli_query($con, "SELECT * FROM Producto INNER JOIN Contiene ON id_producto = id WHERE id_producto = '$id';");

if (!$resultado) {
    echo 'Ha habido un error: ' . mysqli_error($con);
}

$fila=mysqli_fetch_assoc($resultado);
$arrayresultados = array('nombre' => $fila['nombre'], 'cantidad' => $fila['cantidad'], 'descripcion' => $fila['descripcion']);

#Devolver el resultado en formato JSON
$res = json_encode($arrayresultados);
echo $res;

mysqli_close($con);

?>