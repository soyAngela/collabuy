<?php

include 'conexion_collabuy.php';

$lista = $_POST["listId"];
$usuario = $_POST["usuario"];

$resultado = mysqli_query($con, "SELECT id, nombre FROM Producto WHERE creador = '$usuario' OR id IN (SELECT id_producto FROM Sugerencias WHERE id_lista = '$lista');");

if (!$resultado) {
    echo 'Ha habido un error: ' . mysqli_error($con);
}

$arrayresultados = array();
#Construir la lista elemento a elemento
while ($fila = mysqli_fetch_assoc($resultado)) {
    $arrayresultados[] = array('id' => $fila['id'], 'nombre' => $fila['nombre']);
}

#Devolver el resultado en formato JSON
$res = json_encode($arrayresultados);
echo $res;

mysqli_close($con);

?>