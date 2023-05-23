<?php

include 'conexion_collabuy.php';

$lista = $_POST["listId"];
$usuario = $_POST["usuario"];

$resultado = mysqli_query($con, "SELECT nombre FROM Producto INNER JOIN Contiene ON id_producto = id WHERE creador = '$usuario' OR id IN (SELECT id_producto FROM Contiene WHERE id_lista = '$lista');");

if (!$resultado) {
    echo 'Ha habido un error: ' . mysqli_error($con);
}

$arrayresultados = $resultado;

#Devolver el resultado en formato JSON
$res = json_encode($arrayresultados);
echo $res;

mysqli_close($con);

?>