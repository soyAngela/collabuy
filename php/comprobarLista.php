<?php
include 'conexion_collabuy.php';

$usuario = $_POST["usuario"];
$nombreLista = $_POST["nombre"];
$clave = $_POST["clave"];

$resultado = mysqli_query($con, "SELECT * FROM Lista WHERE nombre = '$nombreLista' AND clave = '$clave'");

if (!$resultado) {
    echo 'No existe lista';
}

$arrayresultados = array();
#Construir la lista elemento a elemento
while ($fila = mysqli_fetch_assoc($resultado)) {
    $arrayresultados = ['usuario' => $usuario, 'id' => $fila['id'], 'nombre' => $fila['nombre'], 'clave' => $fila['clave']];
}

#Devolver el resultado en formato JSON
$res = json_encode($arrayresultados);


?>