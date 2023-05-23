<?php

include 'conexion_collabuy.php';

$idLista = $_POST['idLista'];
$idProd = $_POST['idProd'];

$resultado = mysqli_query($con, "INSERT INTO Contiene (id_lista, id_producto, cantidad, comprado) VALUES ('$idLista', '$idProd', 1, false);");

#Comprobar si se ha ejecutado correctamente
if ($resultado) {
    echo '1';
}else{
    echo 'Ha habido un error: ' . mysqli_error($con);
}

mysqli_close($con);

?>