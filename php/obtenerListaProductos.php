<?php

include 'conexion_collabuy.php';

$lista = $_POST["lista"];

$resultado = mysqli_query($con, "SELECT * FROM Producto INNER JOIN Contiene ON id_producto = id WHERE id_lista = '$lista';");

if (!$resultado) {
    echo 'Ha habido un error: ' . mysqli_error($con);
}

$arrayresultados = array();
#Construir la lista elemento a elemento
while ($fila = mysqli_fetch_assoc($resultado)) {
    $arrayresultados[] = array('id' => $fila['id'], 'nombre' => $fila['nombre'], 'cantidad' => $fila['cantidad'], 'comprado' => $fila['comprado']);
}

#Devolver el resultado en formato JSON
$res = json_encode($arrayresultados);
echo $res;

mysqli_close($con);

?>