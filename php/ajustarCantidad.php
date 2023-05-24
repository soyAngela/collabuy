<?php

include 'conexion_collabuy.php';

$idProducto = $_POST["productId"];
$idLista = $_POST["listId"];
$cantidad = $_POST["cantidad"];

$resultado = mysqli_query($con, "UPDATE Contiene SET cantidad = '$cantidad' WHERE id_producto = '$idProducto' AND id_lista = '$idLista';");

if (!$resultado) {
    echo 'Ha habido un error: ' . mysqli_error($con);
}

mysqli_close($con);

?>