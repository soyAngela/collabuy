<?php

include 'conexion_collabuy.php';

$idProducto = $_POST["productId"];
$idLista = $_POST["listId"];
$comprado = $_POST["comprado"];

$resultado = mysqli_query($con, "UPDATE Contiene SET comprado = '$comprado' WHERE id_producto = '$idProducto' AND id_lista = '$idLista';");

if (!$resultado) {
    echo 'Ha habido un error: ' . mysqli_error($con);
}

mysqli_close($con);

?>