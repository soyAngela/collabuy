<?php

#include 'conexion_collabuy.php';
include 'creacion_producto.php';

echo $res;
$data = json_decode($res, true);
$idProd = $data['id'];
$idLista = $data['idLista'];

$resultado = mysqli_query($con, "INSERT INTO Contiene (id_lista, id_producto, cantidad, comprado) VALUES ('$idLista', '$idProd', 1, false)");

$resultado = mysqli_query($con, "INSERT INTO Sugerencias (id_lista, id_producto) VALUES ('$idLista', '$idProd')");

# Comprobar si se ha ejecutado correctamente
if ($resultado) {
    echo '1';
}else{
    echo 'Ha habido un error: ' . mysqli_error($con);
}

mysqli_close($con);

?>