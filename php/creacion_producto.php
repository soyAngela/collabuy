<?php

include 'conexion_collabuy.php';

$nombre = $_POST["nombre"];
$descripcion = $_POST["descripcion"];
$imagen = $_POST["imagen"];
$usuario = $_POST["usuario"];
$idLista = $_POST["idLista"];

# Ejecutar la sentencia SQL de inserción de producto
$resultado = mysqli_query($con, "INSERT INTO Producto (nombre,descripcion,imagen,creador) VALUES('$nombre','$descripcion','$imagen','$usuario')");
# Comprobar si se ha ejecutado correctamente
if (!$resultado) {
    echo 'Ha ocurrido algún error: ' . mysqli_error($con);
}

$id = mysqli_query($con, "SELECT id FROM Producto WHERE creador = '$usuario' ORDER BY id DESC LIMIT 1");
if (!$id) {
    echo 'Ha ocurrido algún error: ' . mysqli_error($con);
}
$arrayresultados = array();
#Construir la lista elemento a elemento
while ($fila = mysqli_fetch_assoc($id)) {
    $arrayresultados = ['id' => $fila['id'], 'idLista' => $idLista];
}

#Devolver el resultado en formato JSON
$res = json_encode($arrayresultados);

echo $res;
#echo ($id);

#mysqli_close($con);
?>