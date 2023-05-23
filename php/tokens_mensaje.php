<?php

include 'conexion_collabuy.php';

$lista = $_POST["idLista"];

$resultado = mysqli_query($con, "SELECT DISTINCT token FROM Usuario,Participacion WHERE id_lista = $lista AND Participacion.id_usuario = Usuario.usuario;");

if (!$resultado) {
    echo 'Ha habido un error: ' . mysqli_error($con);
}

$arrayresultados = array();
#Construir la lista elemento a elemento
while ($fila = mysqli_fetch_assoc($resultado)) {
    $arrayresultados[] = $fila['token'];
}

#Devolver el resultado en formato JSON
$res = json_encode($arrayresultados);
echo $res;

mysqli_close($con);

?>